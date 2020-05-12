package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BgCategoryRepository;

import java.util.List;

public class BgCategoryViewModel extends AndroidViewModel {

    private BgCategoryRepository bgCategoryRepository;
    private LiveData<List<BgCategory>> allBgCategories;

    public BgCategoryViewModel(Application application) {
        super(application);
        bgCategoryRepository = new BgCategoryRepository(application);
        allBgCategories = bgCategoryRepository.getAll();
    }

    public LiveData<List<BgCategory>> getAllBgCategories() { return allBgCategories; }

    // Precondition: bgCategory should not exist in database.
    // Postcondition: new bg category exists in the database.
    public void addBgCategory(BgCategory bgCategory) { bgCategoryRepository.insert(bgCategory); }

    // Precondition: - a BgCategory with editedBgCategory's id (primary key) exists in database.
    // Postcondition: - the BgCategory in the database will be updated to have the details of editedBgCategory.
    //                - edits will cascade, so foreign keys of bgCategory (such as in assigned_categories) will be updated as well.
    public void editBgCategory(BgCategory editedBgCategory) {
        bgCategoryRepository.update(editedBgCategory);
    }

    // Precondition: - bgCategory should exist in the database.
    // Postconditions: - bgCategory will no longer exist in the database.
    //                 - assigned_categories tables with bgCategory in will have been deleted.
    //                 - skill ratings for players for this category will have been deleted.
    public void deleteBgCategory(BgCategory bgCategory) {
        bgCategoryRepository.delete(bgCategory);
    }
}
