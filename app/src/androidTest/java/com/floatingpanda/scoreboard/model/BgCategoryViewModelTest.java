package com.floatingpanda.scoreboard.model;

import android.app.Activity;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BgCategoryDao;
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
        //Test Case 1: Valid input
        String categoryName = "Name";
        boolean isValid = bgCategoryViewModel.addActivityInputsValid(activity, categoryName, true);
        assertTrue(isValid);

        //Test Case 2: Invalid empty String input
        categoryName = "";
        isValid = bgCategoryViewModel.addActivityInputsValid(activity, categoryName, true);
        assertFalse(isValid);

        //Test Case 3: Invalid String input that already exists in category database
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        BgCategory bgCategory = bgCategoryDao.findNonLiveDataByName(TestData.BG_CATEGORY_1.getCategoryName());
        assertTrue(bgCategory != null);

        categoryName = TestData.BG_CATEGORY_1.getCategoryName();
        isValid = bgCategoryViewModel.addActivityInputsValid(activity, categoryName, true);
        assertFalse(isValid);
    }

    @Test
    public void testEditActivityInputsValid() {
        //Test Case 1: Valid input - originalCategoryName and categoryName are same.
        String originalCategoryName = "Original";
        String categoryName = "Original";
        boolean isValid = bgCategoryViewModel.editActivityInputsValid(activity, originalCategoryName, categoryName, true);
        assertTrue(isValid);

        //Test Case 2: Valid input - originalCategoryName and categoryName are different, and categoryName is valid.
        categoryName = "Valid name";
        isValid = bgCategoryViewModel.editActivityInputsValid(activity, originalCategoryName, categoryName, true);
        assertTrue(isValid);

        //Test Case 3: Invalid input - originalCategoryName and categoryName are different, and categoryName is an
        // empty String.
        categoryName = "";
        isValid = bgCategoryViewModel.editActivityInputsValid(activity, originalCategoryName, categoryName, true);
        assertFalse(isValid);

        //Test Case 4: Invalid input - originalCategoryName and categoryName are different, and categoryName is a
        // String that already exists in the database.
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        BgCategory bgCategory = bgCategoryDao.findNonLiveDataByName(TestData.BG_CATEGORY_1.getCategoryName());
        assertTrue(bgCategory != null);

        categoryName = TestData.BG_CATEGORY_1.getCategoryName();
        isValid = bgCategoryViewModel.editActivityInputsValid(activity, originalCategoryName, categoryName, true);
        assertFalse(isValid);
    }
}
