package com.frigobrain.data.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.frigobrain.data.db.entity.FoodCategory;
import com.frigobrain.data.db.entity.FoodItem;

public class FoodWithCategory {
    @Embedded
    public FoodItem food;

    @Relation(parentColumn = "category_id", entityColumn = "category_id")
    @NonNull
    public FoodCategory category;

    /** Days until expiry (negative = already expired) */
    public int getDaysUntilExpiry() {
        long now = System.currentTimeMillis();
        long diff = food.getExpiryDate() - now;
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public String getStatusColor() {
        int days = getDaysUntilExpiry();
        if (days < 0) return "RED";
        if (days <= 3) return "YELLOW";
        return "GREEN";
    }
}
