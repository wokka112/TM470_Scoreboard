package com.floatingpanda.scoreboard.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "play_modes", primaryKeys = {"parent_bg_name", "play_mode_enum"},
        foreignKeys = {@ForeignKey(entity = BoardGame.class,
                parentColumns = "bg_name",
                childColumns = "parent_bg_name",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE)})
public class PlayMode {

    @NonNull
    @ColumnInfo(name = "parent_bg_name")
    String bgName;

    @NonNull
    @ColumnInfo(name = "play_mode_enum")
    PlayModeEnum playModeEnum;

    public PlayMode(String bgName, PlayModeEnum playModeEnum) {
        this.bgName = bgName;
        this.playModeEnum = playModeEnum;
    }

    public String getBgName() { return this.bgName; }
    public void setBgName(String bgName) { this.bgName = bgName; }

    public PlayModeEnum getPlayModeEnum() { return playModeEnum; }

    public void setPlayModeEnum(PlayModeEnum playModeEnum) { this.playModeEnum = playModeEnum; }

    public enum PlayModeEnum {
        COMPETITIVE,
        COOPERATIVE,
        SOLITAIRE
    }
}


