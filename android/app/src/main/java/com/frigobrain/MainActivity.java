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
    private TextView tvExpiryAlert, tvTotalCount, tvExpiringCount, tvWeeklyCal, tvFoodList;
    private LinearLayout bannerExpiry;
    private AppDatabase db;
    private long userId;
    private long backPressedTime = 0;
    private Class<?> currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
        userId = FrigoBrainApp.getCurrentUserId();

        tvTotalCount = findViewById(R.id.tv_total_count);
        tvExpiringCount = findViewById(R.id.tv_expiring_count);
        tvWeeklyCal = findViewById(R.id.tv_weekly_cal);
        tvExpiryAlert = findViewById(R.id.tv_expiry_alert);
        tvFoodList = findViewById(R.id.tv_food_list);
        bannerExpiry = findViewById(R.id.banner_expiry);
        bottomNav = findViewById(R.id.bottom_nav);

        // Quick feature buttons
        findViewById(R.id.btn_quick_add).setOnClickListener(v ->
                startActivity(new Intent(this, FoodAddActivity.class)));
        findViewById(R.id.btn_quick_recipe).setOnClickListener(v ->
                navigateTo(RecipeListActivity.class));
        findViewById(R.id.btn_quick_nutrition).setOnClickListener(v ->
                navigateTo(NutritionActivity.class));
        findViewById(R.id.card_total).setOnClickListener(v ->
                navigateTo(InventoryActivity.class));
        findViewById(R.id.card_expiring).setOnClickListener(v ->
                navigateTo(InventoryActivity.class));

        // Bottom nav
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_fridge) { navigateTo(InventoryActivity.class); return true; }
            if (id == R.id.nav_recipes) { navigateTo(RecipeListActivity.class); return true; }
            if (id == R.id.nav_stats) { navigateTo(NutritionActivity.class); return true; }
            return false;
        });

        loadDashboard();
    }

    private void navigateTo(Class<?> target) {
        if (target != currentPage) {
            currentPage = target;
            Intent intent = new Intent(this, target);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }

    private void loadDashboard() {
        db.foodItemDao().getActiveByUser(userId).observe(this, list -> {
            int total = list != null ? list.size() : 0;
            tvTotalCount.setText(String.valueOf(total));

            int expiring = 0;
            if (list != null) {
                long now = System.currentTimeMillis();
                long threshold = DateUtils.daysFromNow(3);
                for (var f : list) {
                    if (f.getExpiryDate() <= threshold && f.getExpiryDate() >= now) expiring++;
                }
            }
            tvExpiringCount.setText(String.valueOf(expiring));
            if (expiring > 0) {
                bannerExpiry.setVisibility(View.VISIBLE);
                tvExpiryAlert.setText(expiring + " 种食材将在3天内过期");
            } else {
                bannerExpiry.setVisibility(View.GONE);
            }

            if (list != null && !list.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (var f : list) {
                    int days = DateUtils.daysUntil(f.getExpiryDate());
                    String status = days < 0 ? "🔴" : days <= 3 ? "🟡" : "🟢";
                    sb.append(status).append(" ").append(f.getName())
                      .append("  ").append(f.getQuantity()).append(f.getUnit())
                      .append("  ").append(days).append("天\n");
                }
                tvFoodList.setText(sb.toString().trim());
            }
        });

        // Weekly calories
        long weekStart = DateUtils.weekStart();
        long weekEnd = DateUtils.daysFromNow(0);
        db.nutritionLogDao().getWeekSummary(userId, weekStart, weekEnd)
                .observe(this, s -> {
                    if (s != null) {
                        tvWeeklyCal.setText(String.valueOf(s.totalCalories));
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentPage = null;
        loadDashboard();
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
