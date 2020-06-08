package com.floatingpanda.scoreboard.data.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

//TODO add unique constraint to the combination of the three columns in this entity?
@Entity(tableName = "players",
        foreignKeys = {@ForeignKey(entity = PlayerTeam.class,
                parentColumns = "player_team_id",
                childColumns = "player_team_id",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Member.class,
                parentColumns = "nickname",
                childColumns = "member_nickname",
                onDelete = ForeignKey.SET_NULL)})
public class Player {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "player_id")
    private int id;

    @ColumnInfo(name = "player_team_id")
    private int playerTeamId;

    @ColumnInfo(name = "member_nickname")
    private String memberNickname;

    @Ignore
    public Player(int id, int playerTeamId, String memberNickname) {
        this.id = id;
        this.playerTeamId = playerTeamId;
        this.memberNickname = memberNickname;
    }

    public Player(int playerTeamId, String memberNickname) {
        this(0, playerTeamId, memberNickname);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPlayerTeamId() { return playerTeamId; }
    public void setPlayerTeamId(int playerTeamId) { this.playerTeamId = playerTeamId; }
    public String getMemberNickname() { return memberNickname; }
    public void setMemberNickname(String memberNickname) { this.memberNickname = memberNickname; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Player player = (Player) obj;

        return (player.getId() == this.getId()
                && player.getPlayerTeamId() == this.getPlayerTeamId());
    }
}
