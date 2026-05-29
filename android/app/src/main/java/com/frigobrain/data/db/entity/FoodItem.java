package com.frigobrain.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "food_items",
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "user_id", childColumns = "user_id"),
        @ForeignKey(entity = FoodCategory.class, parentColumns = "category_id", childColumns = "category_id")
    },
    indices = {
        @Index("user_id"), @Index("category_id"), @Index("name")
    }
)
public class FoodItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "food_id")
    private long foodId;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "category_id")
    private long categoryId;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "barcode")
    private String barcode;

    @ColumnInfo(name = "quantity", defaultValue = "1.0")
    private double quantity;

    @NonNull
    @ColumnInfo(name = "unit", defaultValue = "个")
    private String unit;

    @ColumnInfo(name = "purchase_date")
    private long purchaseDate;

    @ColumnInfo(name = "expiry_date")
    private long expiryDate;

    @ColumnInfo(name = "price")
    private double price;

    @NonNull
    @ColumnInfo(name = "storage_location", defaultValue = "FRIDGE")
    private String storageLocation;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "is_consumed", defaultValue = "0")
    private int isConsumed;

    @ColumnInfo(name = "consumed_date")
    private long consumedDate;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public FoodItem(long userId, long categoryId, @NonNull String name,
                    double quantity, @NonNull String unit,
                    long purchaseDate, long expiryDate) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.storageLocation = "FRIDGE";
        this.isConsumed = 0;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters
    public long getFoodId() { return foodId; }
    public long getUserId() { return userId; }
    public long getCategoryId() { return categoryId; }
    @NonNull public String getName() { return name; }
    public String getBarcode() { return barcode; }
    public double getQuantity() { return quantity; }
    @NonNull public String getUnit() { return unit; }
    public long getPurchaseDate() { return purchaseDate; }
    public long getExpiryDate() { return expiryDate; }
    public double getPrice() { return price; }
    @NonNull public String getStorageLocation() { return storageLocation; }
    public String getNotes() { return notes; }
    public int getIsConsumed() { return isConsumed; }
    public long getConsumedDate() { return consumedDate; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    // Setters
    public void setFoodId(long foodId) { this.foodId = foodId; }
    public void setUserId(long userId) { this.userId = userId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public void setName(@NonNull String name) { this.name = name; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public void setUnit(@NonNull String unit) { this.unit = unit; }
    public void setPurchaseDate(long purchaseDate) { this.purchaseDate = purchaseDate; }
    public void setExpiryDate(long expiryDate) { this.expiryDate = expiryDate; }
    public void setPrice(double price) { this.price = price; }
    public void setStorageLocation(@NonNull String storageLocation) { this.storageLocation = storageLocation; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setIsConsumed(int isConsumed) { this.isConsumed = isConsumed; }
    public void setConsumedDate(long consumedDate) { this.consumedDate = consumedDate; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
