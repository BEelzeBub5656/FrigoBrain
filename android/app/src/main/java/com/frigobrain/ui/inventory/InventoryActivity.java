package com.frigobrain.ui.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.MainActivity;
import com.frigobrain.R;
import com.frigobrain.adapter.FoodItemAdapter;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.data.model.FoodWithCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class InventoryActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnAdd;
    private LinearLayout categoryTabs;
    private RecyclerView rvFoods;
    private FoodItemAdapter adapter;
    private AppDatabase db;
    private long selectedCategoryId = -1; // -1 = all
    private long userId;
    private List<FoodWithCategory> allFoods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        db = AppDatabase.getInstance(this);
        userId = FrigoBrainApp.getCurrentUserId();

        setupHeader("冰箱总览");

        etSearch = findViewById(R.id.et_search);
        btnAdd = findViewById(R.id.btn_add);
        categoryTabs = findViewById(R.id.category_tabs);
        rvFoods = findViewById(R.id.rv_foods);

        // RecyclerView setup
        adapter = new FoodItemAdapter();
        rvFoods.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rvFoods.setAdapter(adapter);

        // Add button
        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, FoodAddActivity.class)));

        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFoods(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Load categories for tabs
        loadCategoryTabs();
        loadFoods();
    }

    private void loadCategoryTabs() {
        Executors.newSingleThreadExecutor().execute(() -> {
            var categories = db.foodCategoryDao().getAllSync();
            runOnUiThread(() -> {
                categoryTabs.removeAllViews();

                // "All" tab
                addTab("全部", -1, selectedCategoryId == -1);

                for (var cat : categories) {
                    boolean selected = selectedCategoryId == cat.getCategoryId();
                    addTab(cat.getName(), cat.getCategoryId(), selected);
                }
            });
        });
    }

    private void addTab(String name, long catId, boolean selected) {
        TextView tab = new TextView(this);
        tab.setText(name);
        tab.setPadding(24, 10, 24, 10);
        tab.setTextSize(13);
        tab.setBackgroundResource(selected
                ? android.R.drawable.btn_default : android.R.drawable.btn_default_small);
        tab.setTextColor(selected
                ? getResources().getColor(android.R.color.white, null)
                : getResources().getColor(R.color.primary, null));
        if (selected) tab.setBackgroundTintList(getResources().getColorStateList(R.color.primary, null));

        tab.setOnClickListener(v -> {
            selectedCategoryId = catId;
            loadCategoryTabs();
            filterFoods(etSearch.getText().toString());
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(4, 0, 4, 0);
        categoryTabs.addView(tab, params);
    }

    private void loadFoods() {
        db.foodItemDao().getActiveByUser(userId).observe(this, foodItems -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                var categories = db.foodCategoryDao().getAllSync();
                List<FoodWithCategory> result = new ArrayList<>();
                if (foodItems != null) {
                    for (var food : foodItems) {
                        FoodWithCategory fwc = new FoodWithCategory();
                        fwc.food = food;
                        for (var cat : categories) {
                            if (cat.getCategoryId() == food.getCategoryId()) {
                                fwc.category = cat;
                                break;
                            }
                        }
                        result.add(fwc);
                    }
                }
                allFoods = result;
                runOnUiThread(() -> filterFoods(etSearch.getText().toString()));
            });
        });
    }

    private void filterFoods(String query) {
        List<FoodWithCategory> filtered = new ArrayList<>();
        for (var fwc : allFoods) {
            boolean catMatch = selectedCategoryId == -1 ||
                    fwc.category.getCategoryId() == selectedCategoryId;
            boolean nameMatch = query.isEmpty() ||
                    fwc.food.getName().toLowerCase().contains(query.toLowerCase());
            if (catMatch && nameMatch) {
                filtered.add(fwc);
            }
        }
        adapter.setItems(filtered);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFoods();
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
