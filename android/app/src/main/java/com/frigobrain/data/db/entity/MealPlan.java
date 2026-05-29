package com.frigobrain.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "meal_plans",
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "user_id", childColumns = "user_id")
    },
    indices = {@Index("user_id")}
)
public class MealPlan {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "plan_id")
    private long planId;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "week_start")
    private long weekStart;

    @ColumnInfo(name = "week_end")
    private long weekEnd;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public MealPlan(long userId, long weekStart, long weekEnd) {
        this.userId = userId;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public long getPlanId() { return planId; }
    public long getUserId() { return userId; }
    public long getWeekStart() { return weekStart; }
    public long getWeekEnd() { return weekEnd; }
    public String getNotes() { return notes; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    public void setPlanId(long planId) { this.planId = planId; }
    public void setUserId(long userId) { this.userId = userId; }
    public void setWeekStart(long weekStart) { this.weekStart = weekStart; }
    public void setWeekEnd(long weekEnd) { this.weekEnd = weekEnd; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
