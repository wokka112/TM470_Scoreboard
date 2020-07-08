package com.floatingpanda.scoreboard.calculators;

public class CompetitiveScoreCalculator {
    public CompetitiveScoreCalculator() {   }

    public int calculateCompetitiveScore(int position, int numberOfPositions, int difficulty) {
        return difficulty * (numberOfPositions + 1 - position);
    }
}
