package com.floatingpanda.scoreboard.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class BgCategoryRepository {

    private BgCategoryDao bgCategoryDao;
    private LiveData<List<BgCategory>> allBgCategories;

    public BgCategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        bgCategoryDao = db.bgCategoryDao();
        allBgCategories = bgCategoryDao.getAll();
    }

    public LiveData<List<BgCategory>> getAllBgCategories() {
        return allBgCategories;
    }

    public void insert(BgCategory bgCategory) {
        AppDatabase.getExecutorService().execute(() -> {
            bgCategoryDao.insert(bgCategory);
        });
    }

    public void update(BgCategory originalBgCategory, BgCategory editedBgCategory) {
        AppDatabase.getExecutorService().execute(() -> {
            bgCategoryDao.update(originalBgCategory, editedBgCategory);
        });
    }

    public boolean contains(BgCategory bgCategory) {
        Future future = AppDatabase.getExecutorService().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
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
