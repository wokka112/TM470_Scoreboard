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

package com.floatingpanda.scoreboard.data.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Represents a player in a game record. Players belong to teams, even if it is only a team of one.
 * This is a simple table displaying many-to-many relationships between player teams, and through
 * them game records and members. The members are linked to from the members table (Member class)
 * via the memberNickname attribute, and the player team is linked to from the player_teams table
 * (PlayerTeam class) via the playerTeamId attribute.
 */
@Entity(tableName = "players", indices = {@Index(value = {"player_team_id", "member_nickname"},
        unique = true)},
        foreignKeys = {@ForeignKey(entity = PlayerTeam.class,
                parentColumns = "player_team_id",
                childColumns = "player_team_id",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Member.class,
                parentColumns = "nickname",
                childColumns = "member_nickname",
                onUpdate = ForeignKey.CASCADE,
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
