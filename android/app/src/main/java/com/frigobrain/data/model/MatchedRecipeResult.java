package com.frigobrain.data.model;

import androidx.room.ColumnInfo;

/** 菜谱匹配度查询结果 */
public class MatchedRecipeResult {
    @ColumnInfo(name = "recipe_id")
    public long recipeId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "cuisine_type")
    public String cuisineType;

    @ColumnInfo(name = "difficulty")
    public String difficulty;

    @ColumnInfo(name = "cook_time")
    public int cookTime;

    @ColumnInfo(name = "calories")
    public int calories;

    @ColumnInfo(name = "protein")
    public double protein;

    @ColumnInfo(name = "fat")
    public double fat;

    @ColumnInfo(name = "carbs")
    public double carbs;

    @ColumnInfo(name = "instructions")
    public String instructions;

    @ColumnInfo(name = "tags")
    public String tags;

    @ColumnInfo(name = "total_needed")
    public int totalNeeded;

    @ColumnInfo(name = "available")
    public int available;

    @ColumnInfo(name = "match_rate")
    public double matchRate;
}
