/*
ScoreBoard

Copyright © 2020 Adam Poole

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.floatingpanda.scoreboard.db;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.relations.GameRecordWithPlayerTeamsAndPlayers;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.daos.PlayerDao;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.data.daos.PlayerTeamDao;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;

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
public class GameRecordDaoTest {

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
        assertThat(gameRecord.getDateTime(), is(TestData.GAME_RECORD_1.getDateTime()));
        assertThat(gameRecord.getNoOfTeams(), is(TestData.GAME_RECORD_1.getNoOfTeams()));
        assertThat(gameRecord.getPlayModePlayed(), is(TestData.GAME_RECORD_1.getPlayModePlayed()));
        assertThat(gameRecord.getTeams(), is(TestData.GAME_RECORD_1.getTeams()));
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

    @Test
    public void getAllGameRecordsWithPlayerTeamsAndPlayersWhenNoneInserted() throws InterruptedException {
        List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordDao.getAllGameRecordsWithPlayerTeamsAndPlayers());

        assertTrue(gameRecordsWithPlayerTeamsAndPlayers.isEmpty());
    }

    @Test
    public void getAllGameRecordsWithPlayerTeamsAndPlayersWhenAllInserted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordDao.getAllGameRecordsWithPlayerTeamsAndPlayers());

        assertThat(gameRecordsWithPlayerTeamsAndPlayers.size(), is(TestData.GAME_RECORDS.size()));

        List<PlayerTeamWithPlayers> playerTeamsWithPlayers = new ArrayList<>();
        for (GameRecordWithPlayerTeamsAndPlayers gameRecordWithPlayerTeamsAndPlayers : gameRecordsWithPlayerTeamsAndPlayers) {
            if (gameRecordWithPlayerTeamsAndPlayers.getGameRecord().getId() == TestData.GAME_RECORD_1.getId()) {
                playerTeamsWithPlayers = gameRecordWithPlayerTeamsAndPlayers.getPlayerTeamsWithPlayers();
                assertThat(playerTeamsWithPlayers.size(), is(4));

                List<Player> players;
                for(PlayerTeamWithPlayers playerTeamWithPlayers : gameRecordWithPlayerTeamsAndPlayers.getPlayerTeamsWithPlayers()) {
                    if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_1.getId()) {
                        players = playerTeamWithPlayers.getPlayers();
                        assertThat(players.size(), is(1));
                        assertThat(players.get(0), is(TestData.PLAYER_1));
                    } else if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_2.getId()) {
                        players = playerTeamWithPlayers.getPlayers();
                        assertThat(players.size(), is(2));
                        assertTrue(players.contains(TestData.PLAYER_2));
                        assertTrue(players.contains(TestData.PLAYER_3));
                    } else if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_3.getId()) {
                        players = playerTeamWithPlayers.getPlayers();
                        assertTrue(players.isEmpty());
                    } else if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_4.getId()) {
                        players = playerTeamWithPlayers.getPlayers();
                        assertTrue(players.isEmpty());
                    }
                }
            } else if (gameRecordWithPlayerTeamsAndPlayers.getGameRecord().getId() == TestData.GAME_RECORD_2.getId()) {
                playerTeamsWithPlayers = gameRecordWithPlayerTeamsAndPlayers.getPlayerTeamsWithPlayers();
                assertThat(playerTeamsWithPlayers.size(), is(1));
                assertThat(playerTeamsWithPlayers.get(0).getPlayerTeam(), is(TestData.PLAYER_TEAM_5));

                List<Player> players = playerTeamsWithPlayers.get(0).getPlayers();
                assertThat(players.get(0), is(TestData.PLAYER_4));

            } else if (gameRecordWithPlayerTeamsAndPlayers.getGameRecord().getId() == TestData.GAME_RECORD_3.getId()) {
                playerTeamsWithPlayers = gameRecordWithPlayerTeamsAndPlayers.getPlayerTeamsWithPlayers();
                assertThat(playerTeamsWithPlayers.size(), is(1));
                assertThat(playerTeamsWithPlayers.get(0).getPlayerTeam(), is(TestData.PLAYER_TEAM_6));

                List<Player> players = playerTeamsWithPlayers.get(0).getPlayers();
                assertThat(players.get(0), is(TestData.PLAYER_5));
            } else if (gameRecordWithPlayerTeamsAndPlayers.getGameRecord().getId() == TestData.GAME_RECORD_5.getId()) {
                playerTeamsWithPlayers = gameRecordWithPlayerTeamsAndPlayers.getPlayerTeamsWithPlayers();
                assertTrue(playerTeamsWithPlayers.isEmpty());
            }
        }
    }

    @Test
    public void getSpecificGameRecordWithPlayerTeamsAndPlayersByRecordIdWhenNoneInserted() throws InterruptedException {
        GameRecordWithPlayerTeamsAndPlayers gameRecordWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordDao.findGameRecordWithPlayerTeamsAndPlayersByRecordId(TestData.GAME_RECORD_1.getId()));

        assertNull(gameRecordWithPlayerTeamsAndPlayers);
    }

    @Test
    public void getSpecificGameRecordWithPlayerTeamsAndPlayersByRecordIdWhenAllInserted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        GameRecordWithPlayerTeamsAndPlayers gameRecordWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordDao.findGameRecordWithPlayerTeamsAndPlayersByRecordId(TestData.GAME_RECORD_1.getId()));

        assertThat(gameRecordWithPlayerTeamsAndPlayers.getGameRecord().getId(), is(TestData.GAME_RECORD_1.getId()));
        assertThat(gameRecordWithPlayerTeamsAndPlayers.getPlayerTeamsWithPlayers().size(), is(4));

        List<Player> players;
        for(PlayerTeamWithPlayers playerTeamWithPlayers : gameRecordWithPlayerTeamsAndPlayers.getPlayerTeamsWithPlayers()) {
            if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_1.getId()) {
                players = playerTeamWithPlayers.getPlayers();
                assertThat(players.size(), is(1));
                assertThat(players.get(0), is(TestData.PLAYER_1));
            } else if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_2.getId()) {
                players = playerTeamWithPlayers.getPlayers();
                assertThat(players.size(), is(2));
                assertTrue(players.contains(TestData.PLAYER_2));
                assertTrue(players.contains(TestData.PLAYER_3));
            } else if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_3.getId()) {
                players = playerTeamWithPlayers.getPlayers();
                assertTrue(players.isEmpty());
            } else if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_4.getId()) {
                players = playerTeamWithPlayers.getPlayers();
                assertTrue(players.isEmpty());
            }
        }

        gameRecordWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordDao.findGameRecordWithPlayerTeamsAndPlayersByRecordId(TestData.GAME_RECORD_5.getId()));

        assertThat(gameRecordWithPlayerTeamsAndPlayers.getGameRecord().getId(), is(TestData.GAME_RECORD_5.getId()));
        assertTrue(gameRecordWithPlayerTeamsAndPlayers.getPlayerTeamsWithPlayers().isEmpty());
    }

    @Test
    public void getGameRecordsWithPlayerTeamAndPlayersByGroupIdWhenNoneInserted() throws InterruptedException {
        List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordDao.findGameRecordsWithPlayerTeamsAndPlayersByGroupId(TestData.GROUP_1.getId()));

        assertTrue(gameRecordsWithPlayerTeamsAndPlayers.isEmpty());
    }

    @Test
    public void getGameRecordsWithPlayerTeamAndPlayersByGroupIdWhenAllInserted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        //Group 1 has 3 game records associated with it.
        List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordDao.findGameRecordsWithPlayerTeamsAndPlayersByGroupId(TestData.GROUP_1.getId()));

        assertThat(gameRecordsWithPlayerTeamsAndPlayers.size(), is(3));

        for(GameRecordWithPlayerTeamsAndPlayers gameRecordWithPlayerTeamsAndPlayers : gameRecordsWithPlayerTeamsAndPlayers) {
            if (gameRecordWithPlayerTeamsAndPlayers.getGameRecord().getId() == TestData.GAME_RECORD_1.getId()) {
                assertThat(gameRecordWithPlayerTeamsAndPlayers.getPlayerTeamsWithPlayers().size(), is(4));
            }
        }
    }

    @Test
    public void getGameRecordNumberForGroupWhenAllInserted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));

        int noOfGameRecords = gameRecordDao.getNoOfGameRecordsByGroupId(TestData.GROUP_1.getId());
        assertThat(noOfGameRecords, is(3));
    }
}
