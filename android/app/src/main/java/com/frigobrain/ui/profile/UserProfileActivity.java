package com.frigobrain.ui.profile;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;

import java.util.concurrent.Executors;

/** 个人中心 - 用户信息、家庭组成员管理、个人统计 */
public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);
        TextView tv = findViewById(R.id.tv_placeholder);

        long userId = FrigoBrainApp.getCurrentUserId();
        AppDatabase db = AppDatabase.getInstance(this);

        Executors.newSingleThreadExecutor().execute(() -> {
            var user = db.userDao().getByIdSync(userId);
            runOnUiThread(() -> {
                if (user != null) {
                    tv.setText("个人中心\n\n" +
                            "用户名: " + user.getUsername() + "\n" +
                            "显示名称: " + user.getDisplayName() + "\n" +
                            "角色: " + user.getRole() + "\n\n" +
                            "功能:\n" +
                            "- 编辑个人信息\n" +
                            "- 家庭组管理\n" +
                            "- 多用户切换\n" +
                            "- 膳食偏好设置");
                }
            });
        });
    }
}
