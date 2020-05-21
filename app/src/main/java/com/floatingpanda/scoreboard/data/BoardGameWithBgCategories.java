package com.floatingpanda.scoreboard.data;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class BoardGameWithBgCategories {
    @Embedded public BoardGame boardGame;
    @Relation(
            parentColumn = "bg_id",
            entityColumn = "category_id",
            associateBy = @Junction(AssignedCategory.class)
    )
    public List<BgCategory> bgCategories;

    public BoardGame getBoardGame() {
        if (this.boardGame.getBgCategories().isEmpty()) {
            this.boardGame.setBgCategories(this.bgCategories);
        }

        return this.boardGame;
    }
    public List<BgCategory> getBgCategories() { return this.bgCategories; }
}
