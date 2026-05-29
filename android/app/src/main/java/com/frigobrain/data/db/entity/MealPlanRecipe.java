package com.frigobrain.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "meal_plan_recipes",
    foreignKeys = {
        @ForeignKey(entity = MealPlan.class, parentColumns = "plan_id", childColumns = "plan_id"),
        @ForeignKey(entity = Recipe.class, parentColumns = "recipe_id", childColumns = "recipe_id")
    },
    indices = {@Index("plan_id"), @Index("recipe_id")}
)
public class MealPlanRecipe {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "plan_recipe_id")
    private long planRecipeId;

    @ColumnInfo(name = "plan_id")
    private long planId;

    @ColumnInfo(name = "recipe_id")
    private long recipeId;

    @ColumnInfo(name = "day_of_week")
    private int dayOfWeek;

    @NonNull
    @ColumnInfo(name = "meal_time")
    private String mealTime;

    @ColumnInfo(name = "sort_order", defaultValue = "0")
    private int sortOrder;

    public MealPlanRecipe(long planId, long recipeId, int dayOfWeek, @NonNull String mealTime) {
        this.planId = planId;
        this.recipeId = recipeId;
        this.dayOfWeek = dayOfWeek;
        this.mealTime = mealTime;
        this.sortOrder = 0;
    }

    public long getPlanRecipeId() { return planRecipeId; }
    public long getPlanId() { return planId; }
    public long getRecipeId() { return recipeId; }
    public int getDayOfWeek() { return dayOfWeek; }
    @NonNull public String getMealTime() { return mealTime; }
    public int getSortOrder() { return sortOrder; }

    public void setPlanRecipeId(long planRecipeId) { this.planRecipeId = planRecipeId; }
    public void setPlanId(long planId) { this.planId = planId; }
    public void setRecipeId(long recipeId) { this.recipeId = recipeId; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setMealTime(@NonNull String mealTime) { this.mealTime = mealTime; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
