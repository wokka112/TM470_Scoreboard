package com.floatingpanda.scoreboard.system;

//TODO sort out placeholders and add attributes and methods
//TODO add variable to hold groups??

import android.os.Parcel;
import android.os.Parcelable;

import com.floatingpanda.scoreboard.R;

public class Member implements Parcelable {
    private String nickname;
    private int imageResourceId = NO_IMAGE;

    private static final int NO_IMAGE = -1;

    public Member(String nickname) {
        this.nickname = nickname;
    }

    public Member(Parcel source) {
        this.nickname = source.readString();
        this.imageResourceId = source.readInt();
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getOtherNames() { return "Placeholder 1; Placeholder 2"; }

    public String getNotes() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam et risus lorem. " +
                "Maecenas faucibus, sem a lacinia suscipit, lacus velit consectetur nunc, vel " +
                "iaculis tortor nisl ut elit. Morbi ac dictum libero, vulputate congue nisi.";
    }

    public String getBestBoardGame() {
        return "Placeholder best game";
    }

    public String getWorstBoardGame() {
        return "Placeholder worst game";
    }

    public int getImageResourceId() {

        if (this.imageResourceId == NO_IMAGE) {
            //TODO replace this with default group img
            return R.drawable.ic_launcher_foreground;
        }

        return this.imageResourceId;
    }

    //TODO change to return actual number of groups member is part of
    public int getGroupsCount() {
        return 7;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nickname);
        dest.writeInt(this.imageResourceId);
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };
}
