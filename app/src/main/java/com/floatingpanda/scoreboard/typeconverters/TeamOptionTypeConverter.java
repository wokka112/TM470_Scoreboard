package com.floatingpanda.scoreboard.typeconverters;

import androidx.room.TypeConverter;

import com.floatingpanda.scoreboard.data.BoardGame;

public class TeamOptionTypeConverter {
    @TypeConverter
    public static BoardGame.TeamOption toTeamOption(String name) {
        return BoardGame.TeamOption.valueOf(name);
    }

    @TypeConverter
    public static String fromTeamOption(BoardGame.TeamOption teamOption) {
        return teamOption.name();
    }
}