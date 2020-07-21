package com.floatingpanda.scoreboard.model;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.AssignedCategory;
import com.floatingpanda.scoreboard.data.daos.AssignedCategoryDao;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.daos.PlayModeDao;
import com.floatingpanda.scoreboard.viewmodels.BoardGameViewModel;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BoardGameViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private AssignedCategoryDao assignedCategoryDao;
    private BgCategoryDao bgCategoryDao;
    private BoardGameDao boardGameDao;
    private PlayModeDao playModeDao;
    private BoardGameViewModel boardGameViewModel;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        assignedCategoryDao = db.assignedCategoryDao();
        bgCategoryDao = db.bgCategoryDao();
        boardGameDao = db.boardGameDao();
        playModeDao = db.playModeDao();
        boardGameViewModel = new BoardGameViewModel(ApplicationProvider.getApplicationContext(), db);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllBoardGamesWithBgCategoriesWhenNoneInserted() throws InterruptedException {
        List<BoardGameWithBgCategories> boardGamesWithBgCategories = LiveDataTestUtil.getValue(boardGameViewModel.getAllBoardGamesWithBgCategories());

        assertTrue(boardGamesWithBgCategories.isEmpty());
    }

    @Test
    public void getAllBoardGamesWithBgCategoriesWhenInserted() throws InterruptedException {
        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));

        List<BoardGameWithBgCategories> boardGamesWithBgCategories = LiveDataTestUtil.getValue(boardGameViewModel.getAllBoardGamesWithBgCategories());

        assertThat(boardGamesWithBgCategories.size(), is(TestData.BOARD_GAMES_WITH_BG_CATEGORIES.size()));
    }

    @Test
    public void getAllBoardGamesWithBgCategoriesAndPlayModesWhenNoneInserted() throws  InterruptedException {
        List<BoardGameWithBgCategoriesAndPlayModes> boardGamesWithBgCategoriesAndPlayModes = LiveDataTestUtil.getValue(boardGameViewModel.
                getAllBoardGamesWithBgCategoriesAndPlayModes());

        assertTrue(boardGamesWithBgCategoriesAndPlayModes.isEmpty());
    }

    @Test
    public void getAllBoardGamesWithBgCategoriesAndPlayModesWhenInserted() throws  InterruptedException {
        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        playModeDao.insertAll(TestData.PLAY_MODES.toArray(new PlayMode[TestData.PLAY_MODES.size()]));

        List<BoardGameWithBgCategoriesAndPlayModes> boardGamesWithBgCategoriesAndPlayModes = LiveDataTestUtil.getValue(boardGameViewModel.
                getAllBoardGamesWithBgCategoriesAndPlayModes());

        assertThat(boardGamesWithBgCategoriesAndPlayModes.size(), is(TestData.BOARD_GAMES_WITH_BG_CATEGORIES_AND_PLAY_MODES.size()));
    }

    @Test
    public void getBoardGameWithBgCategoriesAndPlayModeWhenNoneInserted() throws InterruptedException {
        //Get boardgame3's categories and play modes
        BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes = LiveDataTestUtil.getValue(boardGameViewModel.
                getBoardGameWithBgCategoriesAndPlayModes(TestData.BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_3.getBoardGame()));

        assertNull(boardGameWithBgCategoriesAndPlayModes);
    }

    @Test
    public void getBoardGameWithBgCategoriesAndPlayModeWhenInserted() throws InterruptedException {
        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        playModeDao.insertAll(TestData.PLAY_MODES.toArray(new PlayMode[TestData.PLAY_MODES.size()]));

        //Get boardgame3's categories and play modes
        BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes = LiveDataTestUtil.getValue(boardGameViewModel.
                getBoardGameWithBgCategoriesAndPlayModes(TestData.BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_3.getBoardGame()));

        assertNotNull(boardGameWithBgCategoriesAndPlayModes);
        assertThat(boardGameWithBgCategoriesAndPlayModes, is(TestData.BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_3));
    }

    @Test
    public void addBoardGameWithBgCategoriesAndPlayModes() throws InterruptedException {
        //Precondition for this is that bg categories to be assigned to board games already exist in the database.
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        List<BoardGameWithBgCategoriesAndPlayModes> boardGamesWithBgCategoriesAndPlayModes = LiveDataTestUtil.
                getValue(boardGameViewModel.getAllBoardGamesWithBgCategoriesAndPlayModes());
        assertTrue(boardGamesWithBgCategoriesAndPlayModes.isEmpty());

        boardGameViewModel.addBoardGameWithBgCategoriesAndPlayModes(TestData.BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_1);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        boardGamesWithBgCategoriesAndPlayModes = LiveDataTestUtil.getValue(boardGameViewModel.getAllBoardGamesWithBgCategoriesAndPlayModes());

        assertThat(boardGamesWithBgCategoriesAndPlayModes.size(), is(1));
        assertThat(boardGamesWithBgCategoriesAndPlayModes.get(0), is(TestData.BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_1));

        //Test board game added to database.
        testBoardGameAddedToDb(boardGamesWithBgCategoriesAndPlayModes.get(0).getBoardGame());

        //Test assigned categories added to database. 1 entry per category associated with the board game.
        testAssignedCategoriesAddedToDb(boardGamesWithBgCategoriesAndPlayModes.get(0).getBoardGameWithBgCategories());

        //Test play modes added to database. 1 entry per play mode associated with the board game.
        testPlayModesAddedToDb(boardGamesWithBgCategoriesAndPlayModes.get(0).getPlayModes());
    }

    @Test
    public void editBoardGameWithBgCategoriesAndPlayModes() throws InterruptedException {
        //Precondition for this is that bg categories to be assigned to board games already exist in the database.
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        boardGameViewModel.addBoardGameWithBgCategoriesAndPlayModes(TestData.BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_1);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        BoardGameWithBgCategoriesAndPlayModes originalBoardGameWithBgCategoriesAndPlayModes = LiveDataTestUtil.getValue(
                boardGameViewModel.getBoardGameWithBgCategoriesAndPlayModes(TestData.BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_1.getBoardGame()));

        BoardGameWithBgCategoriesAndPlayModes editedBoardGameWithBgCategoriesAndPlayModes =
                createEditedBoardGameWithBgCategoriesAndPlayModes(originalBoardGameWithBgCategoriesAndPlayModes);

        boardGameViewModel.editBoardGameWithBgCategoriesAndPlayModes(originalBoardGameWithBgCategoriesAndPlayModes, editedBoardGameWithBgCategoriesAndPlayModes);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        //Test BoardGame dao has been updated.
        BoardGame originalBg = originalBoardGameWithBgCategoriesAndPlayModes.getBoardGame();
        BoardGame editedBg = editedBoardGameWithBgCategoriesAndPlayModes.getBoardGame();
        testBoardGameUpdatedInDb(originalBg, editedBg);

        // Test BgCategory dao has been updated.
        BoardGameWithBgCategories originalBgWithBgCats = originalBoardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories();
        BoardGameWithBgCategories editedBgWithBgCats = editedBoardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories();
        testAssignedCategoriesUpdatedInDb(originalBgWithBgCats, editedBgWithBgCats);

        //Test PlayMode dao has been updated.
        List<PlayMode> originalPlayModes = originalBoardGameWithBgCategoriesAndPlayModes.getPlayModes();
        List<PlayMode> editedPlayModes = editedBoardGameWithBgCategoriesAndPlayModes.getPlayModes();
        testPlayModesUpdatedInDb(originalPlayModes, editedPlayModes);
    }

    @Test
    public void deleteBoardGame() throws InterruptedException {
        boardGameDao.insert(TestData.BOARD_GAME_1);
        TimeUnit.MILLISECONDS.sleep(100);

        BoardGame boardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataById(TestData.BOARD_GAME_1.getId()));
        assertThat(boardGame, is(TestData.BOARD_GAME_1));

        boardGameViewModel.deleteBoardGame(TestData.BOARD_GAME_1);
        TimeUnit.MILLISECONDS.sleep(100);

        boardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataById(TestData.BOARD_GAME_1.getId()));
        assertNull(boardGame);
    }

    @Test
    public void deleteBoardGameWithBgCategoriesAndPlayModes() throws InterruptedException {
        //Precondition for this is that bg categories to be assigned to board games already exist in the database.
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        boardGameViewModel.addBoardGameWithBgCategoriesAndPlayModes(TestData.BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_1);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        List<BoardGameWithBgCategoriesAndPlayModes> boardGamesWithBgCategoriesAndPlayModes = LiveDataTestUtil.
                getValue(boardGameViewModel.getAllBoardGamesWithBgCategoriesAndPlayModes());

        assertThat(boardGamesWithBgCategoriesAndPlayModes.size(), is(1));
        assertThat(boardGamesWithBgCategoriesAndPlayModes.get(0), is(TestData.BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_1));

        //Test board game added to database.
        testBoardGameAddedToDb(boardGamesWithBgCategoriesAndPlayModes.get(0).getBoardGame());

        //Test assigned categories added to database. 1 entry per category associated with the board game.
        testAssignedCategoriesAddedToDb(boardGamesWithBgCategoriesAndPlayModes.get(0).getBoardGameWithBgCategories());

        //Test play modes added to database. 1 entry per play mode associated with the board game.
        testPlayModesAddedToDb(boardGamesWithBgCategoriesAndPlayModes.get(0).getPlayModes());

        boardGameDao.delete(boardGamesWithBgCategoriesAndPlayModes.get(0).getBoardGame());
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        //Test board game deleted from database.
        BoardGame boardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataById(TestData.BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_1.getBoardGame().getId()));
        assertNull(boardGame);

        //Test assigned categories removed from database.
        List<AssignedCategory> assignedCategories = LiveDataTestUtil.getValue(assignedCategoryDao.getAll());
        assertTrue(assignedCategories.isEmpty());

        //Test play modes removed from database.
        List<PlayMode> playModes = LiveDataTestUtil.getValue(playModeDao.getAll());
        assertTrue(playModes.isEmpty());
    }

    private BoardGame createEditedBoardGame(BoardGame originalBoardGame) {
        BoardGame editedBoardGame = new BoardGame(originalBoardGame);

        String newNickname = "Changed";
        int newDifficulty = 5;
        int newMinPlayers = 8;
        int newMaxPlayers = 16;
        BoardGame.TeamOption newTeamOption = BoardGame.TeamOption.TEAMS_AND_SOLOS_ALLOWED;
        String newDescription = "Changed";
        String newHouseRules = "Changed";
        String newNotes = "Changed";
        String newImgFilePath = "Changed";

        editedBoardGame.setBgName(newNickname);
        editedBoardGame.setDifficulty(newDifficulty);
        editedBoardGame.setMinPlayers(newMinPlayers);
        editedBoardGame.setMaxPlayers(newMaxPlayers);
        editedBoardGame.setTeamOptions(newTeamOption);
        editedBoardGame.setDescription(newDescription);
        editedBoardGame.setHouseRules(newHouseRules);
        editedBoardGame.setNotes(newNotes);
        editedBoardGame.setImgFilePath(newImgFilePath);

        return editedBoardGame;
    }

    private BoardGameWithBgCategoriesAndPlayModes createEditedBoardGameWithBgCategoriesAndPlayModes(
            BoardGameWithBgCategoriesAndPlayModes original) {
        BoardGame originalBoardGame = original.getBoardGame();
        BoardGame editedBoardGame = createEditedBoardGame(originalBoardGame);

        List<BgCategory> editedBgCategories = new ArrayList<>();
        editedBgCategories.add(TestData.BG_CATEGORY_2);

        List<PlayMode> editedPlayModes = new ArrayList<>();
        PlayMode playMode = new PlayMode(editedBoardGame.getId(), PlayMode.PlayModeEnum.SOLITAIRE);

        BoardGameWithBgCategories bgWithBgCategories = new BoardGameWithBgCategories(editedBoardGame, editedBgCategories);

        return new BoardGameWithBgCategoriesAndPlayModes(bgWithBgCategories, editedPlayModes);
    }

    private void testBoardGameAddedToDb(BoardGame boardGame) throws InterruptedException {
        //Test board game added to database.
        BoardGame dbBoardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataById(boardGame.getId()));
        assertNotNull(dbBoardGame);
        assertThat(dbBoardGame, is(boardGame));
    }

    private void testBoardGameUpdatedInDb(BoardGame original, BoardGame edited) throws  InterruptedException {
        BoardGame dbBoardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataById(edited.getId()));
        assertThat(dbBoardGame.getId(), is(original.getId()));
        assertThat(dbBoardGame, is(not(original)));
        assertThat(dbBoardGame, is(edited));
    }

    private void testAssignedCategoriesUpdatedInDb(BoardGameWithBgCategories original, BoardGameWithBgCategories edited) throws InterruptedException {
        int bgId = edited.getBoardGame().getId();
        List<AssignedCategory> assignedCategories = LiveDataTestUtil.getValue(assignedCategoryDao.getAll());

        List<BgCategory> originalBgCategories = original.getBgCategories();
        for (BgCategory bgCategory : originalBgCategories) {
            AssignedCategory assignedCategory = new AssignedCategory(bgId, bgCategory.getId());
            assertFalse(assignedCategories.contains(assignedCategory));
        }

        List<BgCategory> editedBgCategories = edited.getBgCategories();
        for (BgCategory bgCategory : editedBgCategories) {
            AssignedCategory assignedCategory = new AssignedCategory(bgId, bgCategory.getId());
            assertTrue(assignedCategories.contains(assignedCategory));
        }
    }

    private void testAssignedCategoriesAddedToDb(BoardGameWithBgCategories boardGameWithBgCategories) throws InterruptedException {
        //Test assigned categories added to database. 1 entry per category associated with the board game.
        List<AssignedCategory> dbAssignedCategories = LiveDataTestUtil.getValue(assignedCategoryDao.getAll());
        assertThat(dbAssignedCategories.size(), is(boardGameWithBgCategories.getBgCategories().size()));

        List<Integer> categoryIds = new ArrayList<>();
        for (BgCategory bgCategory : boardGameWithBgCategories.getBgCategories()) {
            categoryIds.add(bgCategory.getId());
        }

        for(AssignedCategory assignedCategory : dbAssignedCategories) {
            assertThat(assignedCategory.getBgId(), is(boardGameWithBgCategories.getBoardGame().getId()));
            assertTrue(categoryIds.contains(assignedCategory.getCategoryId()));
        }
    }

    private void testPlayModesAddedToDb(List<PlayMode> playModes) throws InterruptedException{
        List<PlayMode> dbPlayModes = LiveDataTestUtil.getValue(playModeDao.getAll());
        assertThat(dbPlayModes.size(), is(playModes.size()));

        for(PlayMode playMode : playModes) {
            assertTrue(dbPlayModes.contains(playMode));
        }
    }

    private void testPlayModesUpdatedInDb(List<PlayMode> original, List<PlayMode> edited) throws InterruptedException {
        List<PlayMode> dbPlayModes = LiveDataTestUtil.getValue(playModeDao.getAll());
        assertThat(dbPlayModes.size(), is(edited.size()));

        for (PlayMode playMode : dbPlayModes) {
            assertFalse(original.contains(playMode));
            assertTrue(edited.contains(playMode));
        }
    }
}
