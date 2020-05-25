package com.floatingpanda.scoreboard.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "play_modes", primaryKeys = {"bg_id", "play_mode_enum"},
        foreignKeys = {@ForeignKey(entity = BoardGame.class,
                parentColumns = "bg_id",
                childColumns = "bg_id",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE)})
public class PlayMode {

    @NonNull
    @ColumnInfo(name = "bg_id")
    int bgId;

    @NonNull
    @ColumnInfo(name = "play_mode_enum")
    PlayModeEnum playModeEnum;

    public PlayMode(int bgId, PlayModeEnum playModeEnum) {
        this.bgId = bgId;
        this.playModeEnum = playModeEnum;
    }

    public int getBgId() { return this.bgId; }
    public void setBgId(int bgId) { this.bgId = bgId; }

    public PlayModeEnum getPlayModeEnum() { return playModeEnum; }

    public void setPlayModeEnum(PlayModeEnum playModeEnum) { this.playModeEnum = playModeEnum; }

    public enum PlayModeEnum {
        COMPETITIVE,
        COOPERATIVE,
        SOLITAIRE
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        PlayMode playMode = (PlayMode) obj;

        return (playMode.getBgId() == this.getBgId()
                && playMode.getPlayModeEnum() == this.getPlayModeEnum());
    }
}


