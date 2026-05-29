package com.frigobrain.web.repository;

import com.frigobrain.web.entity.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<FoodItem, Long> {

    Page<FoodItem> findByIsConsumedFalse(Pageable pageable);

    @Query("SELECT f FROM FoodItem f WHERE f.isConsumed = false " +
           "AND (:query IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:categoryId IS NULL OR f.categoryId = :categoryId)")
    Page<FoodItem> searchFoods(@Param("query") String query,
                               @Param("categoryId") String categoryId,
                               Pageable pageable);

    @Query("SELECT f FROM FoodItem f WHERE f.isConsumed = false " +
           "AND f.expiryDate BETWEEN CURRENT_DATE AND :threshold")
    Page<FoodItem> findExpiringFoods(@Param("threshold") LocalDate threshold, Pageable pageable);

    @Query("SELECT f FROM FoodItem f WHERE f.isConsumed = false " +
           "AND f.expiryDate BETWEEN CURRENT_DATE AND :threshold")
    List<FoodItem> findExpiringFoodsList(@Param("threshold") LocalDate threshold);

    @Query("SELECT f.categoryId, COUNT(f) FROM FoodItem f WHERE f.isConsumed = false GROUP BY f.categoryId")
    List<Object[]> countByCategory();

    long countByIsConsumedFalse();

    long countByIsConsumedTrue();

    @Query("SELECT COALESCE(SUM(f.price * f.quantity), 0) FROM FoodItem f WHERE f.isConsumed = false")
    double totalValueOfNonConsumed();
}
