package com.floatingpanda.scoreboard.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "boardgames")
public class BoardGame {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "bg_name")
    private String bgName;

    //Difficulty must be between 1 - 5 (inclusive).
    @NonNull
    private int difficulty;

    @Nullable
    @ColumnInfo(name = "min_players")
    private int minPlayers;

    @Nullable
    @ColumnInfo(name = "max_players")
    private int maxPlayers;

    //Team types tell us whether the game can be played in singles, teams, or a mix.
    @NonNull
    @ColumnInfo(name = "team_type")
    private String teamType;

    //Game types tell us whether the game can be played competitive, cooperative losable,
    // cooperative non-losable, solitaire losable, solitaire non-losable or a mix of the above.
    @NonNull
    @ColumnInfo(name = "game_type")
    private String gameType;

    @Nullable
    private String description;

    @Nullable
    @ColumnInfo(name = "house_rules")
    private String houseRules;

    @Nullable
    private String notes;

    public BoardGame(String bgName, int difficulty, int minPlayers, int maxPlayers, String teamType,
                     String gameType, String description, String houseRules, String notes) {
        this.bgName = bgName;
        this.difficulty = difficulty;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.teamType = teamType;
        this.gameType = gameType;
        this.description = description;
        this.houseRules = houseRules;
        this.notes = notes;
    }

    @Ignore
    public BoardGame(String bgName, int difficulty, String teamType, String gameType) {
        this(bgName, difficulty, 1, 8, teamType, gameType, "", "", "");
    }

    public String getBgName() { return this.bgName; }
    public void setBgName(String bgName) { this.bgName = bgName; }
    public int getDifficulty() { return this.difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public int getMinPlayers() { return this.minPlayers; }
    public void setMinPlayers(int minPlayers) { this.minPlayers = minPlayers; }
    public int getMaxPlayers() { return this.maxPlayers; }
    public void setMaxPlayerS() { this.maxPlayers = maxPlayers; }
    public String getTeamType() { return this.teamType; }
    public void setTeamType(String teamType) { this.teamType = teamType; }
    public String getGameType() { return this.gameType; }
    public void setGameType() { this.gameType = gameType; }
    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }
    public String getHouseRules() { return this.houseRules; }
    public void setHouseRules(String houseRules) { this.houseRules = houseRules; }
    public String getNotes() { return this.notes; }
    public void setNotes() { this.notes = notes; }
}
