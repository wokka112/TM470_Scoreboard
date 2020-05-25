package com.floatingpanda.scoreboard.db;

import com.floatingpanda.scoreboard.data.AssignedCategory;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.data.PlayMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class holding test values
 */
public class TestData {

    static final BgCategory BG_CATEGORY_1 = new BgCategory(1, "Strategy");
    static final BgCategory BG_CATEGORY_2 = new BgCategory(2, "Luck");
    static final BgCategory BG_CATEGORY_3 = new BgCategory(3, "Ameritrash");

    static final List<BgCategory> BG_CATEGORIES = Arrays.asList(BG_CATEGORY_1, BG_CATEGORY_2, BG_CATEGORY_3);

    static final Member MEMBER_1 = new Member(4, "Ragnar", "", "");
    static final Member MEMBER_2 = new Member(5, "Eggs", "", "");
    static final Member MEMBER_3 = new Member(6, "Bob", "", "");

    static final List<Member> MEMBERS = Arrays.asList(MEMBER_1, MEMBER_2, MEMBER_3);

    static final BoardGame BOARD_GAME_1 = new BoardGame(7, "Medieval", 3,
            1, 8, BoardGame.TeamOption.TEAMS_OR_SOLOS, "", "", "", "", new ArrayList<BgCategory>(),
            new ArrayList<PlayMode.PlayModeEnum>());
    static final BoardGame BOARD_GAME_2 = new BoardGame(8, "Monopoly", 2,
            2, 6, BoardGame.TeamOption.NO_TEAMS, "", "", "", "", new ArrayList<BgCategory>(),
            new ArrayList<PlayMode.PlayModeEnum>());
    static final BoardGame BOARD_GAME_3 = new BoardGame(9, "Go", 5,
            2, 2, BoardGame.TeamOption.TEAMS_ONLY, "", "", "", "", new ArrayList<BgCategory>(),
            new ArrayList<PlayMode.PlayModeEnum>());

    static final List<BoardGame> BOARD_GAMES = Arrays.asList(BOARD_GAME_1, BOARD_GAME_2, BOARD_GAME_3);

    static final AssignedCategory ASSIGNED_CATEGORY_1 = new AssignedCategory(BOARD_GAME_1.getId(), BG_CATEGORY_1.getId());
    static final AssignedCategory ASSIGNED_CATEGORY_2 = new AssignedCategory(BOARD_GAME_2.getId(), BG_CATEGORY_2.getId());
    static final AssignedCategory ASSIGNED_CATEGORY_3 = new AssignedCategory(BOARD_GAME_3.getId(), BG_CATEGORY_3.getId());
    static final AssignedCategory ASSIGNED_CATEGORY_4 = new AssignedCategory(BOARD_GAME_3.getId(), BG_CATEGORY_1.getId());

    static final List<AssignedCategory> ASSIGNED_CATEGORIES = Arrays.asList(ASSIGNED_CATEGORY_1, ASSIGNED_CATEGORY_2, ASSIGNED_CATEGORY_3, ASSIGNED_CATEGORY_4);

    static final PlayMode PLAY_MODE_1 = new PlayMode(BOARD_GAME_1.getId(), PlayMode.PlayModeEnum.COMPETITIVE);
    static final PlayMode PLAY_MODE_2 = new PlayMode(BOARD_GAME_2.getId(), PlayMode.PlayModeEnum.COOPERATIVE);
    static final PlayMode PLAY_MODE_3 = new PlayMode(BOARD_GAME_3.getId(), PlayMode.PlayModeEnum.SOLITAIRE);
    static final PlayMode PLAY_MODE_4 = new PlayMode(BOARD_GAME_3.getId(), PlayMode.PlayModeEnum.COMPETITIVE);

    static final List<PlayMode> PLAY_MODES = Arrays.asList(PLAY_MODE_1, PLAY_MODE_2, PLAY_MODE_3, PLAY_MODE_4);
}
