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

public class BgCategoryRepository {

    private BgCategoryDao bgCategoryDao;
    private LiveData<List<BgCategory>> allBgCategories;

    //Used for testing
    private AssignedCategoryDao assignedCategoryDao;

    public BgCategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        bgCategoryDao = db.bgCategoryDao();
        allBgCategories = bgCategoryDao.getAllLive();

        //Used for testing
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
     * @return live data list of all bg categories from database
     */
    public LiveData<List<BgCategory>> getAll() {
        return allBgCategories;
    }

    // Precondition: BgCategory with bgCategory's name or id should not exist in database.
    // Postcondition: new BgCategory exists in the database.
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

    // Precondition: - a BgCategory with bgCategory's id (primary key) exists in database.
    // Postcondition: - the BgCategory in the database will be updated to have the details of bgCategory.
    //                - edits will cascade, so foreign keys of bgCategory (such as in assigned_categories) will be updated as well.
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

    // Precondition: - bgCategory should exist in the database.
    // Postconditions: - bgCategory will no longer exist in the database.
    //                 - assigned_categories tables with bgCategory in will have been deleted.
    //                 - skill ratings for players for this category will have been deleted.
    /**
     * Deletes bgCategory from the database.
     * @param bgCategory a BgCategory that exists in the database
     */
    public void delete(BgCategory bgCategory) {
        AppDatabase.getExecutorService().execute(() -> {
            bgCategoryDao.delete(bgCategory);
        });
    }

    // Postconditions: - if a BgCategory with bgCategory's name exists in the database, returns true.
    //                 - if no BgCategory with bgCategory's name exists in the database, returns false.
    //TODO look into whether this is basically just running on the main thread. I think it may be.
    //TODO make empty string an illegal argument as well?
    /**
     * Checks whether the database contains a BgCategory with the name categoryName. If it does,
     * returns true. Otherwise, returns false.
     * @param categoryName
     * @return
     */
    public boolean containsCategoryName(String categoryName) throws IllegalArgumentException {
        if(categoryName == null) {
            throw new IllegalArgumentException("null categoryName passed to contains method.");
        }

        Future future = AppDatabase.getExecutorService().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                // If no category found, returns null.
                BgCategory databaseCategory = bgCategoryDao.findNonLiveDataByName(categoryName);
                return databaseCategory != null;
            };
        });

        try {
            return (Boolean) future.get();
        } catch (Exception e) {
            Log.e("BgCatRepos.java", "Exception: " + e);
            return false;
        }
    }
}
