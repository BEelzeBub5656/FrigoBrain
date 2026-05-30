package com.frigobrain.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.frigobrain.data.db.converter.DateConverter;
import com.frigobrain.data.db.dao.FoodCategoryDao;
import com.frigobrain.data.db.dao.FoodItemDao;
import com.frigobrain.data.db.dao.FoodWasteRecordDao;
import com.frigobrain.data.db.dao.DietaryPreferenceDao;
import com.frigobrain.data.db.dao.MealPlanDao;
import com.frigobrain.data.db.dao.NutritionLogDao;
import com.frigobrain.data.db.dao.RecipeDao;
import com.frigobrain.data.db.dao.ShoppingListDao;
import com.frigobrain.data.db.dao.UserDao;
import com.frigobrain.data.db.entity.DeviceInfo;
import com.frigobrain.data.db.entity.DietaryPreference;
import com.frigobrain.data.db.entity.FoodCategory;
import com.frigobrain.data.db.entity.FoodItem;
import com.frigobrain.data.db.entity.FoodWasteRecord;
import com.frigobrain.data.db.entity.MealPlan;
import com.frigobrain.data.db.entity.MealPlanRecipe;
import com.frigobrain.data.db.entity.NutritionLog;
import com.frigobrain.data.db.entity.Recipe;
import com.frigobrain.data.db.entity.RecipeIngredient;
import com.frigobrain.data.db.entity.ShoppingList;
import com.frigobrain.data.db.entity.User;

import java.util.concurrent.Executors;

