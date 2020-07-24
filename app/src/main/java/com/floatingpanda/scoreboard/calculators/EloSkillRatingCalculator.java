package com.floatingpanda.scoreboard.calculators;

import com.floatingpanda.scoreboard.interfaces.EloRateable;

/**
 * Calculator that calculates Elo skill rating changes using Elo's system.
 */
public class EloSkillRatingCalculator {

    /**
     * Calculates and returns the skill rating change for player against opponent. Uses Elo's rating
     * system.
     * @param player
     * @param opponent
     * @return
     */
    public double calculateSkillRatingChange(EloRateable player, EloRateable opponent, int kValue) {
        //Calculate S
        double sValue = calculateSValue(player, opponent);

        //Get player rating
        double playerRating = player.getEloRating();

        //Get opponent rating
        double opponentRating = opponent.getEloRating();

        //Calculate dij
        double difference = playerRating - opponentRating;

        if (difference > 400) {
            difference = 400.0;
        } else if (difference < -400) {
            difference = -400.0;
        }

        //Perform calculation
        //new rating = old rating + K(S - (1 / (1 + 10^(-difference/400))));
        double exponent = -(difference / 400.0);
        double exponentedFigure = Math.pow(10, exponent);
        double denominator = 1 + exponentedFigure;
        double fraction = 1 / denominator;
        double ratingChange = kValue * (sValue - fraction);

        return ratingChange;
    }

    /**
     * Calculates the score of player in relation to opponent.
     *
     * If player did better than opponent, 1 is returned.
     * If player drew with opponent, 0.5 is returned.
     * If player lost to opponent, 0 is returned.
     * @param player
     * @param opponent
     * @return
     */
    private double calculateSValue(EloRateable player, EloRateable opponent) {
        if (player.getFinishingPosition() < opponent.getFinishingPosition()) {
            return 1.0;
        } else if (player.getFinishingPosition() == opponent.getFinishingPosition()) {
            return 0.5;
        } else {
            return 0.0;
        }
    }
}
