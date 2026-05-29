package com.frigobrain.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.frigobrain.data.db.entity.ShoppingList;

import java.util.List;

@Dao
public interface ShoppingListDao {

    @Insert
    long insert(ShoppingList item);

    @Update
    int update(ShoppingList item);

    @Query("SELECT * FROM shopping_list WHERE user_id = :userId ORDER BY checked ASC, created_at DESC")
    LiveData<List<ShoppingList>> getByUser(long userId);

    @Query("UPDATE shopping_list SET checked = :checked WHERE id = :id")
    int toggleChecked(long id, int checked);

    @Query("DELETE FROM shopping_list WHERE checked = 1")
    int clearChecked();

    @Query("DELETE FROM shopping_list WHERE id = :id")
    int deleteById(long id);
}
