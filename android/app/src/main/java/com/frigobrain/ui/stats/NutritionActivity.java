package com.frigobrain.ui.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.util.Constants;
import com.frigobrain.util.DateUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class NutritionActivity extends AppCompatActivity {

    private TextView tvCalories, tvProtein, tvFat, tvCarbs;
    private AppDatabase db;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_nutrition);

            db = AppDatabase.getInstance(this);
            userId = FrigoBrainApp.getCurrentUserId();

            tvCalories = findViewById(R.id.tv_calories);
            tvProtein = findViewById(R.id.tv_protein);
            tvFat = findViewById(R.id.tv_fat);
            tvCarbs = findViewById(R.id.tv_carbs);

            loadWeekSummary();
            setupRadarChart();
            setupTrendChart();
        } catch (Exception e) {
            Toast.makeText(this, "加载失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadWeekSummary() {
        long weekStart = DateUtils.weekStart();
        long weekEnd = DateUtils.daysFromNow(0);
        db.nutritionLogDao().getWeekSummary(userId, weekStart, weekEnd)
                .observe(this, s -> {
                    if (s != null) {
                        tvCalories.setText(s.totalCalories + " kcal");
                        tvProtein.setText((int)s.totalProtein + " g");
                        tvFat.setText((int)s.totalFat + " g");
                        tvCarbs.setText((int)s.totalCarbs + " g");
                    }
                });
    }

    private void setupRadarChart() {
        FrameLayout container = findViewById(R.id.chart_radar_container);
        if (container == null) return;
        RadarChart chart = new RadarChart(this);
        container.addView(chart, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, 900));

        List<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(2000f));
        entries.add(new RadarEntry(60f));
        entries.add(new RadarEntry(60f));
        entries.add(new RadarEntry(300f));

        RadarDataSet dataSet = new RadarDataSet(entries, "推荐摄入");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setFillColor(Color.parseColor("#4CAF50"));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(80);

        RadarData data = new RadarData(dataSet);
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(
                new String[]{"热量", "蛋白质", "脂肪", "碳水"}));
        chart.animateXY(500, 500);
    }

    private void setupTrendChart() {
        FrameLayout container = findViewById(R.id.chart_trend_container);
        if (container == null) return;
        LineChart chart = new LineChart(this);
        container.addView(chart, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, 600));

        List<Entry> entries = new ArrayList<>();
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
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData data = new LineData(dataSet);
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(
                new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"}));
        chart.animateX(800);
    }
}
