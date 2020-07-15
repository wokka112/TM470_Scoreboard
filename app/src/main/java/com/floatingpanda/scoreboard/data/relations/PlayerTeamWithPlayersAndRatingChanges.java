package com.floatingpanda.scoreboard.data.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.floatingpanda.scoreboard.data.entities.AssignedCategory;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;

import java.util.List;

public class PlayerTeamWithPlayersAndRatingChanges {
    @Embedded
    public PlayerTeam playerTeam;
    @Relation(
            entity = Player.class,
            parentColumn = "player_team_id",
            entityColumn = "player_team_id"
    )
    List<PlayerWithRatingChanges> playersWithRatingChanges;

    public PlayerTeamWithPlayersAndRatingChanges(PlayerTeam playerTeam, List<PlayerWithRatingChanges> playersWithRatingChanges) {
        this.playerTeam = playerTeam;
        this.playersWithRatingChanges = playersWithRatingChanges;
    }

    public PlayerTeam getPlayerTeam() { return playerTeam; }
    public void setPlayerTeam(PlayerTeam playerTeam) { this.playerTeam = playerTeam; }
    public List<PlayerWithRatingChanges> getPlayersWithRatingChanges() { return playersWithRatingChanges; }
    public void setPlayersWithRatingChanges(List<PlayerWithRatingChanges> playersWithRatingChanges) { this.playersWithRatingChanges = playersWithRatingChanges; }
}
