package com.frigobrain.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "recipes",
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "user_id", childColumns = "created_by")
    },
    indices = {@Index("created_by"), @Index("name")}
)
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recipe_id")
    private long recipeId;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "cuisine_type")
    private String cuisineType;

    @ColumnInfo(name = "meal_type")
    private String mealType;

    @ColumnInfo(name = "difficulty")
    private String difficulty;

    @ColumnInfo(name = "prep_time")
    private int prepTime;

    @ColumnInfo(name = "cook_time")
    private int cookTime;

    @ColumnInfo(name = "servings", defaultValue = "2")
    private int servings;

    @ColumnInfo(name = "calories")
    private int calories;

    @ColumnInfo(name = "protein")
    private double protein;

    @ColumnInfo(name = "fat")
    private double fat;

    @ColumnInfo(name = "carbs")
    private double carbs;

    @NonNull
    @ColumnInfo(name = "instructions")
    private String instructions;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "tags")
    private String tags;

    @ColumnInfo(name = "is_system", defaultValue = "0")
    private int isSystem;

    @ColumnInfo(name = "created_by")
    private long createdBy;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public Recipe(@NonNull String name, @NonNull String instructions,
                  int calories, double protein, double fat, double carbs) {
        this.name = name;
        this.instructions = instructions;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.isSystem = 0;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters
    public long getRecipeId() { return recipeId; }
    @NonNull public String getName() { return name; }
    public String getCuisineType() { return cuisineType; }
    public String getMealType() { return mealType; }
    public String getDifficulty() { return difficulty; }
    public int getPrepTime() { return prepTime; }
    public int getCookTime() { return cookTime; }
    public int getServings() { return servings; }
    public int getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getFat() { return fat; }
    public double getCarbs() { return carbs; }
    @NonNull public String getInstructions() { return instructions; }
    public String getImageUrl() { return imageUrl; }
    public String getTags() { return tags; }
    public int getIsSystem() { return isSystem; }
    public long getCreatedBy() { return createdBy; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    // Setters
    public void setRecipeId(long recipeId) { this.recipeId = recipeId; }
    public void setName(@NonNull String name) { this.name = name; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setPrepTime(int prepTime) { this.prepTime = prepTime; }
    public void setCookTime(int cookTime) { this.cookTime = cookTime; }
    public void setServings(int servings) { this.servings = servings; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setProtein(double protein) { this.protein = protein; }
    public void setFat(double fat) { this.fat = fat; }
    public void setCarbs(double carbs) { this.carbs = carbs; }
    public void setInstructions(@NonNull String instructions) { this.instructions = instructions; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setTags(String tags) { this.tags = tags; }
    public void setIsSystem(int isSystem) { this.isSystem = isSystem; }
    public void setCreatedBy(long createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
