package com.frigobrain.ui.recipe;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.data.db.entity.NutritionLog;
import com.frigobrain.data.db.entity.ShoppingList;
import com.frigobrain.util.Constants;

import java.util.List;
import java.util.concurrent.Executors;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvName, tvCalories, tvProtein, tvTags, tvSteps;
    private Button btnAddToCart, btnLogIntake;
    private AppDatabase db;
    private long userId;
    private String recipeName;
    private int calories;
    private double protein, fat, carbs;
    private String instructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        db = AppDatabase.getInstance(this);
        userId = FrigoBrainApp.getCurrentUserId();

        tvName = findViewById(R.id.tv_name);
        tvCalories = findViewById(R.id.tv_calories);
        tvProtein = findViewById(R.id.tv_protein);
        tvTags = findViewById(R.id.tv_tags);
        tvSteps = findViewById(R.id.tv_steps);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnLogIntake = findViewById(R.id.btn_log_intake);

        // Read from Intent extras
        recipeName = getIntent().getStringExtra("recipeName");
        calories = getIntent().getIntExtra("calories", 250);
        protein = getIntent().getDoubleExtra("protein", 15.0);
        fat = getIntent().getDoubleExtra("fat", 10.0);
        carbs = getIntent().getDoubleExtra("carbs", 20.0);
        instructions = getIntent().getStringExtra("instructions");
        String tags = getIntent().getStringExtra("tags");

        tvName.setText(recipeName);
        tvCalories.setText(calories + " kcal");
        tvProtein.setText("蛋白 " + (int)protein + "g");
        tvSteps.setText(instructions != null ? instructions : "暂无详细步骤");
        tvTags.setText(tags != null ? tags : "");

        btnAddToCart.setOnClickListener(v -> addMissingToCart());
        btnLogIntake.setOnClickListener(v -> logTodayIntake());
    }

    private void addMissingToCart() {
        long recipeId = getIntent().getLongExtra("recipeId", -1);
        Executors.newSingleThreadExecutor().execute(() -> {
            if (recipeId > 0) {
                List<com.frigobrain.data.db.entity.RecipeIngredient> ingredients =
                        db.recipeDao().getIngredients(recipeId);
                for (var ri : ingredients) {
                    // Check if already in fridge
                    var existing = db.foodItemDao().getByName(userId, ri.getFoodName());
                    if (existing == null) {
                        long catId = guessCategory(ri.getFoodName());
                        ShoppingList item = new ShoppingList(userId, ri.getFoodName(), catId,
                                ri.getQuantity(), ri.getUnit());
                        db.shoppingListDao().insert(item);
                    }
                }
            }
            runOnUiThread(() -> Toast.makeText(this, "缺失食材已加入购物清单", Toast.LENGTH_SHORT).show());
        });
    }

    private void logTodayIntake() {
        NutritionLog log = new NutritionLog(userId, Constants.MEAL_LUNCH,
                System.currentTimeMillis(), calories, protein, fat, carbs);
        long recipeId = getIntent().getLongExtra("recipeId", -1);
        if (recipeId > 0) log.setRecipeId(recipeId);

        Executors.newSingleThreadExecutor().execute(() -> {
            db.nutritionLogDao().insert(log);
            runOnUiThread(() -> Toast.makeText(this, "已记录摄入: " + calories + "kcal", Toast.LENGTH_SHORT).show());
        });
    }

    private long guessCategory(String foodName) {
        String n = foodName.toLowerCase();
        if (n.contains("肉") || n.contains("鸡") || n.contains("鱼") || n.contains("虾") || n.contains("排骨")) return 3;
        if (n.contains("菜") || n.contains("椒") || n.contains("茄") || n.contains("葱") || n.contains("蒜") || n.contains("姜")) return 1;
        if (n.contains("果") || n.contains("蕉") || n.contains("莓")) return 2;
        if (n.contains("奶") || n.contains("酪") || n.contains("酸奶")) return 4;
        return 5;
    }
}
