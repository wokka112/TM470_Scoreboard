package com.floatingpanda.scoreboard.model;

import android.content.Context;
import android.widget.LinearLayout;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.GroupCategoryRatingChange;
import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.calculators.Calculator;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.daos.AssignedCategoryDao;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.daos.GroupCategorySkillRatingDao;
import com.floatingpanda.scoreboard.data.daos.GroupMemberDao;
import com.floatingpanda.scoreboard.data.daos.GroupMonthlyScoreDao;
import com.floatingpanda.scoreboard.data.daos.PlayerSkillRatingChangeDao;
import com.floatingpanda.scoreboard.data.daos.ScoreDao;
import com.floatingpanda.scoreboard.data.entities.AssignedCategory;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.GroupCategorySkillRating;
import com.floatingpanda.scoreboard.data.entities.GroupMember;
import com.floatingpanda.scoreboard.data.entities.GroupMonthlyScore;
import com.floatingpanda.scoreboard.data.entities.PlayerSkillRatingChange;
import com.floatingpanda.scoreboard.data.entities.Score;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayersAndRatingChanges;
import com.floatingpanda.scoreboard.data.relations.PlayerWithRatingChanges;
import com.floatingpanda.scoreboard.repositories.GameRecordRepository;
import com.floatingpanda.scoreboard.data.relations.GameRecordWithPlayerTeamsAndPlayers;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private GroupMonthlyScoreDao groupMonthlyScoreDao;
    private ScoreDao scoreDao;
    private GroupMemberDao groupMemberDao;
    private GroupCategorySkillRatingDao groupCategorySkillRatingDao;
    private BgCategoryDao bgCategoryDao;
    private AssignedCategoryDao assignedCategoryDao;
    private PlayerSkillRatingChangeDao playerSkillRatingChangeDao;

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
        groupMonthlyScoreDao = db.groupMonthlyScoreDao();
        scoreDao = db.scoreDao();
        groupMemberDao = db.groupMemberDao();
        groupCategorySkillRatingDao = db.groupCategorySkillRatingDao();
        bgCategoryDao = db.bgCategoryDao();
        assignedCategoryDao = db.assignedCategoryDao();
        playerSkillRatingChangeDao = db.playerSkillRatingChangeDao();

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
    public void getPlayerTeamsWithPlayersViaRecordIdWhenAllInserted() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        List<PlayerTeamWithPlayers> playerTeamsWithPlayers = LiveDataTestUtil.getValue(gameRecordRepository.getPlayerTeamsWithPlayersViaRecordId(TestData.GAME_RECORD_1.getId()));

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
    public void getPlayerTeamsWithPlayersAndRatingChangesByRecordId() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));

        List<PlayerTeamWithPlayersAndRatingChanges> playerTeamsWithPlayerAndRatingChanges =
                LiveDataTestUtil.getValue(gameRecordRepository.getPlayerTeamsWithPlayersAndRatingChangesByRecordId(TestData.GAME_RECORD_1.getId()));

        assertThat(playerTeamsWithPlayerAndRatingChanges.size(), is(4));

        for (PlayerTeamWithPlayersAndRatingChanges playerTeamWithPlayerAndRatingChanges : playerTeamsWithPlayerAndRatingChanges) {
            if (playerTeamWithPlayerAndRatingChanges.getPlayerTeam().getId() == TestData.PLAYER_TEAM_1.getId()) {
                List<PlayerWithRatingChanges> playersWithRatingChanges = playerTeamWithPlayerAndRatingChanges.getPlayersWithRatingChanges();
                assertThat(playersWithRatingChanges.size(), is(1));

                List<PlayerSkillRatingChange> playerSkillRatingChanges = playersWithRatingChanges.get(0).getPlayerSkillRatingChanges();
                assertThat(playerSkillRatingChanges.size(), is(2));

                assertTrue(playerSkillRatingChanges.contains(TestData.PLAYER_SKILL_RATING_CHANGE_1));
                assertTrue(playerSkillRatingChanges.contains(TestData.PLAYER_SKILL_RATING_CHANGE_2));
            }
        }
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

    @Test
    public void addGameRecordWithPlayersAndCheckScoreInsertionIsCorrect() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));

        TeamOfPlayers team1 = new TeamOfPlayers(1, 1);
        List<Member> team1Members = new ArrayList<>();
        team1Members.add(TestData.MEMBER_1);
        team1Members.add(TestData.MEMBER_4);
        team1.setMembers(team1Members);

        TeamOfPlayers team2 = new TeamOfPlayers(2, 1);
        List<Member> team2Members = new ArrayList<>();
        team2Members.add(TestData.MEMBER_5);
        team2Members.add(TestData.MEMBER_6);
        team2.setMembers(team2Members);

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
        teamsOfPlayers.add(team1);
        teamsOfPlayers.add(team2);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.MONTH, 8 - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 14);

        GameRecord gameRecord = new GameRecord(TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), 5, calendar.getTime(), true, PlayMode.PlayModeEnum.COMPETITIVE, 2);

        // FOR SCORE TESTS //

        Score member1OldScore = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getId(), TestData.MEMBER_1.getId()));
        //int member1OldScoreInt = member1OldScore.getScore();
        assertThat(member1OldScore, is(TestData.SCORE_1));
        assertThat(member1OldScore.getScore(), is(TestData.SCORE_1.getScore()));

        Score member4OldScore = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getId(), TestData.MEMBER_4.getId()));
        //int member4OldScoreInt = member4OldScore.getScore();
        assertThat(member4OldScore, is(TestData.SCORE_2));
        assertThat(member4OldScore.getScore(), is(TestData.SCORE_2.getScore()));

        // METHOD RUN //

        //Runs on background thread, so sleep to give it time.
        gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
        TimeUnit.MILLISECONDS.sleep(300);

        // METHOD FINISH RUN //

        // SCORE TESTS //

        int addScore = team1.getScore();

        Score member1NewScore = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getId(), TestData.MEMBER_1.getId()));
        assertThat(member1NewScore.getScore(), is(member1OldScore.getScore() + addScore));

        Score member4NewScore = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getId(), TestData.MEMBER_4.getId()));
        assertThat(member4NewScore.getScore(), is(member4OldScore.getScore() + addScore));

        // GAME RECORD TEST //
        List<GameRecord> insertedGameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());
        assertThat(insertedGameRecords.size(), is(1));
        assertThat(insertedGameRecords.get(0).getGroupId(), is(gameRecord.getGroupId()));
        assertThat(insertedGameRecords.get(0).getBoardGameName(), is(gameRecord.getBoardGameName()));
        assertThat(insertedGameRecords.get(0).getDateTime(), is(gameRecord.getDateTime()));
    }

    @Test
    public void addGameRecordWithPlayersAndCheckSkillRatingInsertionIsCorrect() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));

        TeamOfPlayers team1 = new TeamOfPlayers(1, 1);
        List<Member> team1Members = new ArrayList<>();
        team1Members.add(TestData.MEMBER_1);
        team1Members.add(TestData.MEMBER_4);
        team1.setMembers(team1Members);

        TeamOfPlayers team2 = new TeamOfPlayers(2, 1);
        List<Member> team2Members = new ArrayList<>();
        team2Members.add(TestData.MEMBER_5);
        team2Members.add(TestData.MEMBER_6);
        team2.setMembers(team2Members);

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
        teamsOfPlayers.add(team1);
        teamsOfPlayers.add(team2);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.MONTH, 8 - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 14);

        GameRecord gameRecord = new GameRecord(TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), 5, calendar.getTime(), true, PlayMode.PlayModeEnum.COMPETITIVE, 2);

        // FOR SKILL RATING TESTS //

        List<Integer> bg1AssignedCategoryIds = assignedCategoryDao.getAllCategoryIdsByBoardGameId(TestData.BOARD_GAME_1.getId());
        Map<Integer, Double> member1Ratings = new HashMap<>();
        Map<Integer, Double> member4Ratings = new HashMap<>();
        for (int assignedCategoryId : bg1AssignedCategoryIds) {
            for (Member member : team1Members) {
                int memberId = member.getId();
                double eloRating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(gameRecord.getGroupId(), assignedCategoryId, memberId);

                if (memberId == TestData.MEMBER_1.getId()) {
                    member1Ratings.put(assignedCategoryId, eloRating);
                } else if (memberId == TestData.MEMBER_4.getId()) {
                    member4Ratings.put(assignedCategoryId, eloRating);
                } else {
                    fail("Member id that doesn't belong in team1 detected.");
                }
            }
        }

        // METHOD RUN //

        //Runs on background thread, so sleep to give it time.
        gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
        TimeUnit.MILLISECONDS.sleep(300);

        // METHOD FINISH RUN //

        // SKILL RATING TESTS //

        for (GroupCategoryRatingChange groupCategoryRatingChange : team1.getGroupCategoryRatingChanges()) {
            int categoryId = groupCategoryRatingChange.getCategoryId();
            double eloRatingChange = groupCategoryRatingChange.getEloRatingChange();

            for (Member member : team1.getMembers()) {
                int memberId = member.getId();
                double newEloRating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(gameRecord.getGroupId(), categoryId, member.getId());
                double oldEloRating = -999999.99;

                if (memberId == TestData.MEMBER_1.getId()) {
                    oldEloRating = member1Ratings.get(categoryId);
                } else if (memberId == TestData.MEMBER_4.getId()) {
                    oldEloRating = member4Ratings.get(categoryId);
                } else {
                    fail("Member id that doesn't belong in team1 detected.");
                }

                assertThat(newEloRating - eloRatingChange, is(oldEloRating));
            }
        }

        // GAME RECORD TEST //
        List<GameRecord> insertedGameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());
        assertThat(insertedGameRecords.size(), is(1));
        assertThat(insertedGameRecords.get(0).getGroupId(), is(gameRecord.getGroupId()));
        assertThat(insertedGameRecords.get(0).getBoardGameName(), is(gameRecord.getBoardGameName()));
        assertThat(insertedGameRecords.get(0).getDateTime(), is(gameRecord.getDateTime()));
    }

    @Test
    public void addGameRecordWithPlayersAndCheckPlayerSkillRatingChangeInsertionIsCorrect() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));

        TeamOfPlayers team1 = new TeamOfPlayers(1, 1);
        List<Member> team1Members = new ArrayList<>();
        team1Members.add(TestData.MEMBER_1);
        team1Members.add(TestData.MEMBER_4);
        team1.setMembers(team1Members);

        TeamOfPlayers team2 = new TeamOfPlayers(2, 1);
        List<Member> team2Members = new ArrayList<>();
        team2Members.add(TestData.MEMBER_5);
        team2Members.add(TestData.MEMBER_6);
        team2.setMembers(team2Members);

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
        teamsOfPlayers.add(team1);
        teamsOfPlayers.add(team2);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.MONTH, 8 - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 14);

        GameRecord gameRecord = new GameRecord(TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), 5, calendar.getTime(), true, PlayMode.PlayModeEnum.COMPETITIVE, 2);

        // FOR SKILL RATING CHANGE INSERTION TESTS //

        List<Integer> assignedCategoryIds = assignedCategoryDao.getAllCategoryIdsByBoardGameId(TestData.BOARD_GAME_1.getId());

        int groupId = TestData.GROUP_1.getId();
        int memberId = team1.getMembers().get(0).getId();
        int categoryId = assignedCategoryIds.get(0);

        GroupCategorySkillRating oldGroupCategorySkillRating = LiveDataTestUtil.getValue(
                groupCategorySkillRatingDao.getGroupCategorySkillRatingByGroupIdAndCategoryIdAndMemberId(TestData.GROUP_1.getId(), categoryId, memberId));

        // METHOD RUN //

        //Runs on background thread, so sleep to give it time.
        gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
        TimeUnit.MILLISECONDS.sleep(300);

        // METHOD FINISH RUN //

        // SKILL RATING CHANGE INSERTION TESTS //
        int noOfPlayers = team1.getMembers().size() + team2.getMembers().size();
        int noOfNewSkillRatingChanges = assignedCategoryIds.size() * noOfPlayers;

        List<PlayerSkillRatingChange> playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());

        assertThat(playerSkillRatingChanges.size(), is(TestData.PLAYER_SKILL_RATING_CHANGES.size() + noOfNewSkillRatingChanges));

        GroupCategorySkillRating newGroupCategorySkillRating = LiveDataTestUtil.getValue(
                groupCategorySkillRatingDao.getGroupCategorySkillRatingByGroupIdAndCategoryIdAndMemberId(TestData.GROUP_1.getId(), categoryId, memberId));

        GameRecord gameRecordFromDao = gameRecordDao.findNonLiveDataGameRecordByRecordId(groupId, TestData.BOARD_GAME_1.getBgName(), calendar.getTimeInMillis());

        int playerTeamId = playerTeamDao.getNonLivePlayerTeamIdByTeamNumberAndRecordId(team1.getTeamNo(), gameRecordFromDao.getId());
        int playerId = playerDao.getPlayerIdByPlayerTeamIdAndMemberNickname(playerTeamId, team1.getMembers().get(0).getNickname());
        String categoryName = bgCategoryDao.getNonLiveCategoryNameByCategoryId(categoryId);
        PlayerSkillRatingChange playerSkillRatingChange = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getPlayerSkillRatingChangeByPlayerIdAndCategoryName(playerId, categoryName));

        // Calculate the change that should have occurred and round to 2 decimal places, rounded up, to match how it is stored in the database.
        double calculatedRatingChange = newGroupCategorySkillRating.getSkillRating() - playerSkillRatingChange.getOldRating();
        BigDecimal calculatedRatingChangeBigDecimal = new BigDecimal(calculatedRatingChange).setScale(2, RoundingMode.HALF_UP);

        assertThat(playerSkillRatingChange.getOldRating(), is(oldGroupCategorySkillRating.getSkillRating()));
        assertThat(playerSkillRatingChange.getRatingChange(), is(calculatedRatingChangeBigDecimal.doubleValue()));

        // GAME RECORD TEST //
        List<GameRecord> insertedGameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());
        assertThat(insertedGameRecords.size(), is(TestData.GAME_RECORDS.size() + 1));
        assertThat(insertedGameRecords.get(TestData.GAME_RECORDS.size()).getGroupId(), is(gameRecord.getGroupId()));
        assertThat(insertedGameRecords.get(TestData.GAME_RECORDS.size()).getBoardGameName(), is(gameRecord.getBoardGameName()));
        assertThat(insertedGameRecords.get(TestData.GAME_RECORDS.size()).getDateTime(), is(gameRecord.getDateTime()));
    }

    @Test
    public void removeGameRecordWithPlayersAndCheckGameRecordAndPlayersAreRemoved() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));

        TeamOfPlayers team1 = new TeamOfPlayers(1, 1);
        List<Member> team1Members = new ArrayList<>();
        team1Members.add(TestData.MEMBER_1);
        team1Members.add(TestData.MEMBER_4);
        team1.setMembers(team1Members);

        TeamOfPlayers team2 = new TeamOfPlayers(2, 1);
        List<Member> team2Members = new ArrayList<>();
        team2Members.add(TestData.MEMBER_5);
        team2Members.add(TestData.MEMBER_6);
        team2.setMembers(team2Members);

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
        teamsOfPlayers.add(team1);
        teamsOfPlayers.add(team2);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.MONTH, 8 - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 14);

        GameRecord gameRecord = new GameRecord(TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), 5, calendar.getTime(), true, PlayMode.PlayModeEnum.COMPETITIVE, 2);

        gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
        TimeUnit.MILLISECONDS.sleep(300);

        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());
        assertThat(gameRecords.size(), is(TestData.GAME_RECORDS.size() + 1));

        List<PlayerTeam> playerTeams = LiveDataTestUtil.getValue(playerTeamDao.getAll());
        assertThat(playerTeams.size(), is (TestData.PLAYER_TEAMS.size() + teamsOfPlayers.size()));

        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());
        int noOfNewPlayers = team1.getMembers().size() + team2.getMembers().size();
        assertThat(players.size(), is(TestData.PLAYERS.size() + noOfNewPlayers));

        List<PlayerSkillRatingChange> playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());
        List<Integer> assignedCategoryIds = assignedCategoryDao.getAllCategoryIdsByBoardGameId(TestData.BOARD_GAME_1.getId());

        int noOfNewSkillRatingChanges = assignedCategoryIds.size() * noOfNewPlayers;
        assertThat(playerSkillRatingChanges.size(), is(TestData.PLAYER_SKILL_RATING_CHANGES.size() + noOfNewSkillRatingChanges));

        gameRecord = gameRecordDao.findNonLiveDataGameRecordByRecordId(TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), gameRecord.getDateTime().getTime());
        gameRecordRepository.deleteGameRecord(gameRecord);
        TimeUnit.MILLISECONDS.sleep(100);

        GameRecord daoGameRecord = LiveDataTestUtil.getValue(gameRecordDao.findLiveDataGameRecordByRecordId(gameRecord.getId()));
        assertNull(daoGameRecord);

        gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());
        assertThat(gameRecords.size(), is(TestData.GAME_RECORDS.size()));

        playerTeams = LiveDataTestUtil.getValue(playerTeamDao.getAll());
        assertThat(playerTeams.size(), is (TestData.PLAYER_TEAMS.size()));

        players = LiveDataTestUtil.getValue(playerDao.getAll());
        assertThat(players.size(), is(TestData.PLAYERS.size()));

        playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());
        assertThat(playerSkillRatingChanges.size(), is(TestData.PLAYER_SKILL_RATING_CHANGES.size()));
    }

    @Test
    public void removeGameRecordWithPlayersAndCheckScoresAreReverted() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));

        TeamOfPlayers team1 = new TeamOfPlayers(1, 1);
        List<Member> team1Members = new ArrayList<>();
        team1Members.add(TestData.MEMBER_1);
        team1Members.add(TestData.MEMBER_4);
        team1.setMembers(team1Members);

        TeamOfPlayers team2 = new TeamOfPlayers(2, 1);
        List<Member> team2Members = new ArrayList<>();
        team2Members.add(TestData.MEMBER_5);
        team2Members.add(TestData.MEMBER_6);
        team2.setMembers(team2Members);

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
        teamsOfPlayers.add(team1);
        teamsOfPlayers.add(team2);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.MONTH, 8 - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 14);

        GameRecord gameRecord = new GameRecord(TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), 5, calendar.getTime(), true, PlayMode.PlayModeEnum.COMPETITIVE, 2);

        // FOR SCORE TESTS //

        Score member1OldScore = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getId(), TestData.MEMBER_1.getId()));
        //int member1OldScoreInt = member1OldScore.getScore();
        assertThat(member1OldScore, is(TestData.SCORE_1));
        assertThat(member1OldScore.getScore(), is(TestData.SCORE_1.getScore()));

        Score member4OldScore = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getId(), TestData.MEMBER_4.getId()));
        //int member4OldScoreInt = member4OldScore.getScore();
        assertThat(member4OldScore, is(TestData.SCORE_2));
        assertThat(member4OldScore.getScore(), is(TestData.SCORE_2.getScore()));

        gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
        TimeUnit.MILLISECONDS.sleep(300);

        // SCORE TESTS //

        int addScore = team1.getScore();

        Score member1NewScore = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getId(), TestData.MEMBER_1.getId()));
        assertThat(member1NewScore.getScore(), is(member1OldScore.getScore() + addScore));

        Score member4NewScore = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getId(), TestData.MEMBER_4.getId()));
        assertThat(member4NewScore.getScore(), is(member4OldScore.getScore() + addScore));

        gameRecordRepository.deleteGameRecord(gameRecord);
        TimeUnit.MILLISECONDS.sleep(300);

        member1NewScore = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getId(), TestData.MEMBER_1.getId()));
        assertThat(member1NewScore.getScore(), is(member1OldScore.getScore()));

        member4NewScore = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getId(), TestData.MEMBER_4.getId()));
        assertThat(member4NewScore.getScore(), is(member4OldScore.getScore()));
    }

    @Test
    public void removeGameRecordWithPlayersAndCheckSkillRatingsAreReverted() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));

        TeamOfPlayers team1 = new TeamOfPlayers(1, 1);
        List<Member> team1Members = new ArrayList<>();
        team1Members.add(TestData.MEMBER_1);
        team1Members.add(TestData.MEMBER_4);
        team1.setMembers(team1Members);

        TeamOfPlayers team2 = new TeamOfPlayers(2, 1);
        List<Member> team2Members = new ArrayList<>();
        team2Members.add(TestData.MEMBER_5);
        team2Members.add(TestData.MEMBER_6);
        team2.setMembers(team2Members);

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
        teamsOfPlayers.add(team1);
        teamsOfPlayers.add(team2);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.MONTH, 8 - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 14);

        GameRecord gameRecord = new GameRecord(TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), 5, calendar.getTime(), true, PlayMode.PlayModeEnum.COMPETITIVE, 2);

        // FOR SKILL RATING TESTS //

        List<Integer> bg1AssignedCategoryIds = assignedCategoryDao.getAllCategoryIdsByBoardGameId(TestData.BOARD_GAME_1.getId());
        Map<Integer, Double> member1Ratings = new HashMap<>();
        Map<Integer, Double> member4Ratings = new HashMap<>();
        for (int assignedCategoryId : bg1AssignedCategoryIds) {
            for (Member member : team1Members) {
                int memberId = member.getId();
                double eloRating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(gameRecord.getGroupId(), assignedCategoryId, memberId);

                if (memberId == TestData.MEMBER_1.getId()) {
                    member1Ratings.put(assignedCategoryId, eloRating);
                } else if (memberId == TestData.MEMBER_4.getId()) {
                    member4Ratings.put(assignedCategoryId, eloRating);
                } else {
                    fail("Member id that doesn't belong in team1 detected.");
                }
            }
        }

        // METHOD RUN //

        //Runs on background thread, so sleep to give it time.
        gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
        TimeUnit.MILLISECONDS.sleep(300);

        // METHOD FINISH RUN //

        // SKILL RATING TESTS //

        for (GroupCategoryRatingChange groupCategoryRatingChange : team1.getGroupCategoryRatingChanges()) {
            int categoryId = groupCategoryRatingChange.getCategoryId();
            double eloRatingChange = groupCategoryRatingChange.getEloRatingChange();

            for (Member member : team1.getMembers()) {
                int memberId = member.getId();
                double newEloRating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(gameRecord.getGroupId(), categoryId, member.getId());
                double oldEloRating = -999999.99;

                if (memberId == TestData.MEMBER_1.getId()) {
                    oldEloRating = member1Ratings.get(categoryId);
                } else if (memberId == TestData.MEMBER_4.getId()) {
                    oldEloRating = member4Ratings.get(categoryId);
                } else {
                    fail("Member id that doesn't belong in team1 detected.");
                }

                assertThat(newEloRating - eloRatingChange, is(oldEloRating));
            }
        }

        gameRecord = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord.getGroupId(), gameRecord.getBoardGameName(), gameRecord.getDateTime().getTime());
        gameRecordRepository.deleteGameRecord(gameRecord);
        TimeUnit.MILLISECONDS.sleep(300);

        for (GroupCategoryRatingChange groupCategoryRatingChange : team1.getGroupCategoryRatingChanges()) {
            int categoryId = groupCategoryRatingChange.getCategoryId();
            double eloRatingChange = groupCategoryRatingChange.getEloRatingChange();

            for (Member member : team1.getMembers()) {
                int memberId = member.getId();
                double newEloRating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(gameRecord.getGroupId(), categoryId, member.getId());
                double oldEloRating = -999999.99;

                if (memberId == TestData.MEMBER_1.getId()) {
                    oldEloRating = member1Ratings.get(categoryId);
                } else if (memberId == TestData.MEMBER_4.getId()) {
                    oldEloRating = member4Ratings.get(categoryId);
                } else {
                    fail("Member id that doesn't belong in team1 detected.");
                }

                assertThat(newEloRating, is(oldEloRating));
            }
        }
    }

    @Test
    public void removeGameRecordWithPlayersAndCheckPlayerSkillRatingChangeRemovalIsCorrect() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));

        TeamOfPlayers team1 = new TeamOfPlayers(1, 1);
        List<Member> team1Members = new ArrayList<>();
        team1Members.add(TestData.MEMBER_1);
        team1Members.add(TestData.MEMBER_4);
        team1.setMembers(team1Members);

        TeamOfPlayers team2 = new TeamOfPlayers(2, 1);
        List<Member> team2Members = new ArrayList<>();
        team2Members.add(TestData.MEMBER_5);
        team2Members.add(TestData.MEMBER_6);
        team2.setMembers(team2Members);

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
        teamsOfPlayers.add(team1);
        teamsOfPlayers.add(team2);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.MONTH, 8 - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 14);

        GameRecord gameRecord = new GameRecord(TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), 5, calendar.getTime(), true, PlayMode.PlayModeEnum.COMPETITIVE, 2);

        // FOR SKILL RATING CHANGE INSERTION TESTS //

        List<Integer> assignedCategoryIds = assignedCategoryDao.getAllCategoryIdsByBoardGameId(TestData.BOARD_GAME_1.getId());

        int groupId = TestData.GROUP_1.getId();
        int memberId = team1.getMembers().get(0).getId();
        int categoryId = assignedCategoryIds.get(0);

        GroupCategorySkillRating oldGroupCategorySkillRating = LiveDataTestUtil.getValue(
                groupCategorySkillRatingDao.getGroupCategorySkillRatingByGroupIdAndCategoryIdAndMemberId(TestData.GROUP_1.getId(), categoryId, memberId));

        // METHOD RUN //

        //Runs on background thread, so sleep to give it time.
        gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
        TimeUnit.MILLISECONDS.sleep(300);

        // METHOD FINISH RUN //

        // SKILL RATING CHANGE INSERTION TESTS //
        int noOfPlayers = team1.getMembers().size() + team2.getMembers().size();
        int noOfNewSkillRatingChanges = assignedCategoryIds.size() * noOfPlayers;

        List<PlayerSkillRatingChange> playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());

        assertThat(playerSkillRatingChanges.size(), is(TestData.PLAYER_SKILL_RATING_CHANGES.size() + noOfNewSkillRatingChanges));

        GroupCategorySkillRating newGroupCategorySkillRating = LiveDataTestUtil.getValue(
                groupCategorySkillRatingDao.getGroupCategorySkillRatingByGroupIdAndCategoryIdAndMemberId(TestData.GROUP_1.getId(), categoryId, memberId));

        GameRecord gameRecordFromDao = gameRecordDao.findNonLiveDataGameRecordByRecordId(groupId, TestData.BOARD_GAME_1.getBgName(), calendar.getTimeInMillis());

        int playerTeamId = playerTeamDao.getNonLivePlayerTeamIdByTeamNumberAndRecordId(team1.getTeamNo(), gameRecordFromDao.getId());
        int playerId = playerDao.getPlayerIdByPlayerTeamIdAndMemberNickname(playerTeamId, team1.getMembers().get(0).getNickname());
        String categoryName = bgCategoryDao.getNonLiveCategoryNameByCategoryId(categoryId);
        PlayerSkillRatingChange playerSkillRatingChange = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getPlayerSkillRatingChangeByPlayerIdAndCategoryName(playerId, categoryName));

        // Calculate the change that should have occurred and round to 2 decimal places, rounded up, to match how it is stored in the database.
        double calculatedRatingChange = newGroupCategorySkillRating.getSkillRating() - playerSkillRatingChange.getOldRating();
        BigDecimal calculatedRatingChangeBigDecimal = new BigDecimal(calculatedRatingChange).setScale(2, RoundingMode.HALF_UP);

        assertThat(playerSkillRatingChange.getOldRating(), is(oldGroupCategorySkillRating.getSkillRating()));
        assertThat(playerSkillRatingChange.getRatingChange(), is(calculatedRatingChangeBigDecimal.doubleValue()));

        gameRecordRepository.deleteGameRecord(gameRecordFromDao);
        TimeUnit.MILLISECONDS.sleep(300);

        playerSkillRatingChange = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getPlayerSkillRatingChangeByPlayerIdAndCategoryName(playerId, categoryName));
        assertNull(playerSkillRatingChange);
    }

    // TESTS FOR THE HELPER METHODS. NEED TO MAKE THE HELPER METHODS PUBLIC BEFORE UNCOMMENTING THESE //

    @Test
    public void calculateCompetitiveScoresThenCooperativeScores() {
        //Calculate scores with repository for this group
        List<TeamOfPlayers> teamsOfPlayersRepository = new ArrayList<TeamOfPlayers>();
        teamsOfPlayersRepository.add(new TeamOfPlayers(1, 1));
        teamsOfPlayersRepository.add(new TeamOfPlayers(2,1));
        teamsOfPlayersRepository.add(new TeamOfPlayers(3, 3));
        teamsOfPlayersRepository.add(new TeamOfPlayers(4, 4));

        //Calculate scores with calculator for this group
        List<TeamOfPlayers> teamsOfPlayersCalculator = new ArrayList<TeamOfPlayers>(teamsOfPlayersRepository);

        //Calculate and test competitive scores
        GameRecord gameRecord = new GameRecord(1, "Test", 5, new Date(), true, PlayMode.PlayModeEnum.COMPETITIVE, 4);
        gameRecordRepository.calculateScores(gameRecord, teamsOfPlayersRepository);

        Calculator calculator = new Calculator();
        calculator.calculateCompetitiveScores(gameRecord.getDifficulty(), teamsOfPlayersCalculator);

        for(TeamOfPlayers teamOfPlayersCalculator : teamsOfPlayersCalculator) {
            for (TeamOfPlayers teamOfPlayersRepository : teamsOfPlayersRepository) {
                if (teamOfPlayersRepository.getTeamNo() == teamOfPlayersCalculator.getTeamNo()) {
                    assertThat(teamOfPlayersRepository.getScore(), is(teamOfPlayersCalculator.getScore()));
                }
            }
        }

        //Calculate and test cooperative scores
        gameRecord.setPlayModePlayed(PlayMode.PlayModeEnum.COOPERATIVE);
        gameRecord.setWon(true);

        gameRecordRepository.calculateScores(gameRecord, teamsOfPlayersRepository);
        calculator.calculateCooperativeSolitaireScores(gameRecord.getDifficulty(), teamsOfPlayersCalculator, gameRecord.getWon());

        for (TeamOfPlayers teamOfPlayersCalculator :teamsOfPlayersCalculator) {
            for (TeamOfPlayers teamOfPlayersRepository : teamsOfPlayersRepository) {
                if (teamOfPlayersCalculator.getTeamNo() == teamOfPlayersRepository.getTeamNo()) {
                    assertThat(teamOfPlayersRepository.getScore(), is(teamOfPlayersCalculator.getScore()));
                }
            }
        }
    }

    @Test
    public void insertScoreForPlayer() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.MONTH, 8 - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 14);

        GameRecord gameRecord = new GameRecord(TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), 5, calendar.getTime(), true, PlayMode.PlayModeEnum.COMPETITIVE, 4);
        Member member = TestData.MEMBER_1;
        int addScore = 50;

        Score score = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getId(), member.getId()));
        assertThat(score, is(TestData.SCORE_1));
        assertThat(score.getScore(), is(TestData.SCORE_1.getScore()));

        gameRecordRepository.insertScore(gameRecord, member, addScore);
        TimeUnit.MILLISECONDS.sleep(100);

        score = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getId(), member.getId()));
        assertThat(score.getId(), is(TestData.SCORE_1.getId()));
        assertThat(score.getScore(), is(TestData.SCORE_1.getScore() + addScore));
    }

    @Test
    public void calculateTeamAverageRating() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));

        List<Member> members = new ArrayList<>();
        members.add(TestData.MEMBER_1);
        members.add(TestData.MEMBER_4);

        int groupId = TestData.GROUP_1.getId();
        int categoryId = TestData.BG_CATEGORY_1.getId();

        double avgRating = gameRecordRepository.calculateTeamAverageRating(groupId, categoryId, members);

        double manualAvgRating = 0.0;
        for (Member member : members) {
            double rating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(groupId, categoryId, member.getId());
            manualAvgRating += rating;
        }

        manualAvgRating = manualAvgRating / members.size();

        assertThat(avgRating, is(manualAvgRating));
    }

    @Test
    public void calculateAndAssignTeamsAverageRatings() {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));

        TeamOfPlayers team1 = new TeamOfPlayers(1, 1);
        List<Member> team1Members = new ArrayList<>();
        team1Members.add(TestData.MEMBER_1);
        team1Members.add(TestData.MEMBER_4);
        team1.setMembers(team1Members);

        TeamOfPlayers team2 = new TeamOfPlayers(2, 1);
        List<Member> team2Members = new ArrayList<>();
        team2Members.add(TestData.MEMBER_5);
        team2Members.add(TestData.MEMBER_6);
        team2.setMembers(team2Members);

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
        teamsOfPlayers.add(team1);
        teamsOfPlayers.add(team2);

        GameRecord gameRecord = new GameRecord(TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), 5, new Date(), true, PlayMode.PlayModeEnum.COMPETITIVE, 2);

        gameRecordRepository.calculateAndAssignTeamsAverageRatings(gameRecord, teamsOfPlayers);

        for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
            for (GroupCategoryRatingChange groupCategoryRatingChange : teamOfPlayers.getGroupCategoryRatingChanges()) {
                int categoryId = groupCategoryRatingChange.getCategoryId();
                double avgEloRating = -999999.99;

                if (teamOfPlayers.getTeamNo() == team1.getTeamNo()) {
                    avgEloRating = gameRecordRepository.calculateTeamAverageRating(gameRecord.getGroupId(), categoryId, team1Members);
                } else if (teamOfPlayers.getTeamNo() == team2.getTeamNo()) {
                    avgEloRating = gameRecordRepository.calculateTeamAverageRating(gameRecord.getGroupId(), categoryId, team2Members);
                } else {
                    fail("Unrecognised team number in list of teams.");
                }

                assertThat(groupCategoryRatingChange.getAvgEloRating(), is(avgEloRating));
            }
        }
    }

    @Test
    public void getCategoryRatingsMap() {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();

        TeamOfPlayers team1 = new TeamOfPlayers(1, 1);
        List<Member> team1Members = new ArrayList<>();
        team1Members.add(TestData.MEMBER_1);
        team1Members.add(TestData.MEMBER_4);
        team1.setMembers(team1Members);

        TeamOfPlayers team2 = new TeamOfPlayers(2, 1);
        List<Member> team2Members = new ArrayList<>();
        team2Members.add(TestData.MEMBER_5);
        team2Members.add(TestData.MEMBER_6);
        team2.setMembers(team2Members);

        TeamOfPlayers team3 = new TeamOfPlayers(3, 3);
        List<Member> team3Members = new ArrayList<>();
        team3Members.add(TestData.MEMBER_7);
        team3.setMembers(team3Members);

        TeamOfPlayers team4 = new TeamOfPlayers(4, 4);
        List<Member> team4Members = new ArrayList<>();
        team4Members.add(TestData.MEMBER_8);
        team4.setMembers(team4Members);

        teamsOfPlayers.add(team1);
        teamsOfPlayers.add(team2);
        teamsOfPlayers.add(team3);
        teamsOfPlayers.add(team4);

        GameRecord gameRecord = new GameRecord(TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), 5, new Date(), true, PlayMode.PlayModeEnum.COMPETITIVE, 4);

        gameRecordRepository.calculateAndAssignTeamsAverageRatings(gameRecord, teamsOfPlayers);
        Map<Integer, List<GroupCategoryRatingChange>> groupCategoryRatingChangesMap = gameRecordRepository.createCategoryRatingChangesMap(teamsOfPlayers);
        List<Integer> assignedCategoryIds = assignedCategoryDao.getAllCategoryIdsByBoardGameId(TestData.BOARD_GAME_1.getId());

        assertThat(groupCategoryRatingChangesMap.keySet().size(), is(assignedCategoryIds.size()));

        int categoryId = assignedCategoryIds.get(0);

        for (GroupCategoryRatingChange groupCategoryRatingChange : groupCategoryRatingChangesMap.get(categoryId)) {
            int teamNo = groupCategoryRatingChange.getTeamNo();
            double avgEloRating = -999999.99;

            if (teamNo == team1.getTeamNo()) {
                avgEloRating = gameRecordRepository.calculateTeamAverageRating(gameRecord.getGroupId(),categoryId, team1Members);
            } else if (teamNo == team2.getTeamNo()) {
                avgEloRating = gameRecordRepository.calculateTeamAverageRating(gameRecord.getGroupId(),categoryId, team2Members);
            } else if (teamNo == team3.getTeamNo()) {
                avgEloRating = gameRecordRepository.calculateTeamAverageRating(gameRecord.getGroupId(),categoryId, team3Members);
            } else if (teamNo == team4.getTeamNo()) {
                avgEloRating = gameRecordRepository.calculateTeamAverageRating(gameRecord.getGroupId(),categoryId, team4Members);
            } else {
                fail("Unrecognised team number in list of teams.");
            }

            assertThat(groupCategoryRatingChange.getAvgEloRating(), is(avgEloRating));
        }
    }

    @Test
    public void updateMemberSkillRatingsAndGetNewRatings() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));

        int groupId = TestData.GROUP_1.getId();
        int memberId = TestData.MEMBER_1.getId();

        GroupCategoryRatingChange bgCategory1Change = new GroupCategoryRatingChange(1, TestData.BG_CATEGORY_1.getId(), TestData.BG_CATEGORY_1.getCategoryName(), 1, 1500.0);
        double category1RatingChange = 15.00;
        bgCategory1Change.setEloRatingChange(category1RatingChange);

        GroupCategoryRatingChange bgCategory2Change = new GroupCategoryRatingChange(1, TestData.BG_CATEGORY_2.getId(), TestData.BG_CATEGORY_2.getCategoryName(), 1, 1500.0);
        double category2RatingChange = 23.45;
        bgCategory2Change.setEloRatingChange(category2RatingChange);

        List<GroupCategoryRatingChange> groupCategoryRatingChanges = new ArrayList<>();
        groupCategoryRatingChanges.add(bgCategory1Change);
        groupCategoryRatingChanges.add(bgCategory2Change);

        double originalCategory1Rating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(groupId, TestData.BG_CATEGORY_1.getId(), memberId);
        double originalCategory2Rating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(groupId, TestData.BG_CATEGORY_2.getId(), memberId);

        gameRecordRepository.updateMemberSkillRatings(groupId, memberId, groupCategoryRatingChanges);
        TimeUnit.MILLISECONDS.sleep(100);

        double updatedCategory1Rating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(groupId, TestData.BG_CATEGORY_1.getId(), memberId);
        double updatedCategory2Rating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(groupId, TestData.BG_CATEGORY_2.getId(), memberId);

        assertThat(updatedCategory1Rating, is(originalCategory1Rating + category1RatingChange));
        assertThat(updatedCategory2Rating, is(originalCategory2Rating + category2RatingChange));
    }

    @Test
    public void getMemberCategorySkillRatingWhenMemberHasRating() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));

        int groupId = TestData.GROUP_CATEGORY_SKILL_RATING_1.getGroupId();
        int categoryId = TestData.GROUP_CATEGORY_SKILL_RATING_1.getCategoryId();
        int memberId = TestData.GROUP_CATEGORY_SKILL_RATING_1.getMemberId();
        double rating = gameRecordRepository.getMemberCategorySkillRating(groupId, categoryId, memberId);

        assertThat(rating, is (TestData.GROUP_CATEGORY_SKILL_RATING_1.getSkillRating()));
    }

    @Test
    public void getMemberCategorySkillRatingWhenMemberDoesntHaveRating() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));

        int groupId = TestData.GROUP_2.getId();
        int categoryId = TestData.BG_CATEGORY_1.getId();
        int memberId = TestData.MEMBER_1.getId();

        GroupCategorySkillRating groupCategorySkillRating = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getGroupCategorySkillRatingByGroupIdAndCategoryIdAndMemberId(groupId, categoryId, memberId));
        assertNull(groupCategorySkillRating);

        double rating = gameRecordRepository.getMemberCategorySkillRating(groupId, categoryId, memberId);
        assertThat(rating, is(1500.00));

        groupCategorySkillRating = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getGroupCategorySkillRatingByGroupIdAndCategoryIdAndMemberId(groupId, categoryId, memberId));
        assertThat(groupCategorySkillRating.getSkillRating(), is(1500.00));
    }

    @Test
    public void recordMemberSkillRatingChangesForPlayerWithSkillRatingsAlready() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        int groupId = TestData.GROUP_1.getId();
        int memberId = TestData.MEMBER_1.getId();
        int playerId = TestData.PLAYER_1.getId();

        List<GroupCategoryRatingChange> groupCategoryRatingChanges = new ArrayList<>();

        double oldEloRating1 = gameRecordRepository.getMemberCategorySkillRating(groupId, TestData.BG_CATEGORY_1.getId(), memberId);
        double eloRatingChange1 = 12.24;

        GroupCategoryRatingChange groupCategoryRatingChange1 =
                new GroupCategoryRatingChange(1, TestData.BG_CATEGORY_1.getId(), TestData.BG_CATEGORY_1.getCategoryName(), 1, 1500.0);
        groupCategoryRatingChange1.setEloRatingChange(eloRatingChange1);

        double oldEloRating2 = gameRecordRepository.getMemberCategorySkillRating(groupId, TestData.BG_CATEGORY_2.getId(), memberId);
        double eloRatingChange2 = 23.24;

        GroupCategoryRatingChange groupCategoryRatingChange2 =
                new GroupCategoryRatingChange(1, TestData.BG_CATEGORY_2.getId(), TestData.BG_CATEGORY_2.getCategoryName(), 2, 1550.0);
        groupCategoryRatingChange2.setEloRatingChange(eloRatingChange2);

        groupCategoryRatingChanges.add(groupCategoryRatingChange1);
        groupCategoryRatingChanges.add(groupCategoryRatingChange2);

        gameRecordRepository.recordMemberSkillRatingChanges(groupId, memberId, playerId, groupCategoryRatingChanges);

        List<PlayerSkillRatingChange> playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());

        assertThat(playerSkillRatingChanges.size(), is(groupCategoryRatingChanges.size()));

        assertThat(playerSkillRatingChanges.get(0).getOldRating(), is(oldEloRating1));
        assertThat(playerSkillRatingChanges.get(0).getRatingChange(), is(eloRatingChange1));

        assertThat(playerSkillRatingChanges.get(1).getOldRating(), is(oldEloRating2));
        assertThat(playerSkillRatingChanges.get(1).getRatingChange(), is(eloRatingChange2));
    }

    @Test
    public void recordMemberSkillRatingChangesForPlayerWithoutSkillRatings() throws InterruptedException {
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        int groupId = TestData.GROUP_2.getId();
        int memberId = TestData.MEMBER_2.getId();
        int playerId = TestData.PLAYER_6.getId();

        List<GroupCategoryRatingChange> groupCategoryRatingChanges = new ArrayList<>();

        double oldEloRating1 = gameRecordRepository.getMemberCategorySkillRating(groupId, TestData.BG_CATEGORY_1.getId(), memberId);
        double eloRatingChange1 = 12.24;

        assertThat(oldEloRating1, is(TestData.GROUP_CATEGORY_SKILL_RATING_21.getSkillRating()));

        GroupCategoryRatingChange groupCategoryRatingChange1 =
                new GroupCategoryRatingChange(1, TestData.BG_CATEGORY_1.getId(), TestData.BG_CATEGORY_1.getCategoryName(), 1, 1500.0);
        groupCategoryRatingChange1.setEloRatingChange(eloRatingChange1);

        assertFalse(groupCategorySkillRatingDao.containsGroupCategorySkillRating(groupId, TestData.BG_CATEGORY_2.getId(), memberId));

        double oldEloRating2 = gameRecordRepository.getMemberCategorySkillRating(groupId, TestData.BG_CATEGORY_2.getId(), memberId);
        double eloRatingChange2 = 23.24;

        assertTrue(groupCategorySkillRatingDao.containsGroupCategorySkillRating(groupId, TestData.BG_CATEGORY_2.getId(), memberId));
        assertThat(oldEloRating2, is(1500.00));

        GroupCategoryRatingChange groupCategoryRatingChange2 =
                new GroupCategoryRatingChange(1, TestData.BG_CATEGORY_2.getId(), TestData.BG_CATEGORY_2.getCategoryName(), 2, 1550.0);
        groupCategoryRatingChange2.setEloRatingChange(eloRatingChange2);

        assertFalse(groupCategorySkillRatingDao.containsGroupCategorySkillRating(groupId, TestData.BG_CATEGORY_3.getId(), memberId));

        double oldEloRating3 = gameRecordRepository.getMemberCategorySkillRating(groupId, TestData.BG_CATEGORY_3.getId(), memberId);
        double eloRatingChange3 = 7.53;

        assertTrue(groupCategorySkillRatingDao.containsGroupCategorySkillRating(groupId, TestData.BG_CATEGORY_3.getId(), memberId));
        assertThat(oldEloRating3, is(1500.00));

        GroupCategoryRatingChange groupCategoryRatingChange3 =
                new GroupCategoryRatingChange(1, TestData.BG_CATEGORY_3.getId(), TestData.BG_CATEGORY_3.getCategoryName(), 2, 1550.0);
        groupCategoryRatingChange3.setEloRatingChange(eloRatingChange3);

        groupCategoryRatingChanges.add(groupCategoryRatingChange1);
        groupCategoryRatingChanges.add(groupCategoryRatingChange2);
        groupCategoryRatingChanges.add(groupCategoryRatingChange3);

        gameRecordRepository.recordMemberSkillRatingChanges(groupId, memberId, playerId, groupCategoryRatingChanges);

        List<PlayerSkillRatingChange> playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());

        assertThat(playerSkillRatingChanges.size(), is(groupCategoryRatingChanges.size()));

        assertThat(playerSkillRatingChanges.get(0).getOldRating(), is(oldEloRating1));
        assertThat(playerSkillRatingChanges.get(0).getRatingChange(), is(eloRatingChange1));

        assertThat(playerSkillRatingChanges.get(1).getOldRating(), is(oldEloRating2));
        assertThat(playerSkillRatingChanges.get(1).getRatingChange(), is(eloRatingChange2));

        assertThat(playerSkillRatingChanges.get(2).getOldRating(), is(oldEloRating3));
        assertThat(playerSkillRatingChanges.get(2).getRatingChange(), is(eloRatingChange3));
    }

    /*
    @Test
    public void calculateAndAssignSkillRatingChanges() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));

        TeamOfPlayers team1 = new TeamOfPlayers(1, 1);
        List<Member> team1Members = new ArrayList<>();
        team1Members.add(TestData.MEMBER_1);
        team1Members.add(TestData.MEMBER_4);
        team1.setMembers(team1Members);

        TeamOfPlayers team2 = new TeamOfPlayers(2, 1);
        List<Member> team2Members = new ArrayList<>();
        team2Members.add(TestData.MEMBER_5);
        team2Members.add(TestData.MEMBER_6);
        team2.setMembers(team2Members);

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
        teamsOfPlayers.add(team1);
        teamsOfPlayers.add(team2);

        GameRecord gameRecord = new GameRecord(TestData.GROUP_1.getId(), TestData.BOARD_GAME_1.getBgName(), 5, new Date(), true, PlayMode.PlayModeEnum.COMPETITIVE, 2);
        int recordId = (int) gameRecordDao.insert(gameRecord);
        gameRecord = LiveDataTestUtil.getValue(gameRecordDao.findLiveDataGameRecordByRecordId(recordId));

        List<Integer> bg1AssignedCategoryIds = assignedCategoryDao.getAllCategoryIdsByBoardGameId(TestData.BOARD_GAME_1.getId());

        Map<Integer, Double> member1Ratings = new HashMap<>();
        Map<Integer, Double> member4Ratings = new HashMap<>();

        for (int assignedCategoryId : bg1AssignedCategoryIds) {
            for (Member member : team1Members) {
                int memberId = member.getId();
                double eloRating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(gameRecord.getGroupId(), assignedCategoryId, memberId);

                if (memberId == TestData.MEMBER_1.getId()) {
                    member1Ratings.put(assignedCategoryId, eloRating);
                } else if (memberId == TestData.MEMBER_4.getId()) {
                    member4Ratings.put(assignedCategoryId, eloRating);
                } else {
                    fail("Member id that doesn't belong in team1 detected.");
                }
            }
        }

        gameRecordRepository.calculateAndAssignSkillRatingChanges(gameRecord, teamsOfPlayers);

        for (GroupCategoryRatingChange groupCategoryRatingChange : team1.getGroupCategoryRatingChanges()) {
            int categoryId = groupCategoryRatingChange.getCategoryId();
            double eloRatingChange = groupCategoryRatingChange.getEloRatingChange();

            for (Member member : team1.getMembers()) {
                int memberId = member.getId();
                double newEloRating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(gameRecord.getGroupId(), categoryId, member.getId());
                double oldEloRating = -999999.99;

                if (memberId == TestData.MEMBER_1.getId()) {
                    oldEloRating = member1Ratings.get(categoryId);
                } else if (memberId == TestData.MEMBER_4.getId()) {
                    oldEloRating = member4Ratings.get(categoryId);
                } else {
                    fail("Member id that doesn't belong in team1 detected.");
                }

                assertThat(newEloRating - eloRatingChange, is(oldEloRating));
            }
        }
    }

     */

}
