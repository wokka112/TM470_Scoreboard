package com.floatingpanda.scoreboard.calculators;

import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.interfaces.Scoreable;

import java.util.List;

public class Calculator {
    private CompetitiveScoreCalculator competitiveScoreCalculator;
    private CooperativeSolitaireScoreCalculator cooperativeSolitaireScoreCalculator;

    public Calculator() {
        competitiveScoreCalculator = new CompetitiveScoreCalculator();
        cooperativeSolitaireScoreCalculator = new CooperativeSolitaireScoreCalculator();
    }

    /**
     * Takes a list of scoreables for a competitive game, calculates their scores and assigns the scoreables the new scores.
     * @param difficulty
     * @param scoreables
     */
    public void calculateCompetitiveScores(int difficulty, List<? extends Scoreable> scoreables) {
        for (Scoreable scoreable : scoreables) {
            int score = competitiveScoreCalculator.calculateCompetitiveScore(scoreable.getPosition(), scoreables.size(), difficulty);
            scoreable.setScore(score);
        }
    }

    /**
     * Takes a list of scoreables for a cooperative game, calculates their scores and assigns the scoreables the new scores.
     * @param difficulty
     * @param scoreables
     */
    public void calculateCooperativeSolitaireScores(int difficulty, List<? extends Scoreable> scoreables, boolean won) {
        for (Scoreable scoreable : scoreables) {
            int score = cooperativeSolitaireScoreCalculator.calculateCooperativeSolitaireScore(won, difficulty);
            scoreable.setScore(score);
        }
    }
}
