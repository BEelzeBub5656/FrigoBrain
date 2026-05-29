package com.frigobrain.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.frigobrain.data.db.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Update
    int update(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password_hash = :passwordHash LIMIT 1")
    User login(String username, String passwordHash);

    @Query("SELECT * FROM users WHERE user_id = :userId")
    LiveData<User> getById(long userId);

    @Query("SELECT * FROM users WHERE user_id = :userId")
    User getByIdSync(long userId);

    @Query("SELECT * FROM users ORDER BY display_name")
    LiveData<List<User>> getAll();

    @Query("SELECT * FROM users WHERE family_group_id = :groupId")
    LiveData<List<User>> getFamilyMembers(String groupId);

    @Query("UPDATE users SET family_group_id = :groupId, updated_at = :now WHERE user_id = :userId")
    int setFamilyGroup(long userId, String groupId, long now);
}
