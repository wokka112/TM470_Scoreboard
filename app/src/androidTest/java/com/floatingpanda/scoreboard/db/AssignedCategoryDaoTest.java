package com.floatingpanda.scoreboard.db;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.AssignedCategory;
import com.floatingpanda.scoreboard.data.AssignedCategoryDao;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BgCategoryDao;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGameDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AssignedCategoryDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private AssignedCategoryDao assignedCategoryDao;
    private BgCategoryDao bgCategoryDao;
    private BoardGameDao boardGameDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        assignedCategoryDao = db.assignedCategoryDao();

        // Needed because assigned categories in the db use foreign keys from bg category and board game tables.
        bgCategoryDao = db.bgCategoryDao();
        boardGameDao = db.boardGameDao();

        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAssignedCategoriesWhenNoAssignedCategoriesInserted() throws InterruptedException {
        List<AssignedCategory> assignedCategories = LiveDataTestUtil.getValue(assignedCategoryDao.getAll());

        assertTrue(assignedCategories.isEmpty());
    }

    @Test
    public void getAssignedCategoriesWhenAssignedCategoriesInserted() throws InterruptedException {
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));

        List<AssignedCategory> assignedCategories = LiveDataTestUtil.getValue(assignedCategoryDao.getAll());

        assertFalse(assignedCategories.isEmpty());
        assertThat(assignedCategories.size(), is(TestData.ASSIGNED_CATEGORIES.size()));
    }

    @Test
    public void getAssignedCategoriesWhenSpecificAssignedCategoryInserted() throws InterruptedException {
        assignedCategoryDao.insert(TestData.ASSIGNED_CATEGORY_1);

        List<AssignedCategory> assignedCategories = LiveDataTestUtil.getValue(assignedCategoryDao.getAll());

        assertFalse(assignedCategories.isEmpty());
        assertThat(assignedCategories.size(), is(1));
        assertThat(assignedCategories.get(0), is(TestData.ASSIGNED_CATEGORY_1));
    }

    @Test
    public void getAssignedCategoriesWhenSameAssignedCategoryInsertedTwice() throws InterruptedException {
        assignedCategoryDao.insert(TestData.ASSIGNED_CATEGORY_1);

        List<AssignedCategory> assignedCategories = LiveDataTestUtil.getValue(assignedCategoryDao.getAll());

        assertFalse(assignedCategories.isEmpty());
        assertThat(assignedCategories.size(), is(1));
        assertThat(assignedCategories.get(0), is(TestData.ASSIGNED_CATEGORY_1));

        assignedCategoryDao.insert(TestData.ASSIGNED_CATEGORY_1);
        TimeUnit.MILLISECONDS.sleep(100);

        assignedCategories = LiveDataTestUtil.getValue(assignedCategoryDao.getAll());

        assertFalse(assignedCategories.isEmpty());
        assertThat(assignedCategories.size(), is(not(2)));
        assertThat(assignedCategories.size(), is(1));
        assertThat(assignedCategories.get(0), is(TestData.ASSIGNED_CATEGORY_1));
    }

    @Test
    public void getSingleNonLiveAssignedCategoryByBoardGameId() {
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));

        List<AssignedCategory> assignedCategories = assignedCategoryDao.findNonLiveDataByBgId(TestData.ASSIGNED_CATEGORY_2.getBgId());

        assertThat(assignedCategories.get(0), is(TestData.ASSIGNED_CATEGORY_2));
    }

    @Test
    public void getSingleNonLiveAssignedCategoryByCategoryId() {
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));

        List<AssignedCategory> assignedCategories = assignedCategoryDao.findNonLiveDataByCategoryId(TestData.ASSIGNED_CATEGORY_2.getCategoryId());

        assertThat(assignedCategories.get(0), is(TestData.ASSIGNED_CATEGORY_2));
    }

    @Test
    public void getSeveralNonLiveAssignedCategoriesByBoardGameId() {
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));

        //Assigned category 4's bg id should be associated with 2 category ids - bg category 1's and bg category 3's.
        List<AssignedCategory> assignedCategories = assignedCategoryDao.findNonLiveDataByBgId(TestData.ASSIGNED_CATEGORY_4.getBgId());
        assertThat(assignedCategories.size(), is(2));
        assertTrue(assignedCategories.contains(TestData.ASSIGNED_CATEGORY_3));
        assertTrue(assignedCategories.contains(TestData.ASSIGNED_CATEGORY_4));

        List<Integer> categoryIds = new ArrayList<>();
        for (AssignedCategory assignedCategory : assignedCategories) {
            categoryIds.add(assignedCategory.getCategoryId());
        }

        assertTrue(categoryIds.contains(TestData.BG_CATEGORY_1.getId()));
        assertTrue(categoryIds.contains(TestData.BG_CATEGORY_3.getId()));
    }

    @Test
    public void getSeveralNonLiveAssignedCategoriesByCategoryId() {
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));

        //Assigned category 4's category id should be associated with 2 bg ids - board game 1's and board game 3's.
        List<AssignedCategory> assignedCategories = assignedCategoryDao.findNonLiveDataByCategoryId(TestData.ASSIGNED_CATEGORY_4.getCategoryId());
        assertThat(assignedCategories.size(), is(2));
        assertTrue(assignedCategories.contains(TestData.ASSIGNED_CATEGORY_1));
        assertTrue(assignedCategories.contains(TestData.ASSIGNED_CATEGORY_4));

        List<Integer> bgIds = new ArrayList<>();
        for (AssignedCategory assignedCategory : assignedCategories) {
            bgIds.add(assignedCategory.getBgId());
        }

        assertTrue(bgIds.contains(TestData.BOARD_GAME_1.getId()));
        assertTrue(bgIds.contains(TestData.BOARD_GAME_3.getId()));
    }

    @Test
    public void insertAndDeleteAllAssignedCategories() throws InterruptedException {
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));

        List<AssignedCategory> assignedCategories = LiveDataTestUtil.getValue(assignedCategoryDao.getAll());
        assertFalse(assignedCategories.isEmpty());
        assertThat(assignedCategories.size(), is(TestData.ASSIGNED_CATEGORIES.size()));

        assignedCategoryDao.deleteAll();

        assignedCategories = LiveDataTestUtil.getValue(assignedCategoryDao.getAll());
        assertTrue(assignedCategories.isEmpty());
    }

    @Test
    public void insertAllAssignedCategoriesAndDeleteSpecificAssignedCategory() throws InterruptedException {
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));

        List<AssignedCategory> allAssignedCategories = LiveDataTestUtil.getValue(assignedCategoryDao.getAll());
        assertFalse(allAssignedCategories.isEmpty());
        assertThat(allAssignedCategories.size(), is(TestData.ASSIGNED_CATEGORIES.size()));

        //Use assigned category 4's bg id so we end up with 2 elements in the list - assigned category 3 and assigned category 4.
        List<AssignedCategory> specificAssignedCategory = assignedCategoryDao.findNonLiveDataByBgId(TestData.ASSIGNED_CATEGORY_4.getBgId());
        assertFalse(specificAssignedCategory.isEmpty());
        assertThat(specificAssignedCategory.size(), is(2));
        assertTrue(specificAssignedCategory.contains(TestData.ASSIGNED_CATEGORY_3));
        assertTrue(specificAssignedCategory.contains(TestData.ASSIGNED_CATEGORY_4));

        //This should delete just one entry in the specific list - assigned category 4 - leaving the other in it - assigned category 1.
        assignedCategoryDao.delete(TestData.ASSIGNED_CATEGORY_4);

        allAssignedCategories = LiveDataTestUtil.getValue(assignedCategoryDao.getAll());
        assertFalse(allAssignedCategories.isEmpty());
        assertThat(allAssignedCategories.size(), is(TestData.ASSIGNED_CATEGORIES.size() - 1));

        //The specific list should now have only 1 entry linked to assigned category 4's bg id in it - assigned category 1.
        //Hence it should no longer contain assigned_category_4.
        specificAssignedCategory = assignedCategoryDao.findNonLiveDataByBgId(TestData.ASSIGNED_CATEGORY_4.getBgId());
        assertFalse(specificAssignedCategory.isEmpty());
        assertThat(specificAssignedCategory.size(), is(1));
        assertFalse(specificAssignedCategory.contains(TestData.ASSIGNED_CATEGORY_4));
    }

    @Test
    public void insertAllAssignedCategoriesAndDeleteAnAssociatedBoardGame() throws InterruptedException {
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));

        //Assigned category 1 is associated with board game 1.
        List<AssignedCategory> assignedCategory = assignedCategoryDao.findNonLiveDataByBgId(TestData.ASSIGNED_CATEGORY_1.getBgId());
        assertThat(assignedCategory.size(), is(1));
        assertThat(assignedCategory.get(0), is(TestData.ASSIGNED_CATEGORY_1));
        assertThat(assignedCategory.get(0).getBgId(), is(TestData.BOARD_GAME_1.getId()));

        BoardGame boardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataById(TestData.ASSIGNED_CATEGORY_1.getBgId()));
        assertTrue(boardGame != null);
        assertThat(boardGame, is(TestData.BOARD_GAME_1));

        //This deletion should cascade, resulting in associated assigned categories being deleted.
        boardGameDao.delete(TestData.BOARD_GAME_1);

        boardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataById(TestData.ASSIGNED_CATEGORY_1.getBgId()));
        assertNull(boardGame);

        assignedCategory = assignedCategoryDao.findNonLiveDataByBgId(TestData.ASSIGNED_CATEGORY_1.getBgId());
        assertTrue(assignedCategory.isEmpty());
    }

    @Test
    public void insertAllAssignedCategoriesAndDeleteAnAssociatedBgCategory() throws InterruptedException {
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));

        //Assigned category 2 is associated with bg category 2.
        List<AssignedCategory> assignedCategory = assignedCategoryDao.findNonLiveDataByCategoryId(TestData.ASSIGNED_CATEGORY_2.getCategoryId());
        assertThat(assignedCategory.size(), is(1));
        assertThat(assignedCategory.get(0), is(TestData.ASSIGNED_CATEGORY_2));
        assertThat(assignedCategory.get(0).getCategoryId(), is(TestData.BG_CATEGORY_2.getId()));

        BgCategory bgCategory = LiveDataTestUtil.getValue(bgCategoryDao.findLiveDataById(TestData.ASSIGNED_CATEGORY_2.getCategoryId()));
        assertTrue(bgCategory != null);
        assertThat(bgCategory, is(TestData.BG_CATEGORY_2));

        //This deletion should cascade, resulting in associated assigned categories being deleted.
        bgCategoryDao.delete(TestData.BG_CATEGORY_2);

        bgCategory = LiveDataTestUtil.getValue(bgCategoryDao.findLiveDataById(TestData.ASSIGNED_CATEGORY_2.getCategoryId()));
        assertNull(bgCategory);

        assignedCategory = assignedCategoryDao.findNonLiveDataByCategoryId(TestData.ASSIGNED_CATEGORY_2.getCategoryId());
        assertTrue(assignedCategory.isEmpty());
    }
}
