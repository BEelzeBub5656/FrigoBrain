package com.frigobrain.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.frigobrain.data.db.entity.MealPlan;
import com.frigobrain.data.db.entity.MealPlanRecipe;

import java.util.List;

@Dao
public interface MealPlanDao {

    @Insert
    long insert(MealPlan plan);

    @Insert
    void insertRecipe(MealPlanRecipe recipe);

    @Query("SELECT * FROM meal_plans WHERE user_id = :userId ORDER BY week_start DESC")
    LiveData<List<MealPlan>> getByUser(long userId);

    @Query("SELECT * FROM meal_plans WHERE user_id = :userId AND week_start = :weekStart LIMIT 1")
    MealPlan getByWeek(long userId, long weekStart);

    @Query("SELECT * FROM meal_plan_recipes WHERE plan_id = :planId ORDER BY day_of_week, sort_order")
    LiveData<List<MealPlanRecipe>> getPlanRecipes(long planId);

    @Query("DELETE FROM meal_plan_recipes WHERE plan_recipe_id = :id")
    int removeRecipe(long id);

    /**
     * 从膳食计划自动生成采购清单
     * 多表 JOIN + 子查询 + HAVING
     */
    @Query("SELECT ri.food_name, SUM(ri.quantity) AS total_needed, ri.unit, " +
           "CASE WHEN fi.available_qty IS NULL THEN 0 ELSE fi.available_qty END AS in_fridge, " +
           "SUM(ri.quantity) - CASE WHEN fi.available_qty IS NULL THEN 0 ELSE fi.available_qty END AS to_buy " +
           "FROM meal_plan_recipes mpr " +
           "JOIN recipe_ingredients ri ON mpr.recipe_id = ri.recipe_id " +
           "LEFT JOIN (SELECT name, SUM(quantity) AS available_qty FROM food_items " +
           "WHERE is_consumed = 0 AND user_id = :userId GROUP BY name) fi " +
           "ON LOWER(ri.food_name) = LOWER(fi.name) " +
           "WHERE mpr.plan_id = :planId " +
           "GROUP BY ri.food_name " +
           "HAVING to_buy > 0 " +
           "ORDER BY to_buy DESC")
    List<Object[]> generateShoppingList(long userId, long planId);
}
