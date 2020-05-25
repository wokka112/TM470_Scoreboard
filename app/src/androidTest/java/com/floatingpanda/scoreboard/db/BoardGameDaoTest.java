package com.floatingpanda.scoreboard.db;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGameDao;
import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.data.MemberDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BoardGameDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private BoardGameDao boardGameDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        boardGameDao = db.boardGameDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getLiveBoardGamesWhenNoBoardGamesInserted() throws InterruptedException {
        List<BoardGame> boardGames = LiveDataTestUtil.getValue(boardGameDao.getAllLive());

        assertTrue(boardGames.isEmpty());
    }

    @Test
    public void getNonLiveBoardGamesWhenNoBoardGamesInserted() {
        List<BoardGame> boardGames = boardGameDao.getAllNonLive();

        assertTrue(boardGames.isEmpty());
    }

    @Test
    public void getLiveBoardGamesWhenBoardGamesInserted() throws InterruptedException {
        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));

        List<BoardGame> boardGames = LiveDataTestUtil.getValue(boardGameDao.getAllLive());

        assertFalse(boardGames.isEmpty());
        assertThat(boardGames.size(), is(TestData.BOARD_GAMES.size()));
    }

    @Test
    public void getNonLiveBoardGamesWhenBoardGamesInserted() {
        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));

        List<BoardGame> boardGames = boardGameDao.getAllNonLive();

        assertFalse(boardGames.isEmpty());
        assertThat(boardGames.size(), is(TestData.BOARD_GAMES.size()));
    }

    @Test
    public void getLiveBoardGameWhenSpecificBoardGameInserted() throws InterruptedException {
        boardGameDao.insert(TestData.BOARD_GAME_1);

        List<BoardGame> boardGames = LiveDataTestUtil.getValue(boardGameDao.getAllLive());

        assertFalse(boardGames.isEmpty());
        assertThat(boardGames.size(), is(1));
        assertThat(boardGames.get(0), is(TestData.BOARD_GAME_1));
    }

    @Test
    public void getNonLiveBoardGameWhenSpecificBoardGameInserted() {
        boardGameDao.insert(TestData.BOARD_GAME_1);

        List<BoardGame> boardGames = boardGameDao.getAllNonLive();

        assertFalse(boardGames.isEmpty());
        assertThat(boardGames.size(), is(1));
        assertThat(boardGames.get(0), is(TestData.BOARD_GAME_1));
    }

    @Test
    public void getLiveBoardGameById() throws InterruptedException {
        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));

        BoardGame boardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataById(TestData.BOARD_GAME_2.getId()));

        assertThat(boardGame, is(TestData.BOARD_GAME_2));
    }

    @Test
    public void getLiveBoardGameByName() throws InterruptedException {
        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));

        BoardGame boardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataByName(TestData.BOARD_GAME_2.getBgName()));

        assertThat(boardGame, is(TestData.BOARD_GAME_2));
    }

    @Test
    public void getNonLiveBoardGameByName() {
        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));

        BoardGame boardGame = boardGameDao.findNonLiveDataByName(TestData.BOARD_GAME_2.getBgName());

        assertThat(boardGame, is(TestData.BOARD_GAME_2));
    }

    @Test
    public void insertAndDeleteAllBoardGames() throws InterruptedException {
        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));

        List<BoardGame> boardGames = LiveDataTestUtil.getValue(boardGameDao.getAllLive());
        assertFalse(boardGames.isEmpty());

        boardGameDao.deleteAll();

        boardGames = LiveDataTestUtil.getValue(boardGameDao.getAllLive());
        assertTrue(boardGames.isEmpty());
    }

    @Test
    public void insertAllBoardGamesAndDeleteSpecificBoardGame() throws InterruptedException {
        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));

        List<BoardGame> boardGames = LiveDataTestUtil.getValue(boardGameDao.getAllLive());
        assertFalse(boardGames.isEmpty());
        assertThat(boardGames.size(), is(TestData.BOARD_GAMES.size()));

        BoardGame boardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataById(TestData.BOARD_GAME_2.getId()));
        assertTrue(boardGame != null);

        boardGameDao.delete(TestData.BOARD_GAME_2);

        boardGames = LiveDataTestUtil.getValue(boardGameDao.getAllLive());
        assertThat(boardGames.size(), is(TestData.BOARD_GAMES.size() - 1));

        boardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataById(TestData.BOARD_GAME_2.getId()));
        assertNull(boardGame);
    }

    @Test
    public void insertAllBoardGamesAndUpdateSpecificBoardGame() throws InterruptedException {
        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));

        BoardGame boardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataById(TestData.BOARD_GAME_2.getId()));
        assertThat(boardGame, is(TestData.BOARD_GAME_2));

        String newName = "Changed Name";
        int newDifficulty = 1;
        int newMinPlayers = 8;
        int newMaxPlayers = 12;
        BoardGame.TeamOption newTeamOption = BoardGame.TeamOption.TEAMS_ONLY;
        String newDescription = "Changed Description";
        String newHouseRules = "Changed House Rules";
        String newNotes = "Changed Notes";
        String newImgFilePath = "Changed Img File Path";

        boardGame.setBgName(newName);
        boardGame.setDifficulty(newDifficulty);
        boardGame.setMinPlayers(newMinPlayers);
        boardGame.setMaxPlayers(newMaxPlayers);
        boardGame.setTeamOptions(newTeamOption);
        boardGame.setDescription(newDescription);
        boardGame.setHouseRules(newHouseRules);
        boardGame.setNotes(newNotes);
        boardGame.setImgFilePath(newImgFilePath);

        boardGameDao.update(boardGame);

        //Should no longer exist in database, hence should return null.
        BoardGame oldBoardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataByName(TestData.BOARD_GAME_2.getBgName()));
        assertNull(oldBoardGame);

        BoardGame updatedBoardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataByName(boardGame.getBgName()));
        assertThat(updatedBoardGame, is(boardGame));
        assertThat(updatedBoardGame, is(not(TestData.BOARD_GAME_2)));

        assertThat(updatedBoardGame.getId(), is(boardGame.getId()));
        assertThat(updatedBoardGame.getBgName(), is(boardGame.getBgName()));
        assertThat(updatedBoardGame.getDifficulty(), is(boardGame.getDifficulty()));
        assertThat(updatedBoardGame.getMinPlayers(), is(boardGame.getMinPlayers()));
        assertThat(updatedBoardGame.getMaxPlayers(), is(boardGame.getMaxPlayers()));
        assertThat(updatedBoardGame.getTeamOptions(), is(boardGame.getTeamOptions()));
        assertThat(updatedBoardGame.getDescription(), is(boardGame.getDescription()));
        assertThat(updatedBoardGame.getHouseRules(), is(boardGame.getHouseRules()));
        assertThat(updatedBoardGame.getNotes(), is(boardGame.getNotes()));
        assertThat(updatedBoardGame.getImgFilePath(), is(boardGame.getImgFilePath()));

        assertThat(updatedBoardGame.getId(), is(TestData.BOARD_GAME_2.getId()));
        assertThat(updatedBoardGame.getBgName(), is(not(TestData.BOARD_GAME_2.getBgName())));
        assertThat(updatedBoardGame.getDifficulty(), is(not(TestData.BOARD_GAME_2.getDifficulty())));
        assertThat(updatedBoardGame.getMinPlayers(), is(not(TestData.BOARD_GAME_2.getMinPlayers())));
        assertThat(updatedBoardGame.getMaxPlayers(), is(not(TestData.BOARD_GAME_2.getMaxPlayers())));
        assertThat(updatedBoardGame.getTeamOptions(), is(not(TestData.BOARD_GAME_2.getTeamOptions())));
        assertThat(updatedBoardGame.getDescription(), is(not(TestData.BOARD_GAME_2.getDescription())));
        assertThat(updatedBoardGame.getHouseRules(), is(not(TestData.BOARD_GAME_2.getHouseRules())));
        assertThat(updatedBoardGame.getNotes(), is(not(TestData.BOARD_GAME_2.getNotes())));
        assertThat(updatedBoardGame.getImgFilePath(), is(not(TestData.BOARD_GAME_2.getImgFilePath())));

        assertThat(updatedBoardGame.getBgName(), is(newName));
        assertThat(updatedBoardGame.getDifficulty(), is(newDifficulty));
        assertThat(updatedBoardGame.getMinPlayers(), is(newMinPlayers));
        assertThat(updatedBoardGame.getMaxPlayers(), is(newMaxPlayers));
        assertThat(updatedBoardGame.getTeamOptions(), is(newTeamOption));
        assertThat(updatedBoardGame.getDescription(), is(newDescription));
        assertThat(updatedBoardGame.getHouseRules(), is(newHouseRules));
        assertThat(updatedBoardGame.getNotes(), is(newNotes));
        assertThat(updatedBoardGame.getImgFilePath(), is(newImgFilePath));
    }

    //TODO write tests for transactions once board game is updated in regards to play modes and associated categories lists.
}
