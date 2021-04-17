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

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Represents a board game. Board games have board game categories, which are shown as relations
 * in the assigned_categories table (AssignedCategory class), and play modes which are shown as
 * foreign keys in the play_modes table (PlayMode class).
 */
@Entity(tableName = "boardgames", indices = {@Index(value = "bg_name",
        unique = true)})
public class BoardGame implements Parcelable {
    @PrimaryKey (autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "bg_id")
    private int id;

    @NonNull
    @ColumnInfo(name = "bg_name")
    private String bgName;

    //Difficulty must be between 1 - 5 (inclusive).
    private int difficulty;

    @ColumnInfo(name = "min_players")
    private int minPlayers;

    @ColumnInfo(name = "max_players")
    private int maxPlayers;

    @NonNull
    @ColumnInfo(name = "team_options")
    private BoardGame.TeamOption teamOptions;

    @Nullable
    private String description;

    @Nullable
    @ColumnInfo(name = "house_rules")
    private String houseRules;

    @Nullable
    private String notes;

    @Ignore
    public BoardGame(int id, String bgName, int difficulty, int minPlayers, int maxPlayers, BoardGame.TeamOption teamOptions,
                     String description, String houseRules, String notes) {
        this.id = id;
        this.bgName = bgName;
        this.difficulty = difficulty;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.teamOptions = teamOptions;
        this.description = description;
        this.houseRules = houseRules;
        this.notes = notes;
    }

    public BoardGame(String bgName, int difficulty, int minPlayers, int maxPlayers, BoardGame.TeamOption teamOptions,
                     String description, String houseRules, String notes) {
        this(0, bgName, difficulty, minPlayers, maxPlayers, teamOptions, description, houseRules, notes);
    }

    @Ignore
    public BoardGame(String bgName, int difficulty, BoardGame.TeamOption teamOptions) {
        this(bgName, difficulty, 1, 8, teamOptions, "", "", "");
    }

    @Ignore
    public BoardGame(BoardGame boardGame) {
        this(boardGame.getId(), boardGame.getBgName(), boardGame.getDifficulty(), boardGame.getMinPlayers(), boardGame.getMaxPlayers(),
                boardGame.getTeamOptions(), boardGame.getDescription(), boardGame.getHouseRules(), boardGame.getNotes());
    }

    @Ignore
    public BoardGame(Parcel source) {
        this.id = source.readInt();
        this.bgName = source.readString();
        this.difficulty = source.readInt();
        this.minPlayers = source.readInt();
        this.maxPlayers = source.readInt();
        this.teamOptions = BoardGame.TeamOption.valueOf(source.readString());
        this.description = source.readString();
        this.houseRules = source.readString();
        this.notes = source.readString();
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public String getBgName() { return this.bgName; }
    public void setBgName(String bgName) { this.bgName = bgName; }

    public int getDifficulty() { return this.difficulty; }

    /**
     * If difficulty is less than 1, sets this.difficulty to 1.
     * If difficulty is greater than 5, sets this.difficulty to 5.
     * @param difficulty
     */
    public void setDifficulty(int difficulty) {
        if (difficulty < 1) {
            this.difficulty = 1;
        } else if (difficulty > 5) {
            this.difficulty = 5;
        } else {
            this.difficulty = difficulty;
        }
    }

    public int getMinPlayers() { return this.minPlayers; }

    /**
     * If minPlayers is less than 1, sets this.minPlayers to 1.
     * @param minPlayers
     */
    public void setMinPlayers(int minPlayers) {
        if (minPlayers < 1 ) {
            this.minPlayers = 1;
        } else {
            this.minPlayers = minPlayers;
        }
    }

    public int getMaxPlayers() { return this.maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public BoardGame.TeamOption getTeamOptions() { return this.teamOptions; }
    public void setTeamOptions(BoardGame.TeamOption teamOptions) { this.teamOptions = teamOptions; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getHouseRules() { return this.houseRules; }
    public void setHouseRules(String houseRules) { this.houseRules = houseRules; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    /**
     * Returns a String representing the team options for the game based on the BoardGame's
     * TeamOption enum.
     *
     * NO_TEAMS: returns "No teams."
     * TEAMS_ONLY: returns "Teams only."
     * TEAMS_OR_SOLOS: returns "Teams, solo players, or a mix of teams and solo players allowed"
     * ERROR: returns "There's been an error somewhere. Please report this."
     * default: returns "No team options set. (This is a bug. Please report.)"
     *
     * @return a String representing the team options set for the board game
     */
    public String getTeamOptionsString() {
        String teamOptionsString;

        switch (this.teamOptions) {
            case NO_TEAMS:
                teamOptionsString = "No teams.";
                break;
            case TEAMS_ONLY:
                teamOptionsString = "Teams only.";
                break;
            case TEAMS_AND_SOLOS_ALLOWED:
                teamOptionsString = "Teams, solo players, or a mix of teams and solo players allowed.";
                break;
            case ERROR:
                teamOptionsString = "There's been an error somewhere. Please report this.";
                break;
            default:
                teamOptionsString = "No team options set. (This is a bug. Please report.)";
        }

        return teamOptionsString;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        BoardGame boardGame = (BoardGame) obj;

        return (boardGame.getBgName().equals(this.getBgName())
                && boardGame.getDifficulty() == this.getDifficulty());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(bgName);
        dest.writeInt(difficulty);
        dest.writeInt(minPlayers);
        dest.writeInt(maxPlayers);
        dest.writeString(teamOptions.name());
        dest.writeString(description);
        dest.writeString(houseRules);
        dest.writeString(notes);
    }

    public static final Creator<BoardGame> CREATOR = new Creator<BoardGame>() {
        @Override
        public BoardGame[] newArray(int size) {
            return new BoardGame[size];
        }

        @Override
        public BoardGame createFromParcel(Parcel source) {
            return new BoardGame(source);
        }
    };

    // The team options that a game can be played in - solo, team, or either / a mix.
    public enum TeamOption {
        ERROR,
        NO_TEAMS,
        TEAMS_ONLY,
        TEAMS_AND_SOLOS_ALLOWED
    }
}
