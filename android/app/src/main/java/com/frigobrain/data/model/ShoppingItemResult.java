package com.frigobrain.data.model;

import androidx.room.ColumnInfo;

/** 膳食计划生成采购清单查询结果 */
public class ShoppingItemResult {
    @ColumnInfo(name = "food_name")
    public String foodName;

    @ColumnInfo(name = "total_needed")
    public double totalNeeded;

    @ColumnInfo(name = "unit")
    public String unit;

    @ColumnInfo(name = "in_fridge")
    public double inFridge;

    @ColumnInfo(name = "to_buy")
    public double toBuy;
}
