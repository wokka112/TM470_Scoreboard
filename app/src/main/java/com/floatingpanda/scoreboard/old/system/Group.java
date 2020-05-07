package com.floatingpanda.scoreboard.old.system;

//TODO sort out placeholders and add attributes and methods
//TODO add constructor for imageresource and one for not
//TODO add variables to hold members and gamerecords
//TODO add variables to hold monthlyscores??

import com.floatingpanda.scoreboard.R;

public class Group {
    private String name;
    private int imageResourceId = NO_IMAGE;

    //TODO change this to default image maybe? Maybe use an enum or some other method than this?
    private static final int NO_IMAGE = -1;

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getImageResourceId() {

        if (this.imageResourceId == NO_IMAGE) {
            //TODO replace this with default group img
            return R.drawable.ic_launcher_foreground;
        }

        return this.imageResourceId;
    }

    //TODO change this from placeholders
    public int getMemberCount() {
        return 20;
    }

    //TODO change this from placeholders
    public int getRecordsCount() {
        return 10;
    }
}
