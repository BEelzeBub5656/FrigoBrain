package com.frigobrain.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "nutrition_logs",
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "user_id", childColumns = "user_id"),
        @ForeignKey(entity = Recipe.class, parentColumns = "recipe_id", childColumns = "recipe_id")
    },
    indices = {@Index("user_id"), @Index("log_date")}
)
public class NutritionLog {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "log_id")
    private long logId;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "recipe_id")
    private Long recipeId;

    @NonNull
    @ColumnInfo(name = "meal_time")
    private String mealTime;

    @ColumnInfo(name = "log_date")
    private long logDate;

    @ColumnInfo(name = "calories")
    private int calories;

    @ColumnInfo(name = "protein")
    private double protein;

    @ColumnInfo(name = "fat")
    private double fat;

    @ColumnInfo(name = "carbs")
    private double carbs;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public NutritionLog(long userId, @NonNull String mealTime, long logDate,
                        int calories, double protein, double fat, double carbs) {
        this.userId = userId;
        this.mealTime = mealTime;
        this.logDate = logDate;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.createdAt = System.currentTimeMillis();
    }

    public long getLogId() { return logId; }
    public long getUserId() { return userId; }
    public Long getRecipeId() { return recipeId; }
    @NonNull public String getMealTime() { return mealTime; }
    public long getLogDate() { return logDate; }
    public int getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getFat() { return fat; }
    public double getCarbs() { return carbs; }
    public long getCreatedAt() { return createdAt; }

    public void setLogId(long logId) { this.logId = logId; }
    public void setUserId(long userId) { this.userId = userId; }
    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }
    public void setMealTime(@NonNull String mealTime) { this.mealTime = mealTime; }
    public void setLogDate(long logDate) { this.logDate = logDate; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setProtein(double protein) { this.protein = protein; }
    public void setFat(double fat) { this.fat = fat; }
    public void setCarbs(double carbs) { this.carbs = carbs; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
