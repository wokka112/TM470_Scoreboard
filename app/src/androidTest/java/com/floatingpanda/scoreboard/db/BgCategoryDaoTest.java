package com.floatingpanda.scoreboard.db;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BgCategoryDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class BgCategoryDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private BgCategoryDao bgCategoryDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        bgCategoryDao = db.bgCategoryDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getLiveBgCategoriesWhenNoBgCategoriesInserted() throws InterruptedException {
        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertTrue(bgCategories.isEmpty());
    }

    @Test
    public void getNonLiveBgCategoriesWhenNoBgCategoriesInserted() {
        List<BgCategory> bgCategories = bgCategoryDao.getAllNonLive();

        assertTrue(bgCategories.isEmpty());
    }

    @Test
    public void getLiveBgCategoriesWhenBgCategoriesInserted() throws InterruptedException {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertFalse(bgCategories.isEmpty());
        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size()));
    }

    @Test
    public void getNonLiveBgCategoriesWhenBgCategoriesInserted() {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        List<BgCategory> bgCategories = bgCategoryDao.getAllNonLive();

        assertFalse(bgCategories.isEmpty());
        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size()));
    }

    @Test
    public void getLiveBgCategoriesWhenSpecificBgCategoryInserted() throws InterruptedException {
        bgCategoryDao.insert(TestData.BG_CATEGORY_1);

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertFalse(bgCategories.isEmpty());
        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(TestData.BG_CATEGORY_1));
    }

    @Test
    public void getNonLiveBgCategoriesWhenSpecificBgCategoryInserted() {
        bgCategoryDao.insert(TestData.BG_CATEGORY_1);

        List<BgCategory> bgCategories = bgCategoryDao.getAllNonLive();

        assertFalse(bgCategories.isEmpty());
        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(TestData.BG_CATEGORY_1));
    }

    @Test
    public void getLiveBgCategoriesWhenSameBgCategoryInsertedTwice() throws  InterruptedException {
        bgCategoryDao.insert(TestData.BG_CATEGORY_1);

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertFalse(bgCategories.isEmpty());
        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(TestData.BG_CATEGORY_1));

        bgCategoryDao.insert(TestData.BG_CATEGORY_1);
        TimeUnit.MILLISECONDS.sleep(100);

        bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertFalse(bgCategories.isEmpty());
        assertThat(bgCategories.size(), is(not(2)));
        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(TestData.BG_CATEGORY_1));
    }

    @Test
    public void getLiveBgCategoriesWhenBgCategoryIsInsertedThenEditedVersionWithSamePrimaryKeyIsInserted() throws InterruptedException {
        bgCategoryDao.insert(TestData.BG_CATEGORY_1);

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertFalse(bgCategories.isEmpty());
        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(TestData.BG_CATEGORY_1));

        BgCategory editedBgCategory = new BgCategory(bgCategories.get(0));
        editedBgCategory.setCategoryName("Changed");
        assertThat(editedBgCategory, is(not(bgCategories.get(0))));

        bgCategoryDao.insert(editedBgCategory);
        TimeUnit.MILLISECONDS.sleep(100);

        bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertFalse(bgCategories.isEmpty());
        assertThat(bgCategories.size(), is(not(2)));
        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(not(editedBgCategory)));
        assertThat(bgCategories.get(0), is(TestData.BG_CATEGORY_1));
    }

    @Test
    public void getLiveBgCategoryById() throws InterruptedException {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        BgCategory bgCategory = LiveDataTestUtil.getValue(bgCategoryDao.findLiveDataById(TestData.BG_CATEGORY_1.getId()));

        assertThat(bgCategory, is(TestData.BG_CATEGORY_1));
    }

    @Test
    public void getLiveBgCategoryByCategoryName() throws InterruptedException {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        BgCategory bgCategory = LiveDataTestUtil.getValue(bgCategoryDao.findLiveDataByName(TestData.BG_CATEGORY_2.getCategoryName()));

        assertThat(bgCategory, is(TestData.BG_CATEGORY_2));
    }

    @Test
    public void getNonLiveBgCategoryByCategoryName() {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        BgCategory bgCategory = bgCategoryDao.findNonLiveDataByName(TestData.BG_CATEGORY_2.getCategoryName());

        assertThat(bgCategory, is(TestData.BG_CATEGORY_2));
    }

    @Test
    public void insertAndDeleteAllBgCategories() throws InterruptedException {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertFalse(bgCategories.isEmpty());

        bgCategoryDao.deleteAll();

        bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertTrue(bgCategories.isEmpty());
    }

    @Test
    public void insertAllBgCategoriesAndDeleteSpecificBgCategory() throws InterruptedException {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertFalse(bgCategories.isEmpty());
        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size()));

        BgCategory bgCategory = LiveDataTestUtil.getValue(bgCategoryDao.findLiveDataById(TestData.BG_CATEGORY_2.getId()));
        assertTrue(bgCategory != null);

        bgCategoryDao.delete(TestData.BG_CATEGORY_2);

        bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size() - 1));

        bgCategory = LiveDataTestUtil.getValue(bgCategoryDao.findLiveDataById(TestData.BG_CATEGORY_2.getId()));
        assertNull(bgCategory);
    }

    @Test
    public void insertAllBgCategoriesAndUpdateSpecificBgCategory() throws InterruptedException {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        BgCategory bgCategory = LiveDataTestUtil.getValue(bgCategoryDao.findLiveDataById(TestData.BG_CATEGORY_2.getId()));
        assertThat(bgCategory, is(TestData.BG_CATEGORY_2));

        String newCategoryName = "Changed";

        bgCategory.setCategoryName(newCategoryName);
        bgCategoryDao.update(bgCategory);

        //Should no longer exist in database, hence should return null.
        BgCategory oldBgCategory = bgCategoryDao.findNonLiveDataByName(TestData.BG_CATEGORY_2.getCategoryName());
        assertNull(oldBgCategory);

        BgCategory updatedBgCategory = bgCategoryDao.findNonLiveDataByName(bgCategory.getCategoryName());
        assertThat(updatedBgCategory, is(bgCategory));
        assertThat(updatedBgCategory, is(not(TestData.BG_CATEGORY_2)));

        assertThat(updatedBgCategory.getId(), is(bgCategory.getId()));
        assertThat(updatedBgCategory.getCategoryName(), is(bgCategory.getCategoryName()));

        assertThat(updatedBgCategory.getId(), is(TestData.BG_CATEGORY_2.getId()));
        assertThat(updatedBgCategory.getCategoryName(), is(not(TestData.BG_CATEGORY_2.getCategoryName())));

        assertThat(updatedBgCategory.getCategoryName(), is(newCategoryName));
    }
}
