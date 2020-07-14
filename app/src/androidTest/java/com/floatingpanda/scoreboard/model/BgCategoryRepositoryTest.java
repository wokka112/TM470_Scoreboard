package com.floatingpanda.scoreboard.model;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.repositories.BgCategoryRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BgCategoryRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private BgCategoryDao bgCategoryDao;
    private BgCategoryRepository bgCategoryRepository;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        bgCategoryDao = db.bgCategoryDao();
        bgCategoryRepository = new BgCategoryRepository(db);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getLiveBgCategoriesWhenNoBgCategoriesInserted() throws InterruptedException {
        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryRepository.getAll());

        assertTrue(bgCategories.isEmpty());
    }

    @Test
    public void getLiveBgCategoriesFromDatabaseWhenBgCategoriesInserted() throws InterruptedException {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryRepository.getAll());

        assertFalse(bgCategories.isEmpty());
        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size()));
    }

    @Test
    public void addBgCategoryToDatabase() throws InterruptedException {
        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryRepository.getAll());
        assertTrue(bgCategories.isEmpty());

        bgCategoryRepository.insert(TestData.BG_CATEGORY_1);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        bgCategories = LiveDataTestUtil.getValue(bgCategoryRepository.getAll());

        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(TestData.BG_CATEGORY_1));
    }

    @Test
    public void editBgCategoryInDatabase() throws  InterruptedException {
        bgCategoryRepository.insert(TestData.BG_CATEGORY_1);
        TimeUnit.MILLISECONDS.sleep(100);

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryRepository.getAll());

        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(TestData.BG_CATEGORY_1));

        BgCategory bgCategory = new BgCategory(TestData.BG_CATEGORY_1);
        assertThat(bgCategory, is(bgCategories.get(0)));

        String categoryName = "Changed";
        bgCategory.setCategoryName(categoryName);
        assertThat(bgCategory, is(not(bgCategories.get(0))));

        bgCategoryRepository.update(bgCategory);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        bgCategories = LiveDataTestUtil.getValue(bgCategoryRepository.getAll());

        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(not(TestData.BG_CATEGORY_1)));
        assertThat(bgCategories.get(0), is(bgCategory));
    }

    @Test
    public void deleteBgCategoryInDatabase() throws InterruptedException {
        bgCategoryRepository.insert(TestData.BG_CATEGORY_1);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryRepository.getAll());

        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(TestData.BG_CATEGORY_1));

        bgCategoryRepository.delete(TestData.BG_CATEGORY_1);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        bgCategories = LiveDataTestUtil.getValue(bgCategoryRepository.getAll());

        assertTrue(bgCategories.isEmpty());
    }

    @Test
    public void testContains() throws InterruptedException {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryRepository.getAll());

        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size()));

        //Test case 1: Contains - categoryName exists in database.
        String categoryName = TestData.BG_CATEGORY_1.getCategoryName();
        boolean contains = bgCategoryRepository.containsCategoryName(categoryName);
        assertTrue(contains);

        //Test case 2: Does not contain - categoryName does not exist in database.
        categoryName = "Non-existent name";
        contains = bgCategoryRepository.containsCategoryName(categoryName);
        assertFalse(contains);

        //Test case 3: Does not contain - empty categoryName does not exist in database.
        categoryName = "";
        contains = bgCategoryRepository.containsCategoryName(categoryName);
        assertFalse(contains);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContainsWithNull() throws InterruptedException {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryRepository.getAll());

        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size()));

        String categoryName = null;
        boolean contains = bgCategoryRepository.containsCategoryName(categoryName);
    }
}
