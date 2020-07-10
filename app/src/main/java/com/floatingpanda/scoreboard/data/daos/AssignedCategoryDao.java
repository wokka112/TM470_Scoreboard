package com.floatingpanda.scoreboard.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.floatingpanda.scoreboard.data.entities.AssignedCategory;

import java.util.List;

@Dao
public interface AssignedCategoryDao {
    @Query("SELECT * FROM assigned_categories")
    LiveData<List<AssignedCategory>> getAll();

    //Used for testing purposes.
    @Query("SELECT * FROM assigned_categories WHERE bg_id LIKE :bgId")
    List<AssignedCategory> findNonLiveDataByBgId(int bgId);

    //Used for testing purposes
    @Query("SELECT * FROM assigned_categories WHERE category_id LIKE :categoryId")
    List<AssignedCategory> findNonLiveDataByCategoryId(int categoryId);

    @Query("SELECT category_id FROM assigned_categories WHERE bg_id LIKE :bgId")
    List<Integer> getAllCategoryIdsByBoardGameId(int bgId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(AssignedCategory... assignedCategories);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(AssignedCategory assignedCategory);

    @Query ("DELETE FROM assigned_categories")
    void deleteAll();

    @Delete
    void delete(AssignedCategory assignedCategory);
}
