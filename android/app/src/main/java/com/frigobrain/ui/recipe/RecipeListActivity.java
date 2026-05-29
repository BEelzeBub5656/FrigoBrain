package com.frigobrain.ui.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.frigobrain.R;
import com.frigobrain.adapter.RecipeAdapter;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.data.db.entity.Recipe;
import com.frigobrain.data.db.entity.ShoppingList;
import com.frigobrain.util.RecipeApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class RecipeListActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearchApi;
    private LinearLayout tabs;
    private RecyclerView rvRecipes;
    private ProgressBar progress;
    private RecipeAdapter adapter;
    private AppDatabase db;
    private RecipeApiClient apiClient;
    private long userId;

    private String currentTab = "推荐"; // 推荐 / 搜索 / 收藏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        db = AppDatabase.getInstance(this);
        apiClient = new RecipeApiClient();
        userId = FrigoBrainApp.getCurrentUserId();

        etSearch = findViewById(R.id.et_search);
        btnSearchApi = findViewById(R.id.btn_search_api);
        tabs = findViewById(R.id.tabs);
        rvRecipes = findViewById(R.id.rv_recipes);
        progress = findViewById(R.id.progress);

        adapter = new RecipeAdapter();
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        rvRecipes.setAdapter(adapter);

        // Category tabs
        addTab("推荐", "推荐");
        addTab("中式", "中式");
        addTab("快手", "快手");
        addTab("汤品", "汤品");

        // Load local recipes
        loadLocalRecipes("推荐");

        // Search button - use Juhui API
        btnSearchApi.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                Toast.makeText(this, "请输入食材或菜名搜索", Toast.LENGTH_SHORT).show();
                return;
            }
            progress.setVisibility(View.VISIBLE);
            searchFromApi(keyword);
        });

        // Local search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) searchLocalRecipes(s.toString());
                else loadLocalRecipes(currentTab);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void addTab(String label, String tag) {
        TextView tab = new TextView(this);
        tab.setText(label);
        tab.setPadding(20, 10, 20, 10);
        tab.setTextSize(13);
        boolean selected = currentTab.equals(tag);
        tab.setTextColor(selected
                ? getResources().getColor(android.R.color.white, null)
                : getResources().getColor(R.color.primary, null));
        if (selected) tab.setBackgroundTintList(getResources().getColorStateList(R.color.primary, null));

        tab.setOnClickListener(v -> {
            currentTab = tag;
            tabs.removeAllViews();
            addTab("推荐", "推荐");
            addTab("中式", "中式");
            addTab("快手", "快手");
            addTab("汤品", "汤品");
            loadLocalRecipes(tag);
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(4, 0, 4, 0);
        tabs.addView(tab, params);
    }

    private void loadLocalRecipes(String filter) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Recipe> recipes;
            switch (filter) {
                case "中式":
                    recipes = db.recipeDao().getByCuisine("中式").getValue();
                    break;
                case "快手":
                    recipes = db.recipeDao().getByTag("快手").getValue();
                    break;
                case "汤品":
                    recipes = db.recipeDao().getByTag("汤品").getValue();
                    break;
                default:
                    // "推荐" = smart match based on fridge inventory
                    var matched = db.recipeDao().getMatchedRecipes(userId).getValue();
                    recipes = new ArrayList<>();
                    if (matched != null) {
                        for (var row : matched) {
                            Recipe r = db.recipeDao().getByIdSync(row.recipeId);
                            if (r != null) {
                                r.setTags(r.getTags() + " | 匹配" + (int)(row.matchRate * 100) + "%");
                                recipes.add(r);
                            }
                        }
                    }
                    // Fallback to all recipes if no matches
                    if (recipes == null || recipes.isEmpty()) {
                        recipes = db.recipeDao().getAllSync();
                    }
                    break;
            }

            if (recipes == null) recipes = new ArrayList<>();
            final List<Recipe> finalRecipes = recipes;
            runOnUiThread(() -> adapter.setRecipes(finalRecipes));
        });
    }

    private void searchLocalRecipes(String query) {
        db.recipeDao().search(query).observe(this, recipes -> {
            if (recipes != null) adapter.setRecipes(recipes);
        });
    }

    /** 用聚合数据API搜索菜谱 */
    private void searchFromApi(String keyword) {
        apiClient.searchRecipes(keyword, 10, new RecipeApiClient.Callback() {
            @Override
            public void onSuccess(List<RecipeApiClient.ApiRecipe> apiRecipes) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    List<Recipe> recipes = new ArrayList<>();
                    for (RecipeApiClient.ApiRecipe ar : apiRecipes) {
                        Recipe r = new Recipe(ar.name, ar.toInstructions(),
                                apiClient.estimateCalories(ar), 15.0, 10.0, 20.0);
                        r.setCuisineType(ar.category != null ? ar.category : "");
                        r.setDifficulty("中等");
                        r.setTags("API搜索");
                        r.setIsSystem(2); // Mark as API result
                        recipes.add(r);
                    }
                    adapter.setRecipes(recipes);
                    Toast.makeText(RecipeListActivity.this,
                            "从网络找到 " + apiRecipes.size() + " 道菜谱", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(RecipeListActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
