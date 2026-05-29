package com.frigobrain.ui.settings;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.frigobrain.R;

/** 系统设置 - 华为云绑定、同步频率、通知开关 */
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);
        TextView tv = findViewById(R.id.tv_placeholder);
        tv.setText("系统设置\n\n" +
                "华为云IoT设备绑定\n" +
                "- 设备ID: 待配置\n" +
                "- 连接状态: 待连接\n" +
                "\n同步设置\n" +
                "- 自动同步频率: 30分钟\n" +
                "- 仅在WiFi下同步\n" +
                "\n通知设置\n" +
                "- 过期提醒: 开启\n" +
                "- 营养周报: 开启");
    }
}
