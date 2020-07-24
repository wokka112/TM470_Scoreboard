package com.floatingpanda.scoreboard.calculators;

/**
 * Calculates scores for competitive games. A player or team's competitive score is calculated as
 *
 *      (n + 1 - pos) * dif
 *
 * where n is the number of players or teams in the game, pos is the current player or team's
 * finishing position in the game (e.g. 1st, 2nd, 3rd), and dif is the game's difficulty (an int
 * value between 1 and 5 (inclusive)).
 */
public class CompetitiveScoreCalculator {
    public CompetitiveScoreCalculator() {   }

    /**
     * Takes a player or team's position, the total number of positions in the game, and the game's
     * difficulty, and from this calculates and returns a score using the formula in the class
     * javadoc.
     * @param position current team or player's position.
     * @param numberOfPositions total number of positions (usually total number of players/teams)
     * @param difficulty difficulty of competitive game played
     * @return
     */
    public int calculateCompetitiveScore(int position, int numberOfPositions, int difficulty) {
        return difficulty * (numberOfPositions + 1 - position);
    }
}
