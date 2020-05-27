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

import java.util.Date;

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

    @Nullable
    @ColumnInfo(name = "banner_file_path")
    private String bannerFilePath;

    //TODO add in list variables for holding game records and members, make them Ignore
    //TODO find a way to make it so the list only partially loads as necessary maybe? Load it only,
    // when going onto a detailed groups activity.
    //TODO make it so the group details view has records and winner lists that expand when tapped,
    // rather than leading you to separate activities just to view the record or winner list details.

    @Ignore
    public Group(int id, String groupName, String notes, String description, String imgFilePath, String bannerFilePath, Date dateCreated) {
        this.id = id;
        this.groupName = groupName;
        this.notes = notes;
        this.description = description;
        this.imgFilePath = imgFilePath;
        this.bannerFilePath = bannerFilePath;
        this.dateCreated = dateCreated;
    }

    public Group(String groupName, String notes, String description, String imgFilePath, String bannerFilePath) {
        this(0, groupName, notes, description, imgFilePath, bannerFilePath, new Date());
    }

    @Ignore
    public Group(Group group) {
        this.id = group.getId();
        this.groupName = group.getGroupName();
        this.notes = group.getNotes();
        this.description = group.getDescription();
        this.imgFilePath = group.getImgFilePath();
        this.bannerFilePath = group.getBannerFilePath();
        this.dateCreated = group.getDateCreated();
    }

    @Ignore
    public Group(Parcel source) {
        this.id = source.readInt();
        this.groupName = source.readString();
        this.notes = source.readString();
        this.description = source.readString();
        this.imgFilePath = source.readString();
        this.bannerFilePath = source.readString();
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
    public String getBannerFilePath() { return this.bannerFilePath; }
    public void setBannerFilePath(String bannerFilePath) { this.bannerFilePath = bannerFilePath; }
    public Date getDateCreated() { return this.dateCreated; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

    //TODO implement proper functionality
    public int getGamesPlayed() {
        return 100;
    }

    //TODO implement proper functionality. Maybe move this elsewhere?
    public int getMembersCount() {
        return 30;
    }

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
        dest.writeString(bannerFilePath);
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
