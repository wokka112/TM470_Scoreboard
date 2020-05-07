package com.floatingpanda.scoreboard.old.system;

import java.util.ArrayList;
import java.util.List;

public class GameRecord {
    private int recordID;
    private int date;
    private int time;
    private boolean teamYesNo;
    private String gamePlayed;
    private int gameDifficulty;
    private String gameTypePlayed;
    private int playerCount;
    private List<Member> players;

    public GameRecord(int id, int date, int time, String boardGame, int gameDifficulty, int playerCount, List<Member> players) {
        this.recordID = id;
        this.date = date;
        this.time = time;
        this.gamePlayed = boardGame;
        this.gameDifficulty = gameDifficulty;
        this.playerCount = playerCount;
        this.players = players;
    }

    public int getId() { return recordID; }

    public int getDate() { return date; }

    public int getTime() { return time; }

    public String getGamePlayed() { return gamePlayed; }

    public int getGameDifficulty() { return gameDifficulty; }

    public int getPlayerCount() { return playerCount; }

    public List<Member> getPlayers() { return new ArrayList<Member>(players); };
}
