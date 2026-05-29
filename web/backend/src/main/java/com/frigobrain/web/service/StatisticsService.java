package com.frigobrain.web.service;

import com.frigobrain.web.dto.NutritionStatsDTO;
import com.frigobrain.web.dto.WasteTrendDTO;
import com.frigobrain.web.entity.NutritionLog;
import com.frigobrain.web.entity.WasteRecord;
import com.frigobrain.web.repository.NutritionLogRepository;
import com.frigobrain.web.repository.WasteRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    private final WasteRecordRepository wasteRecordRepository;
    private final NutritionLogRepository nutritionLogRepository;

    public StatisticsService(WasteRecordRepository wasteRecordRepository,
                             NutritionLogRepository nutritionLogRepository) {
        this.wasteRecordRepository = wasteRecordRepository;
        this.nutritionLogRepository = nutritionLogRepository;
    }

    public List<WasteTrendDTO> getMonthlyWasteByCategory() {
        LocalDate start = LocalDate.now().minusMonths(3).withDayOfMonth(1);
        LocalDate end = LocalDate.now();

        List<WasteRecord> records = wasteRecordRepository
                .findByWasteDateBetweenOrderByWasteDateAsc(start, end);

        Map<String, List<WasteRecord>> byMonth = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getWasteDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                ));

        List<WasteTrendDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<WasteRecord>> entry : byMonth.entrySet()) {
            String month = entry.getKey();
            List<WasteRecord> monthRecords = entry.getValue();

            BigDecimal totalCost = monthRecords.stream()
                    .map(WasteRecord::getEstimatedCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long totalItems = monthRecords.size();

            Map<String, BigDecimal> wasteByCategory = monthRecords.stream()
                    .collect(Collectors.groupingBy(
                            WasteRecord::getCategoryName,
                            Collectors.mapping(
                                    WasteRecord::getEstimatedCost,
                                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                            )
                    ));

            result.add(new WasteTrendDTO(month, totalCost, totalItems, wasteByCategory));
        }

        result.sort(Comparator.comparing(WasteTrendDTO::getMonth));
        return result;
    }

    public NutritionStatsDTO getDailyNutrition(LocalDate date) {
        List<NutritionLog> logs = nutritionLogRepository.findByLogDateOrderByMealTime(date);

        if (logs.isEmpty()) {
            return new NutritionStatsDTO(date.toString(), 0, 0.0, 0.0, 0.0, 0);
        }

        int totalCalories = logs.stream().mapToInt(NutritionLog::getCalories).sum();
        double totalProtein = logs.stream().mapToDouble(NutritionLog::getProtein).sum();
        double totalFat = logs.stream().mapToDouble(NutritionLog::getFat).sum();
        double totalCarbs = logs.stream().mapToDouble(NutritionLog::getCarbs).sum();

        return new NutritionStatsDTO(date.toString(), totalCalories,
                totalProtein, totalFat, totalCarbs, logs.size());
    }

    public List<NutritionStatsDTO> getNutritionTrend() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);

        List<Object[]> summaries = nutritionLogRepository.findDailySummaryBetween(start, end);

        Set<LocalDate> dateSet = new HashSet<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            dateSet.add(d);
        }

        Map<String, NutritionStatsDTO> summaryMap = new HashMap<>();
        for (Object[] row : summaries) {
            LocalDate date = (LocalDate) row[0];
            String dateStr = date.toString();
            int calories = ((Number) row[1]).intValue();
            double protein = ((Number) row[2]).doubleValue();
            double fat = ((Number) row[3]).doubleValue();
            double carbs = ((Number) row[4]).doubleValue();
            int count = ((Number) row[5]).intValue();
            summaryMap.put(dateStr, new NutritionStatsDTO(dateStr, calories, protein, fat, carbs, count));
        }

        List<NutritionStatsDTO> result = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            String dateStr = d.toString();
            result.add(summaryMap.getOrDefault(dateStr,
                    new NutritionStatsDTO(dateStr, 0, 0.0, 0.0, 0.0, 0)));
        }

        return result;
    }
}
