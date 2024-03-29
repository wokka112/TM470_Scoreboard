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

import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.floatingpanda.scoreboard.data.entities.AssignedCategory;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.BoardGame;

import java.util.List;

/**
 * A relation that combines a single board game drawn from the database with a list of board game
 * categories that are applicable for said board game.
 */
public class BoardGameWithBgCategories implements Parcelable {
    @Embedded
    public BoardGame boardGame;
    @Relation(
            parentColumn = "bg_id",
            entityColumn = "category_id",
            associateBy = @Junction(AssignedCategory.class)
    )
    public List<BgCategory> bgCategories;

    public BoardGameWithBgCategories(BoardGame boardGame, List<BgCategory> bgCategories) {
        this.boardGame = boardGame;
        this.bgCategories = bgCategories;
    }

    public BoardGameWithBgCategories(Parcel source) {
        this.boardGame = source.readParcelable(BoardGame.class.getClassLoader());
        this.bgCategories = source.readArrayList(BgCategory.class.getClassLoader());
    }

    public BoardGame getBoardGame() { return this.boardGame; }
    public void setBoardGame(BoardGame boardGame) { this.boardGame = boardGame; }

    public List<BgCategory> getBgCategories() { return this.bgCategories; }
    public void setBgCategories(List<BgCategory> bgCategories) { this.bgCategories = bgCategories; }

    /**
     * Returns a string of the categories that the board game fits into. Each category is separated
     * by a comma.
     *
     * If the board game has no categories set, returns "No categories set"
     * @return A string of the categories the board game fills.
     */
    public String getFullBgCategoriesString() {
        if (bgCategories.isEmpty()) {
            return "No categories set";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(bgCategories.get(0).getCategoryName());

        for (int i = 1; (i < bgCategories.size()) && (i < 3); i++) {
            sb.append(", ");
            sb.append(bgCategories.get(i).getCategoryName());
        }

        return sb.toString();
    }

    /**
     * Returns a string of the categories that the board game fits into, limited to the first 3
     * categories followed by '+x more categories". Each category is separated by a comma.
     *
     * If the board game has no categories set, returns "No categories set"
     * @return A string of the categories the board game fills.
     */
    public String getLimitedBgCategoriesString() {
        if (bgCategories.isEmpty()) {
            return "No categories set";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(bgCategories.get(0).getCategoryName());

        for (int i = 1; (i < bgCategories.size()) && (i < 3); i++) {
            sb.append(", ");
            sb.append(bgCategories.get(i).getCategoryName());
        }

        if (bgCategories.size() > 3) {
            int moreCategories = bgCategories.size() - 3;
            sb.append(" +" + moreCategories + " more categories.");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        BoardGameWithBgCategories boardGameWithBgCategories = (BoardGameWithBgCategories) obj;

        for (BgCategory bgCategory : bgCategories) {
            if (!this.getBgCategories().contains(bgCategory)) {
                return false;
            }
        }

        return (boardGameWithBgCategories.getBoardGame().equals(this.getBoardGame()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(boardGame, 0);
        dest.writeList(bgCategories);
    }

    public static final Creator<BoardGameWithBgCategories> CREATOR = new Creator<BoardGameWithBgCategories>() {
        @Override
        public BoardGameWithBgCategories[] newArray(int size) {
            return new BoardGameWithBgCategories[size];
        }

        @Override
        public BoardGameWithBgCategories createFromParcel(Parcel source) {
            return new BoardGameWithBgCategories(source);
        }
    };
}
