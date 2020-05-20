package com.floatingpanda.scoreboard.typeconverters;

import androidx.room.TypeConverter;

import com.floatingpanda.scoreboard.data.PlayMode;

public class PlayModeTypeConverter {
    @TypeConverter
    public static PlayMode.PlayModeEnum toPlayMode(String name) {
        return PlayMode.PlayModeEnum.valueOf(name);
    }

    @TypeConverter
    public static String fromPlayMode(PlayMode.PlayModeEnum playMode) {
        return playMode.name();
    }
}
