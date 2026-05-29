package com.frigobrain.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frigobrain.FrigoBrainApp;
import com.frigobrain.R;
import com.frigobrain.data.db.AppDatabase;
import com.frigobrain.data.db.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * 个人中心 - 用户信息、家庭组成员管理、个人统计
 */
public class UserProfileActivity extends AppCompatActivity {

    private AppDatabase db;
    private long currentUserId;

    private TextView tvUsername, tvRole;
    private EditText etDisplayName, etFamilyGroup;
    private Button btnSaveProfile, btnJoinFamily;
    private RecyclerView rvUsers;

    private List<User> allUsers = new ArrayList<>();
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        db = FrigoBrainApp.getDatabase();
        currentUserId = FrigoBrainApp.getCurrentUserId();

        tvUsername = findViewById(R.id.tv_username);
        tvRole = findViewById(R.id.tv_role);
        etDisplayName = findViewById(R.id.et_display_name);
        etFamilyGroup = findViewById(R.id.et_family_group);
        btnSaveProfile = findViewById(R.id.btn_save_profile);
        btnJoinFamily = findViewById(R.id.btn_join_family);
        rvUsers = findViewById(R.id.rv_users);

        // Setup user list RecyclerView
        userAdapter = new UserAdapter(allUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);

        // Load current user info
        loadCurrentUser();

        // Load all users (multi-user list)
        loadAllUsers();

        // Save profile button
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        // Join/create family group button
        btnJoinFamily.setOnClickListener(v -> joinFamilyGroup());
    }

    /**
     * Load current user info from DB and populate fields
     */
    private void loadCurrentUser() {
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = db.userDao().getByIdSync(currentUserId);
            runOnUiThread(() -> {
                if (user != null) {
                    tvUsername.setText(user.getUsername());
                    tvRole.setText(user.getRole());
                    etDisplayName.setText(user.getDisplayName());
                    if (user.getFamilyGroupId() != null) {
                        etFamilyGroup.setText(user.getFamilyGroupId());
                    }
                }
            });
        });
    }

    /**
     * Load all users into the multi-user list
     */
    private void loadAllUsers() {
        db.userDao().getAll().observe(this, users -> {
            allUsers.clear();
            if (users != null) {
                allUsers.addAll(users);
            }
            userAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Save profile changes (display name) to the database
     */
    private void saveProfile() {
        String newDisplayName = etDisplayName.getText().toString().trim();
        if (newDisplayName.isEmpty()) {
            Toast.makeText(this, "显示名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            User user = db.userDao().getByIdSync(currentUserId);
            if (user != null) {
                user.setDisplayName(newDisplayName);
                user.setUpdatedAt(System.currentTimeMillis());
                db.userDao().update(user);
                runOnUiThread(() ->
                        Toast.makeText(UserProfileActivity.this,
                                "个人信息已更新", Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * Join or create a family group
     */
    private void joinFamilyGroup() {
        String groupId = etFamilyGroup.getText().toString().trim();
        if (groupId.isEmpty()) {
            Toast.makeText(this, "请输入家庭组ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            db.userDao().setFamilyGroup(currentUserId, groupId, System.currentTimeMillis());
            runOnUiThread(() ->
                    Toast.makeText(UserProfileActivity.this,
                            "已加入家庭组: " + groupId, Toast.LENGTH_SHORT).show());
        });
    }

    // ========== Inner Class: UserAdapter ==========

    /**
     * RecyclerView adapter for multi-user list
     */
    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

        private final List<User> users;

        UserAdapter(List<User> users) {
            this.users = users;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(16, 14, 16, 14);
            tv.setTextSize(15f);
            tv.setCompoundDrawablePadding(8);
            tv.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            User user = users.get(position);
            String displayName = user.getDisplayName() != null ? user.getDisplayName() : user.getUsername();
            String role = user.getRole();
            String groupInfo = user.getFamilyGroupId() != null
                    ? "  [组: " + user.getFamilyGroupId() + "]"
                    : "";
            String text = displayName + " (" + role + ")" + groupInfo;

            ((TextView) holder.itemView).setText(text);

            // Highlight current user
            if (user.getUserId() == currentUserId) {
                holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(UserProfileActivity.this, R.color.primaryLight));
            } else {
                holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(UserProfileActivity.this, R.color.surface));
            }

            // Click to switch to this user
            holder.itemView.setOnClickListener(v -> {
                if (user.getUserId() != currentUserId) {
                    FrigoBrainApp.setCurrentUserId(user.getUserId());
                    currentUserId = user.getUserId();
                    Toast.makeText(UserProfileActivity.this,
                            "已切换到用户: " + displayName, Toast.LENGTH_SHORT).show();
                    loadCurrentUser();
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
