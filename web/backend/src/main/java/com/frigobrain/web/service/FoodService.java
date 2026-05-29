package com.frigobrain.web.service;

import com.frigobrain.web.dto.InventorySummaryDTO;
import com.frigobrain.web.entity.FoodItem;
import com.frigobrain.web.repository.FoodRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class FoodService {

    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    @Transactional(readOnly = true)
    public Page<FoodItem> listFoods(String query, String categoryId, Boolean isExpiring, Pageable pageable) {
        if (Boolean.TRUE.equals(isExpiring)) {
            LocalDate threshold = LocalDate.now().plusDays(7);
            return foodRepository.findExpiringFoods(threshold, pageable);
        }
        return foodRepository.searchFoods(query, categoryId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<FoodItem> getFoodById(Long id) {
        return foodRepository.findById(id);
    }

    public FoodItem createFood(FoodItem foodItem) {
        foodItem.setIsConsumed(false);
        return foodRepository.save(foodItem);
    }

    public Optional<FoodItem> updateFood(Long id, FoodItem updated) {
        return foodRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setCategoryId(updated.getCategoryId());
            existing.setQuantity(updated.getQuantity());
            existing.setUnit(updated.getUnit());
            existing.setPurchaseDate(updated.getPurchaseDate());
            existing.setExpiryDate(updated.getExpiryDate());
            existing.setPrice(updated.getPrice());
            existing.setStorageLocation(updated.getStorageLocation());
            existing.setIsConsumed(updated.getIsConsumed());
            return foodRepository.save(existing);
        });
    }

    public boolean deleteFood(Long id) {
        if (foodRepository.existsById(id)) {
            foodRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<FoodItem> getExpiringFoods(int days) {
        LocalDate threshold = LocalDate.now().plusDays(days);
        return foodRepository.findExpiringFoodsList(threshold);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getCategorySummary() {
        List<Object[]> categoryCounts = foodRepository.countByCategory();
        Map<String, Long> summary = new HashMap<>();
        for (Object[] row : categoryCounts) {
            summary.put((String) row[0], (Long) row[1]);
        }
        return summary;
    }

    @Transactional(readOnly = true)
    public InventorySummaryDTO getInventorySummary() {
        long totalItems = foodRepository.countByIsConsumedFalse();
        long consumedCount = foodRepository.countByIsConsumedTrue();

        LocalDate threshold = LocalDate.now().plusDays(7);
        long expiringCount = foodRepository.findExpiringFoodsList(threshold).size();

        List<Object[]> categoryCounts = foodRepository.countByCategory();
        Map<String, Long> categoryBreakdown = new HashMap<>();
        for (Object[] row : categoryCounts) {
            categoryBreakdown.put((String) row[0], (Long) row[1]);
        }

        double totalValueRaw = foodRepository.totalValueOfNonConsumed();
        BigDecimal totalValue = BigDecimal.valueOf(totalValueRaw);

        return new InventorySummaryDTO(totalItems, expiringCount, consumedCount,
                categoryBreakdown, totalValue);
    }
}
