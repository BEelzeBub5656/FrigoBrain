package com.frigobrain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.ui.inventory.InventoryActivity;
import com.frigobrain.ui.mealplan.MealPlanActivity;
import com.frigobrain.ui.profile.UserProfileActivity;
import com.frigobrain.ui.recipe.RecipeListActivity;
import com.frigobrain.ui.stats.NutritionActivity;
import com.frigobrain.util.DateUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private TextView tvUserName, tvExpiryAlert;
    private LinearLayout bannerExpiry;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
        tvUserName = findViewById(R.id.tv_user_name);
        tvExpiryAlert = findViewById(R.id.tv_expiry_alert);
        bannerExpiry = findViewById(R.id.banner_expiry);
        bottomNav = findViewById(R.id.bottom_nav);

        // Load user display name
        long userId = FrigoBrainApp.getCurrentUserId();
        Executors.newSingleThreadExecutor().execute(() -> {
            var user = db.userDao().getByIdSync(userId);
            runOnUiThread(() -> {
                if (user != null) tvUserName.setText(user.getDisplayName());
            });
        });

        // Check expiring foods
        checkExpiringFoods();

        // Bottom navigation
        bottomNav.setOnItemSelectedListener(item -> {
            Intent intent = null;
            int id = item.getItemId();
            if (id == R.id.nav_fridge) {
                intent = new Intent(this, InventoryActivity.class);
            } else if (id == R.id.nav_recipes) {
                intent = new Intent(this, RecipeListActivity.class);
            } else if (id == R.id.nav_plan) {
                intent = new Intent(this, MealPlanActivity.class);
            } else if (id == R.id.nav_stats) {
                intent = new Intent(this, NutritionActivity.class);
            } else if (id == R.id.nav_profile) {
                intent = new Intent(this, UserProfileActivity.class);
            }
            if (intent != null) {
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Default: go to inventory
        findViewById(R.id.fragment_container).setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class));
        });
        startActivity(new Intent(this, InventoryActivity.class));
    }

    private void checkExpiringFoods() {
        long userId = FrigoBrainApp.getCurrentUserId();
        long now = System.currentTimeMillis();
        long threeDaysLater = DateUtils.daysFromNow(3);

        Executors.newSingleThreadExecutor().execute(() -> {
            var items = db.foodItemDao().getExpiringFoodsWithRecipes(userId, now, threeDaysLater);
            db.foodItemDao().getExpiringFoodsWithRecipes(userId, now, threeDaysLater)
                    .observe(this, foods -> {
                        if (foods != null && !foods.isEmpty()) {
                            bannerExpiry.setVisibility(View.VISIBLE);
                            tvExpiryAlert.setText(foods.size() + " 种食材将在3天内过期，建议尽快使用");
                            bannerExpiry.setOnClickListener(v ->
                                    startActivity(new Intent(this, InventoryActivity.class))
                            );
                        }
                    });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkExpiringFoods();
    }
}
