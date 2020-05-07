package com.floatingpanda.scoreboard.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "groups")
public class Group {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "group_name")
    private String groupName;

    @Nullable
    private String notes;

    @Nullable
    private String description;

    @Nullable
    @ColumnInfo(name = "img_file_path")
    private String imgFilePath;

    public Group(String groupName, String notes, String description) {
        this.groupName = groupName;
        this.notes = notes;
        this.description = description;
    }

    @Ignore
    public Group(String groupName, String notes, String description, String imgFilePath) {
        this.groupName = groupName;
        this.notes = notes;
        this.description = description;
        this.imgFilePath = imgFilePath;
    }

    public String getGroupName() { return this.groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }
    public String getImgFilePath() { return this.imgFilePath; }
    public void setImgFilePath(String imgFilePath) { this.imgFilePath = imgFilePath; }
}
