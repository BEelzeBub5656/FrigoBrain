package com.frigobrain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.ui.inventory.FoodAddActivity;
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
    private Class<?> currentPage;
    private long backPressedTime = 0;

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

        // Bottom navigation: launch activities with FLAG_ACTIVITY_SINGLE_TOP
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Class<?> target = null;
            if (id == R.id.nav_fridge) {
                target = InventoryActivity.class;
            } else if (id == R.id.nav_recipes) {
                target = RecipeListActivity.class;
            } else if (id == R.id.nav_plan) {
                target = MealPlanActivity.class;
            } else if (id == R.id.nav_stats) {
                target = NutritionActivity.class;
            } else if (id == R.id.nav_profile) {
                target = UserProfileActivity.class;
            }
            if (target != null && target != currentPage) {
                currentPage = target;
                Intent intent = new Intent(this, target);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            return target != null;
        });

        // Default: go to inventory
        Intent intent = new Intent(this, InventoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        currentPage = InventoryActivity.class;
    }

    private void checkExpiringFoods() {
        long userId = FrigoBrainApp.getCurrentUserId();
        long now = System.currentTimeMillis();
        long threeDaysLater = DateUtils.daysFromNow(3);

        db.foodItemDao().getExpiringFoodsWithRecipes(userId, now, threeDaysLater)
                .observe(this, foods -> {
                    if (foods != null && !foods.isEmpty()) {
                        bannerExpiry.setVisibility(View.VISIBLE);
                        tvExpiryAlert.setText(foods.size() + " 种食材将在3天内过期，建议尽快使用");
                        bannerExpiry.setOnClickListener(v -> {
                            Intent intent = new Intent(this, InventoryActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                        });
                    } else {
                        bannerExpiry.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkExpiringFoods();
    }

    @Override
    public void onBackPressed() {
        // Double-tap back to exit
        long now = System.currentTimeMillis();
        if (now - backPressedTime < 2000) {
            finishAffinity();
        } else {
            backPressedTime = now;
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        }
    }
}
