package com.floatingpanda.scoreboard.heuristics;

import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.daos.AssignedCategoryDao;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.daos.GroupCategorySkillRatingDao;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.daos.GroupMemberDao;
import com.floatingpanda.scoreboard.data.daos.GroupMonthlyScoreDao;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.daos.PlayModeDao;
import com.floatingpanda.scoreboard.data.daos.PlayerDao;
import com.floatingpanda.scoreboard.data.daos.PlayerSkillRatingChangeDao;
import com.floatingpanda.scoreboard.data.daos.PlayerTeamDao;
import com.floatingpanda.scoreboard.data.daos.ScoreDao;
import com.floatingpanda.scoreboard.data.entities.AssignedCategory;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.GroupCategorySkillRating;
import com.floatingpanda.scoreboard.data.entities.GroupMember;
import com.floatingpanda.scoreboard.data.entities.GroupMonthlyScore;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerSkillRatingChange;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.data.entities.Score;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.repositories.BgCategoryRepository;
import com.floatingpanda.scoreboard.repositories.BoardGameRepository;
import com.floatingpanda.scoreboard.repositories.GameRecordRepository;
import com.floatingpanda.scoreboard.repositories.GroupCategorySkillRatingRepository;
import com.floatingpanda.scoreboard.repositories.GroupMonthlyScoreRepository;
import com.floatingpanda.scoreboard.repositories.GroupRepository;
import com.floatingpanda.scoreboard.repositories.MemberRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AverageDatabaseTests {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private AssignedCategoryDao assignedCategoryDao;
    private BgCategoryDao bgCategoryDao;
    private BoardGameDao boardGameDao;
    private GameRecordDao gameRecordDao;
    private GroupCategorySkillRatingDao groupCategorySkillRatingDao;
    private GroupDao groupDao;
    private GroupMemberDao groupMemberDao;
    private GroupMonthlyScoreDao groupMonthlyScoreDao;
    private MemberDao memberDao;
    private PlayerDao playerDao;
    private PlayerSkillRatingChangeDao playerSkillRatingChangeDao;
    private PlayerTeamDao playerTeamDao;
    private PlayModeDao playModeDao;
    private ScoreDao scoreDao;

    private BgCategoryRepository bgCategoryRepository;
    private BoardGameRepository boardGameRepository;
    private GameRecordRepository gameRecordRepository;
    private GroupCategorySkillRatingRepository groupCategorySkillRatingRepository;
    private GroupMonthlyScoreRepository groupMonthlyScoreRepository;
    private GroupRepository groupRepository;
    private MemberRepository memberRepository;

    private final int TOTAL_MEMBERS = 21;
    private final int TOTAL_BG_CATEGORIES = 12;
    private final int TOTAL_BOARD_GAMES = 20;
    private final int TOTAL_PLAY_MODES = 60;
    //2 categories assigned per board game.
    private final int TOTAL_ASSIGNED_CATEGORIES = TOTAL_BOARD_GAMES * 2;
    private final int TOTAL_GROUPS = 3;
    //This should always be a whole number, no rounding or decimal points required.
    private final int GROUP_MEMBERS_PER_GROUP = TOTAL_MEMBERS / TOTAL_GROUPS;
    //Each member is assigned to 1 group.
    private final int TOTAL_GROUP_MEMBERS = TOTAL_MEMBERS;
    private final int GAME_RECORDS_PER_GROUP = 30;
    private final int TOTAL_GAME_RECORDS = GAME_RECORDS_PER_GROUP * TOTAL_GROUPS;

    // For each game record added there is
    // - 4 player team entries
    private final int PLAYER_TEAMS_PER_GAME_RECORD = 4;
    private final int TOTAL_PLAYER_TEAMS = PLAYER_TEAMS_PER_GAME_RECORD * TOTAL_GAME_RECORDS;
    // - 4 player entries (each team has a single player)
    private final int TOTAL_PLAYERS = PLAYER_TEAMS_PER_GAME_RECORD * TOTAL_GAME_RECORDS;
    // - 2 category skill rating change entries per player entry, so in this case 8 category
    //  skill rating entries per game record
    private final int TOTAL_PLAYER_SKILL_RATING_CHANGES = 2 * TOTAL_PLAYERS;
    // - 1 score entry per player entry
    private final int TOTAL_SCORES = TOTAL_PLAYERS;

    // We also add 1 group monthly score entry per month per group, thus 36 monthly score entries total.
    private final int TOTAL_GROUP_MONTHLY_SCORES = 12 * TOTAL_GROUPS;

    // Group Category Skill Rating entries are manually entered to make it easier to keep track of how
    //  many there are. Each member has a rating entry for each category.
    private final int TOTAL_GROUP_CATEGORY_SKILL_RATINGS = TOTAL_GROUP_MEMBERS * TOTAL_BG_CATEGORIES;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        assignedCategoryDao = db.assignedCategoryDao();
        bgCategoryDao = db.bgCategoryDao();
        boardGameDao = db.boardGameDao();
        gameRecordDao = db.gameRecordDao();
        groupCategorySkillRatingDao = db.groupCategorySkillRatingDao();
        groupDao = db.groupDao();
        groupMemberDao = db.groupMemberDao();
        groupMonthlyScoreDao = db.groupMonthlyScoreDao();
        memberDao = db.memberDao();
        playerDao = db.playerDao();
        playerSkillRatingChangeDao = db.playerSkillRatingChangeDao();
        playerTeamDao = db.playerTeamDao();
        playModeDao = db.playModeDao();
        scoreDao = db.scoreDao();

        bgCategoryRepository = new BgCategoryRepository(db);
        boardGameRepository = new BoardGameRepository(db);
        gameRecordRepository = new GameRecordRepository(db);
        groupCategorySkillRatingRepository = new GroupCategorySkillRatingRepository(db);
        groupMonthlyScoreRepository = new GroupMonthlyScoreRepository(db);
        groupRepository = new GroupRepository(db);
        memberRepository = new MemberRepository(db);

        String notes = "";
        String description = "";
        String imgFilePath = "";
        String houseRules = "";
        String bannerImgFilePath = "";

        int id = 1;

        //Members - 20 members.
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < TOTAL_MEMBERS; i++) {
            String nickname = "Test Member " + id;
            Member member = new Member(id, nickname, notes, imgFilePath, new Date());
            members.add(member);
            memberDao.insert(member);
            id++;
        }

        //Board Game Categories - 12 categories
        List<BgCategory> bgCategories = new ArrayList<>();
        for (int i = 0; i < TOTAL_BG_CATEGORIES; i++) {
            String categoryName = "Test Category " + id;
            BgCategory bgCategory = new BgCategory(id, categoryName);
            bgCategories.add(bgCategory);
            bgCategoryDao.insert(bgCategory);
            id++;
        }

        //Board Games - 20 board games
        List<BoardGame> boardGames = new ArrayList<>();
        for (int i = 0; i < TOTAL_BOARD_GAMES; i++) {
            String boardGameName = "Test Board Game " + id;
            BoardGame boardGame = new BoardGame(id, boardGameName, 3, 1, 8, BoardGame.TeamOption.TEAMS_AND_SOLOS_ALLOWED,
                    description, houseRules, notes, imgFilePath);
            boardGames.add(boardGame);
            boardGameDao.insert(boardGame);
            id++;
        }

        //Play Modes - 60 play modes
        for (BoardGame boardGame : boardGames) {
            PlayMode playMode = new PlayMode(boardGame.getId(), PlayMode.PlayModeEnum.COMPETITIVE);
            playModeDao.insert(playMode);

            playMode = new PlayMode(boardGame.getId(), PlayMode.PlayModeEnum.COOPERATIVE);
            playModeDao.insert(playMode);

            playMode = new PlayMode(boardGame.getId(), PlayMode.PlayModeEnum.SOLITAIRE);
            playModeDao.insert(playMode);
        }

        //Assigned Categories - 40 assigned categories
        for (int i = 0; i < boardGames.size(); i++) {
            BoardGame boardGame = boardGames.get(i);
            int boardGameId = boardGame.getId();

            int categoryIndex = i % bgCategories.size();
            int categoryId = bgCategories.get(categoryIndex).getId();

            AssignedCategory assignedCategory = new AssignedCategory(boardGameId, categoryId);
            assignedCategoryDao.insert(assignedCategory);

            categoryIndex++;
            if (categoryIndex >= bgCategories.size()) {
                categoryIndex = 0;
            }

            categoryId = bgCategories.get(categoryIndex).getId();
            assignedCategory = new AssignedCategory(boardGameId, categoryId);
            assignedCategoryDao.insert(assignedCategory);
        }

        //Groups - 3 groups
        List<Group> groups = new ArrayList<>();
        for (int i = 0; i < TOTAL_GROUPS; i++) {
            String groupName = "Test Group " + id;
            Group group = new Group(id, groupName, notes, description, imgFilePath, bannerImgFilePath, new Date());
            groups.add(group);
            groupDao.insert(group);
            id++;
        }

        //Group Members - 21 group members, 7 per group
        List<Member> group1Members = new ArrayList<>();
        List<Member> group2Members = new ArrayList<>();
        List<Member> group3Members = new ArrayList<>();

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            int memberId = member.getId();

            int groupIndex = i % groups.size();
            int groupId = groups.get(groupIndex).getId();

            GroupMember groupMember = new GroupMember(groupId, memberId);
            groupMemberDao.insert(groupMember);

            if (groupIndex == 0) {
                group1Members.add(member);
            } else if (groupIndex == 1) {
                group2Members.add(member);
            } else {
                group3Members.add(member);
            }
        }

        //GroupCategorySkillRating groupCategorySkillRating = new GroupCategorySkillRating(id, groupId, memberId, categoryId, skillRating, gamesRated);

        //Game records - 30 records for each group. Each record has 4 teams with 1 player each.
        // Each record is competitive. GameRecordRepository is used instead of DAOs because it is simpler.

        // For each game record added this way, we add
        // - 1 game record entry
        // - 4 player team entries
        // - 4 player entries
        // - 2 category skill rating change entries per player entry, so in this case 8 category
        //  skill rating entries per game record
        // - 1 score entry per player entry

        // 30 records are added per group, so that's
        // - 90 game record entries
        // - 360 player team entries
        // - 360 player entries
        // - 720 category skill rating change entries
        // - 360 score entries

        // We also add 1 monthly scores entry per month per group, thus 36 monthly score entries total.

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 0);

        Log.w("Tests", "Before records");

        //TODO make this general, rather than group 1, group 2, group 3, so I can reuse it for the
        // extreme scenario with ease.
        //Group 1
        Group group = groups.get(0);
        for (int i = 0; i < GAME_RECORDS_PER_GROUP; i++) {
            Log.w("Group1Records", "i = " + i);
            int groupId = group.getId();

            int boardGameIndex = i % boardGames.size();
            BoardGame boardGame = boardGames.get(boardGameIndex);
            String boardGameName = boardGame.getBgName();
            int difficulty = boardGame.getDifficulty();

            int calendarMonth = i % 12;
            calendar.set(Calendar.MONTH, calendarMonth);
            Date date = calendar.getTime();

            GameRecord gameRecord = new GameRecord(id, groupId, boardGameName, difficulty, date, false, PlayMode.PlayModeEnum.COMPETITIVE, 4, false);

            List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
            for (int j = 1; j <= PLAYER_TEAMS_PER_GAME_RECORD; j++) {
                int group1MemberIndex = (i + j) % group1Members.size();
                Member teamMember = group1Members.get(group1MemberIndex);

                List<Member> teamMembers = new ArrayList<>();
                teamMembers.add(teamMember);

                TeamOfPlayers teamOfPlayers = new TeamOfPlayers(j, j, 0, teamMembers);
                teamsOfPlayers.add(teamOfPlayers);
            }

            gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
            id++;
        }

        //Group 2
        group = groups.get(1);
        for (int i = 0; i < GAME_RECORDS_PER_GROUP; i++) {
            Log.w("Group2Records", "i = " + i);
            int groupId = group.getId();

            int boardGameIndex = i % boardGames.size();
            BoardGame boardGame = boardGames.get(boardGameIndex);
            String boardGameName = boardGame.getBgName();
            int difficulty = boardGame.getDifficulty();

            int calendarMonth = i % 12;
            calendar.set(Calendar.MONTH, calendarMonth);
            Date date = calendar.getTime();

            GameRecord gameRecord = new GameRecord(id, groupId, boardGameName, difficulty, date, false, PlayMode.PlayModeEnum.COMPETITIVE, 4, false);

            List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
            for (int j = 1; j <= PLAYER_TEAMS_PER_GAME_RECORD; j++) {
                int group2MemberIndex = (i + j) % group2Members.size();
                Member teamMember = group2Members.get(group2MemberIndex);

                List<Member> teamMembers = new ArrayList<>();
                teamMembers.add(teamMember);

                TeamOfPlayers teamOfPlayers = new TeamOfPlayers(j, j, 0, teamMembers);
                teamsOfPlayers.add(teamOfPlayers);
            }

            gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
            id++;
        }

        //Group 3
        group = groups.get(2);
        for (int i = 0; i < GAME_RECORDS_PER_GROUP; i++) {
            Log.w("Group3Records", "i = " + i);
            int groupId = group.getId();

            int boardGameIndex = i % boardGames.size();
            BoardGame boardGame = boardGames.get(boardGameIndex);
            String boardGameName = boardGame.getBgName();
            int difficulty = boardGame.getDifficulty();

            int calendarMonth = i % 12;
            calendar.set(Calendar.MONTH, calendarMonth);
            Date date = calendar.getTime();

            GameRecord gameRecord = new GameRecord(id, groupId, boardGameName, difficulty, date, false, PlayMode.PlayModeEnum.COMPETITIVE, 4, false);

            List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
            for (int j = 1; j <= PLAYER_TEAMS_PER_GAME_RECORD; j++) {
                int group3MemberIndex = (i + j) % group3Members.size();
                Member teamMember = group3Members.get(group3MemberIndex);

                List<Member> teamMembers = new ArrayList<>();
                teamMembers.add(teamMember);

                TeamOfPlayers teamOfPlayers = new TeamOfPlayers(j, j, 0, teamMembers);
                teamsOfPlayers.add(teamOfPlayers);
            }

            gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
            id++;
        }

        try {
            testMembers();
            testBgCategories();
            testBoardGames();
            testPlayModes();
            testAssignedCategories();
            testGroups();
            testGroupMembers();
            testGroupMembersPerGroup();
            testGameRecords();
            testGameRecordsPerGroup();
            testPlayerTeams();
            testPlayerTeamsPerRecord();
            testPlayers();
            testPlayerSkillRatingChanges();
            testScores();
            testGroupMonthlyScores();
            testGroupCategorySkillRatings();
        } catch (Exception e) {

        }
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    private void testMembers() throws InterruptedException {
        Log.w("TestMembers", "Testing Members");
        List<Member> members = LiveDataTestUtil.getValue(memberDao.getAllLive());

        assertThat(members.size(), is(TOTAL_MEMBERS));
    }

    private void testBgCategories() throws InterruptedException {
        Log.w("TestCategories", "Testing Categories");
        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(bgCategoryDao.getAllLive());

        assertThat(bgCategories.size(), is(TOTAL_BG_CATEGORIES));
    }

    private void testBoardGames() throws InterruptedException {
        Log.w("TestBoardGames", "Testing Board Games");
        List<BoardGame> boardGames = LiveDataTestUtil.getValue(boardGameDao.getAllLive());

        assertThat(boardGames.size(), is(TOTAL_BOARD_GAMES));
    }

    private void testPlayModes() throws InterruptedException {
        Log.w("TestPlayModes", "Testing Play Modes");
        List<PlayMode> playModes = LiveDataTestUtil.getValue(playModeDao.getAll());

        assertThat(playModes.size(), is(TOTAL_PLAY_MODES));
    }

    private void testAssignedCategories() throws InterruptedException {
        Log.w("TestAssignedCategories", "Testing Assigned Categories");
        List<AssignedCategory> assignedCategories = LiveDataTestUtil.getValue(assignedCategoryDao.getAll());

        assertThat(assignedCategories.size(), is(TOTAL_ASSIGNED_CATEGORIES));
    }

    private void testGroups() throws InterruptedException {
        Log.w("TestGroups", "Testing Groups");
        List<Group> groups = LiveDataTestUtil.getValue(groupDao.getAll());

        assertThat(groups.size(), is(TOTAL_GROUPS));
    }

    private void testGroupMembers() throws InterruptedException {
        Log.w("TestGroupMembers", "Testing Group Members");
        List<GroupMember> groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());

        assertThat(groupMembers.size(), is(TOTAL_GROUP_MEMBERS));
    }

    private void testGroupMembersPerGroup() throws InterruptedException {
        Log.w("TestGroupMembersPerGroup", "Testing Group Members Per Group");
        List<Group> groups = LiveDataTestUtil.getValue(groupDao.getAll());

        for (Group group : groups) {
            int noOfGroupMembers = groupMemberDao.getNoOfGroupMembersByGroupId(group.getId());

            assertThat(noOfGroupMembers, is(GROUP_MEMBERS_PER_GROUP));
        }
    }

    private void testGameRecords() throws InterruptedException {
        Log.w("TestGameRecords", "Testing Game Records");
        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());

        assertThat(gameRecords.size(), is(TOTAL_GAME_RECORDS));
    }

    private void testGameRecordsPerGroup() throws InterruptedException {
        Log.w("TestGameRecordsPerGroup", "Testing Game Records Per Group");
        List<Group> groups = LiveDataTestUtil.getValue(groupDao.getAll());

        for (Group group : groups) {
            int noOfGameRecords = gameRecordDao.getNoOfGameRecordsByGroupId(group.getId());

            assertThat(noOfGameRecords, is(GAME_RECORDS_PER_GROUP));
        }
    }

    private void testPlayerTeams() throws InterruptedException {
        Log.w("TestPlayerTeams", "Testing Player Teams");
        List<PlayerTeam> playerTeams = LiveDataTestUtil.getValue(playerTeamDao.getAll());

        assertThat(playerTeams.size(), is(TOTAL_PLAYER_TEAMS));
    }

    private void testPlayerTeamsPerRecord() throws InterruptedException {
        Log.w("TestPlayerTeamsPerRecords", "Testing Player Teams Per Record");
        List<GameRecord> gameRecords = LiveDataTestUtil.getValue(gameRecordDao.getAll());
        GameRecord gameRecord = gameRecords.get(0);

        List<PlayerTeamWithPlayers> playerTeamsWithPlayers = LiveDataTestUtil.getValue(playerTeamDao.findPlayerTeamsWithPlayersByRecordId(gameRecord.getId()));

        assertThat(playerTeamsWithPlayers.size(), is(PLAYER_TEAMS_PER_GAME_RECORD));

        for (PlayerTeamWithPlayers playerTeamWithPlayers : playerTeamsWithPlayers) {
            List<Player> players = playerTeamWithPlayers.getPlayers();
            assertThat(players.size(), is(1));
        }
    }

    private void testPlayers() throws InterruptedException {
        Log.w("TestPlayers", "Testing Players");
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TOTAL_PLAYERS));
    }

    private void testPlayerSkillRatingChanges() throws InterruptedException {
        Log.w("TestPlayerSkillRatingChanges", "Testing Player Skill Rating Changes");
        List<PlayerSkillRatingChange> playerSkillRatingChanges = LiveDataTestUtil.getValue(playerSkillRatingChangeDao.getAll());

        assertThat(playerSkillRatingChanges.size(), is(TOTAL_PLAYER_SKILL_RATING_CHANGES));
    }

    private void testScores() throws InterruptedException {
        Log.w("TestScores", "Testing Scores");
        List<Score> scores = LiveDataTestUtil.getValue(scoreDao.getAll());

        assertThat(scores.size(), is(TOTAL_SCORES));
    }

    private void testGroupMonthlyScores() throws InterruptedException {
        Log.w("TestGroupMonthlyScores", "Testing Group Monthly Scores");
        List<GroupMonthlyScore> groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getAll());

        assertThat(groupMonthlyScores.size(), is(TOTAL_GROUP_MONTHLY_SCORES));
    }

    private void testGroupCategorySkillRatings() throws InterruptedException {
        Log.w("TestGroupCategorySkillRatings", "Testing Group Category Skill Ratings");
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getAll());

        assertThat(groupCategorySkillRatings.size(), is(TOTAL_GROUP_CATEGORY_SKILL_RATINGS));
    }

    @Test
    public void testMemberInsertionAndDeletion() {
        Member member = new Member("Member", "", "", new Date());

        memberDao.getAllLive().observeForever(new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> members) {
                Log.w("MemberTest" ,"Member list changed. New size: " + members.size());
            }
        });

        memberRepository.insert(member);

    }
}
