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

package com.floatingpanda.scoreboard.model;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.viewmodels.BgCategoryViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BgCategoryViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private BgCategoryDao bgCategoryDao;
    private BgCategoryViewModel bgCategoryViewModel;

    @Mock
    private Activity activity;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        bgCategoryDao = db.bgCategoryDao();
        bgCategoryViewModel = new BgCategoryViewModel(ApplicationProvider.getApplicationContext(), db);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getLiveBgCategoriesWhenNoBgCategoriesInserted() throws InterruptedException {
        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryViewModel.getAllBgCategories());

        assertTrue(bgCategories.isEmpty());
    }

    @Test
    public void getLiveBgCategoriesFromDatabaseWhenBgCategoriesInserted() throws InterruptedException {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryViewModel.getAllBgCategories());

        assertFalse(bgCategories.isEmpty());
        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size()));
    }

    @Test
    public void addBgCategoryToDatabase() throws InterruptedException {
        bgCategoryViewModel.addBgCategory(TestData.BG_CATEGORY_1);

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryViewModel.getAllBgCategories());

        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(TestData.BG_CATEGORY_1));
    }

    @Test
    public void editBgCategoryInDatabase() throws  InterruptedException {
        bgCategoryViewModel.addBgCategory(TestData.BG_CATEGORY_1);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryViewModel.getAllBgCategories());

        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(TestData.BG_CATEGORY_1));

        BgCategory bgCategory = new BgCategory(TestData.BG_CATEGORY_1);
        assertThat(bgCategory, is(bgCategories.get(0)));

        String categoryName = "Changed";
        bgCategory.setCategoryName(categoryName);
        assertThat(bgCategory, is(not(bgCategories.get(0))));

        bgCategoryViewModel.editBgCategory(bgCategory);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        bgCategories = LiveDataTestUtil.getValue(bgCategoryViewModel.getAllBgCategories());

        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(not(TestData.BG_CATEGORY_1)));
        assertThat(bgCategories.get(0), is(bgCategory));
    }

    @Test
    public void deleteBgCategoryInDatabase() throws InterruptedException {
        bgCategoryViewModel.addBgCategory(TestData.BG_CATEGORY_1);
        TimeUnit.MILLISECONDS.sleep(100);

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryViewModel.getAllBgCategories());

        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(TestData.BG_CATEGORY_1));

        bgCategoryViewModel.deleteBgCategory(TestData.BG_CATEGORY_1);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        bgCategories.clear();
        bgCategories = LiveDataTestUtil.getValue(bgCategoryViewModel.getAllBgCategories());

        assertTrue(bgCategories.isEmpty());
    }

    @Test
    public void testAddActivityInputsValid() {
        Context context = ApplicationProvider.getApplicationContext();
        EditText categoryEditText = new EditText(context);

        //Test Case 1: Valid input
        String categoryName = "Name";
        categoryEditText.setText(categoryName);
        boolean isValid = bgCategoryViewModel.addActivityInputsValid(categoryEditText, true);
        assertTrue(isValid);

        //Test Case 2: Invalid empty String input
        categoryName = "";
        categoryEditText.setText(categoryName);
        isValid = bgCategoryViewModel.addActivityInputsValid(categoryEditText, true);
        assertFalse(isValid);

        //Test Case 3: Invalid String input that already exists in category database
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        BgCategory bgCategory = bgCategoryDao.findNonLiveDataByName(TestData.BG_CATEGORY_1.getCategoryName());
        assertTrue(bgCategory != null);

        categoryName = TestData.BG_CATEGORY_1.getCategoryName();
        categoryEditText.setText(categoryName);
        isValid = bgCategoryViewModel.addActivityInputsValid(categoryEditText, true);
        assertFalse(isValid);
    }

    @Test
    public void testEditActivityInputsValid() {
        Context context = ApplicationProvider.getApplicationContext();
        EditText categoryEditText = new EditText(context);

        //Test Case 1: Valid input - originalCategoryName and categoryName are same.
        String originalCategoryName = "Original";
        String editedCategoryName = "Original";
        categoryEditText.setText(editedCategoryName);
        boolean isValid = bgCategoryViewModel.editActivityInputsValid(originalCategoryName, categoryEditText, true);
        assertTrue(isValid);

        //Test Case 2: Valid input - originalCategoryName and categoryName are different, and categoryName is valid.
        editedCategoryName = "Valid name";
        categoryEditText.setText(editedCategoryName);
        isValid = bgCategoryViewModel.editActivityInputsValid(originalCategoryName, categoryEditText, true);
        assertTrue(isValid);

        //Test Case 3: Invalid input - originalCategoryName and categoryName are different, and categoryName is an
        // empty String.
        editedCategoryName = "";
        categoryEditText.setText(editedCategoryName);
        isValid = bgCategoryViewModel.editActivityInputsValid(originalCategoryName, categoryEditText, true);
        assertFalse(isValid);

        //Test Case 4: Invalid input - originalCategoryName and categoryName are different, and categoryName is a
        // String that already exists in the database.
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        BgCategory bgCategory = bgCategoryDao.findNonLiveDataByName(TestData.BG_CATEGORY_1.getCategoryName());
        assertTrue(bgCategory != null);

        editedCategoryName = TestData.BG_CATEGORY_1.getCategoryName();
        categoryEditText.setText(editedCategoryName);
        isValid = bgCategoryViewModel.editActivityInputsValid(originalCategoryName, categoryEditText, true);
        assertFalse(isValid);
    }
}
