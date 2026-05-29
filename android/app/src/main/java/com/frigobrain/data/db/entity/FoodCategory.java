package com.frigobrain.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_categories")
public class FoodCategory {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    private long categoryId;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "icon")
    private String icon;

    @NonNull
    @ColumnInfo(name = "storage_type", defaultValue = "FRIDGE")
    private String storageType;

    @ColumnInfo(name = "sort_order", defaultValue = "0")
    private int sortOrder;

    public FoodCategory(@NonNull String name, String storageType, int sortOrder) {
        this.name = name;
        this.storageType = storageType != null ? storageType : "FRIDGE";
        this.sortOrder = sortOrder;
    }

    // Getters
    public long getCategoryId() { return categoryId; }
    @NonNull public String getName() { return name; }
    public String getIcon() { return icon; }
    @NonNull public String getStorageType() { return storageType; }
    public int getSortOrder() { return sortOrder; }

    // Setters
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public void setName(@NonNull String name) { this.name = name; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setStorageType(@NonNull String storageType) { this.storageType = storageType; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
