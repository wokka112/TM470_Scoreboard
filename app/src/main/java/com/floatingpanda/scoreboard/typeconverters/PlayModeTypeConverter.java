package com.floatingpanda.scoreboard.typeconverters;

import androidx.room.TypeConverter;

import com.floatingpanda.scoreboard.data.entities.PlayMode;

public class PlayModeTypeConverter {
    @TypeConverter
    public static PlayMode.PlayModeEnum toPlayMode(String name) {
        if (name == null) {
            return PlayMode.PlayModeEnum.ERROR;
        }
        return PlayMode.PlayModeEnum.valueOf(name);
    }

    @TypeConverter
    public static String fromPlayMode(PlayMode.PlayModeEnum playMode) {
        return playMode.name();
    }
}
