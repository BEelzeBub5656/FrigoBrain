package com.frigobrain.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "dietary_preferences",
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "user_id", childColumns = "user_id")
    },
    indices = {@Index(value = "user_id", unique = true)}
)
public class DietaryPreference {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pref_id")
    private long prefId;

    @ColumnInfo(name = "user_id")
    private long userId;

    @NonNull
    @ColumnInfo(name = "diet_type", defaultValue = "NORMAL")
    private String dietType;

    @ColumnInfo(name = "excluded_ingredients")
    private String excludedIngredients;

    @ColumnInfo(name = "preferred_cuisine")
    private String preferredCuisine;

    @ColumnInfo(name = "max_calories_per_meal")
    private int maxCaloriesPerMeal;

    @ColumnInfo(name = "allergy_info")
    private String allergyInfo;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public DietaryPreference(long userId, @NonNull String dietType) {
        this.userId = userId;
        this.dietType = dietType;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public long getPrefId() { return prefId; }
    public long getUserId() { return userId; }
    @NonNull public String getDietType() { return dietType; }
    public String getExcludedIngredients() { return excludedIngredients; }
    public String getPreferredCuisine() { return preferredCuisine; }
    public int getMaxCaloriesPerMeal() { return maxCaloriesPerMeal; }
    public String getAllergyInfo() { return allergyInfo; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    public void setPrefId(long prefId) { this.prefId = prefId; }
    public void setUserId(long userId) { this.userId = userId; }
    public void setDietType(@NonNull String dietType) { this.dietType = dietType; }
    public void setExcludedIngredients(String excludedIngredients) { this.excludedIngredients = excludedIngredients; }
    public void setPreferredCuisine(String preferredCuisine) { this.preferredCuisine = preferredCuisine; }
    public void setMaxCaloriesPerMeal(int maxCaloriesPerMeal) { this.maxCaloriesPerMeal = maxCaloriesPerMeal; }
    public void setAllergyInfo(String allergyInfo) { this.allergyInfo = allergyInfo; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
