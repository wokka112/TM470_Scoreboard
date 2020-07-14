package com.floatingpanda.scoreboard;

import com.floatingpanda.scoreboard.data.entities.AssignedCategory;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.entities.PlayerSkillRatingChange;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.GroupCategorySkillRating;
import com.floatingpanda.scoreboard.data.entities.GroupMember;
import com.floatingpanda.scoreboard.data.entities.GroupMonthlyScore;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.data.entities.Score;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Utility class holding test values
 */
public class TestData {

    public static final BgCategory BG_CATEGORY_1 = new BgCategory(1, "Strategy");
    public static final BgCategory BG_CATEGORY_2 = new BgCategory(2, "Luck");
    public static final BgCategory BG_CATEGORY_3 = new BgCategory(3, "Ameritrash");
    //Category 4 should not be attached to anything by default. It is used simply to test updating board games with
    // categories and play modes, for which it needs to be unattached to anything to begin with.
    public static final BgCategory BG_CATEGORY_4 = new BgCategory(4, "Bloop");

    public static final List<BgCategory> BG_CATEGORIES = Arrays.asList(BG_CATEGORY_1, BG_CATEGORY_2, BG_CATEGORY_3);

    public static final Member MEMBER_1 = new Member(5, "Ragnar", "", "TBA", new Date());
    public static final Member MEMBER_2 = new Member(6, "Eggs", "", "TBA", new Date());
    public static final Member MEMBER_3 = new Member(7, "Bob", "", "TBA", new Date());
    public static final Member MEMBER_4 = new Member(8, "Jenkins", "", "TBA", new Date());
    public static final Member MEMBER_5 = new Member(9, "Jonesy", "", "TBA", new Date());
    public static final Member MEMBER_6 = new Member(10, "Johnny", "", "TBA", new Date());
    public static final Member MEMBER_7 = new Member(11, "Billy", "", "TBA", new Date());
    public static final Member MEMBER_8 = new Member(12, "Bonny", "", "TBA", new Date());

    public static final List<Member> MEMBERS = Arrays.asList(MEMBER_1, MEMBER_2, MEMBER_3, MEMBER_4, MEMBER_5, MEMBER_6, MEMBER_7, MEMBER_8);

    public static final BoardGame BOARD_GAME_1 = new BoardGame(13, "Medieval", 3,
            1, 8, BoardGame.TeamOption.TEAMS_AND_SOLOS_ALLOWED, "", "", "", "");
    public static final BoardGame BOARD_GAME_2 = new BoardGame(14, "Monopoly", 2,
            2, 6, BoardGame.TeamOption.NO_TEAMS, "", "", "", "");
    public static final BoardGame BOARD_GAME_3 = new BoardGame(15, "Go", 5,
            2, 2, BoardGame.TeamOption.TEAMS_ONLY, "", "", "", "");

    public static final List<BoardGame> BOARD_GAMES = Arrays.asList(BOARD_GAME_1, BOARD_GAME_2, BOARD_GAME_3);

    public static final Group GROUP_1 = new Group(16, "Ragnarok", "", "", "TBA", "TBA", new Date());
    public static final Group GROUP_2 = new Group(17, "The Monday Knights", "", "", "TBA", "TBA", new Date());
    public static final Group GROUP_3 = new Group(18, "The Thirds", "", "", "TBA", "TBA", new Date());

    public static final List<Group> GROUPS = Arrays.asList(GROUP_1, GROUP_2, GROUP_3);

    public static final AssignedCategory ASSIGNED_CATEGORY_1 = new AssignedCategory(BOARD_GAME_1.getId(), BG_CATEGORY_1.getId());
    public static final AssignedCategory ASSIGNED_CATEGORY_2 = new AssignedCategory(BOARD_GAME_2.getId(), BG_CATEGORY_2.getId());
    public static final AssignedCategory ASSIGNED_CATEGORY_3 = new AssignedCategory(BOARD_GAME_3.getId(), BG_CATEGORY_3.getId());
    public static final AssignedCategory ASSIGNED_CATEGORY_4 = new AssignedCategory(BOARD_GAME_3.getId(), BG_CATEGORY_1.getId());

