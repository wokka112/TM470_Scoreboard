package com.floatingpanda.scoreboard.comparators;

import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;

import java.util.Comparator;

/**
 * Sorts a list of PlayerTeamWithPlayers objects into ascending order based on the teams' finishing
 * positions (i.e. 1st place before 2nd, 2nd before 3rd, etc.).
 */
public class PlayerTeamWithPlayersComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        PlayerTeamWithPlayers ptwp1 = (PlayerTeamWithPlayers) o1;
        PlayerTeamWithPlayers ptwp2 = (PlayerTeamWithPlayers) o2;

        PlayerTeam pt1 = ptwp1.getPlayerTeam();
        PlayerTeam pt2 = ptwp2.getPlayerTeam();

        return pt1.getPosition() < pt2.getPosition() ? -1 : pt1.getPosition() == pt2.getPosition() ? 0 : 1;
    }
}
