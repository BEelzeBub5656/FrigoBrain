package com.frigobrain.ui.mealplan;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;

/**
 * 周膳食计划 - 7列网格拖拽规划，自动生成采购清单
 */
public class MealPlanActivity extends AppCompatActivity {

    private AppDatabase db;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);

        db = AppDatabase.getInstance(this);
        userId = FrigoBrainApp.getCurrentUserId();

        TextView tv = findViewById(R.id.tv_placeholder);
        tv.setText("周膳食计划\n\n7天网格布局\n拖拽菜谱到每日\n早/午/晚餐规划\n一键生成采购清单\n\n" +
                "数据库表：meal_plans + meal_plan_recipes\n" +
                "自动对比冰箱库存生成采购清单");

        // 功能完整定义：
        // 1. 展示7列网格（周一到周日）
        // 2. 每列有早/午/晚餐三个格子
        // 3. 从菜谱库拖拽或选择添加
        // 4. 调用 MealPlanDao.generateShoppingList() 生成采购清单
    }
}
