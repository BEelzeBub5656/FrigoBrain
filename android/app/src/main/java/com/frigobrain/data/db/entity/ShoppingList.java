package com.frigobrain.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "shopping_list",
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "user_id", childColumns = "user_id"),
        @ForeignKey(entity = FoodCategory.class, parentColumns = "category_id", childColumns = "category_id")
    },
    indices = {@Index("user_id")}
)
public class ShoppingList {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "user_id")
    private long userId;

    @NonNull
    @ColumnInfo(name = "ingredient_name")
    private String ingredientName;

    @ColumnInfo(name = "category_id")
    private long categoryId;

    @ColumnInfo(name = "quantity")
    private double quantity;

    @NonNull
    @ColumnInfo(name = "unit", defaultValue = "个")
    private String unit;

    @ColumnInfo(name = "checked", defaultValue = "0")
    private int checked;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public ShoppingList(long userId, @NonNull String ingredientName, long categoryId,
                        double quantity, @NonNull String unit) {
        this.userId = userId;
        this.ingredientName = ingredientName;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.unit = unit;
        this.checked = 0;
        this.createdAt = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public long getUserId() { return userId; }
    @NonNull public String getIngredientName() { return ingredientName; }
    public long getCategoryId() { return categoryId; }
    public double getQuantity() { return quantity; }
    @NonNull public String getUnit() { return unit; }
    public int getChecked() { return checked; }
    public long getCreatedAt() { return createdAt; }

    public void setId(long id) { this.id = id; }
    public void setUserId(long userId) { this.userId = userId; }
    public void setIngredientName(@NonNull String ingredientName) { this.ingredientName = ingredientName; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public void setUnit(@NonNull String unit) { this.unit = unit; }
    public void setChecked(int checked) { this.checked = checked; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
