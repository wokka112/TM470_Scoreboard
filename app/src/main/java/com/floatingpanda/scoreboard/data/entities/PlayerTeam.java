package com.floatingpanda.scoreboard.data.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Represents a team of players who played in a game record. This is a table showing many-to-many
 * relationships between players (which show many-to-many relationships between player teams and
 * members) and a game record which they played in. The players are stored in the players table
 * (Player class) and link to this table via a foreign key stored there. The game record the player
 * team played in is linked to by the recordId attribute here, which is a foreign key to the
 * game_records table (GameRecord class).
 *
 * The finishing position and score earned in the game is also stored in this class/table.
 */
@Entity(tableName = "player_teams", indices = {@Index(value = {"team_number", "record_id"},
        unique = true)},
        foreignKeys = {@ForeignKey(entity = GameRecord.class,
                parentColumns = "record_id",
                childColumns = "record_id",
                onDelete = ForeignKey.CASCADE)})
public class PlayerTeam {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "player_team_id")
    private int id;

    @ColumnInfo(name = "team_number")
    private int teamNumber;

    @ColumnInfo(name = "record_id")
    private int recordId;

    private int position;

    private int score;

    @Ignore
    public PlayerTeam(int id, int teamNumber, int recordId, int position, int score) {
        this.id = id;
        this.teamNumber = teamNumber;
        this.recordId = recordId;
        this.position = position;
        this.score = score;
    }

    public PlayerTeam(int teamNumber, int recordId, int position, int score) {
        this(0, teamNumber, recordId, position, score);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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
