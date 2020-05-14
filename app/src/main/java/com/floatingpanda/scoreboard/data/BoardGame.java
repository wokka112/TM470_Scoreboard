package com.floatingpanda.scoreboard.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

//TODO look into refactoring code to use serializable instead of Parcelable.
// Parcelable should not be used for persistent data, and all of my data is persistent.
// Hence, I should look into moving to Serializable and what tradeoffs it may involve.

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
    @ColumnInfo(name = "team_options")
    private BoardGame.TeamOption teamOptions;

    //Game types tell us whether the game can be played competitive, cooperative losable,
    // cooperative non-losable, solitaire losable, solitaire non-losable or a mix of the above.
    @NonNull
    @ColumnInfo(name = "play_modes")
    private BoardGame.PlayMode playModes;

    @Nullable
    private String description;

    @Nullable
    @ColumnInfo(name = "house_rules")
    private String houseRules;

    @Nullable
    private String notes;

    @Nullable
    private String imgFilePath;

    //TODO add a list of BgCategories and Ignore it. The list will simply be used to hold the assigned categories
    // but the categories will be stored in a separate table as a many-to-many relationship. This is similar to
    // the Group which will hold a list of Member objects and GameRecord objects.
    @Ignore
    private List<BgCategory> bgCategories;

    //TODO add enums and typeconverters for PlayMode and TeamType?

    //TODO add constructor with id? Is it needed? Not sure... You can set id via a setter, and I want people to
    // autogenerate rather than set their own.

    @Ignore
    public BoardGame(String bgName, int difficulty, int minPlayers, int maxPlayers, BoardGame.TeamOption teamOptions,
                     BoardGame.PlayMode playModes, String description, String houseRules, String notes, String imgFilePath,
                     List<BgCategory> bgCategories) {
        this.id = 0;
        this.bgName = bgName;
        this.difficulty = difficulty;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.teamOptions = teamOptions;
        this.playModes = playModes;
        this.description = description;
        this.houseRules = houseRules;
        this.notes = notes;
        this.imgFilePath = imgFilePath;
        this.bgCategories = bgCategories;
    }

    public BoardGame(String bgName, int difficulty, int minPlayers, int maxPlayers, BoardGame.TeamOption teamOptions,
                     BoardGame.PlayMode playModes, String description, String houseRules, String notes, String imgFilePath) {
        this(bgName, difficulty, minPlayers, maxPlayers, teamOptions, playModes, description, houseRules, notes,
                imgFilePath, new ArrayList<BgCategory>());
    }

    @Ignore
    public BoardGame(String bgName, int difficulty, BoardGame.TeamOption teamOptions, BoardGame.PlayMode playModes) {
        this(bgName, difficulty, 1, 8, teamOptions, playModes, "", "", "", "TBA");
    }

    @Ignore
    public BoardGame(Parcel source) {
        this.id = source.readInt();
        this.bgName = source.readString();
        this.difficulty = source.readInt();
        this.minPlayers = source.readInt();
        this.maxPlayers = source.readInt();
        this.teamOptions = BoardGame.TeamOption.valueOf(source.readString());
        this.playModes = BoardGame.PlayMode.valueOf(source.readString());
        this.description = source.readString();
        this.houseRules = source.readString();
        this.notes = source.readString();
        this.imgFilePath = source.readString();
        this.bgCategories = source.readArrayList(BgCategory.class.getClassLoader());
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public String getBgName() { return this.bgName; }
    public void setBgName(String bgName) { this.bgName = bgName; }

    public int getDifficulty() { return this.difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }

    public int getMinPlayers() { return this.minPlayers; }
    public void setMinPlayers(int minPlayers) { this.minPlayers = minPlayers; }

    public int getMaxPlayers() { return this.maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public BoardGame.TeamOption getTeamOptions() { return this.teamOptions; }
    public void setTeamOptions(BoardGame.TeamOption teamOptions) { this.teamOptions = teamOptions; }

    public BoardGame.PlayMode getPlayModes() { return this.playModes; }
    public void setPlayModes(BoardGame.PlayMode playModes) { this.playModes = playModes; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getHouseRules() { return this.houseRules; }
    public void setHouseRules(String houseRules) { this.houseRules = houseRules; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getImgFilePath() { return this.imgFilePath; }
    public void setImgFilePath(String imgFilePath) { this.imgFilePath = imgFilePath; }

    //TODO remove bgCategories getter?
    public List<BgCategory> getBgCategories() { return new ArrayList<>(bgCategories); }
    public void setBgCategories(List<BgCategory> bgCategories) { this.bgCategories = bgCategories; }

    public void addBgCategory(BgCategory bgCategory) {
        this.bgCategories.add(bgCategory);
    }

    public BgCategory getBgCategory(int index) {
        return this.bgCategories.get(index);
    }

    public void removeBgCategory(BgCategory bgCategory) {
        this.bgCategories.remove(bgCategory);
    }

    //TODO maybe change this to return a list of strings? Then I could do formatting if I wanted somewhere else.
    // This is probably a worthwhile idea.
    public String getPlayModesString() {
        String playModesString;

        switch (this.playModes) {
            case COMPETITIVE:
                playModesString = "Competitive";
                break;
            case COOPERATIVE:
                playModesString = "Cooperative";
                break;
            case SOLITAIRE:
                playModesString = "Solitaire";
                break;
            case COMPETITIVE_OR_COOPERATIVE:
                playModesString = "Competitive, Cooperative";
                break;
            case COMPETITIVE_OR_SOLITAIRE:
                playModesString = "Competitive, Solitaire";
                break;
            case COOPERATIVE_OR_SOLITAIRE:
                playModesString = "Cooperative, Solitaire";
                break;
            case COMPETITIVE_OR_COOPERATIVE_OR_SOLITAIRE:
                playModesString = "Competitive, Cooperative, Solitaire";
                break;
            case ERROR:
                playModesString = "There's been an error somewhere. Please report this.";
                break;
            default:
                playModesString = "No play modes set (This is a bug. Please report it.)";
                break;
        }

        return playModesString;
    }

    //TODO update to return a list of strings?
    public String getTeamOptionsString() {
        String teamOptionsString;

        switch (this.teamOptions) {
            case NO_TEAMS:
                teamOptionsString = "No teams";
                break;
            case TEAMS_ONLY:
                teamOptionsString = "Teams only";
                break;
            case TEAMS_OR_SOLOS:
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
        dest.writeString(playModes.name());
        dest.writeString(description);
        dest.writeString(houseRules);
        dest.writeString(notes);
        dest.writeString(imgFilePath);
        dest.writeList(bgCategories);
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

    // The different modes that a game can be played in - competitive, cooperative, solitaire.
    public enum PlayMode {
        ERROR,
        COMPETITIVE,
        COOPERATIVE,
        SOLITAIRE,
        COMPETITIVE_OR_COOPERATIVE,
        COMPETITIVE_OR_SOLITAIRE,
        COOPERATIVE_OR_SOLITAIRE,
        COMPETITIVE_OR_COOPERATIVE_OR_SOLITAIRE
    }

    //TODO add in a TEAMS_ONLY_OR_NO_TEAMS_ONLY and a TEAMS_OR_SOLOS to represent only teams/only solos, decide at start and then play,
    // or teams, solos or a mix of teams and solos?? Or should I just leave it as is and people can monitor it themselves.

    // The team options that a game can be played in - solo, team, or either.
    public enum TeamOption {
        ERROR,
        NO_TEAMS,
        TEAMS_ONLY,
        TEAMS_OR_SOLOS
    }
}
