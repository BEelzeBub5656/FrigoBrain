package com.frigobrain.ui.profile;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.frigobrain.R;

/** 膳食偏好设置 - 素食/低卡/高蛋白等模式选择 */
public class PreferenceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);
        TextView tv = findViewById(R.id.tv_placeholder);
        tv.setText("膳食偏好设置\n\n" +
                "饮食类型: 正常/素食/纯素/低卡/高蛋白\n" +
                "忌口食材: 自定义列表\n" +
                "偏好菜系: 中式/西式/日式\n" +
                "过敏信息: 填写\n\n" +
                "设置后菜谱推荐自动过滤");
    }
}
