package com.floatingpanda.scoreboard.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BgCategoryDao {
    @Query("SELECT * FROM bg_categories")
    LiveData<List<BgCategory>> getAll();

    @Query("SELECT * FROM bg_categories WHERE category_name LIKE :categoryName")
    LiveData<BgCategory> findByName(String categoryName);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertAll(BgCategory... bgCategories);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(BgCategory bgCategory);

    @Query ("DELETE FROM bg_categories")
    void deleteAll();

    @Delete
    void delete(BgCategory BGCategory);
}