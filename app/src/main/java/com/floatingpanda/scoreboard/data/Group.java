package com.floatingpanda.scoreboard.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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

    //TODO add in list variables for holding game records and members, make them Ignore
    //TODO find a way to make it so the list only partially loads as necessary maybe? Load it only,
    // when going onto a detailed groups activity.
    //TODO make it so the group details view has records and winner lists that expand when tapped,
    // rather than leading you to separate activities just to view the record or winner list details.

    public Group(String groupName, String notes, String description) {
        this.id = 0;
        this.groupName = groupName;
        this.notes = notes;
        this.description = description;
    }

    @Ignore
    public Group(String groupName, String notes, String description, String imgFilePath) {
        this.id = 0;
        this.groupName = groupName;
        this.notes = notes;
        this.description = description;
        this.imgFilePath = imgFilePath;
    }

    @Ignore
    public Group(Parcel source) {
        this.id = source.readInt();
        this.groupName = source.readString();
        this.notes = source.readString();
        this.description = source.readString();
        this.imgFilePath = source.readString();
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

    //TODO implement properly functionality
    public int getGamesPlayed() {
        return 100;
    }

    //TODO implement proper functionality
    //TODO implement a Date created element in the database for this entity
    public int getDateCreated() {
        return 24021995;
    }

    //TODO implement proper functionality
    public int getMembersCount() {
        return 30;
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
