package com.floatingpanda.scoreboard;

import com.floatingpanda.scoreboard.data.AssignedCategory;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.Group;
import com.floatingpanda.scoreboard.data.GroupMember;
import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.data.PlayMode;

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

    public static final List<Member> MEMBERS = Arrays.asList(MEMBER_1, MEMBER_2, MEMBER_3);

    public static final BoardGame BOARD_GAME_1 = new BoardGame(8, "Medieval", 3,
            1, 8, BoardGame.TeamOption.TEAMS_AND_SOLOS_ALLOWED, "", "", "", "");
    public static final BoardGame BOARD_GAME_2 = new BoardGame(9, "Monopoly", 2,
            2, 6, BoardGame.TeamOption.NO_TEAMS, "", "", "", "");
    public static final BoardGame BOARD_GAME_3 = new BoardGame(10, "Go", 5,
            2, 2, BoardGame.TeamOption.TEAMS_ONLY, "", "", "", "");

    public static final List<BoardGame> BOARD_GAMES = Arrays.asList(BOARD_GAME_1, BOARD_GAME_2, BOARD_GAME_3);

    public static final Group GROUP_1 = new Group(11, "Ragnarok", "", "", "TBA", "TBA", new Date());
    public static final Group GROUP_2 = new Group(12, "The Monday Knights", "", "", "TBA", "TBA", new Date());
    public static final Group GROUP_3 = new Group(13, "The Thirds", "", "", "TBA", "TBA", new Date());

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

    public static final List<GroupMember> GROUP_MEMBERS = Arrays.asList(GROUP_MEMBER_1, GROUP_MEMBER_2, GROUP_MEMBER_3, GROUP_MEMBER_4);
}
