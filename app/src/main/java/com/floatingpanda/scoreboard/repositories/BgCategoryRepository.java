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

package com.floatingpanda.scoreboard.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.daos.AssignedCategoryDao;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.entities.BgCategory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * A repository for accessing the bg_categories table in the database.
 */
public class BgCategoryRepository {

    private BgCategoryDao bgCategoryDao;
    private LiveData<List<BgCategory>> allBgCategories;

    //Used for testing
    private AssignedCategoryDao assignedCategoryDao;

    public BgCategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        bgCategoryDao = db.bgCategoryDao();
        allBgCategories = bgCategoryDao.getAllLive();

        assignedCategoryDao = db.assignedCategoryDao();
    }

    // Constructor used for testing purposes.
    public BgCategoryRepository(AppDatabase database) {
        bgCategoryDao = database.bgCategoryDao();
        allBgCategories = bgCategoryDao.getAllLive();

        //Used for testing
        assignedCategoryDao = database.assignedCategoryDao();
    }

    /**
     * Returns a livedata list of all the bg categories in the database.
     */
    public LiveData<List<BgCategory>> getAll() {
        return allBgCategories;
    }

    /**
     * Inserts a new Bgcategory into the database. If the BgCategory already exists in the database,
     * no new category is inserted.
     *
     * bgCategory should have an id of 0 so Room can autogenerate an id for it.
     *
     * bgCategory should have a unique category name, i.e. no BgCategory should already exist in the
     * database with the same name as bgCategory.
     * @param bgCategory a BgCategory with a unique category name and an id of 0
     */
    public void insert(BgCategory bgCategory) {
        AppDatabase.getExecutorService().execute(() -> {
            bgCategoryDao.insert(bgCategory);
        });
    }

    /**
     * Updates a BgCategory in the database to match the data of bgCategory. A BgCategory with
     * bgCategory's id should already exist in the database for this to work.
     *
     * Note that a BgCategory's id should not change, so update will not update BgCategory ids in
     * the database, only their category names.
     *
     * bgCategory should have a unique category name, i.e. no BgCategory should already exist in the
     * database with the same name as bgCategory.
     * @param bgCategory a BgCategory with a unique category name
     */
    public void update(BgCategory bgCategory) {
        AppDatabase.getExecutorService().execute(() -> {
            bgCategoryDao.update(bgCategory);
        });
    }

    /**
     * Deletes bgCategory from the database. This also deletes any related assigned categories
     * tables and skill rating tables.
     * @param bgCategory a BgCategory that exists in the database
     */
    public void delete(BgCategory bgCategory) {
        AppDatabase.getExecutorService().execute(() -> {
            bgCategoryDao.delete(bgCategory);
        });
    }

    /**
     * Checks whether the database contains a BgCategory with the name categoryName. If it does,
     * returns true. Otherwise, returns false.
     * @param categoryName
     * @return
     */
    public boolean containsCategoryName(String categoryName) {
        Future future = AppDatabase.getExecutorService().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return bgCategoryDao.containsBgCategory(categoryName);
            }
        });

        try {
            return (Boolean) future.get();
        } catch (Exception e) {
            Log.e("BgCategoryRepos.java", "Could not get future for contains. Exception: " + e);
            return true;
        }
    }
}
