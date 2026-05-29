package com.frigobrain.ui.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.data.db.entity.FoodWasteRecord;
import com.frigobrain.data.model.MonthlyWasteSummary;
import com.frigobrain.util.Constants;
import com.frigobrain.util.DateUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 食物浪费统计页面 - 展示月度浪费金额、分类排行、趋势图
 */
public class WasteStatsActivity extends AppCompatActivity {

    private AppDatabase db;
    private long userId;

    private TextView tvTotalWasteCost;
    private BarChart barChart;
    private PieChart pieChart;

    private final DecimalFormat costFormat = new DecimalFormat("¥ #,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waste_stats);

        db = FrigoBrainApp.getDatabase();
        userId = FrigoBrainApp.getCurrentUserId();

        tvTotalWasteCost = findViewById(R.id.tv_total_waste_cost);
        barChart = findViewById(R.id.bar_chart);
        pieChart = findViewById(R.id.pie_chart);

        // Calculate month boundaries
        long monthStart = DateUtils.monthStart();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(monthStart);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long monthEnd = cal.getTimeInMillis();

        // Setup charts
        setupBarChart();
        setupPieChart();

        // Load data
        loadTotalWasteCost(monthStart, monthEnd);
        loadMonthlyRanking(monthStart, monthEnd);
        loadWasteReasonDistribution(monthStart, monthEnd);
    }

    /**
     * Setup BarChart for monthly waste by category
     */
    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.setDrawGridBackground(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.animateY(800);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(11f);

        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisLeft().setGridColor(Color.LTGRAY);
        barChart.getAxisRight().setEnabled(false);
    }

    /**
     * Setup PieChart for waste reason distribution
     */
    private void setupPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.DKGRAY);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.animateY(800);
        pieChart.setCenterText("浪费原因");
        pieChart.setCenterTextSize(14f);
    }

    /**
     * Load and display total waste cost for the month
     */
    private void loadTotalWasteCost(long monthStart, long monthEnd) {
        LiveData<Double> costLive = db.foodWasteRecordDao()
                .getTotalWasteCost(userId, monthStart, monthEnd);
        costLive.observe(this, cost -> {
            if (cost != null) {
                tvTotalWasteCost.setText(costFormat.format(cost));
            } else {
                tvTotalWasteCost.setText("¥ 0.00");
            }
        });
    }

    /**
     * Load monthly waste ranking data and populate the BarChart
     */
    private void loadMonthlyRanking(long monthStart, long monthEnd) {
        LiveData<List<MonthlyWasteSummary>> rankingLive = db.foodWasteRecordDao()
                .getMonthlyWasteRanking(userId, monthStart, monthEnd);
        rankingLive.observe(this, summaries -> {
            if (summaries == null || summaries.isEmpty()) {
                barChart.setNoDataText("暂无浪费数据");
                barChart.invalidate();
                return;
            }

            List<BarEntry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();
            for (int i = 0; i < summaries.size(); i++) {
                MonthlyWasteSummary s = summaries.get(i);
                entries.add(new BarEntry(i, (float) s.totalCost));
                labels.add(s.categoryName);
            }

            BarDataSet dataSet = new BarDataSet(entries, "浪费金额 (元)");
            dataSet.setColors(ContextCompat.getColor(this, R.color.statusExpired),
                    ContextCompat.getColor(this, R.color.accent),
                    ContextCompat.getColor(this, R.color.statusExpiringSoon),
                    ContextCompat.getColor(this, R.color.primary),
                    ContextCompat.getColor(this, R.color.protein));

            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.6f);
            barData.setValueTextSize(11f);

            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            barChart.setData(barData);
            barChart.invalidate();
        });
    }

    /**
     * Load waste records for the month, aggregate by reason, and populate PieChart
     */
    private void loadWasteReasonDistribution(long monthStart, long monthEnd) {
        LiveData<List<FoodWasteRecord>> recordsLive = db.foodWasteRecordDao()
                .getByMonth(userId, monthStart, monthEnd);
        recordsLive.observe(this, records -> {
            if (records == null || records.isEmpty()) {
                pieChart.setNoDataText("暂无浪费数据");
                pieChart.invalidate();
                return;
            }

            int expiredCount = 0;
            int spoiledCount = 0;
            int otherCount = 0;

            for (FoodWasteRecord r : records) {
                switch (r.getWasteReason()) {
                    case Constants.WASTE_EXPIRED:
                        expiredCount++;
                        break;
                    case Constants.WASTE_SPOILED:
                        spoiledCount++;
                        break;
                    default:
                        otherCount++;
                        break;
                }
            }

            List<PieEntry> entries = new ArrayList<>();
            if (expiredCount > 0) {
                entries.add(new PieEntry(expiredCount, "过期"));
            }
            if (spoiledCount > 0) {
                entries.add(new PieEntry(spoiledCount, "变质"));
            }
            if (otherCount > 0) {
                entries.add(new PieEntry(otherCount, "其他"));
            }

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(
                    ContextCompat.getColor(this, R.color.statusExpired),
                    ContextCompat.getColor(this, R.color.accent),
                    ContextCompat.getColor(this, R.color.statusExpiringSoon)
            );
            dataSet.setValueTextSize(13f);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            PieData pieData = new PieData(dataSet);
            pieData.setValueFormatter(new PercentFormatter(pieChart));

            pieChart.setData(pieData);
            pieChart.invalidate();
        });
    }
}