    public static final List<AssignedCategory> ASSIGNED_CATEGORIES = Arrays.asList(ASSIGNED_CATEGORY_1, ASSIGNED_CATEGORY_2, ASSIGNED_CATEGORY_3, ASSIGNED_CATEGORY_4);

    private static final List<BgCategory> BOARD_GAME_1_BG_CATEGORIES = Arrays.asList(BG_CATEGORY_1);
    private static final List<BgCategory> BOARD_GAME_2_BG_CATEGORIES = Arrays.asList(BG_CATEGORY_2);
    private static final List<BgCategory> BOARD_GAME_3_BG_CATEGORIES = Arrays.asList(BG_CATEGORY_3, BG_CATEGORY_1);

    public static final BoardGameWithBgCategories BOARD_GAME_WITH_BG_CATEGORIES_1 = new BoardGameWithBgCategories(BOARD_GAME_1, BOARD_GAME_1_BG_CATEGORIES);
    public static final BoardGameWithBgCategories BOARD_GAME_WITH_BG_CATEGORIES_2 = new BoardGameWithBgCategories(BOARD_GAME_2, BOARD_GAME_2_BG_CATEGORIES);
    public static final BoardGameWithBgCategories BOARD_GAME_WITH_BG_CATEGORIES_3 = new BoardGameWithBgCategories(BOARD_GAME_3, BOARD_GAME_3_BG_CATEGORIES);

    public static final List<BoardGameWithBgCategories> BOARD_GAMES_WITH_BG_CATEGORIES = Arrays.asList(BOARD_GAME_WITH_BG_CATEGORIES_1, BOARD_GAME_WITH_BG_CATEGORIES_2,
            BOARD_GAME_WITH_BG_CATEGORIES_3);

    public static final PlayMode PLAY_MODE_1 = new PlayMode(BOARD_GAME_1.getId(), PlayMode.PlayModeEnum.COMPETITIVE);
    public static final PlayMode PLAY_MODE_2 = new PlayMode(BOARD_GAME_2.getId(), PlayMode.PlayModeEnum.COOPERATIVE);
    public static final PlayMode PLAY_MODE_3 = new PlayMode(BOARD_GAME_3.getId(), PlayMode.PlayModeEnum.SOLITAIRE);
    public static final PlayMode PLAY_MODE_4 = new PlayMode(BOARD_GAME_3.getId(), PlayMode.PlayModeEnum.COMPETITIVE);

    public static final List<PlayMode> PLAY_MODES = Arrays.asList(PLAY_MODE_1, PLAY_MODE_2, PLAY_MODE_3, PLAY_MODE_4);

    private static final List<PlayMode> BOARD_GAME_WITH_BG_CATEGORIES_1_PLAY_MODES = Arrays.asList(PLAY_MODE_1);
    private static final List<PlayMode> BOARD_GAME_WITH_BG_CATEGORIES_2_PLAY_MODES = Arrays.asList(PLAY_MODE_2);
    private static final List<PlayMode> BOARD_GAME_WITH_BG_CATEGORIES_3_PLAY_MODES = Arrays.asList(PLAY_MODE_3, PLAY_MODE_4);

    public static final BoardGameWithBgCategoriesAndPlayModes BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_1 =
            new BoardGameWithBgCategoriesAndPlayModes(BOARD_GAME_WITH_BG_CATEGORIES_1, BOARD_GAME_WITH_BG_CATEGORIES_1_PLAY_MODES);
    public static final BoardGameWithBgCategoriesAndPlayModes BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_2 =
            new BoardGameWithBgCategoriesAndPlayModes(BOARD_GAME_WITH_BG_CATEGORIES_2, BOARD_GAME_WITH_BG_CATEGORIES_2_PLAY_MODES);
    public static final BoardGameWithBgCategoriesAndPlayModes BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_3 =
            new BoardGameWithBgCategoriesAndPlayModes(BOARD_GAME_WITH_BG_CATEGORIES_3, BOARD_GAME_WITH_BG_CATEGORIES_3_PLAY_MODES);

    public static final List<BoardGameWithBgCategoriesAndPlayModes> BOARD_GAMES_WITH_BG_CATEGORIES_AND_PLAY_MODES =
            Arrays.asList(BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_1, BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_2, BOARD_GAME_WITH_BG_CATEGORIES_AND_PLAY_MODES_3);

