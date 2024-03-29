/*
ScoreBoard

Copyright © 2020 Adam Poole

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.floatingpanda.scoreboard.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.floatingpanda.scoreboard.data.entities.BgCategory;

import java.util.List;

@Dao
public interface BgCategoryDao {
    /**
     * @return live data list of bg categories from database
     */
    @Query("SELECT * FROM bg_categories ORDER BY category_name")
    LiveData<List<BgCategory>> getAllLive();

    /**
     * @return normal list of bg categories from database
     */
    @Query("SELECT * FROM bg_categories ORDER BY category_name")
    List<BgCategory> getAllNonLive();

    @Query("SELECT * FROM bg_categories WHERE category_id LIKE :categoryId")
    LiveData<BgCategory> findLiveDataById(int categoryId);

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

    @Query("SELECT category_name FROM bg_categories WHERE category_id LIKE :categoryId")
    String getNonLiveCategoryNameByCategoryId(int categoryId);

    @Query("SELECT category_id FROM bg_categories WHERE category_name LIKE :categoryName")
    int getCategoryIdByCategoryName(String categoryName);

    @Query("SELECT EXISTS(SELECT * FROM bg_categories WHERE category_name LIKE :categoryName)")
    boolean containsBgCategory(String categoryName);

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
