package com.frigobrain.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frigobrain.MainActivity;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.data.db.entity.User;

import java.security.MessageDigest;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private AppDatabase db;
    private boolean isRegistering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = AppDatabase.getInstance(this);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        btnLogin.setOnClickListener(v -> handleLogin());
        tvRegister.setOnClickListener(v -> toggleMode());
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        String hash = sha256(password);
        Executors.newSingleThreadExecutor().execute(() -> {
            if (isRegistering) {
                // Register
                User existing = db.userDao().login(username, hash);
                if (existing != null) {
                    runOnUiThread(() -> Toast.makeText(this, "用户已存在", Toast.LENGTH_SHORT).show());
                    return;
                }
                User user = new User(username, hash, username, "OWNER");
                long userId = db.userDao().insert(user);
                com.frigobrain.FrigoBrainApp.setCurrentUserId(userId);
            } else {
                // Login
                User user = db.userDao().login(username, hash);
                if (user == null) {
                    runOnUiThread(() -> Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show());
                    return;
                }
                com.frigobrain.FrigoBrainApp.setCurrentUserId(user.getUserId());
            }
            runOnUiThread(() -> {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            });
        });
    }

    private void toggleMode() {
        isRegistering = !isRegistering;
        btnLogin.setText(isRegistering ? "注册" : "登录");
        tvRegister.setText(isRegistering ? "已有账号？点此登录" : "首次使用？点此注册");
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return input;
        }
    }
}
