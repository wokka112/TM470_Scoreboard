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

    //TODO add in list variables for holding game records and members, make them Ignore
    //TODO find a way to make it so the list only partially loads as necessary maybe? Load it only,
    // when going onto a detailed groups activity.
    //TODO make it so the group details view has records and winner lists that expand when tapped,
    // rather than leading you to separate activities just to view the record or winner list details.

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
