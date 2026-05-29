package com.frigobrain.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    private long userId;

    @NonNull
    @ColumnInfo(name = "username")
    private String username;

    @NonNull
    @ColumnInfo(name = "password_hash")
    private String passwordHash;

    @NonNull
    @ColumnInfo(name = "display_name")
    private String displayName;

    @ColumnInfo(name = "avatar")
    private String avatar;

    @NonNull
    @ColumnInfo(name = "role", defaultValue = "MEMBER")
    private String role;

    @ColumnInfo(name = "family_group_id")
    private String familyGroupId;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public User(@NonNull String username, @NonNull String passwordHash,
                @NonNull String displayName, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.role = role != null ? role : "MEMBER";
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters
    public long getUserId() { return userId; }
    @NonNull public String getUsername() { return username; }
    @NonNull public String getPasswordHash() { return passwordHash; }
    @NonNull public String getDisplayName() { return displayName; }
    public String getAvatar() { return avatar; }
    @NonNull public String getRole() { return role; }
    public String getFamilyGroupId() { return familyGroupId; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    // Setters
    public void setUserId(long userId) { this.userId = userId; }
    public void setUsername(@NonNull String username) { this.username = username; }
    public void setPasswordHash(@NonNull String passwordHash) { this.passwordHash = passwordHash; }
    public void setDisplayName(@NonNull String displayName) { this.displayName = displayName; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setRole(@NonNull String role) { this.role = role; }
    public void setFamilyGroupId(String familyGroupId) { this.familyGroupId = familyGroupId; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
