package com.floatingpanda.scoreboard.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "player_teams", primaryKeys = {"team_number", "record_id"},
        foreignKeys = {@ForeignKey(entity = GameRecord.class,
                parentColumns = "record_id",
                childColumns = "record_id",
                onDelete = ForeignKey.CASCADE)})
public class PlayerTeam {

    @ColumnInfo(name = "team_number")
    private int teamNumber;

    @ColumnInfo(name = "record_id")
    private int recordId;

    private int position;

    private int score;

    public PlayerTeam(int teamNumber, int recordId, int position, int score) {
        this.teamNumber = teamNumber;
        this.recordId = recordId;
        this.position = position;
        this.score = score;
    }

    public int getTeamNumber() { return teamNumber; }
    public void setTeamNumber(int teamNumber) { this.teamNumber = teamNumber; }
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        PlayerTeam playerTeam = (PlayerTeam) obj;

        return (playerTeam.getTeamNumber() == this.getTeamNumber()
                && playerTeam.getRecordId() == this.getRecordId());
    }
}