@Database(entities = {
    User.class, FoodCategory.class, FoodItem.class,
    Recipe.class, RecipeIngredient.class,
    DietaryPreference.class,
    MealPlan.class, MealPlanRecipe.class,
    FoodWasteRecord.class, NutritionLog.class,
    ShoppingList.class, DeviceInfo.class
}, version = 2, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract FoodCategoryDao foodCategoryDao();
    public abstract FoodItemDao foodItemDao();
    public abstract RecipeDao recipeDao();
    public abstract FoodWasteRecordDao foodWasteRecordDao();
    public abstract NutritionLogDao nutritionLogDao();
    public abstract ShoppingListDao shoppingListDao();
    public abstract MealPlanDao mealPlanDao();
    public abstract DietaryPreferenceDao dietaryPreferenceDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "frigobrain.db"
                    )
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadExecutor().execute(() ->
                                getInstance(context).seedDatabase()
                            );
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 种子数据：预置分类、常见食材、菜谱和营养参考
     * 数据来源：中国食物成分表公开数据 + 中国居民膳食指南
     */
    private void seedDatabase() {
        // --- 食材分类（5类）---
        FoodCategory[] categories = {
            new FoodCategory("蔬菜", "FRIDGE", 1),
            new FoodCategory("水果", "FRIDGE", 2),
            new FoodCategory("肉类", "FREEZER", 3),
            new FoodCategory("乳制品", "FRIDGE", 4),
            new FoodCategory("其他", "PANTRY", 5)
        };
        for (FoodCategory c : categories) foodCategoryDao().insert(c);

        // --- 预置菜谱库（20道，含完整营养数据）---
        long now = System.currentTimeMillis();
        Recipe[] recipes = {
            createRecipe("番茄炒蛋", "家常菜", "午/晚餐", "简单", 5, 10, 2,
                180, 10.5, 12.0, 8.5,
                "1. 鸡蛋打散加盐；2. 番茄切块；3. 热油炒蛋盛出；4. 炒番茄出汁后倒入蛋块翻炒均匀",
                "炒菜,家常,高蛋白"),
            createRecipe("清炒菠菜", "家常菜", "午/晚餐", "简单", 5, 5, 2,
                60, 4.5, 1.5, 5.0,
                "1. 菠菜洗净沥干；2. 蒜末爆香；3. 大火翻炒1-2分钟加盐出锅",
                "素食,低卡,快手"),
            createRecipe("番茄蛋汤", "家常菜", "任意", "简单", 5, 10, 2,
                95, 6.0, 5.5, 4.0,
                "1. 番茄切块，鸡蛋打散；2. 水烧开放番茄煮2分钟；3. 淋入蛋液搅拌成蛋花；4. 加盐葱花出锅",
                "汤品,低卡,快手"),
            createRecipe("土豆炖牛肉", "东北菜", "午/晚餐", "中等", 20, 45, 3,
                430, 32.0, 18.0, 30.0,
                "1. 牛肉切块焯水；2. 土豆胡萝卜切滚刀；3. 油热炒牛肉加料酒老抽；4. 加开水小火炖40分钟；5. 加土豆再炖15分钟收汁",
                "炖菜,高蛋白"),
            createRecipe("西兰花炒鸡胸肉", "轻食", "午/晚餐", "简单", 10, 15, 2,
                220, 28.0, 8.0, 6.0,
                "1. 鸡胸肉切丁加料酒淀粉腌10分钟；2. 西兰花焯水；3. 热油炒鸡丁至变色；4. 加入西兰花蚝油翻炒均匀",
                "减脂,高蛋白"),
            createRecipe("葱花鸡蛋饼", "家常菜", "早餐", "简单", 5, 8, 2,
                240, 12.0, 14.0, 18.0,
                "1. 鸡蛋打散加面粉水调成糊；2. 加葱花盐；3. 平底锅薄油倒入面糊摊平；4. 两面各煎2分钟至金黄",
                "早餐,快手"),
            createRecipe("麻婆豆腐", "川菜", "午/晚餐", "中等", 10, 15, 2,
                280, 18.0, 20.0, 8.0,
                "1. 豆腐切块焯水；2. 肉末炒出油加豆瓣酱；3. 加豆腐轻轻翻炒；4. 加水淀粉勾芡撒花椒粉",
                "川菜,下饭"),
            createRecipe("蒜蓉西兰花", "家常菜", "任意", "简单", 5, 8, 2,
                70, 5.0, 2.0, 6.0,
                "1. 西兰花焯水；2. 蒜末爆香；3. 加入西兰花蚝油翻炒均匀",
                "素食,低卡,快手"),
            createRecipe("水果沙拉", "轻食", "任意", "简单", 10, 0, 2,
                150, 2.0, 1.5, 32.0,
                "1. 苹果香蕉切块；2. 加入酸奶拌匀；3. 冷藏10分钟即可",
                "素食,甜点,低卡"),
            createRecipe("牛奶燕麦粥", "早餐", "早餐", "简单", 2, 5, 1,
                220, 8.0, 5.0, 35.0,
                "1. 燕麦加牛奶煮开；2. 小火煮3分钟；3. 加蜂蜜调味",
                "早餐,快手"),
            createRecipe("青椒肉丝", "家常菜", "午/晚餐", "中等", 10, 12, 2,
                260, 20.0, 16.0, 5.0,
                "1. 猪肉切丝加料酒生抽腌10分钟；2. 青椒切丝；3. 热油炒肉丝盛出；4. 炒青椒后加入肉丝翻炒",
                "家常,下饭"),
            createRecipe("冬瓜排骨汤", "粤菜", "任意", "中等", 10, 50, 3,
                310, 22.0, 18.0, 10.0,
                "1. 排骨焯水；2. 冬瓜去皮切块；3. 排骨加水姜片炖40分钟；4. 加冬瓜再炖10分钟加盐",
                "汤品,滋补"),
            createRecipe("宫保鸡丁", "川菜", "午/晚餐", "中等", 15, 15, 2,
                320, 25.0, 18.0, 12.0,
                "1. 鸡胸切丁加料酒生抽腌；2. 花生米炸熟；3. 热油炒鸡丁加干辣椒花椒；4. 加糖醋酱油汁勾芡；5. 撒花生米",
                "川菜,下饭"),
            createRecipe("蔬菜沙拉", "轻食", "任意", "简单", 10, 0, 2,
                80, 3.0, 4.0, 8.0,
                "1. 生菜番茄黄瓜洗净切好；2. 橄榄油醋汁拌匀",
                "素食,低碳,快手"),
            createRecipe("鸡蛋羹", "家常菜", "任意", "简单", 3, 12, 2,
                120, 9.0, 7.0, 2.0,
                "1. 鸡蛋加水打散过滤；2. 盅内淋生抽；3. 蒸锅水开后蒸10分钟；4. 淋香油葱花",
                "快手,高蛋白"),
            createRecipe("醋溜白菜", "鲁菜", "任意", "简单", 5, 8, 2,
                85, 3.0, 5.0, 8.0,
                "1. 白菜帮切片；2. 热油爆香干辣椒花椒；3. 大火炒白菜；4. 加醋糖盐调味",
                "素食,快手,低卡"),
            createRecipe("香菇鸡汤", "闽菜", "任意", "中等", 15, 60, 3,
                180, 20.0, 8.0, 5.0,
                "1. 鸡块焯水；2. 香菇泡发；3. 鸡块+姜片+香菇+水大火煮开；4. 小火炖1小时加盐",
                "汤品,滋补"),
            createRecipe("糖醋里脊", "鲁菜", "午/晚餐", "中等", 15, 15, 2,
                350, 22.0, 16.0, 28.0,
                "1. 猪里脊切条加料酒盐腌；2. 调糖醋汁：番茄酱+糖+醋+水；3. 肉条挂糊油炸至金黄；4. 锅中倒糖醋汁烧浓放入肉条翻匀",
                "家常,下饭"),
            createRecipe("清蒸鲈鱼", "粤菜", "午/晚餐", "中等", 10, 15, 2,
                180, 25.0, 6.0, 2.0,
                "1. 鲈鱼洗净划花刀；2. 鱼身摆姜片葱段；3. 蒸锅水开蒸8分钟；4. 倒掉汤汁淋蒸鱼豉油；5. 热油浇在鱼身上",
                "海鲜,高蛋白,低脂"),
            createRecipe("香菇青菜", "家常菜", "任意", "简单", 5, 8, 2,
                65, 4.0, 2.0, 6.0,
                "1. 香菇切片，青菜洗净；2. 热油炒香菇；3. 加入青菜大火翻炒加蚝油",
                "素食,低卡,快手"),
        };
        for (Recipe r : recipes) recipeDao().insert(r);

        // --- 菜谱食材关联 ---
        RecipeIngredient[][] recipeIngredients = {
            // 番茄炒蛋 (recipe_id=1)
            {ri(1, "番茄", 2, "个"), ri(1, "鸡蛋", 3, "个"), ri(1, "油", 1, "勺")},
            // 清炒菠菜 (2)
            {ri(2, "菠菜", 300, "g"), ri(2, "蒜", 3, "瓣")},
            // 番茄蛋汤 (3)
            {ri(3, "番茄", 1, "个"), ri(3, "鸡蛋", 1, "个"), ri(3, "葱", 1, "根")},
            // 土豆炖牛肉 (4)
            {ri(4, "牛肉", 300, "g"), ri(4, "土豆", 2, "个"), ri(4, "胡萝卜", 1, "根")},
            // 西兰花炒鸡胸肉 (5)
            {ri(5, "西兰花", 1, "颗"), ri(5, "鸡胸肉", 200, "g"), ri(5, "蒜", 2, "瓣")},
            // 葱花鸡蛋饼 (6)
            {ri(6, "鸡蛋", 2, "个"), ri(6, "面粉", 100, "g"), ri(6, "葱", 2, "根")},
            // 麻婆豆腐 (7)
            {ri(7, "豆腐", 1, "块"), ri(7, "猪肉", 100, "g"), ri(7, "葱", 1, "根")},
            // 蒜蓉西兰花 (8)
            {ri(8, "西兰花", 1, "颗"), ri(8, "蒜", 4, "瓣")},
            // 水果沙拉 (9)
            {ri(9, "苹果", 1, "个"), ri(9, "香蕉", 1, "根"), ri(9, "酸奶", 1, "杯")},
            // 牛奶燕麦粥 (10)
            {ri(10, "牛奶", 250, "ml"), ri(10, "燕麦", 50, "g")},
            // 青椒肉丝 (11)
            {ri(11, "猪肉", 200, "g"), ri(11, "青椒", 3, "个"), ri(11, "蒜", 2, "瓣")},
            // 冬瓜排骨汤 (12)
            {ri(12, "排骨", 400, "g"), ri(12, "冬瓜", 500, "g"), ri(12, "姜", 3, "片")},
            // 宫保鸡丁 (13)
            {ri(13, "鸡胸肉", 250, "g"), ri(13, "花生米", 50, "g"), ri(13, "青椒", 1, "个")},
            // 蔬菜沙拉 (14)
            {ri(14, "生菜", 100, "g"), ri(14, "番茄", 1, "个"), ri(14, "黄瓜", 1, "根")},
            // 鸡蛋羹 (15)
            {ri(15, "鸡蛋", 2, "个"), ri(15, "葱", 1, "根")},
            // 醋溜白菜 (16)
            {ri(16, "白菜", 300, "g"), ri(16, "蒜", 2, "瓣")},
            // 香菇鸡汤 (17)
            {ri(17, "鸡", 500, "g"), ri(17, "香菇", 6, "朵"), ri(17, "姜", 3, "片")},
            // 糖醋里脊 (18)
            {ri(18, "猪肉", 250, "g"), ri(18, "鸡蛋", 1, "个"), ri(18, "面粉", 50, "g")},
            // 清蒸鲈鱼 (19)
            {ri(19, "鲈鱼", 1, "条"), ri(19, "姜", 4, "片"), ri(19, "葱", 2, "根")},
            // 香菇青菜 (20)
            {ri(20, "香菇", 4, "朵"), ri(20, "青菜", 200, "g"), ri(20, "蒜", 2, "瓣")},
        };
        for (RecipeIngredient[] arr : recipeIngredients) {
            for (RecipeIngredient ri : arr) recipeDao().insertIngredient(ri);
        }

        // === 演示数据 ===
        long d = System.currentTimeMillis();
        long day = 24 * 60 * 60 * 1000L;

        // 内置管理员
        User admin = new User("admin", sha256("admin"), "管理员", "OWNER");
        userDao().insert(admin);

        // 演示食材（各种过期状态）
        FoodItem[] demos = {
            item(1, 1, "番茄", 3, "个", d - 2*day, d + 5*day, 5.0),
            item(1, 1, "菠菜", 1, "把", d - 1*day, d + 1*day, 3.5),
            item(1, 2, "苹果", 5, "个", d - 3*day, d + 10*day, 12.0),
            item(1, 1, "西兰花", 1, "颗", d - 4*day, d + 2*day, 8.0),
            item(1, 3, "鸡胸肉", 500, "g", d - 1*day, d + 3*day, 15.0),
            item(1, 4, "牛奶", 2, "L", d - 1*day, d + 6*day, 18.0),
            item(1, 3, "鸡蛋", 10, "个", d - 5*day, d + 20*day, 8.0),
            item(1, 1, "黄瓜", 2, "根", d, d + 7*day, 4.0),
        };
        for (FoodItem fi : demos) foodItemDao().insert(fi);

        // 营养日志（过去7天）
        NutritionLog[] logs = {
            log(1, 1L, "BREAKFAST", d-6*day, 350, 12, 15, 28),
            log(1, null, "LUNCH", d-6*day, 620, 28, 22, 60),
            log(1, 1L, "DINNER", d-6*day, 180, 10, 12, 8),
            log(1, null, "BREAKFAST", d-5*day, 240, 12, 14, 18),
            log(1, 7L, "LUNCH", d-5*day, 280, 18, 20, 8),
            log(1, 5L, "DINNER", d-5*day, 220, 28, 8, 6),
            log(1, 1L, "BREAKFAST", d-4*day, 180, 10, 12, 8),
            log(1, 11L, "LUNCH", d-4*day, 260, 20, 16, 5),
            log(1, null, "DINNER", d-4*day, 450, 25, 18, 30),
            log(1, 6L, "BREAKFAST", d-3*day, 240, 12, 14, 18),
            log(1, null, "LUNCH", d-3*day, 550, 22, 18, 50),
            log(1, 16L, "DINNER", d-3*day, 85, 3, 5, 8),
            log(1, 10L, "BREAKFAST", d-2*day, 220, 8, 5, 35),
            log(1, 5L, "LUNCH", d-2*day, 220, 28, 8, 6),
            log(1, 3L, "DINNER", d-2*day, 95, 6, 5, 4),
            log(1, 1L, "BREAKFAST", d-1*day, 180, 10, 12, 8),
            log(1, 7L, "LUNCH", d-1*day, 280, 18, 20, 8),
            log(1, 19L, "DINNER", d-1*day, 180, 25, 6, 2),
            log(1, 6L, "BREAKFAST", d, 240, 12, 14, 18),
        };
        for (NutritionLog l : logs) nutritionLogDao().insert(l);
    }

    private String sha256(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { return input; }
    }

    private FoodItem item(long uid, long cid, String name, double qty, String unit,
                          long buy, long exp, double price) {
        FoodItem f = new FoodItem(uid, cid, name, qty, unit, buy, exp);
        f.setPrice(price);
        return f;
    }

    private NutritionLog log(long uid, Long recipeId, String meal, long date,
                             int cal, double pro, double fat, double carb) {
        NutritionLog l = new NutritionLog(uid, meal, date, cal, pro, fat, carb);
        l.setRecipeId(recipeId);
        return l;
    }

    private Recipe createRecipe(String name, String cuisine, String meal, String diff,
                                 int prep, int cook, int servings,
                                 int cal, double pro, double f, double carb, String steps, String tags) {
        Recipe r = new Recipe(name, steps, cal, pro, f, carb);
        r.setCuisineType(cuisine);
        r.setMealType(meal);
        r.setDifficulty(diff);
        r.setPrepTime(prep);
        r.setCookTime(cook);
        r.setServings(servings);
        r.setTags(tags);
        r.setIsSystem(1);
        return r;
    }

    private RecipeIngredient ri(long recipeId, String foodName, double quantity, String unit) {
        RecipeIngredient ri = new RecipeIngredient(recipeId, foodName, quantity, unit);
        return ri;
    }
}
