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

    @Nullable
    private String imgFilePath;

    //TODO add a list of BgCategories and Ignore it. The list will simply be used to hold the assigned categories
    // but the categories will be stored in a separate table as a many-to-many relationship. This is similar to
    // the Group which will hold a list of Member objects and GameRecord objects.
    @Ignore
    private List<BgCategory> bgCategories;

    @Ignore
    private List<PlayMode.PlayModeEnum> playModes;

    //TODO add constructor with id? Is it needed? Not sure... You can set id via a setter, and I want people to
    // autogenerate rather than set their own.

    @Ignore
    public BoardGame(String bgName, int difficulty, int minPlayers, int maxPlayers, BoardGame.TeamOption teamOptions,
                     String description, String houseRules, String notes, String imgFilePath,
                     List<BgCategory> bgCategories, List<PlayMode.PlayModeEnum> playModes) {
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
        this.playModes = playModes;
    }

    public BoardGame(String bgName, int difficulty, int minPlayers, int maxPlayers, BoardGame.TeamOption teamOptions, String description, String houseRules, String notes, String imgFilePath) {
        this(bgName, difficulty, minPlayers, maxPlayers, teamOptions, description, houseRules, notes,
                imgFilePath, new ArrayList<BgCategory>(), new ArrayList<PlayMode.PlayModeEnum>());
    }

    @Ignore
    public BoardGame(String bgName, int difficulty, int minPlayers, int maxPlayers, BoardGame.TeamOption teamOptions,
                     String description, String houseRules, String notes, String imgFilePath, List<PlayMode.PlayModeEnum> playModes) {
        this(bgName, difficulty, minPlayers, maxPlayers, teamOptions, description, houseRules, notes,
                imgFilePath, new ArrayList<BgCategory>(), playModes);
    }

    @Ignore
    public BoardGame(String bgName, int difficulty, BoardGame.TeamOption teamOptions) {
        this(bgName, difficulty, 1, 8, teamOptions, "", "", "", "TBA");
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
        this.imgFilePath = source.readString();
        this.bgCategories = source.readArrayList(BgCategory.class.getClassLoader());
        this.playModes = source.readArrayList(PlayMode.PlayModeEnum.class.getClassLoader());
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

    public List<PlayMode.PlayModeEnum> getPlayModes() { return new ArrayList<>(playModes); }
    public void setPlayModes(List<PlayMode.PlayModeEnum> playModes) { this.playModes = playModes; }

    //TODO add guard like for addPlayMode?
    public void addBgCategory(BgCategory bgCategory) { this.bgCategories.add(bgCategory); }

    public BgCategory getBgCategory(int index) {
        return this.bgCategories.get(index);
    }

    public void removeBgCategory(BgCategory bgCategory) {
        this.bgCategories.remove(bgCategory);
    }

    public void addPlayMode(PlayMode.PlayModeEnum playMode) {
        if (!playModes.contains(playMode)) {
            playModes.add(playMode);
        }
    }

    public PlayMode.PlayModeEnum getPlayMode (int index) { return this.playModes.get(index); }

    public void removePlayMode(PlayMode.PlayModeEnum playMode) { this.playModes.remove(playMode); }

    //TODO maybe change this to return a list of strings? Then I could do formatting if I wanted somewhere else.
    // This is probably a worthwhile idea.
    public String getPlayModesString() {
        if (playModes.isEmpty()) {
            return "No play modes assigned. This is an error. Please report.";
        }

        StringBuilder sb = new StringBuilder();

        if (playModes.contains(PlayMode.PlayModeEnum.COMPETITIVE)) {
            sb.append("Competitive");
        }

        if (playModes.contains(PlayMode.PlayModeEnum.COOPERATIVE)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("Cooperative");
        }

        if (playModes.contains(PlayMode.PlayModeEnum.SOLITAIRE)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("Solitaire");
        }

        return sb.toString();
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
        dest.writeString(description);
        dest.writeString(houseRules);
        dest.writeString(notes);
        dest.writeString(imgFilePath);
        dest.writeList(bgCategories);
        dest.writeList(playModes);
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
