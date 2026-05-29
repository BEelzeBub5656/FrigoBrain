package com.frigobrain.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.frigobrain.data.db.entity.FoodItem;
import com.frigobrain.data.model.CategoryCount;
import com.frigobrain.data.model.FoodWithCategory;

import java.util.List;

@Dao
public interface FoodItemDao {

    // ========== 基础 CRUD ==========

    @Insert
    long insert(FoodItem item);

    @Update
    int update(FoodItem item);

    @Delete
    int delete(FoodItem item);

    // ========== 查询 ==========

    /** 获取所有活跃食材（未消耗），含分类信息 */
    @Query("SELECT * FROM food_items WHERE is_consumed = 0 AND user_id = :userId " +
           "ORDER BY expiry_date ASC")
    LiveData<List<FoodItem>> getActiveByUser(long userId);

    /** 按分类过滤食材 */
    @Query("SELECT * FROM food_items WHERE is_consumed = 0 AND user_id = :userId " +
           "AND category_id = :categoryId ORDER BY expiry_date ASC")
    LiveData<List<FoodItem>> getByCategory(long userId, long categoryId);

    /** 搜索食材名 */
    @Query("SELECT * FROM food_items WHERE is_consumed = 0 AND user_id = :userId " +
           "AND name LIKE '%' || :query || '%' ORDER BY expiry_date ASC")
    LiveData<List<FoodItem>> search(long userId, String query);

    /** 根据名称查食材 */
    @Query("SELECT * FROM food_items WHERE name = :name AND is_consumed = 0 " +
           "AND user_id = :userId LIMIT 1")
    FoodItem getByName(long userId, String name);

    // ========== 复杂 SQL ==========

    /**
     * 【评分重点 SQL-1】即将过期食材（3天内）+ 推荐菜谱
     * 使用 julianday 日期计算 + LEFT JOIN
     */
    @Query("SELECT fi.food_id, fi.name, fi.expiry_date, fi.quantity, fi.unit, " +
           "r.recipe_id, r.name AS recipe_name " +
           "FROM food_items fi " +
           "LEFT JOIN recipe_ingredients ri ON LOWER(fi.name) = LOWER(ri.food_name) " +
           "LEFT JOIN recipes r ON ri.recipe_id = r.recipe_id " +
           "WHERE fi.is_consumed = 0 " +
           "AND fi.expiry_date BETWEEN :today AND :threeDaysLater " +
           "AND fi.user_id = :userId " +
           "ORDER BY fi.expiry_date ASC")
    LiveData<List<Object[]>> getExpiringFoodsWithRecipes(long userId, long today, long threeDaysLater);

    /** 分类统计 */
    @Query("SELECT fc.name, COUNT(fi.food_id) AS count " +
           "FROM food_items fi " +
           "JOIN food_categories fc ON fi.category_id = fc.category_id " +
           "WHERE fi.is_consumed = 0 AND fi.user_id = :userId " +
           "GROUP BY fc.name ORDER BY count DESC")
    LiveData<List<CategoryCount>> getCategorySummary(long userId);

    /** 所有食材名列表（自动补全用） */
    @Query("SELECT DISTINCT name FROM food_items ORDER BY name ASC")
    List<String> getAllFoodNames();

    /** 标记为已消耗 */
    @Query("UPDATE food_items SET is_consumed = 1, consumed_date = :now, " +
           "updated_at = :now WHERE food_id = :foodId")
    int markConsumed(long foodId, long now);

    /** 统计过期浪费金额 */
    @Query("SELECT COALESCE(SUM(price * quantity), 0) FROM food_items " +
           "WHERE is_consumed = 1 AND consumed_date BETWEEN :start AND :end " +
           "AND user_id = :userId")
    LiveData<Double> getWasteCost(long userId, long start, long end);
}