    public static final GroupMember GROUP_MEMBER_1 = new GroupMember(TestData.GROUP_1.getId(), TestData.MEMBER_1.getId());
    public static final GroupMember GROUP_MEMBER_2 = new GroupMember(TestData.GROUP_2.getId(), TestData.MEMBER_2.getId());
    public static final GroupMember GROUP_MEMBER_3 = new GroupMember(TestData.GROUP_3.getId(), TestData.MEMBER_3.getId());
    public static final GroupMember GROUP_MEMBER_4 = new GroupMember(TestData.GROUP_3.getId(), TestData.MEMBER_1.getId());

    //These group members are used for adding a game record with players and player teams.
    public static final GroupMember GROUP_MEMBER_5 = new GroupMember(TestData.GROUP_1.getId(), TestData.MEMBER_4.getId());
    public static final GroupMember GROUP_MEMBER_6 = new GroupMember(TestData.GROUP_1.getId(), TestData.MEMBER_5.getId());
    public static final GroupMember GROUP_MEMBER_7 = new GroupMember(TestData.GROUP_1.getId(), TestData.MEMBER_6.getId());
    public static final GroupMember GROUP_MEMBER_8 = new GroupMember(TestData.GROUP_1.getId(), TestData.MEMBER_7.getId());
    public static final GroupMember GROUP_MEMBER_9 = new GroupMember(TestData.GROUP_1.getId(), TestData.MEMBER_8.getId());

    public static final List<GroupMember> GROUP_MEMBERS = Arrays.asList(GROUP_MEMBER_1, GROUP_MEMBER_2, GROUP_MEMBER_3, GROUP_MEMBER_4,
            GROUP_MEMBER_5, GROUP_MEMBER_6, GROUP_MEMBER_7, GROUP_MEMBER_8, GROUP_MEMBER_9);

    public static final GameRecord GAME_RECORD_1 = new GameRecord(19, GROUP_1.getId(), BOARD_GAME_1.getBgName(), BOARD_GAME_1.getDifficulty(), new Date(),
            true, PlayMode.PlayModeEnum.COMPETITIVE, 4, false);
    public static final GameRecord GAME_RECORD_2 = new GameRecord(20, GROUP_1.getId(), BOARD_GAME_1.getBgName(), BOARD_GAME_1.getDifficulty(), new Date(),
            true, PlayMode.PlayModeEnum.COMPETITIVE, 4, false);
    public static final GameRecord GAME_RECORD_3 = new GameRecord(21, GROUP_1.getId(), BOARD_GAME_2.getBgName(), BOARD_GAME_2.getDifficulty(), new Date(),
            true, PlayMode.PlayModeEnum.COOPERATIVE, 4, true);
    public static final GameRecord GAME_RECORD_4 = new GameRecord(22, GROUP_2.getId(), BOARD_GAME_3.getBgName(), BOARD_GAME_3.getDifficulty(), new Date(),
            false, PlayMode.PlayModeEnum.SOLITAIRE, 3, false);

    public static final List<GameRecord> GAME_RECORDS = Arrays.asList(GAME_RECORD_1, GAME_RECORD_2, GAME_RECORD_3, GAME_RECORD_4);

    public static final PlayerTeam PLAYER_TEAM_1 = new PlayerTeam(23, 1, GAME_RECORD_1.getId(), 1, 30);
    public static final PlayerTeam PLAYER_TEAM_2 = new PlayerTeam(24, 2, GAME_RECORD_1.getId(), 2, 20);
    public static final PlayerTeam PLAYER_TEAM_3 = new PlayerTeam(25, 3, GAME_RECORD_1.getId(), 2, 20);
    public static final PlayerTeam PLAYER_TEAM_4 = new PlayerTeam(26, 4, GAME_RECORD_1.getId(), 4, 0);
    public static final PlayerTeam PLAYER_TEAM_5 = new PlayerTeam(27, 1, GAME_RECORD_2.getId(), 1, 60);
    public static final PlayerTeam PLAYER_TEAM_6 = new PlayerTeam(28, 1, GAME_RECORD_3.getId(), 1, 20);

