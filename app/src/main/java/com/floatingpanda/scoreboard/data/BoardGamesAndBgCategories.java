package com.floatingpanda.scoreboard.data;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class BoardGamesAndBgCategories {
    @Embedded public BoardGame boardGame;
    @Relation(
            parentColumn = "bg_name",
            entityColumn = "category_name",
            associateBy = @Junction(AssignedCategories.class)
    )
    public List<BgCategory> bgCategories;

    public BoardGame getBoardGame() { return this.boardGame; }
    public List<BgCategory> getBgCategories() { return this.bgCategories; }
}
