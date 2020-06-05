package com.floatingpanda.scoreboard.data;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "players",
        foreignKeys = {@ForeignKey(entity = PlayerTeam.class,
                parentColumns = {"team_number", "record_id"},
                childColumns = {"team_number", "record_id"},
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Member.class,
                parentColumns = "nickname",
                childColumns = "member_nickname",
                onDelete = ForeignKey.SET_NULL)})
public class Player {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "player_id")
    private int id;

    @ColumnInfo(name = "team_number")
    private int teamNumber;

    @ColumnInfo(name = "record_id")
    private int recordId;

    @ColumnInfo(name = "member_nickname")
    private String memberNickname;

    @Ignore
    public Player(int id, int teamNumber, int recordId, String memberNickname) {
        this.id = id;
        this.teamNumber = teamNumber;
        this.recordId = recordId;
        this.memberNickname = memberNickname;
    }

    public Player(int teamNumber, int recordId, String memberNickname) {
        this(0, teamNumber, recordId, memberNickname);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTeamNumber() { return teamNumber; }
    public void setTeamNumber(int teamNumber) { this.teamNumber = teamNumber; }
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public String getMemberNickname() { return memberNickname; }
    public void setMemberNickname(String memberNickname) { this.memberNickname = memberNickname; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Player player = (Player) obj;

        return (player.getId() == this.getId()
                && player.getRecordId() == this.getRecordId()
                && player.getTeamNumber() == this.getTeamNumber());
    }
}
