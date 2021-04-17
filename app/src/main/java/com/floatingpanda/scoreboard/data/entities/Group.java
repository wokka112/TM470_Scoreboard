/*
ScoreBoard

Copyright © 2020 Adam Poole

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.floatingpanda.scoreboard.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Represents a group which can have group members and game records linked to it. Group members are
 * linked to the group via many-to-many relations in the group_members table (GroupMember class).
 * Game records are linked to the group via a foreign key in the game records table (GameRecord
 * class).
 */
@Entity(tableName = "groups", indices = {@Index(value = "group_name",
        unique = true)})
public class Group implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "group_id")
    private int id;

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

    @NonNull
    @ColumnInfo(name = "date_created")
    private Date dateCreated;

    @Ignore
    public Group(int id, String groupName, String notes, String description, String imgFilePath, Date dateCreated) {
        this.id = id;
        this.groupName = groupName;
        this.notes = notes;
        this.description = description;
        this.imgFilePath = imgFilePath;
        this.dateCreated = dateCreated;
    }

    public Group(String groupName, String notes, String description, String imgFilePath) {
        this(0, groupName, notes, description, imgFilePath, new Date());
    }

    @Ignore
    public Group(String groupName, String notes, String description) {
        this(0, groupName, notes, description, null, new Date());
    }

    @Ignore
    public Group(Group group) {
        this.id = group.getId();
        this.groupName = group.getGroupName();
        this.notes = group.getNotes();
        this.description = group.getDescription();
        this.imgFilePath = group.getImgFilePath();
        this.dateCreated = group.getDateCreated();
    }

    @Ignore
    public Group(Parcel source) {
        this.id = source.readInt();
        this.groupName = source.readString();
        this.notes = source.readString();
        this.description = source.readString();
        this.imgFilePath = source.readString();
        this.dateCreated = new Date(source.readLong());
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }
    public String getGroupName() { return this.groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }
    public String getImgFilePath() { return this.imgFilePath; }
    public void setImgFilePath(String imgFilePath) { this.imgFilePath = imgFilePath; }
    public void setImgFilePathToDefault() { this.imgFilePath = null; }
    public Date getDateCreated() { return this.dateCreated; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Group group = (Group) obj;

        return (group.getGroupName().equals(this.getGroupName()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(groupName);
        dest.writeString(notes);
        dest.writeString(description);
        dest.writeString(imgFilePath);
        dest.writeLong(dateCreated.getTime());
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }

        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }
    };
}
