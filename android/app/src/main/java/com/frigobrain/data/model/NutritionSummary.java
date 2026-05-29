package com.frigobrain.data.model;

public class NutritionSummary {
    public int totalCalories;
    public double totalProtein;
    public double totalFat;
    public double totalCarbs;

    public NutritionSummary(int totalCalories, double totalProtein, double totalFat, double totalCarbs) {
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalFat = totalFat;
        this.totalCarbs = totalCarbs;
    }
}
