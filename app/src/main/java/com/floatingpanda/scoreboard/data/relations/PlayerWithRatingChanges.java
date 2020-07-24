package com.floatingpanda.scoreboard.data.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerSkillRatingChange;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;

import java.util.Collections;
import java.util.List;

/**
 * A relation that combines a single player with a list of the category skill rating changes they
 * earned from playing in the game record they were a player in.
 */
public class PlayerWithRatingChanges {
    @Embedded
    public Player player;
    @Relation(
            parentColumn = "player_id",
            entityColumn = "player_id"
    )
    List<PlayerSkillRatingChange> playerSkillRatingChanges;

    public PlayerWithRatingChanges(Player player, List<PlayerSkillRatingChange> playerSkillRatingChanges) {
        this.player = player;
        this.playerSkillRatingChanges = playerSkillRatingChanges;
    }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    public List<PlayerSkillRatingChange> getPlayerSkillRatingChanges() { return playerSkillRatingChanges; }
    public void setPlayerSkillRatingChanges(List<PlayerSkillRatingChange> playerSkillRatingChanges) { this.playerSkillRatingChanges = playerSkillRatingChanges; }

    public void sortSkillRatingChanges() {
        Collections.sort(playerSkillRatingChanges);
    }
}
