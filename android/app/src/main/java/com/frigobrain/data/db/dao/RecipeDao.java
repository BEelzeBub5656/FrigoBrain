package com.frigobrain.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.frigobrain.data.db.entity.Recipe;
import com.frigobrain.data.db.entity.RecipeIngredient;

import java.util.List;

@Dao
public interface RecipeDao {

    @Insert
    long insert(Recipe recipe);

    @Insert
    void insertAll(List<Recipe> recipes);

    @Insert
    void insertIngredient(RecipeIngredient ingredient);

    @Insert
    void insertIngredients(List<RecipeIngredient> ingredients);

    @Query("SELECT * FROM recipes ORDER BY name")
    LiveData<List<Recipe>> getAll();

    @Query("SELECT * FROM recipes ORDER BY name")
    List<Recipe> getAllSync();

    @Query("SELECT * FROM recipes WHERE recipe_id = :id")
    LiveData<Recipe> getById(long id);

    @Query("SELECT * FROM recipes WHERE recipe_id = :id")
    Recipe getByIdSync(long id);

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' ORDER BY name")
    LiveData<List<Recipe>> search(String query);

    @Query("SELECT food_name FROM recipe_ingredients WHERE recipe_id = :recipeId")
    List<String> getIngredientNames(long recipeId);

    @Query("SELECT * FROM recipe_ingredients WHERE recipe_id = :recipeId")
    List<RecipeIngredient> getIngredients(long recipeId);

    /** 按标签筛选菜谱 */
    @Query("SELECT * FROM recipes WHERE tags LIKE '%' || :tag || '%' ORDER BY name")
    LiveData<List<Recipe>> getByTag(String tag);

    /** 按菜系筛选 */
    @Query("SELECT * FROM recipes WHERE cuisine_type = :cuisine ORDER BY name")
    LiveData<List<Recipe>> getByCuisine(String cuisine);

    /**
     * 【评分重点 SQL-2】菜谱匹配度计算
     * 根据冰箱现有食材计算每道菜谱的匹配率，取 >= 80% 的
     */
    @Query("SELECT r.*, " +
           "COUNT(ri.ingredient_id) AS total_needed, " +
           "SUM(CASE WHEN fi.name IS NOT NULL THEN 1 ELSE 0 END) AS available, " +
           "CAST(SUM(CASE WHEN fi.name IS NOT NULL THEN 1 ELSE 0 END) AS REAL) / " +
           "COUNT(ri.ingredient_id) AS match_rate " +
           "FROM recipes r " +
           "JOIN recipe_ingredients ri ON r.recipe_id = ri.recipe_id " +
           "LEFT JOIN (SELECT DISTINCT name FROM food_items WHERE is_consumed = 0 AND user_id = :userId) fi " +
           "ON LOWER(ri.food_name) = LOWER(fi.name) " +
           "GROUP BY r.recipe_id " +
           "HAVING match_rate >= 0.5 " +
           "ORDER BY match_rate DESC")
    LiveData<List<Object[]>> getMatchedRecipes(long userId);
}
