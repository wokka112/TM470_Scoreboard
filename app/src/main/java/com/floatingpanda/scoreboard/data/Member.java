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

    public Member(@NonNull String nickname, String notes) {
        this.id = 0;
        this.nickname = nickname;
        this.notes = notes;
    }

    @Ignore
    public Member(@NonNull String nickname, String notes, String imgFilePath) {
        this.id = 0;
        this.nickname = nickname;
        this.notes = notes;
        this.imgFilePath = imgFilePath;
    }

    @Ignore
    public Member(Parcel source) {
        this.id = source.readInt();
        this.nickname = source.readString();
        this.notes = source.readString();
        this.imgFilePath = source.readString();
    }

    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }
    public String getNickname() { return this.nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getImgFilePath() { return this.imgFilePath; }
    public void setImgFilePath(String imgFilePath) { this.imgFilePath = imgFilePath; }

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
}
