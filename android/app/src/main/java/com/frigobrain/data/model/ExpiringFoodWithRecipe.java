package com.frigobrain.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

/** 即将过期食材 + 推荐菜谱 查询结果 */
public class ExpiringFoodWithRecipe {
    @ColumnInfo(name = "food_id")
    public long foodId;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "expiry_date")
    public long expiryDate;

    @ColumnInfo(name = "quantity")
    public double quantity;

    @NonNull
    @ColumnInfo(name = "unit")
    public String unit;

    @ColumnInfo(name = "recipe_id")
    public Long recipeId;

    @ColumnInfo(name = "recipe_name")
    public String recipeName;
}
