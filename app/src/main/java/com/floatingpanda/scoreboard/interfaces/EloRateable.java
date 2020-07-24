package com.floatingpanda.scoreboard.interfaces;

/**
 * An interface providing two simply methods required for rating players using Elo's rating system
 * in this app - their Elo rating, and their finishing position in the game.
 */
public interface EloRateable {
    double getEloRating();
    int getFinishingPosition();
}
