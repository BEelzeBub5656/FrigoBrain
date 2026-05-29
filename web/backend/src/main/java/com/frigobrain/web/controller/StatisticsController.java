package com.frigobrain.web.controller;

import com.frigobrain.web.dto.InventorySummaryDTO;
import com.frigobrain.web.dto.NutritionStatsDTO;
import com.frigobrain.web.dto.WasteTrendDTO;
import com.frigobrain.web.service.FoodService;
import com.frigobrain.web.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final FoodService foodService;

    public StatisticsController(StatisticsService statisticsService, FoodService foodService) {
        this.statisticsService = statisticsService;
        this.foodService = foodService;
    }

    @GetMapping("/waste/monthly")
    public ResponseEntity<List<WasteTrendDTO>> getMonthlyWaste() {
        List<WasteTrendDTO> wasteData = statisticsService.getMonthlyWasteByCategory();
        return ResponseEntity.ok(wasteData);
    }

    @GetMapping("/nutrition/daily")
    public ResponseEntity<NutritionStatsDTO> getDailyNutrition(
            @RequestParam(required = false) String date) {
        LocalDate queryDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        NutritionStatsDTO stats = statisticsService.getDailyNutrition(queryDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/nutrition/trend")
    public ResponseEntity<List<NutritionStatsDTO>> getNutritionTrend() {
        List<NutritionStatsDTO> trend = statisticsService.getNutritionTrend();
        return ResponseEntity.ok(trend);
    }

    @GetMapping("/inventory/summary")
    public ResponseEntity<InventorySummaryDTO> getInventorySummary() {
        InventorySummaryDTO summary = foodService.getInventorySummary();
        return ResponseEntity.ok(summary);
    }
}
