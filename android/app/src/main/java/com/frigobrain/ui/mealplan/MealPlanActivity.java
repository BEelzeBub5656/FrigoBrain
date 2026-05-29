package com.frigobrain.ui.mealplan;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.data.db.entity.MealPlan;
import com.frigobrain.data.db.entity.MealPlanRecipe;
import com.frigobrain.data.db.entity.Recipe;
import com.frigobrain.util.Constants;
import com.frigobrain.util.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 周膳食计划 - 7列网格拖拽规划，自动生成采购清单
 */
public class MealPlanActivity extends AppCompatActivity {

    private AppDatabase db;
    private long userId;

    private TableLayout weekGrid;
    private Button btnGenerateList;
    private RecyclerView rvShoppingList;

    private MealPlan currentPlan;
    private long planId;

    // Days of week labels (Chinese)
    private static final String[] DAY_NAMES = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    // Meal time labels (Chinese)
    private static final String[] MEAL_TIMES = {"早餐", "午餐", "晚餐"};
    // Meal time label -> DB constant
    private static final Map<String, String> MEAL_LABEL_TO_CONST = new HashMap<>();
    static {
        MEAL_LABEL_TO_CONST.put("早餐", Constants.MEAL_BREAKFAST);
        MEAL_LABEL_TO_CONST.put("午餐", Constants.MEAL_LUNCH);
        MEAL_LABEL_TO_CONST.put("晚餐", Constants.MEAL_DINNER);
    }
    // DB constant -> Meal time label
    private static final Map<String, String> MEAL_CONST_TO_LABEL = new HashMap<>();
    static {
        MEAL_CONST_TO_LABEL.put(Constants.MEAL_BREAKFAST, "早餐");
        MEAL_CONST_TO_LABEL.put(Constants.MEAL_LUNCH, "午餐");
        MEAL_CONST_TO_LABEL.put(Constants.MEAL_DINNER, "晚餐");
    }

    // slotContents: dayOfWeek (1=Monday..7=Sunday) -> mealTime -> recipeId
    private Map<Integer, Map<String, Long>> slotContents = new HashMap<>();
    // recipeId -> recipeName cache
    private Map<Long, String> recipeNameCache = new HashMap<>();
    // All recipe id -> name mapping
    private List<Recipe> allRecipes = new ArrayList<>();

    // Shopping list adapter
    private ShoppingListAdapter shoppingAdapter;
    private List<ShoppingItem> shoppingItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        db = FrigoBrainApp.getDatabase();
        userId = FrigoBrainApp.getCurrentUserId();

        weekGrid = findViewById(R.id.week_grid);
        btnGenerateList = findViewById(R.id.btn_generate_shopping_list);
        rvShoppingList = findViewById(R.id.rv_shopping_list);

        // Setup shopping list RecyclerView
        shoppingAdapter = new ShoppingListAdapter(shoppingItems);
        rvShoppingList.setLayoutManager(new LinearLayoutManager(this));
        rvShoppingList.setAdapter(shoppingAdapter);

        // Initialize slot contents
        for (int d = 1; d <= 7; d++) {
            Map<String, Long> daySlots = new HashMap<>();
            daySlots.put("早餐", null);
            daySlots.put("午餐", null);
            daySlots.put("晚餐", null);
            slotContents.put(d, daySlots);
        }

        // Build the week grid programmatically
        buildWeekGrid();

        // Load recipes and meal plan from DB
        loadData();

