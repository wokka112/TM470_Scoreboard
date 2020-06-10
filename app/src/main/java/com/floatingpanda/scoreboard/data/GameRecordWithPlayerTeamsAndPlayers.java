package com.floatingpanda.scoreboard.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;

import java.util.List;

public class GameRecordWithPlayerTeamsAndPlayers {
    @Embedded
    public GameRecord gameRecord;
    @Relation(
            entity = PlayerTeam.class,
            parentColumn = "record_id",
            entityColumn = "record_id"
    )
    List<PlayerTeamWithPlayers> playerTeamsWithPlayers;

    public GameRecordWithPlayerTeamsAndPlayers(GameRecord gameRecord, List<PlayerTeamWithPlayers> playerTeamsWithPlayers) {
        this.gameRecord = gameRecord;
        this.playerTeamsWithPlayers = playerTeamsWithPlayers;
    }

    public GameRecord getGameRecord() {
        return gameRecord;
    }

    public void setGameRecord(GameRecord gameRecord) {
        this.gameRecord = gameRecord;
    }

    public List<PlayerTeamWithPlayers> getPlayerTeamsWithPlayers() {
        return playerTeamsWithPlayers;
    }

    public void setPlayerTeamsWithPlayers(List<PlayerTeamWithPlayers> playerTeamsWithPlayers) {
        this.playerTeamsWithPlayers = playerTeamsWithPlayers;
    }
}