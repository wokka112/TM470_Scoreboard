package com.floatingpanda.scoreboard.system;

//TODO add attributes and methods

import com.floatingpanda.scoreboard.R;

public class Member {
    private String nickname;
    private int imageResourceId = NO_IMAGE;

    private static final int NO_IMAGE = -1;

    public Member(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
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
}
