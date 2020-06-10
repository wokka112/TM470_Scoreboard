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
 * Represents a member of board game groups. The member may belong to zero, one or more groups.
 */
@Entity(tableName = "members", indices = {@Index(value = "nickname",
        unique = true)})
public class Member implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "member_id")
    private int id;

    @NonNull
    @ColumnInfo(name = "nickname")
    private String nickname;

    @Nullable
    private String notes;

    @Nullable
    @ColumnInfo(name = "img_file_path")
    private String imgFilePath;

    @NonNull
    @ColumnInfo(name = "date_created")
    private Date dateCreated;

    @Ignore
    public Member(int id, String nickname, String notes, String imgFilePath, Date dateCreated) {
        this.id = id;
        this.nickname = nickname;
        this.notes = notes;
        this.imgFilePath = imgFilePath;
        this.dateCreated = dateCreated;
    }

    public Member(@NonNull String nickname, String notes, String imgFilePath) {
        this(0, nickname, notes, imgFilePath, new Date());
    }

    @Ignore
    public Member(@NonNull String nickname, String notes, String imgFilePath, Date dateCreated) {
        this(0, nickname, notes, imgFilePath, dateCreated);
    }

    @Ignore
    public Member(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.notes = member.getNotes();
        this.imgFilePath = member.getImgFilePath();
        this.dateCreated = member.getDateCreated();
    }

    @Ignore
    public Member(Parcel source) {
        this.id = source.readInt();
        this.nickname = source.readString();
        this.notes = source.readString();
        this.imgFilePath = source.readString();
        this.dateCreated = new Date(source.readLong());
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }
    public String getNickname() { return this.nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getImgFilePath() { return this.imgFilePath; }
    public void setImgFilePath(String imgFilePath) { this.imgFilePath = imgFilePath; }
    public Date getDateCreated() { return this.dateCreated; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nickname);
        dest.writeString(notes);
        dest.writeString(imgFilePath);
        dest.writeLong(dateCreated.getTime());
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }

        @Override
        public Member createFromParcel(Parcel source) {
            return new Member(source);
        }
    };

    /**
     * A member is equal to another member if they have the same nickname.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Member other = (Member) obj;

        return (other.getNickname().equals(this.getNickname()));
    }

    @NonNull
    @Override
    public String toString() {
        return "Member ID:" + this.getId() + ", Nickname: " + this.getNickname() + ", Date created: " + this.getDateCreated().toString();
    }
}