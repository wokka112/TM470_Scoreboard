package com.floatingpanda.scoreboard.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BgCategoryDao {
    /**
     * @return live data list of bg categories from database
     */
    @Query("SELECT * FROM bg_categories")
    LiveData<List<BgCategory>> getAllLive();

    /**
     * @return normal list of bg categories from database
     */
    @Query("SELECT * FROM bg_categories")
    List<BgCategory> getAllNonLive();

    /**
     * @param categoryName the name of a category
     * @return live data bg category from database
     */
    @Query("SELECT * FROM bg_categories WHERE category_name LIKE :categoryName")
    LiveData<BgCategory> findLiveDataByName(String categoryName);

    /**
     * @param categoryName the name of a category
     * @return normal bg category (not live data) from database
     */
    @Query("SELECT * FROM bg_categories WHERE category_name LIKE :categoryName")
    BgCategory findNonLiveDataByName(String categoryName);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertAll(BgCategory... bgCategories);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(BgCategory bgCategory);

    @Query ("DELETE FROM bg_categories")
    void deleteAll();

    @Update
    void update(BgCategory BgCategory);

    @Delete
    void delete(BgCategory BgCategory);
}
