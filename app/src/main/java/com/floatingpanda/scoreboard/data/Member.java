package com.floatingpanda.scoreboard.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "members")
public class Member {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "nickname")
    private String nickname;

    @Nullable
    @ColumnInfo(name = "real_name")
    private String realName;

    @Nullable
    private String notes;

    @Nullable
    @ColumnInfo(name = "img_file_path")
    private String imgFilePath;

    public Member(@NonNull String nickname, String realName, String notes) {
        this.nickname = nickname;
        this.realName = realName;
        this.notes = notes;
    }

    @Ignore
    public Member(@NonNull String nickname, String realName, String notes, String imgFilePath) {
        this.nickname = nickname;
        this.realName = realName;
        this.notes = notes;
        this.imgFilePath = imgFilePath;
    }

    public String getNickname() { return this.nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getRealName() { return this.realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getImgFilePath() { return this.imgFilePath; }
    public void setImgFilePath(String imgFilePath) { this.imgFilePath = imgFilePath; }
}
