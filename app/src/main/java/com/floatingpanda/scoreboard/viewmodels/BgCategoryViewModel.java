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

    /**
     * @return live data list of all bg categories from database
     */
    public LiveData<List<BgCategory>> getAllBgCategories() { return allBgCategories; }

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
    public void addBgCategory(BgCategory bgCategory) { bgCategoryRepository.insert(bgCategory); }

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
    public void editBgCategory(BgCategory bgCategory) { bgCategoryRepository.update(bgCategory); }

    // Precondition: - bgCategory should exist in the database.
    // Postconditions: - bgCategory will no longer exist in the database.
    //                 - assigned_categories tables with bgCategory in will have been deleted.
    //                 - skill ratings for players for this category will have been deleted.
    /**
     * Deletes bgCategory from the database.
     * @param bgCategory a BgCategory that exists in the database
     */
    public void deleteBgCategory(BgCategory bgCategory) { bgCategoryRepository.delete(bgCategory); }
}
