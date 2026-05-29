package com.frigobrain.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 聚合数据 - 菜谱查询API 客户端
 * 文档: https://www.juhe.cn/docs/api/id/733
 *
 * 接口: GET http://apis.juhe.cn/fapigx/caipu/query
 * 参数: key (必填), word (必填-食材/菜名), num, page
 * 返回: JSON - cp_name(菜名), zuofa(做法), tiaoliao(调料), yuanliao(原料), texing(特色), type_name(分类)
 */
public class RecipeApiClient {

    private static final String TAG = "RecipeApiClient";
    private static final String BASE_URL = "http://apis.juhe.cn/fapigx/caipu/query";
    private static final String API_KEY = "02d37a41f4885157b12f1d07c9eb1081";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /** 菜谱查询结果 */
    public static class ApiRecipe {
        public String id;
        public String name;       // cp_name
        public String category;   // type_name (如"川菜"/"家常菜")
        public String steps;      // zuofa - 做法
        public String seasonings; // tiaoliao - 调料
        public String ingredients;// yuanliao - 原料
        public String feature;    // texing - 特色
        public String tips;       // tishi - 小贴士

        /** 转为本地 Recipe 表的 instructions 格式 */
        public String toInstructions() {
            StringBuilder sb = new StringBuilder();
            if (ingredients != null && !ingredients.isEmpty()) {
                sb.append("【原料】").append(ingredients).append("\n\n");
            }
            if (seasonings != null && !seasonings.isEmpty()) {
                sb.append("【调料】").append(seasonings).append("\n\n");
            }
            if (steps != null && !steps.isEmpty()) {
                sb.append("【做法】\n").append(steps);
            }
            return sb.toString();
        }
    }

    public interface Callback {
        void onSuccess(List<ApiRecipe> recipes);
        void onError(String message);
    }

    /**
     * 按食材或菜名搜索菜谱
     * @param keyword 食材名或菜名（如"番茄"/"麻婆豆腐"）
     * @param count 返回数量 (1-20)
     */
    public void searchRecipes(String keyword, int count, Callback callback) {
        executor.execute(() -> {
            HttpURLConnection conn = null;
            try {
                String url = BASE_URL + "?key=" + API_KEY
                        + "&word=" + URLEncoder.encode(keyword, "UTF-8")
                        + "&num=" + count
                        + "&page=1";
                Log.d(TAG, "Request: " + url);

                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                int code = conn.getResponseCode();
                if (code != 200) {
                    callback.onError("网络请求失败: HTTP " + code);
                    return;
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                JSONObject json = new JSONObject(sb.toString());
                int errorCode = json.getInt("error_code");

                if (errorCode != 0) {
                    callback.onError("API错误: " + json.optString("reason", "未知错误"));
                    return;
                }

                JSONObject result = json.getJSONObject("result");
                JSONArray list = result.getJSONArray("list");

                List<ApiRecipe> recipes = new ArrayList<>();
                for (int i = 0; i < list.length(); i++) {
                    JSONObject item = list.getJSONObject(i);
                    ApiRecipe r = new ApiRecipe();
                    r.id = item.optString("id", "");
                    r.name = item.optString("cp_name", "");
                    r.category = item.optString("type_name", "");
                    r.steps = item.optString("zuofa", "");
                    r.seasonings = item.optString("tiaoliao", "");
                    r.ingredients = item.optString("yuanlaio", "");
                    r.feature = item.optString("texing", "");
                    r.tips = item.optString("tishi", "");
                    recipes.add(r);
                }
                callback.onSuccess(recipes);

            } catch (Exception e) {
                Log.e(TAG, "API error", e);
                callback.onError("查询失败: " + e.getMessage());
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
    }

    /**
     * 搜索菜谱并自动计算营养估算值
     * 基于常见食材营养参考值粗略估算（非精确计算）
     */
    public int estimateCalories(ApiRecipe recipe) {
        // 粗估：根据菜系和原料关键词估算
        // 这里返回一个合理的默认值，实际由本地菜谱库补充
        String text = (recipe.ingredients + recipe.name).toLowerCase();

        int baseCal = 250; // 默认中等热量

        if (text.contains("肉") || text.contains("排骨") || text.contains("鸡")) baseCal += 100;
        if (text.contains("鱼") || text.contains("虾")) baseCal += 30;
        if (text.contains("蛋")) baseCal += 50;
        if (text.contains("豆腐") || text.contains("蔬菜") || text.contains("青菜")) baseCal -= 50;
        if (text.contains("汤")) baseCal -= 80;
        if (text.contains("炒")) baseCal += 50;
        if (text.contains("炸") || text.contains("煎")) baseCal += 120;
        if (text.contains("蒸") || text.contains("煮")) baseCal -= 30;

        return Math.max(baseCal, 60);
    }
}
