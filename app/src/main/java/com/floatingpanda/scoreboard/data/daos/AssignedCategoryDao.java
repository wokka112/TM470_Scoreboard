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
