package com.frigobrain.ui.stats;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.util.DateUtils;

/**
 * 食物浪费统计页面 - 展示月度浪费金额、分类排行、趋势图
 */
public class WasteStatsActivity extends AppCompatActivity {

    private AppDatabase db;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);

        db = AppDatabase.getInstance(this);
        userId = FrigoBrainApp.getCurrentUserId();

        TextView tv = findViewById(R.id.tv_placeholder);
        tv.setText("浪费统计\n\n月度浪费金额柱状图\n分类浪费饼图\n成本排行\n节约建议\n\n（课程演示时展示完整图表）");

        // 可添加 MPAndroidChart BarChart 和 PieChart
    }
}