        // Generate shopping list button
        btnGenerateList.setOnClickListener(v -> generateShoppingList());
    }

    /**
     * Build the 4x8 grid: header row + 3 meal rows, each with label + 7 day cells
     */
    private void buildWeekGrid() {
        // Clear existing rows
        weekGrid.removeAllViews();

        // Header row: empty cell + day names
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryLight));

        // Top-left empty cell
        headerRow.addView(createHeaderCell(""));

        // Day name cells
        for (String dayName : DAY_NAMES) {
            headerRow.addView(createHeaderCell(dayName));
        }
        weekGrid.addView(headerRow);

        // Meal rows (Breakfast, Lunch, Dinner)
        for (final String mealLabel : MEAL_TIMES) {
            TableRow row = new TableRow(this);

            // Meal type label
            TextView labelCell = new TextView(this);
            labelCell.setText(mealLabel);
            labelCell.setPadding(4, 12, 4, 12);
            labelCell.setGravity(Gravity.CENTER);
            labelCell.setTextSize(12f);
            labelCell.setTypeface(null, android.graphics.Typeface.BOLD);
            labelCell.setTextColor(ContextCompat.getColor(this, R.color.primaryDark));
            TableRow.LayoutParams labelLp = new TableRow.LayoutParams(
                    0, TableRow.LayoutParams.WRAP_CONTENT, 0.7f);
            labelCell.setLayoutParams(labelLp);
            row.addView(labelCell);

            // Day cells (Monday=1 .. Sunday=7)
            for (int dayIdx = 0; dayIdx < 7; dayIdx++) {
                final int dayOfWeek = dayIdx + 1; // 1=Monday
                final String meal = mealLabel;

                TextView cell = new TextView(this);
                cell.setTag("slot_" + dayOfWeek + "_" + meal);
                cell.setText("+");
                cell.setPadding(4, 16, 4, 16);
                cell.setGravity(Gravity.CENTER);
                cell.setTextSize(12f);
                cell.setTextColor(ContextCompat.getColor(this, R.color.primary));
                cell.setBackgroundResource(R.drawable.edit_bg);

                TableRow.LayoutParams cellLp = new TableRow.LayoutParams(
                        0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                cellLp.setMargins(2, 2, 2, 2);
                cell.setLayoutParams(cellLp);

                cell.setOnClickListener(v -> showSlotOptions(dayOfWeek, meal, cell));
                row.addView(cell);
            }
            weekGrid.addView(row);
        }
    }

    /**
     * Create a header cell TextView
     */
    private TextView createHeaderCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(4, 10, 4, 10);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(13f);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setTextColor(ContextCompat.getColor(this, R.color.primary));
        TableRow.LayoutParams lp = new TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * Load all recipes and meal plan data from the database
     */
    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Load all recipes
            allRecipes = db.recipeDao().getAllSync();
            recipeNameCache.clear();
            for (Recipe r : allRecipes) {
                recipeNameCache.put(r.getRecipeId(), r.getName());
            }

            // Find or create meal plan for this week
            long weekStart = DateUtils.weekStart();
            long weekEnd = weekStart + 7 * 24 * 60 * 60 * 1000L;
            currentPlan = db.mealPlanDao().getByWeek(userId, weekStart);
            if (currentPlan == null) {
                currentPlan = new MealPlan(userId, weekStart, weekEnd);
                long newPlanId = db.mealPlanDao().insert(currentPlan);
                currentPlan.setPlanId(newPlanId);
            }
            planId = currentPlan.getPlanId();

            // Load existing plan recipes
            final long pid = planId;
            List<MealPlanRecipe> existingRecipes = db.mealPlanDao().getPlanRecipes(pid).getValue();
            // If LiveData not active yet, query directly via a raw approach
            // Since getPlanRecipes returns LiveData, we observe it
            db.mealPlanDao().getPlanRecipes(pid).observe(MealPlanActivity.this, mprList -> {
                if (mprList == null) return;

                // Reset all slots
                for (int d = 1; d <= 7; d++) {
                    slotContents.get(d).put("早餐", null);
                    slotContents.get(d).put("午餐", null);
                    slotContents.get(d).put("晚餐", null);
                }

                // Fill in actual assigned recipes
                for (MealPlanRecipe mpr : mprList) {
                    int dow = mpr.getDayOfWeek();
                    String mealLabel = MEAL_CONST_TO_LABEL.get(mpr.getMealTime());
                    if (mealLabel != null && slotContents.containsKey(dow)) {
                        slotContents.get(dow).put(mealLabel, mpr.getRecipeId());
                    }
                }

                // Update grid cell displays
                runOnUiThread(() -> updateGridDisplay());
            });
        });
    }

    /**
     * Update all grid cell text to reflect current slot contents
     */
    private void updateGridDisplay() {
        for (int d = 1; d <= 7; d++) {
            for (String meal : MEAL_TIMES) {
                String tag = "slot_" + d + "_" + meal;
                View cell = weekGrid.findViewWithTag(tag);
                if (cell instanceof TextView) {
                    TextView tv = (TextView) cell;
                    Long recipeId = slotContents.get(d).get(meal);
                    if (recipeId != null && recipeNameCache.containsKey(recipeId)) {
                        tv.setText(recipeNameCache.get(recipeId));
                        tv.setTextColor(ContextCompat.getColor(this, R.color.onBackground));
                        tv.setTextSize(11f);
                    } else {
                        tv.setText("+");
                        tv.setTextColor(ContextCompat.getColor(this, R.color.primary));
                        tv.setTextSize(12f);
                    }
                }
            }
        }
    }

    /**
     * Show options dialog when a meal slot is tapped
     */
    private void showSlotOptions(int dayOfWeek, String mealLabel, TextView cell) {
        Long currentRecipeId = slotContents.get(dayOfWeek).get(mealLabel);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(DAY_NAMES[dayOfWeek - 1] + " " + mealLabel);

        if (currentRecipeId != null) {
            String recipeName = recipeNameCache.get(currentRecipeId);
            builder.setMessage("当前: " + recipeName);
            builder.setPositiveButton("更换菜谱", (d, w) -> showRecipePicker(dayOfWeek, mealLabel));
            builder.setNeutralButton("移除", (d, w) -> removeRecipeFromSlot(dayOfWeek, mealLabel));
            builder.setNegativeButton("取消", null);
        } else {
            builder.setMessage("点击选择菜谱");
            builder.setPositiveButton("选择菜谱", (d, w) -> showRecipePicker(dayOfWeek, mealLabel));
            builder.setNegativeButton("取消", null);
        }
        builder.show();
    }

    /**
     * Show a recipe picker dialog listing all available recipes
     */
    private void showRecipePicker(int dayOfWeek, String mealLabel) {
        if (allRecipes.isEmpty()) {
            Toast.makeText(this, "暂无菜谱数据", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] recipeNames = new String[allRecipes.size()];
        long[] recipeIds = new long[allRecipes.size()];
        for (int i = 0; i < allRecipes.size(); i++) {
            recipeNames[i] = allRecipes.get(i).getName();
            recipeIds[i] = allRecipes.get(i).getRecipeId();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择" + mealLabel + " - " + DAY_NAMES[dayOfWeek - 1]);
        builder.setItems(recipeNames, (dialog, which) -> {
            long selectedRecipeId = recipeIds[which];
            slotContents.get(dayOfWeek).put(mealLabel, selectedRecipeId);
            String mealConst = MEAL_LABEL_TO_CONST.get(mealLabel);

            // Save to database
            Executors.newSingleThreadExecutor().execute(() -> {
                MealPlanRecipe mpr = new MealPlanRecipe(planId, selectedRecipeId, dayOfWeek, mealConst);
                db.mealPlanDao().insertRecipe(mpr);
            });

            // Update display immediately
            updateGridDisplay();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * Remove a recipe from a meal slot
     */
    private void removeRecipeFromSlot(int dayOfWeek, String mealLabel) {
        final Long recipeId = slotContents.get(dayOfWeek).get(mealLabel);
        if (recipeId == null) return;

        slotContents.get(dayOfWeek).put(mealLabel, null);
        String mealConst = MEAL_LABEL_TO_CONST.get(mealLabel);

        Executors.newSingleThreadExecutor().execute(() -> {
            // Find and delete the matching MealPlanRecipe entry
            List<MealPlanRecipe> plans = db.mealPlanDao().getPlanRecipes(planId).getValue();
            if (plans != null) {
                for (MealPlanRecipe mpr : plans) {
                    if (mpr.getDayOfWeek() == dayOfWeek
                            && mpr.getMealTime().equals(mealConst)
                            && mpr.getRecipeId() == recipeId) {
                        db.mealPlanDao().removeRecipe(mpr.getPlanRecipeId());
                        break;
                    }
                }
            }
        });

        updateGridDisplay();
    }

    /**
     * Generate shopping list from current meal plan
     */
    private void generateShoppingList() {
        Toast.makeText(this, "正在生成采购清单...", Toast.LENGTH_SHORT).show();

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Object[]> result = db.mealPlanDao().generateShoppingList(userId, planId);
            List<ShoppingItem> items = new ArrayList<>();
            if (result != null) {
                for (Object[] row : result) {
                    String name = (String) row[0];
                    double needed = row[1] instanceof Number ? ((Number) row[1]).doubleValue() : 0;
                    String unit = (String) row[2];
                    double inFridge = row[3] instanceof Number ? ((Number) row[3]).doubleValue() : 0;
                    double toBuy = row[4] instanceof Number ? ((Number) row[4]).doubleValue() : 0;
                    items.add(new ShoppingItem(name, needed, unit, inFridge, toBuy));
                }
            }

            runOnUiThread(() -> {
                shoppingItems.clear();
                shoppingItems.addAll(items);
                shoppingAdapter.notifyDataSetChanged();

                if (items.isEmpty()) {
                    Toast.makeText(MealPlanActivity.this,
                            "所有食材冰箱里都有，无需采购！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MealPlanActivity.this,
                            "需要采购 " + items.size() + " 种食材", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // ========== Inner Classes ==========

    /**
     * Shopping list item data class
     */
    private static class ShoppingItem {
        String name;
        double needed;
        String unit;
        double inFridge;
        double toBuy;

        ShoppingItem(String name, double needed, String unit, double inFridge, double toBuy) {
            this.name = name;
            this.needed = needed;
            this.unit = unit;
            this.inFridge = inFridge;
            this.toBuy = toBuy;
        }
    }

    /**
     * RecyclerView adapter for shopping list display
     */
    private static class ShoppingListAdapter
            extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

        private List<ShoppingItem> items;

        ShoppingListAdapter(List<ShoppingItem> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(12, 14, 12, 14);
            tv.setTextSize(14f);
            tv.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ShoppingItem item = items.get(position);
            String text = item.name + "  x " + item.needed + item.unit
                    + "   (需购: " + item.toBuy + item.unit + ")";
            ((TextView) holder.itemView).setText(text);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
