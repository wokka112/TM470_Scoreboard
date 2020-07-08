package com.floatingpanda.scoreboard.calculators;

public class CooperativeSolitaireScoreCalculator {
    public CooperativeSolitaireScoreCalculator() {

    }

    public int calculateCooperativeSolitaireScore(boolean won, int difficulty) {
        if (won) {
            return difficulty;
        }

        return 0;
    }
}
