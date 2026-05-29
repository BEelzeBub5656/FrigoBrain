package com.frigobrain.web.dto;

import java.math.BigDecimal;
import java.util.Map;

public class WasteTrendDTO {

    private String month;
    private BigDecimal totalCost;
    private long totalItems;
    private Map<String, BigDecimal> wasteByCategory;

    public WasteTrendDTO() {}

    public WasteTrendDTO(String month, BigDecimal totalCost, long totalItems,
                         Map<String, BigDecimal> wasteByCategory) {
        this.month = month;
        this.totalCost = totalCost;
        this.totalItems = totalItems;
        this.wasteByCategory = wasteByCategory;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public Map<String, BigDecimal> getWasteByCategory() {
        return wasteByCategory;
    }

    public void setWasteByCategory(Map<String, BigDecimal> wasteByCategory) {
        this.wasteByCategory = wasteByCategory;
    }
}
