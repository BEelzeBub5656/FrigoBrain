package com.frigobrain.data.model;

import androidx.annotation.NonNull;

public class MonthlyWasteSummary {
    @NonNull
    public String categoryName;

    public int wasteCount;

    public double totalCost;

    public double avgCost;

    @NonNull
    public String month;

    public MonthlyWasteSummary(@NonNull String categoryName, int wasteCount,
                               double totalCost, double avgCost, @NonNull String month) {
        this.categoryName = categoryName;
        this.wasteCount = wasteCount;
        this.totalCost = totalCost;
        this.avgCost = avgCost;
        this.month = month;
    }
}
