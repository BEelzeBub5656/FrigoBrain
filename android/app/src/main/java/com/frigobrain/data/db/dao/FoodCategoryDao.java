package com.frigobrain.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.frigobrain.data.db.entity.FoodCategory;

import java.util.List;

@Dao
public interface FoodCategoryDao {

    @Insert
    long insert(FoodCategory category);

    @Query("SELECT * FROM food_categories ORDER BY sort_order")
    LiveData<List<FoodCategory>> getAll();

    @Query("SELECT * FROM food_categories ORDER BY sort_order")
    List<FoodCategory> getAllSync();

    @Query("SELECT * FROM food_categories WHERE category_id = :id")
    FoodCategory getByIdSync(long id);

    @Query("SELECT name FROM food_categories WHERE category_id = :id")
    String getNameById(long id);
}
