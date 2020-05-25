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

    @Nullable
    private String imgFilePath;

    @Ignore
    private List<BgCategory> bgCategories;

    @Ignore
    private List<PlayMode.PlayModeEnum> playModes;

    @Ignore
    public BoardGame(int id, String bgName, int difficulty, int minPlayers, int maxPlayers, BoardGame.TeamOption teamOptions,
                     String description, String houseRules, String notes, String imgFilePath, List<BgCategory> bgCategories,
                     List<PlayMode.PlayModeEnum> playModes) {
        this.id = id;
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

    /**
     * The default builder. Creates a BoardGame with empty BgCategory and PlayMode lists.
     */
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
    //Precondition: difficulty is between 1 - 5 (inclusive).
    //TODO add defensive programming? if less than 1 set to 1, if greater than 5 set to 5?
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }

    public int getMinPlayers() { return this.minPlayers; }
    //TODO add defensive programming? if less than 0 set to 1?
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

    public List<BgCategory> getBgCategories() { return new ArrayList<>(bgCategories); }
    public void setBgCategories(List<BgCategory> bgCategories) { this.bgCategories = bgCategories; }

    public List<PlayMode.PlayModeEnum> getPlayModes() { return new ArrayList<>(playModes); }
    public void setPlayModes(List<PlayMode.PlayModeEnum> playModes) { this.playModes = playModes; }

    /**
     * Adds a category to the list of board game categories.
     *
     * If the list already contains the category, nothing happens.
     * @param bgCategory a BgCategory to add to the list
     */
    public void addBgCategory(BgCategory bgCategory) {
        if (!bgCategories.contains(bgCategory)) {
            this.bgCategories.add(bgCategory);
        }
    }

    /**
     * Gets the category at a specific index from the list of categories.
     * @param index the index of the desired category
     * @return a BgCategory for the category at the index
     */
    public BgCategory getBgCategory(int index) {
        return this.bgCategories.get(index);
    }

    /**
     * Removes a category from the list of board game categories.
     * @param bgCategory a board game category
     */
    public void removeBgCategory(BgCategory bgCategory) {
        this.bgCategories.remove(bgCategory);
    }

    /**
     * Adds a play mode to the list of board game play modes.
     *
     * If the list already contains the play mode, does nothing.
     * @param playMode the play mode to be added.
     */
    public void addPlayMode(PlayMode.PlayModeEnum playMode) {
        if (!playModes.contains(playMode)) {
            playModes.add(playMode);
        }
    }

    /**
     * Gets the play mode at a specific index from the list of board game play modes.
     * @param index the index of the wanted play mode
     * @return a PlayMode
     */
    public PlayMode.PlayModeEnum getPlayMode (int index) { return this.playModes.get(index); }

    /**
     * Removes a play mode from the list of board game play modes.
     * @param playMode a PlayMode
     */
    public void removePlayMode(PlayMode.PlayModeEnum playMode) { this.playModes.remove(playMode); }

    //TODO maybe change this to return a list of strings? Then I could do formatting if I wanted somewhere else.
    /**
     * Returns a string of the categories that the board game fits into. Each category is separated
     * by a comma.
     *
     * If the board game has no categories set, returns "No categories set"
     * @return A string of the categories the board game fills.
     */
    public String getBgCategoriesString() {
        if (bgCategories.isEmpty()) {
            return "No categories set";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(bgCategories.get(0).getCategoryName());

        for (int i = 1; i < bgCategories.size(); i++) {
            sb.append(", ");
            sb.append(bgCategories.get(i).getCategoryName());
        }

        return sb.toString();
    }

    //TODO maybe change this to return a list of strings? Then I could do formatting if I wanted somewhere else.
    /**
     * Builds a string to represent the potential play modes that the board game can be played in.
     * String is built up based on the PlayModeEnum's that are set for the board game, a substring
     * being appended for each enum.
     *
     * COMPETITIVE: appends "Competitive"
     * COOPERATIVE: appends "Cooperative"
     * SOLITAIRE: appends "Solitaire"
     *
     * Competitive always comes first, followed by cooperative, then solitaire. If a play mode comes
     * after another one (i.e. a board game is set for both competitive and solitaire) then a comma
     * is appended before the playmode.
     *
     * If no play modes are set, returns "No play modes assigned. This is an error. Please report."
     *
     * @return a String representing the potential play modes set for the board game
     */
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

    // The team options that a game can be played in - solo, team, or either / a mix.
    public enum TeamOption {
        ERROR,
        NO_TEAMS,
        TEAMS_ONLY,
        TEAMS_OR_SOLOS
    }
}
