package com.frigobrain.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "food_waste_records",
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "user_id", childColumns = "user_id"),
        @ForeignKey(entity = FoodCategory.class, parentColumns = "category_id", childColumns = "category_id")
    },
    indices = {@Index("user_id"), @Index("category_id"), @Index("waste_date")}
)
public class FoodWasteRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "waste_id")
    private long wasteId;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "food_id")
    private Long foodId;

    @NonNull
    @ColumnInfo(name = "food_name")
    private String foodName;

    @ColumnInfo(name = "category_id")
    private long categoryId;

    @ColumnInfo(name = "quantity")
    private double quantity;

    @NonNull
    @ColumnInfo(name = "unit")
    private String unit;

    @ColumnInfo(name = "estimated_cost")
    private double estimatedCost;

    @NonNull
    @ColumnInfo(name = "waste_reason", defaultValue = "EXPIRED")
    private String wasteReason;

    @ColumnInfo(name = "waste_date")
    private long wasteDate;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public FoodWasteRecord(long userId, @NonNull String foodName, long categoryId,
                           double quantity, @NonNull String unit, long wasteDate) {
        this.userId = userId;
        this.foodName = foodName;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.unit = unit;
        this.wasteDate = wasteDate;
        this.wasteReason = "EXPIRED";
        this.createdAt = System.currentTimeMillis();
    }

    public long getWasteId() { return wasteId; }
    public long getUserId() { return userId; }
    public Long getFoodId() { return foodId; }
    @NonNull public String getFoodName() { return foodName; }
    public long getCategoryId() { return categoryId; }
    public double getQuantity() { return quantity; }
    @NonNull public String getUnit() { return unit; }
    public double getEstimatedCost() { return estimatedCost; }
    @NonNull public String getWasteReason() { return wasteReason; }
    public long getWasteDate() { return wasteDate; }
    public long getCreatedAt() { return createdAt; }

    public void setWasteId(long wasteId) { this.wasteId = wasteId; }
    public void setUserId(long userId) { this.userId = userId; }
    public void setFoodId(Long foodId) { this.foodId = foodId; }
    public void setFoodName(@NonNull String foodName) { this.foodName = foodName; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public void setUnit(@NonNull String unit) { this.unit = unit; }
    public void setEstimatedCost(double estimatedCost) { this.estimatedCost = estimatedCost; }
    public void setWasteReason(@NonNull String wasteReason) { this.wasteReason = wasteReason; }
    public void setWasteDate(long wasteDate) { this.wasteDate = wasteDate; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
