package com.frigobrain.web.repository;

import com.frigobrain.web.entity.NutritionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NutritionLogRepository extends JpaRepository<NutritionLog, Long> {

    List<NutritionLog> findByLogDateOrderByMealTime(LocalDate logDate);

    List<NutritionLog> findByLogDateBetweenOrderByLogDateAsc(LocalDate start, LocalDate end);

    @Query("SELECT n.logDate, SUM(n.calories), SUM(n.protein), SUM(n.fat), SUM(n.carbs), COUNT(n) " +
           "FROM NutritionLog n " +
           "WHERE n.logDate = :date " +
           "GROUP BY n.logDate")
    Object[] findDailySummary(@Param("date") LocalDate date);

    @Query("SELECT n.logDate, SUM(n.calories), SUM(n.protein), SUM(n.fat), SUM(n.carbs), COUNT(n) " +
           "FROM NutritionLog n " +
           "WHERE n.logDate BETWEEN :start AND :end " +
           "GROUP BY n.logDate " +
           "ORDER BY n.logDate ASC")
    List<Object[]> findDailySummaryBetween(@Param("start") LocalDate start,
                                            @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(n.calories), 0) FROM NutritionLog n WHERE n.logDate = :date")
    int totalCaloriesByDate(@Param("date") LocalDate date);
}
