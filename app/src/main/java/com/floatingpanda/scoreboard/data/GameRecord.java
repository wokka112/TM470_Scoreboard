package com.floatingpanda.scoreboard.data;

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
public class GameRecord {
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

    //private Time time;

    @ColumnInfo(name = "teams_allowed")
    private boolean teamsAllowed;

    @ColumnInfo(name = "play_mode_played")
    private PlayMode.PlayModeEnum playModePlayed;

    @ColumnInfo(name = "no_of_teams")
    private int noOfTeams;

    @Ignore
    public GameRecord(int id, int groupId, String boardGameName, int difficulty, Date date, boolean teamsAllowed, PlayMode.PlayModeEnum playModePlayed,
                      int noOfTeams) {
        this.id = id;
        this.groupId = groupId;
        this.boardGameName = boardGameName;
        this.difficulty = difficulty;
        this.date = date;
        this.teamsAllowed = teamsAllowed;
        this.playModePlayed = playModePlayed;
        this.noOfTeams = noOfTeams;
    }

    public GameRecord(int groupId, String boardGameName, int difficulty, Date date, boolean teamsAllowed, PlayMode.PlayModeEnum playModePlayed,
                      int noOfTeams) {
        this(0, groupId, boardGameName, difficulty, date, teamsAllowed, playModePlayed, noOfTeams);
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
    public boolean getTeamsAllowed() { return teamsAllowed; }
    public void setTeamsAllowed(boolean teamsAllowed) { this.teamsAllowed = teamsAllowed; }
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
}
