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

package com.floatingpanda.scoreboard.data.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;

import java.util.List;

/**
 * A relation that combines a single player team with a list of players who played in that player
 * team.
 */
public class PlayerTeamWithPlayers {
    @Embedded
    public PlayerTeam playerTeam;
    @Relation(
            parentColumn = "player_team_id",
            entityColumn = "player_team_id",
            associateBy = @Junction(PlayerTeam.class)
    )
    List<Player> players;

    public PlayerTeamWithPlayers(PlayerTeam playerTeam, List<Player> players) {
        this.playerTeam = playerTeam;
        this.players = players;
    }

    public PlayerTeam getPlayerTeam() {
        return playerTeam;
    }

    public void setPlayerTeam(PlayerTeam playerTeam) {
        this.playerTeam = playerTeam;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
