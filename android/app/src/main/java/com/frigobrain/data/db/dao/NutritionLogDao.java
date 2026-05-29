package com.frigobrain.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.frigobrain.data.db.entity.NutritionLog;
import com.frigobrain.data.model.NutritionSummary;

import com.frigobrain.data.model.DailyCalorie;
import java.util.List;

@Dao
public interface NutritionLogDao {

    @Insert
    long insert(NutritionLog log);

    @Query("SELECT * FROM nutrition_logs WHERE user_id = :userId ORDER BY log_date DESC")
    LiveData<List<NutritionLog>> getByUser(long userId);

    @Query("SELECT * FROM nutrition_logs WHERE user_id = :userId " +
           "AND log_date BETWEEN :start AND :end ORDER BY log_date ASC")
    LiveData<List<NutritionLog>> getByDateRange(long userId, long start, long end);

    /**
     * 本周营养摄入统计 — 按天聚合
     */
    @Query("SELECT COALESCE(SUM(calories), 0) AS totalCalories, " +
           "COALESCE(SUM(protein), 0) AS totalProtein, " +
           "COALESCE(SUM(fat), 0) AS totalFat, " +
           "COALESCE(SUM(carbs), 0) AS totalCarbs " +
           "FROM nutrition_logs " +
           "WHERE user_id = :userId AND log_date BETWEEN :weekStart AND :weekEnd")
    LiveData<NutritionSummary> getWeekSummary(long userId, long weekStart, long weekEnd);

    /** 本周每日热量趋势 */
    @Query("SELECT log_date, SUM(calories) AS dailyCal FROM nutrition_logs " +
           "WHERE user_id = :userId AND log_date BETWEEN :start AND :end " +
           "GROUP BY log_date ORDER BY log_date ASC")
    LiveData<List<DailyCalorie>> getDailyCalorieTrend(long userId, long start, long end);
}
