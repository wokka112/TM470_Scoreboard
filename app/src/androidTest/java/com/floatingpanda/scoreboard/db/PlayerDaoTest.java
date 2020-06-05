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
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGameDao;
import com.floatingpanda.scoreboard.data.GameRecord;
import com.floatingpanda.scoreboard.data.GameRecordDao;
import com.floatingpanda.scoreboard.data.Group;
import com.floatingpanda.scoreboard.data.GroupDao;
import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.data.MemberDao;
import com.floatingpanda.scoreboard.data.Player;
import com.floatingpanda.scoreboard.data.PlayerDao;
import com.floatingpanda.scoreboard.data.PlayerTeam;
import com.floatingpanda.scoreboard.data.PlayerTeamDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PlayerDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private GameRecordDao gameRecordDao;
    private GroupDao groupDao;
    private BoardGameDao boardGameDao;
    private PlayerTeamDao playerTeamDao;
    private PlayerDao playerDao;
    private MemberDao memberDao;

    @Before
    public void createDb() throws InterruptedException {
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

        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllPlayersWhenNoneInserted() throws InterruptedException {
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertTrue(players.isEmpty());
    }

    @Test
    public void getAllPlayersWhenAllInserted() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size()));
    }

    @Test
    public void getPlayerByPlayerIdWhenNoneInserted() throws InterruptedException {
        Player player = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayerByPlayerId(TestData.PLAYER_2.getId()));

        assertNull(player);
    }

    @Test
    public void getPlayerByPlayerIdWhenAllInserted() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        Player player = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayerByPlayerId(TestData.PLAYER_2.getId()));

        assertNotNull(player);
        assertThat(player, is(TestData.PLAYER_2));
    }

    @Test
    public void getPlayersByTeamNumberAndRecordIdWhenNoneInserted() throws InterruptedException {
        int recordId = TestData.PLAYER_2.getRecordId();
        int teamNo = TestData.PLAYER_2.getTeamNumber();
        List<Player> players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByTeamNumberAndRecordId(teamNo, recordId));

        assertTrue(players.isEmpty());
    }

    @Test
    public void getPlayersByTeamNumberAndRecordIdWhenAllInserted() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        // The record id and team number for player 2 are shared by one other player - player 3.
        int recordId = TestData.PLAYER_2.getRecordId();
        int teamNo = TestData.PLAYER_2.getTeamNumber();
        List<Player> players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByTeamNumberAndRecordId(teamNo, recordId));

        assertThat(players.size(), is(2));
        assertTrue(players.contains(TestData.PLAYER_2));
        assertTrue(players.contains(TestData.PLAYER_3));
    }

    @Test
    public void getPlayersByRecordIdWhenNoneInserted() throws InterruptedException {
        List<Player> players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByRecordId(TestData.PLAYER_1.getRecordId()));

        assertTrue(players.isEmpty());
    }

    @Test
    public void getPlayersByRecordIdWhenAllInserted() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        //The record id associated with player 1 is shared by 2 other players - players 2 and 3.
        List<Player> players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByRecordId(TestData.PLAYER_1.getRecordId()));

        assertThat(players.size(), is(3));
        assertTrue(players.contains(TestData.PLAYER_1));
        assertTrue(players.contains(TestData.PLAYER_2));
        assertTrue(players.contains(TestData.PLAYER_3));
    }

    @Test
    public void getPlayersByMemberNicknameWhenNoneInserted() throws InterruptedException {
        List<Player> players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByMemberNickname(TestData.PLAYER_1.getMemberNickname()));

        assertTrue(players.isEmpty());
    }

    @Test
    public void getPlayersByMemberNicknameWhenAllInserted() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        // The member nickname associated with player 1 is shared by 1 other player - player 5.
        List<Player> players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByMemberNickname(TestData.PLAYER_1.getMemberNickname()));

        assertThat(players.size(), is(2));
        assertTrue(players.contains(TestData.PLAYER_1));
        assertTrue(players.contains(TestData.PLAYER_5));
    }

    @Test
    public void getPlayersWhenOnlyOneInserted() throws InterruptedException {
        playerDao.insert(TestData.PLAYER_1);

        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is (1));
        assertThat(players.get(0), is(TestData.PLAYER_1));
    }

    @Test
    public void getAllPlayersAfterInsertingAndDeletingAll() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size()));

        playerDao.deleteAll();
        TimeUnit.MILLISECONDS.sleep(100);

        players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertTrue(players.isEmpty());
    }

    @Test
    public void getAllPlayersAfterInsertingAllAndDeletingOne() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size()));
        assertTrue(players.contains(TestData.PLAYER_2));

        playerDao.delete(TestData.PLAYER_2);
        TimeUnit.MILLISECONDS.sleep(100);

        players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size() - 1));
        assertFalse(players.contains(TestData.PLAYER_2));
    }

    @Test
    public void getAllPlayersAfterInsertingAllAndDeletingAnAssociatedMember() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size()));

        Player player = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayerByPlayerId(TestData.PLAYER_3.getId()));

        assertNotNull(player);
        assertThat(player, is(TestData.PLAYER_3));
        assertThat(player.getMemberNickname(), is(TestData.PLAYER_3.getMemberNickname()));
        assertThat(player.getMemberNickname(), is(TestData.MEMBER_3.getNickname()));

        memberDao.delete(TestData.MEMBER_3);
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> members = LiveDataTestUtil.getValue(memberDao.getAllLive());

        assertThat(members.size(), is(TestData.MEMBERS.size() - 1));
        assertFalse(members.contains(TestData.MEMBER_3));

        players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size()));

        player = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayerByPlayerId(TestData.PLAYER_3.getId()));

        assertNotNull(player);
        assertThat(player, is(TestData.PLAYER_3));
        assertThat(player.getId(), is(TestData.PLAYER_3.getId()));
        assertThat(player.getRecordId(), is(TestData.PLAYER_3.getRecordId()));
        assertThat(player.getTeamNumber(), is(TestData.PLAYER_3.getTeamNumber()));
        assertThat(player.getMemberNickname(), is(not(TestData.PLAYER_3.getMemberNickname())));
        assertNull(player.getMemberNickname());
    }

    @Test
    public void getAllPlayersAfterInsertingAllAndDeletingAnAssociatedGameRecord() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size()));
        assertTrue(players.contains(TestData.PLAYER_1));

        // Game record 1 is associated with 3 players - players 1, 2 and 3
        List<Player> gameRecord1Players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByRecordId(TestData.GAME_RECORD_1.getId()));

        assertThat(gameRecord1Players.size(), is(3));
        assertTrue(gameRecord1Players.contains(TestData.PLAYER_1));

        gameRecordDao.delete(TestData.GAME_RECORD_1);
        TimeUnit.MILLISECONDS.sleep(100);

        players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size() - 3));
        assertFalse(players.contains(TestData.PLAYER_1));

        gameRecord1Players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByRecordId(TestData.GAME_RECORD_1.getId()));

        assertTrue(gameRecord1Players.isEmpty());
    }
}