    public static final List<PlayerTeam> PLAYER_TEAMS = Arrays.asList(PLAYER_TEAM_1, PLAYER_TEAM_2, PLAYER_TEAM_3, PLAYER_TEAM_4, PLAYER_TEAM_5, PLAYER_TEAM_6);

    public static final Player PLAYER_1 = new Player(29, PLAYER_TEAM_1.getId(), MEMBER_1.getNickname());
    public static final Player PLAYER_2 = new Player(30, PLAYER_TEAM_2.getId(), MEMBER_2.getNickname());
    public static final Player PLAYER_3 = new Player(31, PLAYER_TEAM_2.getId(), MEMBER_3.getNickname());
    public static final Player PLAYER_4 = new Player(32, PLAYER_TEAM_5.getId(), MEMBER_2.getNickname());
    public static final Player PLAYER_5 = new Player(33, PLAYER_TEAM_6.getId(), MEMBER_1.getNickname());

    public static final List<Player> PLAYERS = Arrays.asList(PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4, PLAYER_5);

    public static final GroupMonthlyScore GROUP_MONTHLY_SCORE_1 = new GroupMonthlyScore(34, GROUP_1.getId(), 2019, 3, 8);
    public static final GroupMonthlyScore GROUP_MONTHLY_SCORE_2 = new GroupMonthlyScore(35, GROUP_1.getId(), 2020, 3, 8);
    public static final GroupMonthlyScore GROUP_MONTHLY_SCORE_3 = new GroupMonthlyScore(36, GROUP_1.getId(), 2020, 3, 9);
    public static final GroupMonthlyScore GROUP_MONTHLY_SCORE_4 = new GroupMonthlyScore(37, GROUP_1.getId(), 2020, 4, 10);
    public static final GroupMonthlyScore GROUP_MONTHLY_SCORE_5 = new GroupMonthlyScore(38, GROUP_3.getId(), 2020, 1, 2);
    public static final GroupMonthlyScore GROUP_MONTHLY_SCORE_6 = new GroupMonthlyScore(39, GROUP_2.getId(), 2019, 4, 12);
    public static final GroupMonthlyScore GROUP_MONTHLY_SCORE_7 = new GroupMonthlyScore(40, GROUP_2.getId(), 2020, 1, 1);

    public static final List<GroupMonthlyScore> GROUP_MONTHLY_SCORES = Arrays.asList(GROUP_MONTHLY_SCORE_1, GROUP_MONTHLY_SCORE_2, GROUP_MONTHLY_SCORE_3,
            GROUP_MONTHLY_SCORE_4, GROUP_MONTHLY_SCORE_5, GROUP_MONTHLY_SCORE_6, GROUP_MONTHLY_SCORE_7);

    //Monthly scores 1
    public static final Score SCORE_1 = new Score(41, GROUP_MONTHLY_SCORE_1.getId(), MEMBER_1.getId(), 70);
    public static final Score SCORE_2 = new Score(42, GROUP_MONTHLY_SCORE_1.getId(), MEMBER_4.getId(), 45);
    public static final Score SCORE_3 = new Score(43, GROUP_MONTHLY_SCORE_1.getId(), MEMBER_5.getId(), 60);

    //Monthly scores 2
    public static final Score SCORE_4 = new Score(44, GROUP_MONTHLY_SCORE_2.getId(), MEMBER_1.getId(), 40);
    public static final Score SCORE_5 = new Score(45, GROUP_MONTHLY_SCORE_2.getId(), MEMBER_4.getId(), 65);
    public static final Score SCORE_6 = new Score(46, GROUP_MONTHLY_SCORE_2.getId(), MEMBER_5.getId(), 80);

    //Monthly scores 3
    public static final Score SCORE_7 = new Score(47, GROUP_MONTHLY_SCORE_3.getId(), MEMBER_1.getId(), 90);
    public static final Score SCORE_8 = new Score(48, GROUP_MONTHLY_SCORE_3.getId(), MEMBER_4.getId(), 55);
    public static final Score SCORE_9 = new Score(49, GROUP_MONTHLY_SCORE_3.getId(), MEMBER_5.getId(), 30);

