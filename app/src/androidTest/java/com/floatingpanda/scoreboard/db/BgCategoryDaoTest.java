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
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.AssignedCategory;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BgCategoryDao;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.List;

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
    public void getBgCategoriesWhenNoBgCategoriesInserted() throws InterruptedException {
        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertTrue(bgCategories.isEmpty());
    }

    @Test
    public void getBgCategoriesWhenBgCategoriesInserted() throws InterruptedException {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertFalse(bgCategories.isEmpty());
        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size()));
    }

    @Test
    public void getBgCategoryByName() throws InterruptedException {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        BgCategory bgCategory = LiveDataTestUtil.getValue(bgCategoryDao.findLiveDataByName(TestData.BG_CATEGORY_1.getCategoryName()));

        assertThat(bgCategory.getId(), is(TestData.BG_CATEGORY_1.getId()));
        assertThat(bgCategory.getCategoryName(), is(TestData.BG_CATEGORY_1.getCategoryName()));
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
    public void insertAndDeleteSpecificBgCategory() throws InterruptedException {
        bgCategoryDao.insert(TestData.BG_CATEGORY_1);

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertFalse(bgCategories.isEmpty());
        assertThat(bgCategories.size(), is(1));

        bgCategoryDao.delete(TestData.BG_CATEGORY_1);

        bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertTrue(bgCategories.isEmpty());
    }

    @Test
    public void insertAndUpdateBgCategory() throws InterruptedException {
        bgCategoryDao.insert(TestData.BG_CATEGORY_1);

        BgCategory bgCategory = bgCategoryDao.findNonLiveDataByName(TestData.BG_CATEGORY_1.getCategoryName());
        assertThat(bgCategory, is(TestData.BG_CATEGORY_1));

        bgCategory.setCategoryName("Changed");
        bgCategoryDao.update(bgCategory);

        BgCategory noBgCategory = bgCategoryDao.findNonLiveDataByName(TestData.BG_CATEGORY_1.getCategoryName());
        assertNull(noBgCategory);

        BgCategory testBgCategory = bgCategoryDao.findNonLiveDataByName(bgCategory.getCategoryName());
        assertThat(testBgCategory, is(bgCategory));
        assertThat(testBgCategory, is(not(TestData.BG_CATEGORY_1)));
    }
}
