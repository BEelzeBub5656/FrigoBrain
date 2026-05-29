package com.frigobrain.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "recipe_ingredients",
    foreignKeys = {
        @ForeignKey(entity = Recipe.class, parentColumns = "recipe_id", childColumns = "recipe_id")
    },
    indices = {@Index("recipe_id")}
)
public class RecipeIngredient {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ingredient_id")
    private long ingredientId;

    @ColumnInfo(name = "recipe_id")
    private long recipeId;

    @NonNull
    @ColumnInfo(name = "food_name")
    private String foodName;

    @ColumnInfo(name = "quantity", defaultValue = "1.0")
    private double quantity;

    @NonNull
    @ColumnInfo(name = "unit", defaultValue = "个")
    private String unit;

    public RecipeIngredient(long recipeId, @NonNull String foodName, double quantity, @NonNull String unit) {
        this.recipeId = recipeId;
        this.foodName = foodName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public long getIngredientId() { return ingredientId; }
    public long getRecipeId() { return recipeId; }
    @NonNull public String getFoodName() { return foodName; }
    public double getQuantity() { return quantity; }
    @NonNull public String getUnit() { return unit; }

    public void setIngredientId(long ingredientId) { this.ingredientId = ingredientId; }
    public void setRecipeId(long recipeId) { this.recipeId = recipeId; }
    public void setFoodName(@NonNull String foodName) { this.foodName = foodName; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public void setUnit(@NonNull String unit) { this.unit = unit; }
}
