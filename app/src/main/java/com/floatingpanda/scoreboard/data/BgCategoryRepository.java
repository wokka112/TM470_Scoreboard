package com.floatingpanda.scoreboard.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class BgCategoryRepository {

    private AssignedCategoriesDao acsDao; // Used for testing.
    private BgCategoryDao bgCategoryDao;
    private LiveData<List<BgCategory>> allBgCategories;

    public BgCategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        acsDao = db.assignedCategoriesDao();
        bgCategoryDao = db.bgCategoryDao();
        allBgCategories = bgCategoryDao.getAll();
    }

    public LiveData<List<BgCategory>> getAll() {
        return allBgCategories;
    }

    public List<BgCategory> getAllNonLive() { return bgCategoryDao.getAllNonLive(); }

    // Precondition: bgCategory should not exist in database.
    // Postcondition: new bg category exists in the database.
    public void insert(BgCategory bgCategory) {
        AppDatabase.getExecutorService().execute(() -> {
            bgCategoryDao.insert(bgCategory);
        });
    }

    // Precondition: - a BgCategory with editedBgCategory's id (primary key) exists in database.
    // Postcondition: - the BgCategory in the database will be updated to have the details of editedBgCategory.
    //                - edits will cascade, so foreign keys of bgCategory (such as in assigned_categories) will be updated as well.
    public void update(BgCategory editedBgCategory) {
        AppDatabase.getExecutorService().execute(() -> {
            bgCategoryDao.update(editedBgCategory);
        });
    }

    // Precondition: - bgCategory should exist in the database.
    // Postconditions: - bgCategory will no longer exist in the database.
    //                 - assigned_categories tables with bgCategory in will have been deleted.
    //                 - skill ratings for players for this category will have been deleted.
    //TODO add assert tests. Maybe need to include bg and bgcategory name, rather than ids.
    public void delete(BgCategory bgCategory) {
        AppDatabase.getExecutorService().execute(() -> {
            //TODO remove logs, they are for testing.
            List<AssignedCategories> acs = acsDao.findByCategoryId(bgCategory.getId());

            for (int i = 0; i < acs.size(); i++) {
                Log.w("BGCatRepos.java", "1 Bg: " + bgCategory.getId() + ", AC: " + acs.get(i).getBgId());
            }

            bgCategoryDao.delete(bgCategory);

            acs = acsDao.findByCategoryId(bgCategory.getId());

            if (acs.isEmpty()) {
                Log.w("BGCatRepos.java", "2 Empty");
            } else {
                for (int i = 0; i < acs.size(); i++) {
                    Log.w("BGCatRepos.java", "2 Category: " + bgCategory.getId() + ", AC: " + acs.get(i).getBgId());
                }
            }
        });
    }

    // Postconditions: - if a BgCategory with bgCategory's name exists in the database, returns true.
    //                 - if no BgCategory with bgCategory's name exists in the database, returns false.
    //TODO change bgcategoryrepository contains method to simply take a string bgcategoryname.
    public boolean contains(BgCategory bgCategory) {
        Future future = AppDatabase.getExecutorService().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                //TODO remove log warnings once done with tests.
                Log.w("BgCatRepos.java", "Started thread");
                BgCategory databaseCategory = bgCategoryDao.findNonLiveDataByName(bgCategory.getCategoryName());
                Log.w("BgCatRepos.java", "Category: " + databaseCategory);
                boolean same = (bgCategory.equals(databaseCategory));
                Log.w("BgCatRepos.java", "Got category: " + databaseCategory);
                Log.w("BgCatRepos.java", "Same: " + same);
                return same;
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
