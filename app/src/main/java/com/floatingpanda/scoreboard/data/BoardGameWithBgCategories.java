package com.floatingpanda.scoreboard.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.os.ParcelableCompatCreatorCallbacks;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

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
