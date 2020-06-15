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

    private Date date;

    //TODO include time.
    //private Time time;

    @ColumnInfo(name = "teams")
    private boolean teams;

    @ColumnInfo(name = "play_mode_played")
    private PlayMode.PlayModeEnum playModePlayed;

    @ColumnInfo(name = "no_of_teams")
    private int noOfTeams;

    //TODO include a boolean for cooperative/solitaire win/lose.
    // If the game is competitive, ignore and use the competitive layout.
    // If the game is cooperative or solitaire, use another layout which says whether you win or lose.

    @Ignore
    public GameRecord(int id, int groupId, String boardGameName, int difficulty, Date date, boolean teams, PlayMode.PlayModeEnum playModePlayed,
                      int noOfTeams) {
        this.id = id;
        this.groupId = groupId;
        this.boardGameName = boardGameName;
        this.difficulty = difficulty;
        this.date = date;
        this.teams = teams;
        this.playModePlayed = playModePlayed;
        this.noOfTeams = noOfTeams;
    }

    public GameRecord(int groupId, String boardGameName, int difficulty, Date date, boolean teams, PlayMode.PlayModeEnum playModePlayed,
                      int noOfTeams) {
        this(0, groupId, boardGameName, difficulty, date, teams, playModePlayed, noOfTeams);
    }

    @Ignore
    public GameRecord(Parcel source) {
        id = source.readInt();
        groupId = source.readInt();
        boardGameName = source.readString();
        difficulty = source.readInt();
        date = new Date(source.readLong());
        teams = (Boolean) source.readValue(null);
        playModePlayed = PlayMode.PlayModeEnum.valueOf(source.readString());
        noOfTeams = source.readInt();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public String getBoardGameName() { return boardGameName; }
    public void setBoardGameName(String boardGameName) { this.boardGameName = boardGameName; }
    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public boolean getTeams() { return teams; }
    public void setTeams(boolean teams) { this.teams = teams; }
    public PlayMode.PlayModeEnum getPlayModePlayed() { return playModePlayed; }
    public void setPlayModePlayed(PlayMode.PlayModeEnum playModePlayed) { this.playModePlayed = playModePlayed; }
    public int getNoOfTeams() { return noOfTeams; }
    public void setNoOfTeams(int noOfTeams) { this.noOfTeams = noOfTeams; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        GameRecord gameRecord = (GameRecord) obj;

        return (gameRecord.getId() == this.getId()
                && gameRecord.getGroupId() == this.getGroupId()
                && gameRecord.getBoardGameName().equals(this.getBoardGameName())
                && gameRecord.getDate().getTime() == this.getDate().getTime());
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
        dest.writeLong(date.getTime());
        dest.writeValue(teams);
        dest.writeString(playModePlayed.name());
        dest.writeInt(noOfTeams);
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
