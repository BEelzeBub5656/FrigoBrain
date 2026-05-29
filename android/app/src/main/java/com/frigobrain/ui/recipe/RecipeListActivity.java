package com.frigobrain.ui.recipe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.MainActivity;
import com.frigobrain.R;
import com.frigobrain.adapter.RecipeAdapter;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.data.db.entity.Recipe;
import com.frigobrain.util.RecipeApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class RecipeListActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearchApi;
    private LinearLayout suggestionsArea, suggestionsGrid;
    private TextView tvSectionTitle, tvHistoryTitle, tvHistory;
    private RecyclerView rvRecipes;
    private ProgressBar progress;
    private RecipeAdapter adapter;
    private AppDatabase db;
    private RecipeApiClient apiClient;
    private long userId;
    private SharedPreferences prefs;
    private Set<String> historySet;

    private static final String PREFS_NAME = "recipe_search_prefs";
    private static final String KEY_HISTORY = "search_history";

    private static final String[] SUGGESTIONS = {
        "番茄炒蛋", "麻婆豆腐", "宫保鸡丁", "红烧排骨", "清蒸鲈鱼",
        "青椒肉丝", "糖醋里脊", "冬瓜排骨汤", "鸡蛋羹", "土豆炖牛肉",
        "酸菜鱼", "回锅肉", "水煮肉片", "鱼香肉丝", "可乐鸡翅"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        setupHeader("智能菜谱");

        db = AppDatabase.getInstance(this);
        apiClient = new RecipeApiClient();
        userId = FrigoBrainApp.getCurrentUserId();
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        historySet = prefs.getStringSet(KEY_HISTORY, new HashSet<>());

        etSearch = findViewById(R.id.et_search);
        btnSearchApi = findViewById(R.id.btn_search_api);
        suggestionsArea = findViewById(R.id.suggestions_area);
        suggestionsGrid = findViewById(R.id.suggestions_grid);
        tvSectionTitle = findViewById(R.id.tv_section_title);
        tvHistoryTitle = findViewById(R.id.tv_history_title);
        tvHistory = findViewById(R.id.tv_history);
        rvRecipes = findViewById(R.id.rv_recipes);
        progress = findViewById(R.id.progress);

        adapter = new RecipeAdapter();
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        rvRecipes.setAdapter(adapter);

        // Search with animation
        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showSuggestions();
                suggestionsArea.setVisibility(View.VISIBLE);
                rvRecipes.setVisibility(View.GONE);
            }
        });
        etSearch.setOnClickListener(v -> {
            showSuggestions();
            suggestionsArea.setVisibility(View.VISIBLE);
            rvRecipes.setVisibility(View.GONE);
        });

        btnSearchApi.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (keyword.isEmpty()) return;
            addToHistory(keyword);
            progress.setVisibility(View.VISIBLE);
            suggestionsArea.setVisibility(View.GONE);
            searchFromApi(keyword);
        });

        // Show suggestions on load
        showSuggestions();
        loadMatchedRecipes();
    }

    private void showSuggestions() {
        // Build suggestion chips
        suggestionsGrid.removeAllViews();
        for (String sug : SUGGESTIONS) {
            TextView chip = new TextView(this);
            chip.setText(sug);
            chip.setPadding(20, 10, 20, 10);
            chip.setTextSize(13f);
            chip.setTextColor(getColor(R.color.primary));
            chip.setBackgroundColor(getColor(R.color.primaryLight));
            chip.setClickable(true);
            chip.setFocusable(true);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 10, 10);
            chip.setLayoutParams(lp);
            chip.setOnClickListener(v -> {
                etSearch.setText(sug);
                addToHistory(sug);
                progress.setVisibility(View.VISIBLE);
                suggestionsArea.setVisibility(View.GONE);
                searchFromApi(sug);
            });
            suggestionsGrid.addView(chip);
        }

        // History
        if (historySet != null && !historySet.isEmpty()) {
            tvHistoryTitle.setVisibility(View.VISIBLE);
            tvHistory.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (String h : historySet) {
                sb.append("🔍 ").append(h).append("\n");
            }
            tvHistory.setText(sb.toString().trim());
        } else {
            tvHistoryTitle.setVisibility(View.GONE);
            tvHistory.setVisibility(View.GONE);
        }
    }

    private void addToHistory(String keyword) {
        if (historySet == null) historySet = new HashSet<>();
        historySet.add(keyword);
        prefs.edit().putStringSet(KEY_HISTORY, historySet).apply();
    }

    private void loadMatchedRecipes() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Recipe> recipes = db.recipeDao().getAllSync();
            if (recipes == null) recipes = new ArrayList<>();
            runOnUiThread(() -> adapter.setRecipes(recipes));
        });
    }

    private void searchFromApi(String keyword) {
        apiClient.searchRecipes(keyword, 10, new RecipeApiClient.Callback() {
            @Override
            public void onSuccess(List<RecipeApiClient.ApiRecipe> apiRecipes) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    rvRecipes.setVisibility(View.VISIBLE);
                    List<Recipe> recipes = new ArrayList<>();
                    for (var ar : apiRecipes) {
                        Recipe r = new Recipe(ar.name, ar.toInstructions(),
                                apiClient.estimateCalories(ar), 15.0, 10.0, 20.0);
                        r.setCuisineType(ar.category != null ? ar.category : "");
                        r.setDifficulty("中等");
                        r.setTags("网络搜索");
                        r.setIsSystem(2);
                        recipes.add(r);
                    }
                    adapter.setRecipes(recipes);
                    Toast.makeText(RecipeListActivity.this,
                            "找到 " + apiRecipes.size() + " 道菜谱", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    // Fallback to local
                    Executors.newSingleThreadExecutor().execute(() -> {
                        List<Recipe> local = db.recipeDao().getAllSync();
                        runOnUiThread(() -> {
                            rvRecipes.setVisibility(View.VISIBLE);
                            adapter.setRecipes(local != null ? local : new ArrayList<>());
                            Toast.makeText(RecipeListActivity.this,
                                    "网络查询失败，显示本地菜谱", Toast.LENGTH_SHORT).show();
                        });
                    });
                });
            }
        });
    }

    private void setupHeader(String title) {
        TextView tv = findViewById(R.id.tv_page_title);
        if (tv != null) tv.setText(title);
        findViewById(R.id.btn_home).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
