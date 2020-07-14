package com.floatingpanda.scoreboard.db;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.daos.PlayerDao;
import com.floatingpanda.scoreboard.data.daos.PlayerSkillRatingChangeDao;
import com.floatingpanda.scoreboard.data.daos.PlayerTeamDao;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerSkillRatingChange;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PlayerSkillRatingChangeDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private GameRecordDao gameRecordDao;
    private GroupDao groupDao;
    private BoardGameDao boardGameDao;
    private PlayerTeamDao playerTeamDao;
    private PlayerDao playerDao;
    private MemberDao memberDao;
    private BgCategoryDao bgCategoryDao;
    private PlayerSkillRatingChangeDao playerSkillRatingChangeDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        gameRecordDao = db.gameRecordDao();
        boardGameDao = db.boardGameDao();
        groupDao = db.groupDao();
        playerTeamDao = db.playerTeamDao();
        playerDao = db.playerDao();
        memberDao = db.memberDao();
        bgCategoryDao = db.bgCategoryDao();
        playerSkillRatingChangeDao = db.playerSkillRatingChangeDao();

        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllWhenNoneInserted() throws InterruptedException {
        List<PlayerSkillRatingChange> playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());

        assertTrue(playerSkillRatingChanges.isEmpty());
    }

    @Test
    public void getAllWhenAllInserted() throws InterruptedException {
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));
        List<PlayerSkillRatingChange> playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());

        assertThat(playerSkillRatingChanges.size(), is(TestData.PLAYER_SKILL_RATING_CHANGES.size()));
    }

    @Test
    public void getPlayerSkillRatingChangeByPlayerSkillRatingChangeIdWhenNoneInserted() throws InterruptedException {
        PlayerSkillRatingChange playerSkillRatingChange = LiveDataTestUtil.getValue(
                playerSkillRatingChangeDao.getPlayerSkillRatingChangeByPlayerSkillRatingChangeId(TestData.PLAYER_SKILL_RATING_CHANGE_1.getId()));

        assertNull(playerSkillRatingChange);
    }

    @Test
    public void getPlayerSkillRatingChangeByPlayerSkillRatingChangeIdWhenAllInserted() throws InterruptedException {
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));
        PlayerSkillRatingChange playerSkillRatingChange = LiveDataTestUtil.getValue(
                playerSkillRatingChangeDao.getPlayerSkillRatingChangeByPlayerSkillRatingChangeId(TestData.PLAYER_SKILL_RATING_CHANGE_1.getId()));

        assertThat(playerSkillRatingChange, is(TestData.PLAYER_SKILL_RATING_CHANGE_1));
    }

    @Test
    public void getPlayerSkillRatingChangesByPlayerIdWhenNoneInserted() throws InterruptedException {
        List<PlayerSkillRatingChange> playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getPlayerSkillRatingChangesByPlayerId(TestData.PLAYER_1.getId()));

        assertTrue(playerSkillRatingChanges.isEmpty());
    }

    @Test
    public void getPlayerSkillRatingChangesByPlayerIdWhenAllInserted() throws InterruptedException {
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));
        List<PlayerSkillRatingChange> playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getPlayerSkillRatingChangesByPlayerId(TestData.PLAYER_1.getId()));

        assertThat(playerSkillRatingChanges.size(), is(2));

        for (PlayerSkillRatingChange playerSkillRatingChange : playerSkillRatingChanges) {
            if (playerSkillRatingChange.getId() == TestData.PLAYER_SKILL_RATING_CHANGE_1.getId()) {
                assertThat(playerSkillRatingChange, is(TestData.PLAYER_SKILL_RATING_CHANGE_1));
            } else if (playerSkillRatingChange.getId() == TestData.PLAYER_SKILL_RATING_CHANGE_2.getId()) {
                assertThat(playerSkillRatingChange, is(TestData.PLAYER_SKILL_RATING_CHANGE_2));
            } else {
                fail("Unknown player skill rating change id in batch for player 1's skill rating changes");
            }
        }
    }

    @Test
    public void getPlayerSkillRatingChangeByPlayerIdAndCategoryIdWhenNoneInserted() throws InterruptedException {
        int playerId = TestData.PLAYER_SKILL_RATING_CHANGE_1.getPlayerId();
        String categoryName = TestData.PLAYER_SKILL_RATING_CHANGE_1.getCategoryName();

        PlayerSkillRatingChange playerSkillRatingChange = LiveDataTestUtil.getValue(
                playerSkillRatingChangeDao.getPlayerSkillRatingChangeByPlayerIdAndCategoryName(playerId, categoryName));

        assertNull(playerSkillRatingChange);
    }

    @Test
    public void getPlayerSkillRatingChangeByPlayerIdAndCategoryIdWhenAllInserted() throws InterruptedException {
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));

        int playerId = TestData.PLAYER_SKILL_RATING_CHANGE_1.getPlayerId();
        String categoryName = TestData.PLAYER_SKILL_RATING_CHANGE_1.getCategoryName();

        PlayerSkillRatingChange playerSkillRatingChange = LiveDataTestUtil.getValue(
                playerSkillRatingChangeDao.getPlayerSkillRatingChangeByPlayerIdAndCategoryName(playerId, categoryName));

        assertThat(playerSkillRatingChange, is(TestData.PLAYER_SKILL_RATING_CHANGE_1));
    }

    @Test
    public void getPlayerSkillRatingsAfterInsertingOne() throws InterruptedException {
        playerSkillRatingChangeDao.insert(TestData.PLAYER_SKILL_RATING_CHANGE_1);
        List<PlayerSkillRatingChange> playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());

        assertThat(playerSkillRatingChanges.size(), is(1));
        assertThat(playerSkillRatingChanges.get(0), is(TestData.PLAYER_SKILL_RATING_CHANGE_1));
    }

    @Test
    public void getPlayerSkillRatingsAfterInsertingAllAndDeletingAll() throws InterruptedException {
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));
        List<PlayerSkillRatingChange> playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());

        assertThat(playerSkillRatingChanges.size(), is(TestData.PLAYER_SKILL_RATING_CHANGES.size()));

        playerSkillRatingChangeDao.deleteAll();
        TimeUnit.MILLISECONDS.sleep(100);

        playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());

        assertTrue(playerSkillRatingChanges.isEmpty());
    }

    @Test
    public void getPlayerSkillRatingsAfterInsertingAllAndDeletingOne() throws InterruptedException {
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));
        List<PlayerSkillRatingChange> playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());

        assertThat(playerSkillRatingChanges.size(), is(TestData.PLAYER_SKILL_RATING_CHANGES.size()));

        PlayerSkillRatingChange playerSkillRatingChange = LiveDataTestUtil.getValue(
                playerSkillRatingChangeDao.getPlayerSkillRatingChangeByPlayerSkillRatingChangeId(TestData.PLAYER_SKILL_RATING_CHANGE_1.getId()));

        assertThat(playerSkillRatingChange, is(TestData.PLAYER_SKILL_RATING_CHANGE_1));

        playerSkillRatingChangeDao.delete(TestData.PLAYER_SKILL_RATING_CHANGE_1);
        TimeUnit.MILLISECONDS.sleep(100);

        playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());

        assertThat(playerSkillRatingChanges.size(), is(TestData.PLAYER_SKILL_RATING_CHANGES.size() - 1));

        playerSkillRatingChange = LiveDataTestUtil.getValue(
                playerSkillRatingChangeDao.getPlayerSkillRatingChangeByPlayerSkillRatingChangeId(TestData.PLAYER_SKILL_RATING_CHANGE_1.getId()));

        assertNull(playerSkillRatingChange);
    }
}
