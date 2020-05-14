package com.floatingpanda.scoreboard.typeconverters;

import androidx.room.TypeConverter;

import com.floatingpanda.scoreboard.data.BoardGame;

public class PlayModeTypeConverter {
    @TypeConverter
    public static BoardGame.PlayMode toPlayMode(String name) {
        return BoardGame.PlayMode.valueOf(name);
    }

    @TypeConverter
    public static String fromPlayMode(BoardGame.PlayMode playMode) {
        return playMode.name();
    }
}
