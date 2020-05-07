package com.floatingpanda.scoreboard.old.system;

//TODO add attributes and methods

import android.os.Parcel;
import android.os.Parcelable;

import com.floatingpanda.scoreboard.R;

import java.util.ArrayList;
import java.util.List;

//TODO sort out placeholders and add methods and attributes

public class BoardGame implements Parcelable {
    private String name;
    private int difficulty;
    private int minPlayers;
    private int maxPlayers;
    private int imageResourceId = NO_IMAGE;

    //TODO add in category class and change to hold Category objects.
    private List<String> categories;

    private final static int NO_IMAGE = -1;

    public BoardGame(String name, int difficulty, int minPlayers, int maxPlayers) {
        this.name = name;
        this.difficulty = difficulty;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;

        if (this.categories != null) {
            this.categories = new ArrayList<String>();
        }
    }

    public BoardGame(String name, int difficulty, int minPlayers, int maxPlayers, List<String> categories) {
        this.name = name;
        this.difficulty = difficulty;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;

        if (categories != null) {
            this.categories = categories;
        } else {
            this.categories = new ArrayList<String>();
        }
    }

    public BoardGame(Parcel source) {
        name = source.readString();
        difficulty = source.readInt();
        minPlayers = source.readInt();
        maxPlayers = source.readInt();
        imageResourceId = source.readInt();
        categories = source.createStringArrayList();
    }

    public String getName() {
        return this.name;
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    public int getMinPlayers() {
        return this.minPlayers;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public String getDescription() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam et risus lorem. " +
                "Maecenas faucibus, sem a lacinia suscipit, lacus velit consectetur nunc, vel " +
                "iaculis tortor nisl ut elit. Morbi ac dictum libero, vulputate congue nisi.";
    }

    public String getHouseRules() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam et risus lorem. " +
                "Maecenas faucibus, sem a lacinia suscipit, lacus velit consectetur nunc, vel " +
                "iaculis tortor nisl ut elit. Morbi ac dictum libero, vulputate congue nisi.";
    }

    public String getNotes() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam et risus lorem. " +
                "Maecenas faucibus, sem a lacinia suscipit, lacus velit consectetur nunc, vel " +
                "iaculis tortor nisl ut elit. Morbi ac dictum libero, vulputate congue nisi.";
    }

    public List<String> getCategories() {
        if (this.categories == null || this.categories.isEmpty()) {
            return new ArrayList<String>();
        } else {
            return this.categories;
        }
    }

    public int getImageResourceId() {

        if (this.imageResourceId == NO_IMAGE) {
            //TODO replace this with default group img
            return R.drawable.ic_launcher_foreground;
        }

        return this.imageResourceId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.difficulty);
        dest.writeInt(this.minPlayers);
        dest.writeInt(this.maxPlayers);
        dest.writeInt(imageResourceId);
        dest.writeList(categories);
    }

    public static final Creator<BoardGame> CREATOR = new Creator<BoardGame>() {
        @Override
        public BoardGame createFromParcel(Parcel in) {
            return new BoardGame(in);
        }

        @Override
        public BoardGame[] newArray(int size) {
            return new BoardGame[size];
        }
    };
}
