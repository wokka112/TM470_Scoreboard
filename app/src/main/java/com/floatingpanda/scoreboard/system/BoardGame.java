package com.floatingpanda.scoreboard.system;

//TODO add attributes and methods

import com.floatingpanda.scoreboard.R;

import java.util.ArrayList;
import java.util.List;

public class BoardGame {
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
}
