package com.floatingpanda.scoreboard.db;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGameDao;
import com.floatingpanda.scoreboard.data.GameRecord;
import com.floatingpanda.scoreboard.data.GameRecordDao;
import com.floatingpanda.scoreboard.data.Group;
import com.floatingpanda.scoreboard.data.GroupDao;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class GameRecordDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private GameRecordDao gameRecordDao;
    private GroupDao groupDao;
    private BoardGameDao boardGameDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        gameRecordDao = db.gameRecordDao();
        boardGameDao = db.boardGameDao();
        groupDao = db.groupDao();

        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getGameRecordsWhenNoneInserted() throws InterruptedException {
        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());

        assertTrue(gameRecords.isEmpty());
    }

    @Test
    public void getGameRecordsWhenAllInserted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());

        assertThat(gameRecords.size(), is(TestData.GAME_RECORDS.size()));
    }

    @Test
    public void getGameRecordByGameRecordIdWhenNoneInserted() throws InterruptedException {
        GameRecord gameRecord = LiveDataTestUtil.getValue(gameRecordDao.findLiveDataGameRecordByRecordId(TestData.GAME_RECORD_2.getId()));

        assertNull(gameRecord);
    }

    @Test
    public void getGameRecordByGameRecordIdWhenAllInserted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        GameRecord gameRecord = LiveDataTestUtil.getValue(gameRecordDao.findLiveDataGameRecordByRecordId(TestData.GAME_RECORD_2.getId()));

        assertNotNull(gameRecord);
        assertThat(gameRecord, is(TestData.GAME_RECORD_2));
    }

    @Test
    public void getGameRecordsByGroupIdWhenNoneInserted() throws InterruptedException {
        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.findLiveDataGameRecordsByGroupId(TestData.GAME_RECORD_1.getGroupId()));

        assertTrue(gameRecords.isEmpty());
    }

    @Test
    public void getGameRecordsByGroupIdWhenAllInserted() throws InterruptedException {
        // The group that played game record 1 also played game records 2 and 3.
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.findLiveDataGameRecordsByGroupId(TestData.GAME_RECORD_1.getGroupId()));

        assertThat(gameRecords.size(), is(3));
        assertTrue(gameRecords.contains(TestData.GAME_RECORD_1));
        assertTrue(gameRecords.contains(TestData.GAME_RECORD_2));
        assertTrue(gameRecords.contains(TestData.GAME_RECORD_3));
    }

    @Test
    public void getGameRecordsByBoardGameNameWhenNoneInserted() throws InterruptedException {
        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.findLiveDataGameRecordsByBoardGameName(TestData.GAME_RECORD_1.getBoardGameName()));

        assertTrue(gameRecords.isEmpty());
    }

    @Test
    public void getGameRecordsByBoardGameNameWhenAllInserted() throws InterruptedException {
        // The board game played in game record 1 was also played in game record 2.
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.findLiveDataGameRecordsByBoardGameName(TestData.GAME_RECORD_1.getBoardGameName()));

        assertThat(gameRecords.size(), is(2));
        assertTrue(gameRecords.contains(TestData.GAME_RECORD_1));
        assertTrue(gameRecords.contains(TestData.GAME_RECORD_2));
    }

    @Test
    public void getGameRecordsWhenSpecificGameRecordInserted() throws InterruptedException {
        gameRecordDao.insert(TestData.GAME_RECORD_1);
        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());

        assertThat(gameRecords.size(), is(1));
        assertThat(gameRecords.get(0), is(TestData.GAME_RECORD_1));
    }

    @Test
    public void getGameRecordsWhenAllInsertedAndDeleted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());

        assertThat(gameRecords.size(), is(TestData.GAME_RECORDS.size()));

        gameRecordDao.deleteAll();
        TimeUnit.MILLISECONDS.sleep(100);

        gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());

        assertTrue(gameRecords.isEmpty());
    }

    @Test
    public void getGameRecordsWhenAllInsertedAndOneDeleted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());

        assertThat(gameRecords.size(), is(TestData.GAME_RECORDS.size()));

        gameRecordDao.delete(TestData.GAME_RECORD_2);
        TimeUnit.MILLISECONDS.sleep(100);

        gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());

        assertThat(gameRecords.size(), is(TestData.GAME_RECORDS.size() - 1));
        assertFalse(gameRecords.contains(TestData.GAME_RECORD_2));
    }

    @Test
    public void getGameRecordsWhenAllInsertedAndPlayedBoardGameDeleted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());

        assertThat(gameRecords.size(), is(TestData.GAME_RECORDS.size()));

        GameRecord gameRecord = LiveDataTestUtil.getValue(gameRecordDao.findLiveDataGameRecordByRecordId(TestData.GAME_RECORD_1.getId()));

        assertThat(gameRecord, is(TestData.GAME_RECORD_1));
        assertThat(gameRecord.getBoardGameName(), is(TestData.GAME_RECORD_1.getBoardGameName()));
        assertThat(gameRecord.getBoardGameName(), is(TestData.BOARD_GAME_1.getBgName()));

        boardGameDao.delete(TestData.BOARD_GAME_1);
        TimeUnit.MILLISECONDS.sleep(100);

        List<BoardGame> boardGames = LiveDataTestUtil.getValue(boardGameDao.getAllLive());

        assertThat(boardGames.size(), is(TestData.BOARD_GAMES.size() - 1));
        assertFalse(boardGames.contains(TestData.BOARD_GAME_1));

        gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());

        assertThat(gameRecords.size(), is(TestData.GAME_RECORDS.size()));

        gameRecord = LiveDataTestUtil.getValue(gameRecordDao.findLiveDataGameRecordByRecordId(TestData.GAME_RECORD_1.getId()));

        assertThat(gameRecord.getId(), is(TestData.GAME_RECORD_1.getId()));
        assertThat(gameRecord.getGroupId(), is(TestData.GAME_RECORD_1.getGroupId()));
        assertThat(gameRecord.getDifficulty(), is(TestData.GAME_RECORD_1.getDifficulty()));
        assertThat(gameRecord.getDate(), is(TestData.GAME_RECORD_1.getDate()));
        assertThat(gameRecord.getNoOfTeams(), is(TestData.GAME_RECORD_1.getNoOfTeams()));
        assertThat(gameRecord.getPlayModePlayed(), is(TestData.GAME_RECORD_1.getPlayModePlayed()));
        assertThat(gameRecord.getTeamsAllowed(), is(TestData.GAME_RECORD_1.getTeamsAllowed()));
        assertThat(gameRecord.getBoardGameName(), is(not(TestData.GAME_RECORD_1.getBoardGameName())));
        assertNull(gameRecord.getBoardGameName());
    }

    @Test
    public void getGameRecordsWhenAllInsertedAndGroupPlayedInIsDeleted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());

        assertThat(gameRecords.size(), is(TestData.GAME_RECORDS.size()));

        GameRecord gameRecord = LiveDataTestUtil.getValue(gameRecordDao.findLiveDataGameRecordByRecordId(TestData.GAME_RECORD_1.getId()));

        assertThat(gameRecord, is(TestData.GAME_RECORD_1));
        assertThat(gameRecord.getBoardGameName(), is(TestData.GAME_RECORD_1.getBoardGameName()));
        assertThat(gameRecord.getGroupId(), is(TestData.GROUP_1.getId()));

        groupDao.delete(TestData.GROUP_1);
        TimeUnit.MILLISECONDS.sleep(100);

        List<Group> groups = LiveDataTestUtil.getValue(groupDao.getAll());

        assertThat(groups.size(), is(TestData.GROUPS.size() - 1));
        assertFalse(groups.contains(TestData.GROUP_1));

        // Group 1 was involved in 3 of the game records.
        gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());

        assertThat(gameRecords.size(), is(TestData.GAME_RECORDS.size() - 3));

        gameRecord = LiveDataTestUtil.getValue(gameRecordDao.findLiveDataGameRecordByRecordId(TestData.GAME_RECORD_1.getId()));

        assertNull(gameRecord);
    }
}
