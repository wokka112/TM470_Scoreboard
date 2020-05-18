package com.floatingpanda.scoreboard.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AssignedCategoriesDao {
    @Query("SELECT * FROM assigned_categories")
    LiveData<List<AssignedCategories>> getAll();

    @Query("SELECT * FROM assigned_categories WHERE bg_id LIKE :bgId")
    List<AssignedCategories> findByBgId(int bgId);

    @Query("SELECT * FROM assigned_categories WHERE category_id like :categoryId")
    List<AssignedCategories> findByCategoryId(int categoryId);

    /*
    @Query("SELECT * FROM assigned_categories WHERE bg_name LIKE :bgName")
    LiveData<BgCategory> findByName(String bgName);

     */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(AssignedCategories... assignedCategories);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(AssignedCategories assignedCategories);

    @Query ("DELETE FROM assigned_categories")
    void deleteAll();

    @Delete
    void delete(AssignedCategories assignedCategories);
}
