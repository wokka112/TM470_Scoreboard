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
        allBgCategories = bgCategoryRepository.getAllBgCategories();
    }

    public LiveData<List<BgCategory>> getAllBgCategories() { return allBgCategories; }

    // Precondition: bgCategory should not exist in database.
    // Postcondition: new bg category exists in the database.
    public void addBgCategory(BgCategory bgCategory) { bgCategoryRepository.insert(bgCategory); }

    // Precondition: - originalBgCategory already exists in database.
    //               - editedBgCategory does not already exist in database.
    //               - editedBgCategory has different name to originalBgCategory.
    // Postcondition: - originalBgCategory will be updated in database to have the details of editedBgCategory.
    //                - edits will cascade, so foreign keys of bgCategory (such as in assigned_categories) will be updated as well.
    public void editBgCategory(BgCategory originalBgCategory, BgCategory editedBgCategory) {
        bgCategoryRepository.update(originalBgCategory, editedBgCategory);
    }

    public boolean bgCategoryExists(BgCategory bgCategory) {
        return getAllBgCategories().getValue().contains(bgCategory);
    }


}
