package com.frigobrain.ui.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.data.db.entity.NutritionLog;
import com.frigobrain.data.db.entity.ShoppingList;
import com.frigobrain.data.model.NutritionSummary;
import com.frigobrain.util.Constants;
import com.frigobrain.util.DateUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class NutritionActivity extends AppCompatActivity {

    private TextView tvCalories, tvProtein, tvFat, tvCarbs;
    private AppDatabase db;
    private long userId;
    private RecyclerView rvShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        db = AppDatabase.getInstance(this);
        userId = FrigoBrainApp.getCurrentUserId();

        tvCalories = findViewById(R.id.tv_calories);
        tvProtein = findViewById(R.id.tv_protein);
        tvFat = findViewById(R.id.tv_fat);
        tvCarbs = findViewById(R.id.tv_carbs);
        rvShopping = findViewById(R.id.rv_shopping);

        loadWeekSummary();
        setupRadarChart();
        setupTrendChart();
        loadShoppingList();
    }

    private void loadWeekSummary() {
        long weekStart = DateUtils.weekStart();
        long weekEnd = DateUtils.daysFromNow(0);

        Executors.newSingleThreadExecutor().execute(() -> {
            var summary = db.nutritionLogDao().getWeekSummary(userId, weekStart, weekEnd);
            db.nutritionLogDao().getWeekSummary(userId, weekStart, weekEnd).observe(this, s -> {
                if (s != null) {
                    tvCalories.setText(s.totalCalories + " kcal");
                    tvProtein.setText((int)s.totalProtein + " g");
                    tvFat.setText((int)s.totalFat + " g");
                    tvCarbs.setText((int)s.totalCarbs + " g");
                }
            });
        });
    }

    private void setupRadarChart() {
        RadarChart chart = new RadarChart(this);
        FrameLayout container = findViewById(R.id.chart_radar_container);
        container.addView(chart, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        List<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(2000f)); // Calories / 10 for scale
        entries.add(new RadarEntry(60f));   // Protein
        entries.add(new RadarEntry(60f));   // Fat
        entries.add(new RadarEntry(300f));  // Carbs / 5 for scale

        RadarDataSet dataSet = new RadarDataSet(entries, "推荐摄入");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setFillColor(Color.parseColor("#4CAF50"));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(80);

        RadarData data = new RadarData(dataSet);
        chart.setData(data);
        chart.getDescription().setEnabled(false);

        String[] labels = {"热量", "蛋白质", "脂肪", "碳水"};
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.invalidate();
    }

    private void setupTrendChart() {
        LineChart chart = new LineChart(this);
        FrameLayout container = findViewById(R.id.chart_trend_container);
        container.addView(chart, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        List<Entry> entries = new ArrayList<>();
        // Placeholder - real data from DB
        entries.add(new Entry(0, 1800));
        entries.add(new Entry(1, 2100));
        entries.add(new Entry(2, 1950));
        entries.add(new Entry(3, 2200));
        entries.add(new Entry(4, 1900));
        entries.add(new Entry(5, 2000));
        entries.add(new Entry(6, 2150));

        LineDataSet dataSet = new LineDataSet(entries, "热量 (kcal)");
        dataSet.setColor(Color.parseColor("#FF5722"));
        dataSet.setCircleColor(Color.parseColor("#FF5722"));
        dataSet.setLineWidth(2f);

        LineData data = new LineData(dataSet);
        chart.setData(data);
        chart.getDescription().setEnabled(false);

        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(days));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.invalidate();
    }

    private void loadShoppingList() {
        db.shoppingListDao().getByUser(userId).observe(this, list -> {
            // Simple shopping list display
            StringBuilder sb = new StringBuilder();
            if (list != null) {
                for (ShoppingList item : list) {
                    sb.append(item.getChecked() == 1 ? "☑ " : "☐ ")
                            .append(item.getIngredientName())
                            .append("  ").append(item.getQuantity()).append(item.getUnit())
                            .append("\n");
                }
            }
        });
    }
}
