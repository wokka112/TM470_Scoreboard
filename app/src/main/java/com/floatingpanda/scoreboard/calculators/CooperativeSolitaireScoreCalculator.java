package com.floatingpanda.scoreboard.calculators;

/**
 * Calculates the score for a team or a single player in a cooperative or solitaire game. Scores for
 * these games are calculated based on whether the team/player won or lost the game. If the
 * team/player lost, they got 0 points. If the team/player won, they get the game's difficulty in
 * points.
 */
public class CooperativeSolitaireScoreCalculator {
    public CooperativeSolitaireScoreCalculator() {

    }

    /**
     * Takes a boolean representing whether the game was won, and takes the game's difficulty (an
     * int between 1 and 5 (inclusive)) and calculates the score based on whether the game was won
     * or lost. If the game was won, score = difficulty. If the game was lost, score = 0.
     * @param won whether game was won or lost
     * @param difficulty difficulty of game played (int between 1 and 5 (inclusive))
     * @return
     */
    public int calculateCooperativeSolitaireScore(boolean won, int difficulty) {
        if (won) {
            return difficulty;
        }

        return 0;
    }
}
