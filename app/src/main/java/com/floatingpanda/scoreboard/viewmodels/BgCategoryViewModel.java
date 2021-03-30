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

package com.floatingpanda.scoreboard.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.widget.EditText;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.utils.AlertDialogHelper;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.repositories.BgCategoryRepository;

import java.util.List;

/**
 * ViewModel providing data to the BgCategory related views.
 */
public class BgCategoryViewModel extends AndroidViewModel {

    private BgCategoryRepository bgCategoryRepository;
    private LiveData<List<BgCategory>> allBgCategories;

    public BgCategoryViewModel(Application application) {
        super(application);
        bgCategoryRepository = new BgCategoryRepository(application);
        allBgCategories = bgCategoryRepository.getAll();
    }

    // Used for testing purposes.
    public BgCategoryViewModel(Application application, AppDatabase database) {
        super(application);
        bgCategoryRepository = new BgCategoryRepository(database);
        allBgCategories = bgCategoryRepository.getAll();
    }

    /**
     * Returns a livedata list of all the bg categories in the database.
     */
    public LiveData<List<BgCategory>> getAllBgCategories() { return allBgCategories; }

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

    /**
     * Deletes bgCategory from the database. This will also delete any assigned categories and skill
     * rating tables it is a part of.
     * @param bgCategory a BgCategory that exists in the database
     */
    public void deleteBgCategory(BgCategory bgCategory) { bgCategoryRepository.delete(bgCategory); }

    /**
     * Tests whether the new bg category name provided in categoryNameEditText is valid, i.e. is not
     * empty and does not already exist in the database.
     *
     * If it is valid, returns true. Otherwise, returns false.
     *
     * This version is used when adding new categories to the database.
     * @param categoryNameEditText edittext containing the category name for the new category to be added to the database
     * @param testing boolean determining whether this is being run in a test
     * @return
     */
    public boolean addActivityInputsValid(EditText categoryNameEditText, boolean testing) {
        return editActivityInputsValid("", categoryNameEditText, testing);
    }

    /**
     * Tests whether the edited bg category name provided in categoryNameEditText is valid, i.e. is
     * not empty, and does not already exist in the database or does exist in the database but is
     * the same as the original name, provided as originalCategoryName.
     *
     * If it is valid, returns true. Otherwise, returns false.
     *
     * This version is used when adding new categories to the database.
     * @param originalCategoryName string containing original category name for the category to be edited in the database
     * @param categoryNameEditText edittext containing the edited category name for the category to be edited in the database
     * @param testing boolean determining whether this is being run in a test
     * @return
     */
    public boolean editActivityInputsValid(String originalCategoryName, EditText categoryNameEditText, boolean testing) {
        if (categoryNameEditText.getText().toString().trim().isEmpty()) {
            if (!testing) {
                categoryNameEditText.setError("You must enter a name for the category.");
            }
            return false;
        }

        String categoryName = categoryNameEditText.getText().toString();

        // Do not return false if the category name has not been edited.
        if (!categoryName.equals(originalCategoryName)
                && bgCategoryRepository.containsCategoryName(categoryName)) {
            if (!testing) {
                categoryNameEditText.setError("You must enter a unique name for the category.");
            }
            return false;
        }

        return true;
    }
}
