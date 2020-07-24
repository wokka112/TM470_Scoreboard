package com.floatingpanda.scoreboard.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

/**
 * Represents the play modes - competitive, cooperative and solitaire - a board game can be played
 * in. The board game the play modes are viable for is stored in the board_games table (BoardGame
 * class) and linked to via the bgId attribute, which is a foreign key to the board_games table.
 */
@Entity(tableName = "play_modes", primaryKeys = {"bg_id", "play_mode_enum"},
        foreignKeys = {@ForeignKey(entity = BoardGame.class,
                parentColumns = "bg_id",
                childColumns = "bg_id",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE)})
public class PlayMode implements Parcelable {

    @ColumnInfo(name = "bg_id")
    int bgId;

    @NonNull
    @ColumnInfo(name = "play_mode_enum")
    PlayModeEnum playModeEnum;

    public PlayMode(int bgId, PlayModeEnum playModeEnum) {
        this.bgId = bgId;
        this.playModeEnum = playModeEnum;
    }

    @Ignore
    public PlayMode(Parcel source) {
        this.bgId = source.readInt();
        this.playModeEnum = PlayMode.PlayModeEnum.valueOf(source.readString());

    }

    public int getBgId() { return this.bgId; }
    public void setBgId(int bgId) { this.bgId = bgId; }

    public PlayModeEnum getPlayModeEnum() { return playModeEnum; }
    public void setPlayModeEnum(PlayModeEnum playModeEnum) { this.playModeEnum = playModeEnum; }

    public String getPlayModeString() {
        return this.playModeEnum.toString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bgId);
        dest.writeString(playModeEnum.name());
    }

    public static final Creator<PlayMode> CREATOR = new Creator<PlayMode>() {
        @Override
        public PlayMode[] newArray(int size) {
            return new PlayMode[size];
        }

        @Override
        public PlayMode createFromParcel(Parcel source) {
            return new PlayMode(source);
        }
    };

    public enum PlayModeEnum {
        ERROR,
        COMPETITIVE,
        COOPERATIVE,
        SOLITAIRE
    }
}


