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
