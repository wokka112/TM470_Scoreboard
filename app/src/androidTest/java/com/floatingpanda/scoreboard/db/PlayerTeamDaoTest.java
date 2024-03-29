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
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.daos.PlayerSkillRatingChangeDao;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.daos.PlayerDao;
import com.floatingpanda.scoreboard.data.entities.PlayerSkillRatingChange;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.data.daos.PlayerTeamDao;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayersAndRatingChanges;
import com.floatingpanda.scoreboard.data.relations.PlayerWithRatingChanges;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PlayerTeamDaoTest {

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
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllPlayerTeamsWhenNoneInserted() throws InterruptedException {
        List<PlayerTeam> playerTeams = LiveDataTestUtil.getValue(playerTeamDao.getAll());

        assertTrue(playerTeams.isEmpty());
    }

    @Test
    public void getAllPlayerTeamsWhenAllInserted() throws InterruptedException {
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        List<PlayerTeam> playerTeams = LiveDataTestUtil.getValue(playerTeamDao.getAll());

        assertThat(playerTeams.size(), is(TestData.PLAYER_TEAMS.size()));
    }

    @Test
    public void getPlayerTeamsByGameRecordIdWhenNoneInserted() throws InterruptedException {
        List<PlayerTeam> playerTeams = LiveDataTestUtil.getValue(playerTeamDao.findLiveDataPlayerTeamsByRecordId(TestData.PLAYER_TEAM_1.getRecordId()));

        assertTrue(playerTeams.isEmpty());
    }

    @Test
    public void getPlayerTeamsByGameRecordIdWhenAllInserted() throws InterruptedException {
        // The game record associated with player team 1 is associated with 4 player teams in total.
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        List<PlayerTeam> playerTeams = LiveDataTestUtil.getValue(playerTeamDao.findLiveDataPlayerTeamsByRecordId(TestData.PLAYER_TEAM_1.getRecordId()));

        assertThat(playerTeams.size(), is(4));
        assertTrue(playerTeams.contains(TestData.PLAYER_TEAM_1));
        assertTrue(playerTeams.contains(TestData.PLAYER_TEAM_2));
        assertTrue(playerTeams.contains(TestData.PLAYER_TEAM_3));
        assertTrue(playerTeams.contains(TestData.PLAYER_TEAM_4));
    }

    @Test
    public void getPlayerTeamByTeamNumberAndRecordIdWhenNoneInserted() throws InterruptedException {
        int teamNumber = TestData.PLAYER_TEAM_1.getTeamNumber();
        int recordId = TestData.PLAYER_TEAM_1.getRecordId();
        PlayerTeam playerTeam = LiveDataTestUtil.getValue(playerTeamDao.findLiveDataPlayerTeamByTeamNumberAndRecordId(teamNumber, recordId));

        assertNull(playerTeam);
    }

    @Test
    public void getPlayerTeamByTeamNumberAndRecordIdWhenAllInserted() throws InterruptedException {
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        int teamNumber = TestData.PLAYER_TEAM_2.getTeamNumber();
        int recordId = TestData.PLAYER_TEAM_2.getRecordId();
        PlayerTeam playerTeam = LiveDataTestUtil.getValue(playerTeamDao.findLiveDataPlayerTeamByTeamNumberAndRecordId(teamNumber, recordId));

        assertNotNull(playerTeam);
        assertThat(playerTeam, is(TestData.PLAYER_TEAM_2));
    }

    @Test
    public void getAllPlayerTeamsWhenSpecificTeamInserted() throws InterruptedException {
        playerTeamDao.insert(TestData.PLAYER_TEAM_1);
        List<PlayerTeam> playerTeams = LiveDataTestUtil.getValue(playerTeamDao.getAll());

        assertThat(playerTeams.size(), is(1));
        assertThat(playerTeams.get(0), is(TestData.PLAYER_TEAM_1));
    }

    @Test
    public void getAllPlayerTeamsWhenAllInsertedAndThenDeleted() throws InterruptedException {
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        List<PlayerTeam> playerTeams = LiveDataTestUtil.getValue(playerTeamDao.getAll());

        assertThat(playerTeams.size(), is(TestData.PLAYER_TEAMS.size()));

        playerTeamDao.deleteAll();
        TimeUnit.MILLISECONDS.sleep(100);

        playerTeams = LiveDataTestUtil.getValue(playerTeamDao.getAll());

        assertTrue(playerTeams.isEmpty());
    }

    @Test
    public void getAllPlayerTeamsWhenAllInsertedAndOneDeleted() throws InterruptedException {
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        List<PlayerTeam> playerTeams = LiveDataTestUtil.getValue(playerTeamDao.getAll());

        assertThat(playerTeams.size(), is(TestData.PLAYER_TEAMS.size()));
        assertTrue(playerTeams.contains(TestData.PLAYER_TEAM_2));

        playerTeamDao.delete(TestData.PLAYER_TEAM_2);
        TimeUnit.MILLISECONDS.sleep(100);

        playerTeams = LiveDataTestUtil.getValue(playerTeamDao.getAll());

        assertThat(playerTeams.size(), is(TestData.PLAYER_TEAMS.size() - 1));
        assertFalse(playerTeams.contains(TestData.PLAYER_TEAM_2));
    }

    @Test
    public void getAllPlayerTeamsWhenAllInsertedAndRelatedGameRecordDeleted() throws InterruptedException {
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        List<PlayerTeam> playerTeams = LiveDataTestUtil.getValue(playerTeamDao.getAll());

        assertThat(playerTeams.size(), is(TestData.PLAYER_TEAMS.size()));
        assertTrue(playerTeams.contains(TestData.PLAYER_TEAM_1));

        // 4 player teams are associated with game record 1, including player team 1.
        List<PlayerTeam> gameRecord1PlayerTeams = LiveDataTestUtil.getValue(playerTeamDao.findLiveDataPlayerTeamsByRecordId(TestData.GAME_RECORD_1.getId()));

        assertThat(gameRecord1PlayerTeams.size(), is(4));

        gameRecordDao.delete(TestData.GAME_RECORD_1);
        TimeUnit.MILLISECONDS.sleep(100);

        playerTeams = LiveDataTestUtil.getValue(playerTeamDao.getAll());

        assertThat(playerTeams.size(), is(TestData.PLAYER_TEAMS.size() - 4));
        assertFalse(playerTeams.contains(TestData.PLAYER_TEAM_1));

        gameRecord1PlayerTeams = LiveDataTestUtil.getValue(playerTeamDao.findLiveDataPlayerTeamsByRecordId(TestData.GAME_RECORD_1.getId()));

        assertTrue(gameRecord1PlayerTeams.isEmpty());
    }

    @Test
    public void getAllPlayerTeamsWithPlayersWhenNoneInserted() throws InterruptedException {
        List<PlayerTeamWithPlayers> playerTeamsWithPlayers = LiveDataTestUtil.getValue(playerTeamDao.getAllPlayerTeamsWithPlayers());

        assertTrue(playerTeamsWithPlayers.isEmpty());
    }

    @Test
    public void getAllPlayerTeamsWithPlayersWhenAllInserted() throws InterruptedException {
        //4 player teams have players
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        List<PlayerTeamWithPlayers> playerTeamsWithPlayers = LiveDataTestUtil.getValue(playerTeamDao.getAllPlayerTeamsWithPlayers());

        assertThat(playerTeamsWithPlayers.size(), is(TestData.PLAYER_TEAMS.size()));

        for (PlayerTeamWithPlayers playerTeamWithPlayers : playerTeamsWithPlayers) {
            if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_1.getId()) {
                assertTrue(playerTeamWithPlayers.getPlayers().contains(TestData.PLAYER_1));
                assertFalse(playerTeamWithPlayers.getPlayers().contains(TestData.PLAYER_2));
            } else if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_2.getId()) {
                assertTrue(playerTeamWithPlayers.getPlayers().contains(TestData.PLAYER_2));
                assertTrue(playerTeamWithPlayers.getPlayers().contains(TestData.PLAYER_3));
                assertFalse(playerTeamWithPlayers.getPlayers().contains(TestData.PLAYER_1));
            } else if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_3.getId()) {
                assertTrue(playerTeamWithPlayers.getPlayers().isEmpty());
            } else if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_4.getId()) {
                assertTrue(playerTeamWithPlayers.getPlayers().isEmpty());
            } else if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_5.getId()) {
                assertTrue(playerTeamWithPlayers.getPlayers().contains(TestData.PLAYER_4));
                assertFalse(playerTeamWithPlayers.getPlayers().contains(TestData.PLAYER_1));
            } else if (playerTeamWithPlayers.getPlayerTeam().getId() == TestData.PLAYER_TEAM_6.getId()) {
                assertTrue(playerTeamWithPlayers.getPlayers().contains(TestData.PLAYER_5));
                assertFalse(playerTeamWithPlayers.getPlayers().contains(TestData.PLAYER_1));
            }
        }
    }

    @Test
    public void getSpecificPlayerTeamWithPlayersViaPlayerTeamIdWhenNoneInserted() throws InterruptedException {
        PlayerTeamWithPlayers playerTeamWithPlayers = LiveDataTestUtil.getValue(playerTeamDao.findPlayerTeamWithPlayersByPlayerTeamId(TestData.PLAYER_TEAM_2.getId()));

        assertNull(playerTeamWithPlayers);
    }

    @Test
    public void getSpecificPlayerTeamWithPlayersViaPlayerTeamIdWhenAllInserted() throws InterruptedException {
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        List<PlayerTeamWithPlayers> playerTeamsWithPlayers = LiveDataTestUtil.getValue(playerTeamDao.getAllPlayerTeamsWithPlayers());

        assertThat(playerTeamsWithPlayers.size(), is(TestData.PLAYER_TEAMS.size()));

        PlayerTeamWithPlayers playerTeamWithPlayers = LiveDataTestUtil.getValue(playerTeamDao.findPlayerTeamWithPlayersByPlayerTeamId(TestData.PLAYER_TEAM_2.getId()));

        assertThat(playerTeamWithPlayers.getPlayerTeam(), is(TestData.PLAYER_TEAM_2));
        assertThat(playerTeamWithPlayers.getPlayers().size(), is(2));
        assertTrue(playerTeamWithPlayers.getPlayers().contains(TestData.PLAYER_2));
        assertTrue(playerTeamWithPlayers.getPlayers().contains(TestData.PLAYER_3));
        assertFalse(playerTeamWithPlayers.getPlayers().contains(TestData.PLAYER_1));
    }

    @Test
    public void getPlayerTeamWithPlayersViaRecordIdWhenAllInserted() throws InterruptedException {
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        List<PlayerTeamWithPlayers> playerTeamsWithPlayers = LiveDataTestUtil.getValue(playerTeamDao.findPlayerTeamsWithPlayersByRecordId(TestData.GAME_RECORD_1.getId()));

        assertThat(playerTeamsWithPlayers.size(), is(4));

        List<Integer> playerTeamNos = new ArrayList<>();

        for (PlayerTeamWithPlayers playerTeamWithPlayers : playerTeamsWithPlayers) {
            playerTeamNos.add(playerTeamWithPlayers.getPlayerTeam().getTeamNumber());
        }

        assertTrue(playerTeamNos.contains(TestData.PLAYER_TEAM_1.getTeamNumber()));
        assertTrue(playerTeamNos.contains(TestData.PLAYER_TEAM_2.getTeamNumber()));
        assertTrue(playerTeamNos.contains(TestData.PLAYER_TEAM_3.getTeamNumber()));
        assertTrue(playerTeamNos.contains(TestData.PLAYER_TEAM_4.getTeamNumber()));
    }

    @Test
    public void getAllPlayerTeamsWithPlayersAndRatingChangesWhenAllInserted() throws InterruptedException {
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));

        List<PlayerTeamWithPlayersAndRatingChanges> playerTeamsWithPlayersAndRatingChanges = LiveDataTestUtil.getValue(playerTeamDao.getAllPlayerTeamsWithPlayersAndRatingChanges());
        assertThat(playerTeamsWithPlayersAndRatingChanges.size(), is(TestData.PLAYER_TEAMS.size()));
    }

    @Test
    public void getPlayerTeamsWithPlayersAndRatingChangesByRecordIdWhenAllInserted() throws InterruptedException {
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));

        List<PlayerTeamWithPlayersAndRatingChanges> playerTeamsWithPlayersAndRatingChanges =
                LiveDataTestUtil.getValue(playerTeamDao.getPlayerTeamsWithPlayersAndRatingChangesByRecordId(TestData.GAME_RECORD_1.getId()));
        assertThat(playerTeamsWithPlayersAndRatingChanges.size(), is(4));

        int previousPosition = 0;
        int previousTeamNo = 0;
        for (PlayerTeamWithPlayersAndRatingChanges playerTeamWithPlayersAndRatingChanges : playerTeamsWithPlayersAndRatingChanges) {
            int currentPosition = playerTeamWithPlayersAndRatingChanges.getPlayerTeam().getPosition();
            int currentTeamNo = playerTeamWithPlayersAndRatingChanges.getPlayerTeam().getTeamNumber();

            assertTrue(previousPosition <= currentPosition);

            if (previousPosition == currentPosition) {
                assertTrue(previousTeamNo < currentTeamNo);
            }

            previousPosition = currentPosition;
            previousTeamNo = currentTeamNo;
        }
    }

    @Test
    public void getPlayerTeamWithPlayersAndRatingChangesByPlayerTeamId() throws InterruptedException {
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));

        PlayerTeamWithPlayersAndRatingChanges playerTeamWithPlayersAndRatingChanges =
                LiveDataTestUtil.getValue(playerTeamDao.getPlayerTeamWithPlayersAndRatingChangesByPlayerTeamId(TestData.PLAYER_TEAM_1.getId()));

        assertThat(playerTeamWithPlayersAndRatingChanges.getPlayersWithRatingChanges().size(), is(1));

        PlayerWithRatingChanges playerWithRatingChanges = playerTeamWithPlayersAndRatingChanges.getPlayersWithRatingChanges().get(0);

        assertThat(playerWithRatingChanges.getPlayer(), is(TestData.PLAYER_1));
        assertThat(playerWithRatingChanges.getPlayerSkillRatingChanges().size(), is(2));
    }

    @Test
    public void getPlayerTeamIdByTeamNumberAndRecordId() throws InterruptedException {
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        int playerTeamId = playerTeamDao.getNonLivePlayerTeamIdByTeamNumberAndRecordId(TestData.PLAYER_TEAM_1.getTeamNumber(), TestData.PLAYER_TEAM_1.getRecordId());
        assertThat(playerTeamId, is(TestData.PLAYER_TEAM_1.getId()));
    }
}
