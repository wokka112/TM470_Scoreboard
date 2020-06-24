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
import com.floatingpanda.scoreboard.data.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.GameRecordRepository;
import com.floatingpanda.scoreboard.data.GameRecordWithPlayerTeamsAndPlayers;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.daos.PlayerDao;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.data.daos.PlayerTeamDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class GameRecordRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private GameRecordDao gameRecordDao;
    private GroupDao groupDao;
    private BoardGameDao boardGameDao;
    private PlayerTeamDao playerTeamDao;
    private PlayerDao playerDao;
    private MemberDao memberDao;

    private GameRecordRepository gameRecordRepository;

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

        gameRecordRepository = new GameRecordRepository(db);

        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllGameRecordsWithPlayerTeamsAndPlayersWhenNoneInserted() throws InterruptedException {
        List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordRepository.getAllGameRecordsWithTeamsAndPlayers());

        assertTrue(gameRecordsWithPlayerTeamsAndPlayers.isEmpty());
    }

    @Test
    public void getAllGameRecordsWithPlayerTeamsAndPlayersWhenAllInserted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordRepository.getAllGameRecordsWithTeamsAndPlayers());

        assertThat(gameRecordsWithPlayerTeamsAndPlayers.size(), is(TestData.GAME_RECORDS.size()));
    }

    @Test
    public void getGameRecordsWithPlayerTeamsAndPlayersViaGroupIdWhenNoneInserted() throws InterruptedException {
        List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordRepository.getGameRecordWithTeamsAndPlayersViaGroupId(TestData.GROUP_1.getId()));

        assertTrue(gameRecordsWithPlayerTeamsAndPlayers.isEmpty());
    }

    @Test
    public void getGameRecordsWithPlayerTeamsAndPlayersViaGroupIdWhenAllInserted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        //Group 1 should have 3 game records associated with it
        List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordRepository.getGameRecordWithTeamsAndPlayersViaGroupId(TestData.GROUP_1.getId()));

        assertThat(gameRecordsWithPlayerTeamsAndPlayers.size(), is(3));
        assertThat(gameRecordsWithPlayerTeamsAndPlayers.get(0).getGameRecord().getGroupId(), is(TestData.GROUP_1.getId()));
    }

    @Test
    public void addGameRecordWithPlayers() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        //Game record with 3 teams:
        // Team 1 - 1st place, 2 players, 30 points;
        List<Member> team1Members = new ArrayList<Member>();
        team1Members.add(TestData.MEMBER_1);
        team1Members.add(TestData.MEMBER_4);

        TeamOfPlayers team1 = new TeamOfPlayers(1, 1, 30, team1Members);

        // Team 2 - 2nd place, 1 player, 20 points;
        List<Member> team2Members = new ArrayList<Member>();
        team2Members.add(TestData.MEMBER_5);

        TeamOfPlayers team2 = new TeamOfPlayers(2, 2, 20, team2Members);

        // Team 3 - 3rd place, 3 players, 10 points.
        List<Member> team3Members = new ArrayList<>();
        team3Members.add(TestData.MEMBER_6);
        team3Members.add(TestData.MEMBER_7);
        team3Members.add(TestData.MEMBER_8);

        TeamOfPlayers team3 = new TeamOfPlayers(3, 3, 10, team3Members);

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
        teamsOfPlayers.add(team1);
        teamsOfPlayers.add(team2);
        teamsOfPlayers.add(team3);

        int recordId = 100;
        boolean teams = true;
        int noOfTeams = teamsOfPlayers.size();
        GameRecord gameRecord = new GameRecord(recordId, TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), TestData.BOARD_GAME_1.getDifficulty(),
                new Date(), teams, PlayMode.PlayModeEnum.COMPETITIVE, noOfTeams, false);

        gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
        TimeUnit.MILLISECONDS.sleep(300);

        //Test the record was added.
        GameRecordWithPlayerTeamsAndPlayers gameRecordWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordDao.findGameRecordWithPlayerTeamsAndPlayersByRecordId(recordId));

        GameRecord dbGameRecord = gameRecordWithPlayerTeamsAndPlayers.getGameRecord();
        assertThat(dbGameRecord, is(gameRecord));

        //Test the player teams were added.
        List<PlayerTeamWithPlayers> playerTeamsWithPlayers = gameRecordWithPlayerTeamsAndPlayers.getPlayerTeamsWithPlayers();
        assertThat(playerTeamsWithPlayers.size(), is(noOfTeams));

        for(PlayerTeamWithPlayers playerTeamWithPlayers : playerTeamsWithPlayers) {
            //Test the players were added.
            PlayerTeam playerTeam = playerTeamWithPlayers.getPlayerTeam();
            List<Player> players = playerTeamWithPlayers.getPlayers();

            List<String> nicknames = new ArrayList<>();
            for (Player player : players) {
                nicknames.add(player.getMemberNickname());
            }

            if (playerTeam.getPosition() == 1) {
                assertThat(playerTeam.getScore(), is(team1.getScore()));
                assertThat(playerTeam.getTeamNumber(), is(team1.getTeamNo()));

                assertThat(players.size(), is(team1Members.size()));
                assertTrue(nicknames.contains(TestData.MEMBER_1.getNickname()));
                assertTrue(nicknames.contains(TestData.MEMBER_4.getNickname()));
            } else if (playerTeam.getPosition() == 2) {
                assertThat(playerTeam.getScore(), is(team2.getScore()));
                assertThat(playerTeam.getTeamNumber(), is(team2.getTeamNo()));

                assertThat(players.size(), is(team2Members.size()));
                assertTrue(nicknames.contains(TestData.MEMBER_5.getNickname()));
            } else if (playerTeam.getPosition() == 3) {
                assertThat(playerTeam.getScore(), is(team3.getScore()));
                assertThat(playerTeam.getTeamNumber(), is(team3.getTeamNo()));

                assertThat(players.size(), is(team3Members.size()));
                assertTrue(nicknames.contains(TestData.MEMBER_6.getNickname()));
                assertTrue(nicknames.contains(TestData.MEMBER_7.getNickname()));
                assertTrue(nicknames.contains(TestData.MEMBER_8.getNickname()));
            } else {
                //There's been an error in the adding of teams and team members somewhere.
                assertTrue(false);
            }
        }
    }

    @Test
    public void addGameRecordWithPlayersAnd2TeamsInFirstPlace() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        //Game record with 3 teams:
        // Team 1 - 1st place, 2 players, 30 points;
        List<Member> team1Members = new ArrayList<Member>();
        team1Members.add(TestData.MEMBER_1);
        team1Members.add(TestData.MEMBER_4);

        TeamOfPlayers team1 = new TeamOfPlayers(1, 1, 30, team1Members);

        // Team 2 - 3rd place, 1 player, 10 points;
        List<Member> team2Members = new ArrayList<Member>();
        team2Members.add(TestData.MEMBER_5);

        TeamOfPlayers team2 = new TeamOfPlayers(2, 3, 10, team2Members);

        // Team 3 - 1st place, 3 players, 30 points.
        List<Member> team3Members = new ArrayList<>();
        team3Members.add(TestData.MEMBER_6);
        team3Members.add(TestData.MEMBER_7);
        team3Members.add(TestData.MEMBER_8);

        TeamOfPlayers team3 = new TeamOfPlayers(3, 1, 30, team3Members);

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
        teamsOfPlayers.add(team1);
        teamsOfPlayers.add(team2);
        teamsOfPlayers.add(team3);

        int recordId = 100;
        boolean teams = true;
        int noOfTeams = teamsOfPlayers.size();
        GameRecord gameRecord = new GameRecord(recordId, TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), TestData.BOARD_GAME_1.getDifficulty(),
                new Date(), teams, PlayMode.PlayModeEnum.COMPETITIVE, noOfTeams, false);

        gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
        TimeUnit.MILLISECONDS.sleep(300);

        //Test the record was added.
        GameRecordWithPlayerTeamsAndPlayers gameRecordWithPlayerTeamsAndPlayers = LiveDataTestUtil.getValue(gameRecordDao.findGameRecordWithPlayerTeamsAndPlayersByRecordId(recordId));

        GameRecord dbGameRecord = gameRecordWithPlayerTeamsAndPlayers.getGameRecord();
        assertThat(dbGameRecord, is(gameRecord));

        //Test the player teams were added.
        List<PlayerTeamWithPlayers> playerTeamsWithPlayers = gameRecordWithPlayerTeamsAndPlayers.getPlayerTeamsWithPlayers();
        assertThat(playerTeamsWithPlayers.size(), is(noOfTeams));

        for(PlayerTeamWithPlayers playerTeamWithPlayers : playerTeamsWithPlayers) {
            //Test the players were added.
            PlayerTeam playerTeam = playerTeamWithPlayers.getPlayerTeam();
            List<Player> players = playerTeamWithPlayers.getPlayers();

            List<String> nicknames = new ArrayList<>();
            for (Player player : players) {
                nicknames.add(player.getMemberNickname());
            }

            //Position 1 will have 2 teams to test.
            if (playerTeam.getPosition() == 1
                    && playerTeam.getTeamNumber() == team1.getTeamNo()) {
                assertThat(playerTeam.getScore(), is(team1.getScore()));
                assertThat(playerTeam.getTeamNumber(), is(team1.getTeamNo()));

                assertThat(players.size(), is(team1Members.size()));
                assertTrue(nicknames.contains(TestData.MEMBER_1.getNickname()));
                assertTrue(nicknames.contains(TestData.MEMBER_4.getNickname()));
            } else if (playerTeam.getPosition() == 1
                    && playerTeam.getTeamNumber() == team3.getTeamNo()) {
                assertThat(playerTeam.getScore(), is(team3.getScore()));
                assertThat(playerTeam.getTeamNumber(), is(team3.getTeamNo()));

                assertThat(players.size(), is(team3Members.size()));
                assertTrue(nicknames.contains(TestData.MEMBER_6.getNickname()));
                assertTrue(nicknames.contains(TestData.MEMBER_7.getNickname()));
                assertTrue(nicknames.contains(TestData.MEMBER_8.getNickname()));
            } else if (playerTeam.getPosition() == 3
                    && playerTeam.getTeamNumber() == team2.getTeamNo()) {
                assertThat(playerTeam.getScore(), is(team2.getScore()));
                assertThat(playerTeam.getTeamNumber(), is(team2.getTeamNo()));

                assertThat(players.size(), is(team2Members.size()));
                assertTrue(nicknames.contains(TestData.MEMBER_5.getNickname()));
            } else {
                //There's been an error in the adding of teams and team members somewhere.
                fail();
            }
        }
    }
}
