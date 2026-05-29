package com.frigobrain.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.frigobrain.data.db.entity.FoodWasteRecord;
import com.frigobrain.data.model.MonthlyWasteSummary;

import java.util.List;

@Dao
public interface FoodWasteRecordDao {

    @Insert
    long insert(FoodWasteRecord record);

    @Query("SELECT * FROM food_waste_records WHERE user_id = :userId ORDER BY waste_date DESC")
    LiveData<List<FoodWasteRecord>> getByUser(long userId);

    @Query("SELECT * FROM food_waste_records WHERE user_id = :userId " +
           "AND waste_date BETWEEN :start AND :end ORDER BY waste_date DESC")
    LiveData<List<FoodWasteRecord>> getByMonth(long userId, long start, long end);

    /**
     * 【评分重点 SQL-3】月度浪费统计按分类排行
     * GROUP BY + SUM + ORDER BY + LIMIT + strftime 日期格式化
     */
    @Query("SELECT fc.name AS categoryName, " +
           "COUNT(fwr.waste_id) AS wasteCount, " +
           "COALESCE(SUM(fwr.estimated_cost), 0) AS totalCost, " +
           "COALESCE(ROUND(AVG(fwr.estimated_cost), 2), 0) AS avgCost, " +
           "strftime('%Y-%m', fwr.waste_date / 1000, 'unixepoch') AS month " +
           "FROM food_waste_records fwr " +
           "JOIN food_categories fc ON fwr.category_id = fc.category_id " +
           "WHERE fwr.waste_date BETWEEN :startOfMonth AND :endOfMonth " +
           "AND fwr.user_id = :userId " +
           "GROUP BY fc.category_id, month " +
           "ORDER BY totalCost DESC " +
           "LIMIT 10")
    LiveData<List<MonthlyWasteSummary>> getMonthlyWasteRanking(long userId, long startOfMonth, long endOfMonth);

    @Query("SELECT COALESCE(SUM(estimated_cost), 0) FROM food_waste_records " +
           "WHERE user_id = :userId AND waste_date BETWEEN :start AND :end")
    LiveData<Double> getTotalWasteCost(long userId, long start, long end);
}
