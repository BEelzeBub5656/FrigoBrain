package com.frigobrain.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.frigobrain.data.db.entity.DietaryPreference;

@Dao
public interface DietaryPreferenceDao {

    @Query("SELECT * FROM dietary_preferences WHERE user_id = :userId LIMIT 1")
    DietaryPreference getByUser(long userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DietaryPreference pref);

    @Query("UPDATE dietary_preferences SET diet_type = :dietType, " +
           "excluded_ingredients = :excluded, max_calories_per_meal = :maxCal, " +
           "allergy_info = :allergy, updated_at = :now WHERE user_id = :userId")
    int update(long userId, String dietType, String excluded, int maxCal,
               String allergy, long now);

    @Query("SELECT COUNT(pref_id) FROM dietary_preferences WHERE user_id = :userId")
    int countByUser(long userId);
}
