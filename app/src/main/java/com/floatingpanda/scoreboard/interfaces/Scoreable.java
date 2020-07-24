package com.floatingpanda.scoreboard.interfaces;

/**
 * Interface that provides 3 methods needed to calculate and set scores in this application.
 */
public interface Scoreable {
    int getPosition();
    int getScore();
    void setScore(int score);
}
