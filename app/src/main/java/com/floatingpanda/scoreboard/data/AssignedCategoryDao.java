package com.floatingpanda.scoreboard.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AssignedCategoryDao {
    @Query("SELECT * FROM assigned_categories")
    LiveData<List<AssignedCategory>> getAll();

    @Query("SELECT * FROM assigned_categories WHERE bg_id LIKE :bgId")
    List<AssignedCategory> findByBgId(int bgId);

    @Query("SELECT * FROM assigned_categories WHERE category_id like :categoryId")
    List<AssignedCategory> findByCategoryId(int categoryId);

    /*
    @Query("SELECT * FROM assigned_categories WHERE bg_name LIKE :bgName")
    LiveData<BgCategory> findByName(String bgName);

     */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(AssignedCategory... assignedCategories);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(AssignedCategory assignedCategory);

    @Query ("DELETE FROM assigned_categories")
    void deleteAll();

    @Delete
    void delete(AssignedCategory assignedCategory);
}
