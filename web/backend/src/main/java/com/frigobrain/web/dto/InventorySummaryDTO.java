package com.frigobrain.web.dto;

import java.math.BigDecimal;
import java.util.Map;

public class InventorySummaryDTO {

    private long totalItems;
    private long expiringCount;
    private long consumedCount;
    private Map<String, Long> categoryBreakdown;
    private BigDecimal totalValue;

    public InventorySummaryDTO() {}

    public InventorySummaryDTO(long totalItems, long expiringCount, long consumedCount,
                               Map<String, Long> categoryBreakdown, BigDecimal totalValue) {
        this.totalItems = totalItems;
        this.expiringCount = expiringCount;
        this.consumedCount = consumedCount;
        this.categoryBreakdown = categoryBreakdown;
        this.totalValue = totalValue;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public long getExpiringCount() {
        return expiringCount;
    }

    public void setExpiringCount(long expiringCount) {
        this.expiringCount = expiringCount;
    }

    public long getConsumedCount() {
        return consumedCount;
    }

    public void setConsumedCount(long consumedCount) {
        this.consumedCount = consumedCount;
    }

    public Map<String, Long> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(Map<String, Long> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
}
