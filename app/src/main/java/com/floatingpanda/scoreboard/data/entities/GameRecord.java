package com.floatingpanda.scoreboard.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "game_records",
        foreignKeys = {@ForeignKey(entity = Group.class,
                parentColumns = "group_id",
                childColumns = "group_id",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = BoardGame.class,
                parentColumns = "bg_name",
                childColumns = "bg_name",
                onDelete = ForeignKey.SET_NULL,
                onUpdate = ForeignKey.CASCADE)})
public class GameRecord implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id")
    private int id;

    @ColumnInfo(name = "group_id")
    private int groupId;

    @Nullable
    @ColumnInfo(name = "bg_name")
    private String boardGameName;

    private int difficulty;

    @ColumnInfo(name = "date_time")
    private Date dateTime;

    @ColumnInfo(name = "teams")
    private boolean teams;

    @ColumnInfo(name = "play_mode_played")
    private PlayMode.PlayModeEnum playModePlayed;

    @ColumnInfo(name = "no_of_teams")
    private int noOfTeams;

    private boolean won;

    //TODO include a boolean for cooperative/solitaire win/lose.
    // If the game is competitive, ignore and use the competitive layout.
    // If the game is cooperative or solitaire, use another layout which says whether you win or lose.

    @Ignore
    public GameRecord(int id, int groupId, String boardGameName, int difficulty, Date dateTime, boolean teams, PlayMode.PlayModeEnum playModePlayed,
                      int noOfTeams, boolean won) {
        this.id = id;
        this.groupId = groupId;
        this.boardGameName = boardGameName;
        this.difficulty = difficulty;
        this.dateTime = dateTime;
        this.teams = teams;
        this.playModePlayed = playModePlayed;
        this.noOfTeams = noOfTeams;
        this.won = won;
    }

    public GameRecord(int groupId, String boardGameName, int difficulty, Date dateTime, boolean teams, PlayMode.PlayModeEnum playModePlayed,
                      int noOfTeams, boolean won) {
        this(0, groupId, boardGameName, difficulty, dateTime, teams, playModePlayed, noOfTeams, won);
    }

    @Ignore
    public GameRecord(int groupId, String boardGameName, int difficulty, Date dateTime, boolean teams, PlayMode.PlayModeEnum playModePlayed,
                      int noOfTeams) {
        this(0, groupId, boardGameName, difficulty, dateTime, teams, playModePlayed, noOfTeams, false);
    }

    @Ignore
    public GameRecord(Parcel source) {
        id = source.readInt();
        groupId = source.readInt();
        boardGameName = source.readString();
        difficulty = source.readInt();
        dateTime = new Date(source.readLong());
        teams = (Boolean) source.readValue(null);
        playModePlayed = PlayMode.PlayModeEnum.valueOf(source.readString());
        noOfTeams = source.readInt();
        won = (Boolean) source.readValue(null);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public String getBoardGameName() { return boardGameName; }
    public void setBoardGameName(String boardGameName) { this.boardGameName = boardGameName; }
    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public Date getDateTime() { return dateTime; }
    public void setDateTime(Date dateTime) { this.dateTime = dateTime; }
    public boolean getTeams() { return teams; }
    public void setTeams(boolean teams) { this.teams = teams; }
    public PlayMode.PlayModeEnum getPlayModePlayed() { return playModePlayed; }
    public void setPlayModePlayed(PlayMode.PlayModeEnum playModePlayed) { this.playModePlayed = playModePlayed; }
    public int getNoOfTeams() { return noOfTeams; }
    public void setNoOfTeams(int noOfTeams) { this.noOfTeams = noOfTeams; }
    public boolean getWon() { return won; }
    public void setWon(boolean won) { this.won = won;}

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        GameRecord gameRecord = (GameRecord) obj;

        return (gameRecord.getId() == this.getId()
                && gameRecord.getGroupId() == this.getGroupId()
                && gameRecord.getBoardGameName().equals(this.getBoardGameName())
                && gameRecord.getDateTime().getTime() == this.getDateTime().getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(groupId);
        dest.writeString(boardGameName);
        dest.writeInt(difficulty);
        dest.writeLong(dateTime.getTime());
        dest.writeValue(teams);
        dest.writeString(playModePlayed.name());
        dest.writeInt(noOfTeams);
        dest.writeValue(won);
    }

    public static final Creator<GameRecord> CREATOR = new Creator<GameRecord>() {
        @Override
        public GameRecord[] newArray(int size) {
            return new GameRecord[size];
        }

        @Override
        public GameRecord createFromParcel(Parcel source) {
            return new GameRecord(source);
        }
    };
}
