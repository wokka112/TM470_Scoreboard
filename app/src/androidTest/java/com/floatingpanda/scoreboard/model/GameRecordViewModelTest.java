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

package com.floatingpanda.scoreboard.model;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.daos.PlayerSkillRatingChangeDao;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.entities.PlayerSkillRatingChange;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayersAndRatingChanges;
import com.floatingpanda.scoreboard.data.relations.PlayerWithRatingChanges;
import com.floatingpanda.scoreboard.repositories.GameRecordRepository;
import com.floatingpanda.scoreboard.data.relations.GameRecordWithPlayerTeamsAndPlayers;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.daos.PlayerDao;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.data.daos.PlayerTeamDao;
import com.floatingpanda.scoreboard.viewmodels.GameRecordViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class GameRecordViewModelTest {
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

    private GameRecordRepository gameRecordRepository;
    private GameRecordViewModel gameRecordViewModel;

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

        gameRecordRepository = new GameRecordRepository(db);
        gameRecordViewModel = new GameRecordViewModel(ApplicationProvider.getApplicationContext(), db);

        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllGameRecordsWithTeamsAndPlayersWhenNoneInserted() throws InterruptedException {
        List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordViewModel.getAllGameRecordsWithTeamsAndPlayers());

        assertTrue(gameRecordsWithPlayerTeamsAndPlayers.isEmpty());
    }

    @Test
    public void getAllGameRecordsWithTeamsAndPlayersWhenAllInserted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordViewModel.getAllGameRecordsWithTeamsAndPlayers());

        assertThat(gameRecordsWithPlayerTeamsAndPlayers.size(), is(TestData.GAME_RECORDS.size()));
    }

    @Test
    public void getGroupsGameRecordsWithTeamsAndPlayersWhenNoneInserted() throws InterruptedException {
        gameRecordViewModel.initGameRecordWithPlayerTeamsAndPlayers(TestData.GROUP_1.getId());
        List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordViewModel.getGroupsGameRecordsWithTeamsAndPlayers());

        assertTrue(gameRecordsWithPlayerTeamsAndPlayers.isEmpty());
    }

    @Test
    public void getGroupsGameRecordsWithTeamsAndPlayersWhenAllInserted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        gameRecordViewModel.initGameRecordWithPlayerTeamsAndPlayers(TestData.GROUP_1.getId());

        //Group 1 should have 3 game records associated with it
        List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordViewModel.getGroupsGameRecordsWithTeamsAndPlayers());

        assertThat(gameRecordsWithPlayerTeamsAndPlayers.size(), is(3));
        assertThat(gameRecordsWithPlayerTeamsAndPlayers.get(0).getGameRecord().getGroupId(), is(TestData.GROUP_1.getId()));
    }

    @Test
    public void extractPlayersWithRatingChangeViews() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));

        List<PlayerTeamWithPlayersAndRatingChanges> playerTeamsWithPlayersAndRatingChanges =
                LiveDataTestUtil.getValue(gameRecordViewModel.getPlayerTeamsWithPlayersAndRatingChangesByRecordId(TestData.GAME_RECORD_1.getId()));

        assertThat(playerTeamsWithPlayersAndRatingChanges.size(), is(4));

        int noOfPlayers = 0;

        for(PlayerTeamWithPlayersAndRatingChanges playerTeamWithPlayersAndRatingChanges : playerTeamsWithPlayersAndRatingChanges) {
            noOfPlayers += playerTeamWithPlayersAndRatingChanges.getPlayersWithRatingChanges().size();
        }

        List<PlayerWithRatingChanges> playersWithRatingChangeViews = gameRecordViewModel.extractPlayersWithRatingChanges(playerTeamsWithPlayersAndRatingChanges);

        assertThat(playersWithRatingChangeViews.size(), is(noOfPlayers));
    }

    @Test
    public void getPlayerTeamsWithPlayersWhenAllInserted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        List<PlayerTeamWithPlayers> playerTeamsWithPlayers = LiveDataTestUtil.getValue(gameRecordViewModel.getPlayerTeamsWithPlayers(TestData.GAME_RECORD_1.getId()));

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

    /*
    public void addGameRecordAndPlayers(GameRecord gameRecord, List<TeamOfPlayers> teamsOfPlayers) {
        //Add game record, player team and players, then assign skill rating changes and score changes
        gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
    }

     */
}
