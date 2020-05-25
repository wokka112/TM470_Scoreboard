package com.floatingpanda.scoreboard.db;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.AssignedCategory;
import com.floatingpanda.scoreboard.data.AssignedCategoryDao;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BgCategoryDao;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGameDao;
import com.floatingpanda.scoreboard.data.PlayMode;
import com.floatingpanda.scoreboard.data.PlayModeDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PlayModeDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private PlayModeDao playModeDao;
    private BoardGameDao boardGameDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        playModeDao = db.playModeDao();

        // Needed because play modes in the db use foreign keys from board game tables.
        boardGameDao = db.boardGameDao();
        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getPlayModesWhenNoPlayModesInserted() throws InterruptedException {
        List<PlayMode> playModes = LiveDataTestUtil.getValue(playModeDao.getAll());

        assertTrue(playModes.isEmpty());
    }

    @Test
    public void getPlayModesWhenPlayModesInserted() throws InterruptedException {
        playModeDao.insertAll(TestData.PLAY_MODES.toArray(new PlayMode[TestData.PLAY_MODES.size()]));

        List<PlayMode> playModes = LiveDataTestUtil.getValue(playModeDao.getAll());

        assertFalse(playModes.isEmpty());
        assertThat(playModes.size(), is(TestData.PLAY_MODES.size()));
    }

    @Test
    public void getPlayModesWhenSpecificPlayModeInserted() throws InterruptedException {
        playModeDao.insert(TestData.PLAY_MODE_1);

        List<PlayMode> playModes = LiveDataTestUtil.getValue(playModeDao.getAll());

        assertFalse(playModes.isEmpty());
        assertThat(playModes.size(), is(1));
        assertThat(playModes.get(0), is(TestData.PLAY_MODE_1));
    }

    @Test
    public void getSingleNonLivePlayModeByBoardGameId() {
        playModeDao.insertAll(TestData.PLAY_MODES.toArray(new PlayMode[TestData.PLAY_MODES.size()]));

        List<PlayMode> playModes = playModeDao.findNonLiveDataByBgId(TestData.PLAY_MODE_2.getBgId());

        assertThat(playModes.get(0), is(TestData.PLAY_MODE_2));
    }

    @Test
    public void getSeveralNonLivePlayModesByBoardGameId() {
        playModeDao.insertAll(TestData.PLAY_MODES.toArray(new PlayMode[TestData.PLAY_MODES.size()]));

        //Play mode 3's bg id should be associated with 2 play mode enums.
        List<PlayMode> playModes = playModeDao.findNonLiveDataByBgId(TestData.PLAY_MODE_3.getBgId());
        assertThat(playModes.size(), is(2));
        assertTrue(playModes.contains(TestData.PLAY_MODE_3));
        assertTrue(playModes.contains(TestData.PLAY_MODE_4));

        List<PlayMode.PlayModeEnum> playModeEnums = new ArrayList<>();
        for (PlayMode playMode : playModes) {
            playModeEnums.add(playMode.getPlayModeEnum());
        }

        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.COMPETITIVE));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.SOLITAIRE));
    }

    @Test
    public void getSeveralNonLivePlayModesByPlayModeEnum() {
        playModeDao.insertAll(TestData.PLAY_MODES.toArray(new PlayMode[TestData.PLAY_MODES.size()]));

        //Play mode 4's play mode enum should be associated with 2 play modes.
        List<PlayMode> playModes = playModeDao.findNonLiveDataByPlayModeEnum(TestData.PLAY_MODE_4.getPlayModeEnum());
        assertThat(playModes.size(), is(2));
        assertTrue(playModes.contains(TestData.PLAY_MODE_1));
        assertTrue(playModes.contains(TestData.PLAY_MODE_4));

        List<Integer> bgIds = new ArrayList<>();
        for (PlayMode playMode : playModes) {
            bgIds.add(playMode.getBgId());
        }

        assertTrue(bgIds.contains(TestData.BOARD_GAME_1.getId()));
        assertTrue(bgIds.contains(TestData.BOARD_GAME_3.getId()));
    }

    @Test
    public void insertAndDeleteAllPlayModes() throws InterruptedException {
        playModeDao.insertAll(TestData.PLAY_MODES.toArray(new PlayMode[TestData.PLAY_MODES.size()]));

        List<PlayMode> playModes = LiveDataTestUtil.getValue(playModeDao.getAll());
        assertFalse(playModes.isEmpty());
        assertThat(playModes.size(), is(TestData.PLAY_MODES.size()));

        playModeDao.deleteAll();

        playModes = LiveDataTestUtil.getValue(playModeDao.getAll());
        assertTrue(playModes.isEmpty());
    }

    @Test
    public void insertAllPlayModesAndDeleteSpecificPlayMode() throws InterruptedException {
        playModeDao.insertAll(TestData.PLAY_MODES.toArray(new PlayMode[TestData.PLAY_MODES.size()]));

        List<PlayMode> allPlayModes = LiveDataTestUtil.getValue(playModeDao.getAll());
        assertFalse(allPlayModes.isEmpty());
        assertThat(allPlayModes.size(), is(TestData.PLAY_MODES.size()));

        //Use play mode 4's bg id so we end up with 2 elements in the list - play mode 3 and play mode 4.
        List<PlayMode> specificPlayModes = playModeDao.findNonLiveDataByBgId(TestData.PLAY_MODE_4.getBgId());
        assertFalse(specificPlayModes.isEmpty());
        assertThat(specificPlayModes.size(), is(2));
        assertTrue(specificPlayModes.contains(TestData.PLAY_MODE_4));

        //This should delete just one entry in the specific list - play mode 4 - leaving the other in it - play mode 1.
        playModeDao.delete(TestData.PLAY_MODE_4);

        allPlayModes = LiveDataTestUtil.getValue(playModeDao.getAll());
        assertFalse(allPlayModes.isEmpty());
        assertThat(allPlayModes.size(), is(TestData.PLAY_MODES.size() - 1));

        specificPlayModes = playModeDao.findNonLiveDataByBgId(TestData.PLAY_MODE_4.getBgId());
        assertFalse(specificPlayModes.isEmpty());
        assertThat(specificPlayModes.size(), is(1));
        assertFalse(specificPlayModes.contains(TestData.PLAY_MODE_4));
    }

    @Test
    public void insertAllPlayModesAndDeleteAnAssociatedBoardGame() throws InterruptedException {
        playModeDao.insertAll(TestData.PLAY_MODES.toArray(new PlayMode[TestData.PLAY_MODES.size()]));

        //Play mode 1 is associated with board game 1.
        List<PlayMode> playModes = playModeDao.findNonLiveDataByBgId(TestData.PLAY_MODE_1.getBgId());
        assertThat(playModes.size(), is(1));
        assertThat(playModes.get(0), is(TestData.PLAY_MODE_1));
        assertThat(playModes.get(0).getBgId(), is(TestData.BOARD_GAME_1.getId()));

        BoardGame boardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataById(TestData.PLAY_MODE_1.getBgId()));
        assertTrue(boardGame != null);
        assertThat(boardGame, is(TestData.BOARD_GAME_1));

        //This deletion should cascade, resulting in associated play modes being deleted.
        boardGameDao.delete(TestData.BOARD_GAME_1);

        boardGame = LiveDataTestUtil.getValue(boardGameDao.findLiveDataById(TestData.ASSIGNED_CATEGORY_1.getBgId()));
        assertNull(boardGame);

        playModes = playModeDao.findNonLiveDataByBgId(TestData.PLAY_MODE_1.getBgId());
        assertTrue(playModes.isEmpty());
    }
}
