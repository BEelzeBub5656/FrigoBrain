package com.frigobrain.web.controller;

import com.frigobrain.web.entity.FoodItem;
import com.frigobrain.web.service.FoodService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/foods")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public ResponseEntity<Page<FoodItem>> listFoods(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) Boolean isExpiring,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("expiryDate").ascending());
        Page<FoodItem> foods = foodService.listFoods(query, categoryId, isExpiring, pageable);
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItem> getFood(@PathVariable Long id) {
        return foodService.getFoodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FoodItem> createFood(@Valid @RequestBody FoodItem foodItem) {
        FoodItem created = foodService.createFood(foodItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItem> updateFood(@PathVariable Long id,
                                               @Valid @RequestBody FoodItem foodItem) {
        return foodService.updateFood(id, foodItem)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        if (foodService.deleteFood(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<FoodItem>> getExpiringFoods(
            @RequestParam(defaultValue = "7") int days) {
        List<FoodItem> expiring = foodService.getExpiringFoods(days);
        return ResponseEntity.ok(expiring);
    }

    @GetMapping("/categories/summary")
    public ResponseEntity<Map<String, Long>> getCategorySummary() {
        Map<String, Long> summary = foodService.getCategorySummary();
        return ResponseEntity.ok(summary);
    }
}
