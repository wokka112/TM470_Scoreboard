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