    //Monthly scores 4
    public static final Score SCORE_10 = new Score(50, GROUP_MONTHLY_SCORE_4.getId(), MEMBER_1.getId(), 100);
    public static final Score SCORE_11 = new Score(51, GROUP_MONTHLY_SCORE_4.getId(), MEMBER_4.getId(), 20);
    public static final Score SCORE_12 = new Score(52, GROUP_MONTHLY_SCORE_4.getId(), MEMBER_5.getId(), 5);

    //Monthly scores 5
    public static final Score SCORE_13 = new Score(53, GROUP_MONTHLY_SCORE_5.getId(), MEMBER_1.getId(), 30);
    public static final Score SCORE_14 = new Score(54, GROUP_MONTHLY_SCORE_5.getId(), MEMBER_3.getId(), 10);

    public static final List<Score> SCORES = Arrays.asList(SCORE_1, SCORE_2, SCORE_3, SCORE_4, SCORE_5, SCORE_6, SCORE_7, SCORE_8, SCORE_9,
            SCORE_10, SCORE_11, SCORE_12, SCORE_13, SCORE_14);

    //Group 1's skill ratings
    //Category 1
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_1 = new GroupCategorySkillRating(55, GROUP_1.getId(), GROUP_MEMBER_1.getMemberId(),
            BG_CATEGORY_1.getId(), 1700.00, 14);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_2 = new GroupCategorySkillRating(56, GROUP_1.getId(), GROUP_MEMBER_5.getMemberId(),
            BG_CATEGORY_1.getId(), 1700.00, 13);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_3 = new GroupCategorySkillRating(57, GROUP_1.getId(), GROUP_MEMBER_6.getMemberId(),
            BG_CATEGORY_1.getId(), 1250.25, 26);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_4 = new GroupCategorySkillRating(58, GROUP_1.getId(), GROUP_MEMBER_7.getMemberId(),
            BG_CATEGORY_1.getId(), 1600.42, 10);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_5 = new GroupCategorySkillRating(59, GROUP_1.getId(), GROUP_MEMBER_8.getMemberId(),
            BG_CATEGORY_1.getId(), 1800.00, 8);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_6 = new GroupCategorySkillRating(60, GROUP_1.getId(), GROUP_MEMBER_9.getMemberId(),
            BG_CATEGORY_1.getId(), 1400.12, 4);

    //Category 2
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_7 = new GroupCategorySkillRating(61, GROUP_1.getId(), GROUP_MEMBER_1.getMemberId(),
            BG_CATEGORY_2.getId(), 1550.00, 2);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_8 = new GroupCategorySkillRating(62, GROUP_1.getId(), GROUP_MEMBER_5.getMemberId(),
            BG_CATEGORY_2.getId(), 1600.00, 3);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_9 = new GroupCategorySkillRating(63, GROUP_1.getId(), GROUP_MEMBER_6.getMemberId(),
            BG_CATEGORY_2.getId(), 2000.00, 300);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_10 = new GroupCategorySkillRating(64, GROUP_1.getId(), GROUP_MEMBER_7.getMemberId(),
            BG_CATEGORY_2.getId(), 1140.00, 72);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_11 = new GroupCategorySkillRating(65, GROUP_1.getId(), GROUP_MEMBER_8.getMemberId(),
            BG_CATEGORY_2.getId(), 1372.00, 64);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_12 = new GroupCategorySkillRating(66, GROUP_1.getId(), GROUP_MEMBER_9.getMemberId(),
            BG_CATEGORY_2.getId(), 900.00, 104);

