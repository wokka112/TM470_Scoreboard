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

package com.floatingpanda.scoreboard.data.relations;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.entities.PlayMode;

import java.util.ArrayList;
import java.util.List;

/**
 * A relation that combines a single board game with assigned categories and a list of the play
 * modes the board game can be played in.
 */
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

    public BoardGame getBoardGame() { return bgWithBgCategories.getBoardGame(); }
    public List<BgCategory> getBgCategories() { return bgWithBgCategories.getBgCategories(); }

    /**
     * Creates a list of PlayModeEnum's from the list of PlayMode objects and returns said list of
     * enums.
     * @return
     */
    public List<PlayMode.PlayModeEnum> getPlayModeEnums() {
        List<PlayMode.PlayModeEnum> playModeEnums = new ArrayList<>();
        for (PlayMode playMode : playModes) {
            playModeEnums.add(playMode.getPlayModeEnum());
        }

        return playModeEnums;
    }

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

        //TODO look into how to change this so I can pull strings from the strings file. I don't
        // want this class holding context, but I don't know how else to deal with this other than
        // move the enums and provide them with specific strings or move this method somewhere
        // else. Such as do it in the code somewhere.
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes =
                (BoardGameWithBgCategoriesAndPlayModes) obj;

        for (PlayMode playMode : boardGameWithBgCategoriesAndPlayModes.getPlayModes()) {
            if (!this.getPlayModes().contains(playMode)) {
                return false;
            }
        }

        return (boardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().equals(this.getBoardGameWithBgCategories()));
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

    @NonNull
    @Override
    public String toString() {
        return getBoardGame().getBgName();
    }

    public static BoardGameWithBgCategoriesAndPlayModes createUsingPlayModeEnumList(BoardGameWithBgCategories bgWithBgCategories,
                                                                                    List<PlayMode.PlayModeEnum> playModeEnums) {
        List<PlayMode> playModes = new ArrayList<>();
        for (PlayMode.PlayModeEnum playModeEnum :playModeEnums) {
            playModes.add(new PlayMode(bgWithBgCategories.getBoardGame().getId(), playModeEnum));
        }

        return new BoardGameWithBgCategoriesAndPlayModes(bgWithBgCategories, playModes);
    }
}
