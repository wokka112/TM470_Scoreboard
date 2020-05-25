package com.floatingpanda.scoreboard.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class BoardGameWithBgCategoriesAndPlayModes implements Parcelable {
    @Embedded
    public BoardGameWithBgCategories bgWithBgCategories;
    @Relation(
            parentColumn = "bg_id",
            entityColumn = "bg_id"
    )
    public List<PlayMode> playModes;

    public BoardGameWithBgCategoriesAndPlayModes(BoardGameWithBgCategories bgWithBgCategories, List<PlayMode> playModes) {
        this.bgWithBgCategories = bgWithBgCategories;
        this.playModes = playModes;
    }

    public BoardGameWithBgCategoriesAndPlayModes(Parcel source) {
        this.bgWithBgCategories = source.readParcelable(BoardGameWithBgCategories.class.getClassLoader());
        this.playModes = source.readArrayList(PlayMode.class.getClassLoader());
    }

    public BoardGameWithBgCategories getBoardGameWithBgCategories() { return bgWithBgCategories; }
    public void setBoardGameWithBgCategories(BoardGameWithBgCategories bgWithBgCategories) { this.bgWithBgCategories = bgWithBgCategories; }

    public List<PlayMode> getPlayModes() { return this.playModes; }
    public void setPlayModes(List<PlayMode> playModes) { this.playModes = playModes; }

    public void addPlayMode(PlayMode playMode) {
        if (!playModes.contains(playMode)) {
            playModes.add(playMode);
        }
    }

    public PlayMode getPlayMode(int index) {
        return playModes.get(index);
    }

    public void removePlayMode(PlayMode playMode) {
        playModes.remove(playMode);
    }

    public List<PlayMode.PlayModeEnum> getPlayModeEnums() {
        List<PlayMode.PlayModeEnum> playModeEnums = new ArrayList<>();
        for (PlayMode playMode : playModes) {
            playModeEnums.add(playMode.getPlayModeEnum());
        }

        return playModeEnums;
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

        // Although a bit longer to get a list of enums and then check them using contains, I have
        // done it this way to get the string in a particular format - competitive, cooperative, solitaire.
        List<PlayMode.PlayModeEnum> playModeEnums = getPlayModeEnums();

        StringBuilder sb = new StringBuilder();

        if (playModeEnums.contains(PlayMode.PlayModeEnum.COMPETITIVE)) {
            sb.append("Competitive");
        }

        if (playModeEnums.contains(PlayMode.PlayModeEnum.COOPERATIVE)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("Cooperative");
        }

        if (playModeEnums.contains(PlayMode.PlayModeEnum.SOLITAIRE)) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("Solitaire");
        }

        return sb.toString();
    }

    public static BoardGameWithBgCategoriesAndPlayModes createUsingPlayModeEnumList(BoardGameWithBgCategories bgWithBgCategories,
                                                                                    List<PlayMode.PlayModeEnum> playModeEnums) {
        List<PlayMode> playModes = new ArrayList<>();
        for (PlayMode.PlayModeEnum playModeEnum :playModeEnums) {
            playModes.add(new PlayMode(bgWithBgCategories.getBoardGame().getId(), playModeEnum));
        }

        return new BoardGameWithBgCategoriesAndPlayModes(bgWithBgCategories, playModes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(bgWithBgCategories, 0);
        dest.writeList(playModes);
    }

    public static final Creator<BoardGameWithBgCategoriesAndPlayModes> CREATOR = new Creator<BoardGameWithBgCategoriesAndPlayModes>() {
        @Override
        public BoardGameWithBgCategoriesAndPlayModes[] newArray(int size) {
            return new BoardGameWithBgCategoriesAndPlayModes[size];
        }

        @Override
        public BoardGameWithBgCategoriesAndPlayModes createFromParcel(Parcel source) {
            return new BoardGameWithBgCategoriesAndPlayModes(source);
        }
    };

    /*
    public List<PlayMode.PlayModeEnum> getPlayModeEnums() {
        List<PlayMode.PlayModeEnum> playModeEnums = new ArrayList<>();

        for (PlayMode playMode : playModes) {
            playModeEnums.add(playMode.getPlayModeEnum());
        }

        return playModeEnums;
    }

    /**
     * Adds a play mode to the list of board game play modes.
     *
     * If the list already contains the play mode, does nothing.
     * @param playMode the play mode to be added.
     */
    /*
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
    /*
    public PlayMode.PlayModeEnum getPlayMode (int index) { return this.playModes.get(index); }

    /**
     * Removes a play mode from the list of board game play modes.
     * @param playMode a PlayMode
     */
    /*
    public void removePlayMode(PlayMode.PlayModeEnum playMode) { this.playModes.remove(playMode); }

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
    /*
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

     */
}
