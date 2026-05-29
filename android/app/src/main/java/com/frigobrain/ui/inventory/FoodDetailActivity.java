package com.frigobrain.ui.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.data.db.entity.FoodItem;
import com.frigobrain.data.db.entity.FoodWasteRecord;
import com.frigobrain.util.Constants;
import com.frigobrain.util.DateUtils;

import java.util.concurrent.Executors;

public class FoodDetailActivity extends AppCompatActivity {

    private TextView tvName, tvCategory, tvQuantity, tvPurchaseDate, tvExpiryDate, tvStatus;
    private Button btnMarkConsumed, btnDelete;
    private AppDatabase db;
    private FoodItem food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        db = AppDatabase.getInstance(this);
        tvName = findViewById(R.id.tv_name);
        tvCategory = findViewById(R.id.tv_category);
        tvQuantity = findViewById(R.id.tv_quantity);
        tvPurchaseDate = findViewById(R.id.tv_purchase_date);
        tvExpiryDate = findViewById(R.id.tv_expiry_date);
        tvStatus = findViewById(R.id.tv_status);
        btnMarkConsumed = findViewById(R.id.btn_mark_consumed);
        btnDelete = findViewById(R.id.btn_delete);

        long foodId = getIntent().getLongExtra("foodId", -1);
        if (foodId == -1) { finish(); return; }

        loadFood(foodId);

        btnMarkConsumed.setOnClickListener(v -> markConsumed());
        btnDelete.setOnClickListener(v -> deleteFood());
    }

    private void loadFood(long foodId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            var items = db.foodItemDao().getActiveByUser(FrigoBrainApp.getCurrentUserId());
            db.foodItemDao().getActiveByUser(FrigoBrainApp.getCurrentUserId())
                    .observe(this, list -> {
                        for (FoodItem f : list) {
                            if (f.getFoodId() == foodId) {
                                food = f;
                                displayFood();
                                break;
                            }
                        }
                    });
        });
    }

    private void displayFood() {
        tvName.setText(food.getName());
        tvQuantity.setText(food.getQuantity() + " " + food.getUnit());
        tvPurchaseDate.setText(Constants.DATE_FMT_CN.format(food.getPurchaseDate()));
        tvExpiryDate.setText(Constants.DATE_FMT_CN.format(food.getExpiryDate()));

        int days = DateUtils.daysUntil(food.getExpiryDate());
        if (days < 0) {
            tvStatus.setText("已过期 " + Math.abs(days) + " 天");
            tvStatus.setTextColor(getResources().getColor(R.color.statusExpired, null));
        } else if (days <= 3) {
            tvStatus.setText("即将过期，剩 " + days + " 天");
            tvStatus.setTextColor(getResources().getColor(R.color.statusExpiringSoon, null));
        } else {
            tvStatus.setText("新鲜，剩 " + days + " 天");
            tvStatus.setTextColor(getResources().getColor(R.color.statusFresh, null));
        }

        // Category
        Executors.newSingleThreadExecutor().execute(() -> {
            String catName = db.foodCategoryDao().getNameById(food.getCategoryId());
            runOnUiThread(() -> tvCategory.setText(catName != null ? catName : "其他"));
        });
    }

    private void markConsumed() {
        long now = System.currentTimeMillis();
        Executors.newSingleThreadExecutor().execute(() -> {
            db.foodItemDao().markConsumed(food.getFoodId(), now);
            runOnUiThread(() -> {
                Toast.makeText(this, "已标记为消耗", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteFood() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Record waste
            FoodWasteRecord waste = new FoodWasteRecord(
                    food.getUserId(), food.getName(), food.getCategoryId(),
                    food.getQuantity(), food.getUnit(), System.currentTimeMillis());
            waste.setEstimatedCost(food.getPrice() * food.getQuantity());
            waste.setWasteReason(Constants.WASTE_OTHER);
            if (food.getFoodId() > 0) waste.setFoodId(food.getFoodId());
            db.foodWasteRecordDao().insert(waste);

            db.foodItemDao().delete(food);
            runOnUiThread(() -> {
                Toast.makeText(this, "已删除，浪费已记录", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
