package com.floatingpanda.scoreboard;


import com.floatingpanda.scoreboard.calculators.CompetitiveScoreCalculator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CompetitiveScoreCalculatorTest {
    @Test
    public void testCalculateCompetitiveScore() {
        CompetitiveScoreCalculator competitiveScoreCalculator = new CompetitiveScoreCalculator();

        int numberOfPositions = 4;
        int difficulty = 5;

        for (int position = 1; position <= numberOfPositions; position++) {
            int correctScore = difficulty * (numberOfPositions + 1 - position);
            int calculatedScore = competitiveScoreCalculator.calculateCompetitiveScore(position, numberOfPositions, difficulty);

            assertEquals(correctScore, calculatedScore);
            assertNotEquals(0, calculatedScore);
        }

        numberOfPositions = 8;
        difficulty = 1;

        for (int position = 1; position <= numberOfPositions; position++) {
            int correctScore = difficulty * (numberOfPositions + 1 - position);
            int calculatedScore = competitiveScoreCalculator.calculateCompetitiveScore(position, numberOfPositions, difficulty);

            assertEquals(correctScore, calculatedScore);
            assertNotEquals(0, calculatedScore);
        }
    }
}
