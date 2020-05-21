package com.floatingpanda.scoreboard.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class BoardGameWithBgCategoriesAndPlayModes {
    @Embedded
    public BoardGameWithBgCategories BgAndBgCategories;
    @Relation(
            parentColumn = "bg_name",
            entityColumn = "parent_bg_name"
    )
    public List<PlayMode> playModes;

    public BoardGame getBoardGame() {
        BoardGame boardGame = BgAndBgCategories.getBoardGame();
        boardGame.setPlayModes(getPlayModeEnums());

        return boardGame;
    }

    public List<PlayMode> getPlayModes() { return this.playModes; }

    public List<PlayMode.PlayModeEnum> getPlayModeEnums() {
        List<PlayMode.PlayModeEnum> playModeEnums = new ArrayList<>();

        for (PlayMode playMode : playModes) {
            playModeEnums.add(playMode.getPlayModeEnum());
        }

        return playModeEnums;
    }
}
