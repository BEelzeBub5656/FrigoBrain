package com.frigobrain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.ui.inventory.FoodAddActivity;
import com.frigobrain.ui.inventory.InventoryActivity;
import com.frigobrain.ui.recipe.RecipeListActivity;
import com.frigobrain.ui.stats.NutritionActivity;
import com.frigobrain.util.DateUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private AppDatabase db;
    private long userId;
    private long backPressedTime = 0;
    private Class<?> currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            db = AppDatabase.getInstance(this);
            userId = FrigoBrainApp.getCurrentUserId();
            bottomNav = findViewById(R.id.bottom_nav);

            // Quick action buttons
            View btnAdd = findViewById(R.id.btn_quick_add);
            View btnRecipe = findViewById(R.id.btn_quick_recipe);
            View btnNutrition = findViewById(R.id.btn_quick_nutrition);
            if (btnAdd != null) btnAdd.setOnClickListener(v -> startActivity(new Intent(this, FoodAddActivity.class)));
            if (btnRecipe != null) btnRecipe.setOnClickListener(v -> navigateTo(RecipeListActivity.class));
            if (btnNutrition != null) btnNutrition.setOnClickListener(v -> navigateTo(NutritionActivity.class));

            // Bottom nav
            if (bottomNav != null) {
                bottomNav.setOnItemSelectedListener(item -> {
                    int id = item.getItemId();
                    if (id == R.id.nav_home) return true;
                    if (id == R.id.nav_fridge) { navigateTo(InventoryActivity.class); return true; }
                    if (id == R.id.nav_recipes) { navigateTo(RecipeListActivity.class); return true; }
                    if (id == R.id.nav_stats) { navigateTo(NutritionActivity.class); return true; }
                    return false;
                });
            }

            refreshDashboard();
        } catch (Exception e) {
            Toast.makeText(this, "仪表盘加载失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void navigateTo(Class<?> target) {
        if (target != currentPage) {
            currentPage = target;
            Intent intent = new Intent(this, target);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }

    @SuppressWarnings("unchecked")
    private void refreshDashboard() {
        try {
            // Food stats
            db.foodItemDao().getActiveByUser(userId).observe(this, list -> {
                try {
                    TextView tvTotal = findViewById(R.id.tv_total_count);
                    TextView tvExpiring = findViewById(R.id.tv_expiring_count);
                    TextView tvFoodList = findViewById(R.id.tv_food_list);
                    LinearLayout banner = findViewById(R.id.banner_expiry);
                    LinearLayout bannerExpiry = banner;
                    TextView tvExpiryAlert = findViewById(R.id.tv_expiry_alert);

                    int total = list != null ? list.size() : 0;
                    if (tvTotal != null) tvTotal.setText(String.valueOf(total));

                    int expiring = 0;
                    if (list != null) {
                        long now = System.currentTimeMillis();
                        long limit = DateUtils.daysFromNow(3);
                        for (var f : list) {
                            if (f.getExpiryDate() <= limit && f.getExpiryDate() >= now) expiring++;
                        }
                    }
                    if (tvExpiring != null) tvExpiring.setText(String.valueOf(expiring));
                    if (bannerExpiry != null && tvExpiryAlert != null) {
                        if (expiring > 0) {
                            bannerExpiry.setVisibility(View.VISIBLE);
                            tvExpiryAlert.setText(expiring + " 种食材将在3天内过期");
                        } else {
                            bannerExpiry.setVisibility(View.GONE);
                        }
                    }

                    if (tvFoodList != null && list != null && !list.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (var f : list) {
                            int days = DateUtils.daysUntil(f.getExpiryDate());
                            String s = days < 0 ? "🔴" : days <= 3 ? "🟡" : "🟢";
                            sb.append(s).append(" ").append(f.getName())
                              .append("  ").append(f.getQuantity()).append(f.getUnit())
                              .append("  ").append(days).append("天\n");
                        }
                        tvFoodList.setText(sb.toString().trim());
                    }
                } catch (Exception ignored) {}
            });

            // Weekly calories
            db.nutritionLogDao().getWeekSummary(userId, DateUtils.weekStart(), DateUtils.daysFromNow(0))
                    .observe(this, s -> {
                        try {
                            TextView tv = findViewById(R.id.tv_weekly_cal);
                            if (tv != null && s != null) tv.setText(String.valueOf(s.totalCalories));
                        } catch (Exception ignored) {}
                    });
        } catch (Exception ignored) {}
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentPage = null;
    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - backPressedTime < 2000) {
            finishAffinity();
        } else {
            backPressedTime = now;
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        }
    }
}
