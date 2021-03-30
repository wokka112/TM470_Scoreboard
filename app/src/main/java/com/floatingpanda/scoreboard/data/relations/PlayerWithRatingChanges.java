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