    //Category 3
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_13 = new GroupCategorySkillRating(67, GROUP_1.getId(), GROUP_MEMBER_1.getMemberId(),
            BG_CATEGORY_3.getId(), 1500.00, 1);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_14 = new GroupCategorySkillRating(68, GROUP_1.getId(), GROUP_MEMBER_2.getMemberId(),
            BG_CATEGORY_3.getId(), 1546.00, 2);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_15 = new GroupCategorySkillRating(69, GROUP_1.getId(), GROUP_MEMBER_3.getMemberId(),
            BG_CATEGORY_3.getId(), 1235.00, 30);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_16 = new GroupCategorySkillRating(70, GROUP_1.getId(), GROUP_MEMBER_5.getMemberId(),
            BG_CATEGORY_3.getId(), 1862.00, 20);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_17 = new GroupCategorySkillRating(71, GROUP_1.getId(), GROUP_MEMBER_6.getMemberId(),
            BG_CATEGORY_3.getId(), 1200.00, 300);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_18 = new GroupCategorySkillRating(72, GROUP_1.getId(), GROUP_MEMBER_7.getMemberId(),
            BG_CATEGORY_3.getId(), 1500.00, 80);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_19 = new GroupCategorySkillRating(73, GROUP_1.getId(), GROUP_MEMBER_8.getMemberId(),
            BG_CATEGORY_3.getId(), 1400.00, 41);
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_20 = new GroupCategorySkillRating(74, GROUP_1.getId(), GROUP_MEMBER_9.getMemberId(),
            BG_CATEGORY_3.getId(), 1300.00, 25);

    //Group 2's skill ratings
    //Category 1
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_21 = new GroupCategorySkillRating(75, GROUP_2.getId(), GROUP_MEMBER_2.getMemberId(),
            BG_CATEGORY_1.getId(), 1300.00, 12);

    //Category 3
    public static final GroupCategorySkillRating GROUP_CATEGORY_SKILL_RATING_22 = new GroupCategorySkillRating(76, GROUP_2.getId(), GROUP_MEMBER_2.getMemberId(),
            BG_CATEGORY_3.getId(), 1700.00, 25);

    public static final List<GroupCategorySkillRating> GROUP_CATEGORY_SKILL_RATINGS = Arrays.asList(GROUP_CATEGORY_SKILL_RATING_1, GROUP_CATEGORY_SKILL_RATING_2, GROUP_CATEGORY_SKILL_RATING_3,
            GROUP_CATEGORY_SKILL_RATING_4, GROUP_CATEGORY_SKILL_RATING_5, GROUP_CATEGORY_SKILL_RATING_6, GROUP_CATEGORY_SKILL_RATING_7, GROUP_CATEGORY_SKILL_RATING_8,
            GROUP_CATEGORY_SKILL_RATING_9, GROUP_CATEGORY_SKILL_RATING_10, GROUP_CATEGORY_SKILL_RATING_11, GROUP_CATEGORY_SKILL_RATING_12, GROUP_CATEGORY_SKILL_RATING_13,
            GROUP_CATEGORY_SKILL_RATING_14, GROUP_CATEGORY_SKILL_RATING_15, GROUP_CATEGORY_SKILL_RATING_16, GROUP_CATEGORY_SKILL_RATING_17, GROUP_CATEGORY_SKILL_RATING_18,
            GROUP_CATEGORY_SKILL_RATING_19, GROUP_CATEGORY_SKILL_RATING_20, GROUP_CATEGORY_SKILL_RATING_21, GROUP_CATEGORY_SKILL_RATING_22);

    public static final PlayerSkillRatingChange PLAYER_SKILL_RATING_CHANGE_1 = new PlayerSkillRatingChange(77, TestData.PLAYER_1.getId(), BG_CATEGORY_1.getCategoryName(),
            1775.23, 15.16);
    public static final PlayerSkillRatingChange PLAYER_SKILL_RATING_CHANGE_2 = new PlayerSkillRatingChange(78, TestData.PLAYER_1.getId(), BG_CATEGORY_2.getCategoryName(),
            1640.00, 23.11);
    public static final PlayerSkillRatingChange PLAYER_SKILL_RATING_CHANGE_3 = new PlayerSkillRatingChange(79, TestData.PLAYER_2.getId(), BG_CATEGORY_1.getCategoryName(),
            1201.56, -12.4);
    public static final PlayerSkillRatingChange PLAYER_SKILL_RATING_CHANGE_4 = new PlayerSkillRatingChange(80, TestData.PLAYER_3.getId(), BG_CATEGORY_1.getCategoryName(),
            1500.00, 7.5);

    public static final List<PlayerSkillRatingChange> PLAYER_SKILL_RATING_CHANGES = Arrays.asList(PLAYER_SKILL_RATING_CHANGE_1, PLAYER_SKILL_RATING_CHANGE_2, PLAYER_SKILL_RATING_CHANGE_3,
            PLAYER_SKILL_RATING_CHANGE_4);
}
