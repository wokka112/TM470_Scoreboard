package com.floatingpanda.scoreboard;

import com.floatingpanda.scoreboard.calculators.CooperativeSolitaireScoreCalculator;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CooperativeScoreCalculatorTest {
    @Test
    public void calculateCooperativeSolitaireScore() {
        CooperativeSolitaireScoreCalculator cooperativeSolitaireScoreCalculator = new CooperativeSolitaireScoreCalculator();

        boolean won = true;
        int difficulty = 5;
        int correctScore = difficulty;
        int calculatedScore = cooperativeSolitaireScoreCalculator.calculateCooperativeSolitaireScore(won, difficulty);

        assertEquals(correctScore, calculatedScore);
        assertNotEquals(0, calculatedScore);

        difficulty = 1;
        correctScore = difficulty;
        calculatedScore = cooperativeSolitaireScoreCalculator.calculateCooperativeSolitaireScore(won, difficulty);

        assertEquals(correctScore, calculatedScore);
        assertNotEquals(0, calculatedScore);

        won = false;
        difficulty = 5;
        correctScore = 0;
        calculatedScore = cooperativeSolitaireScoreCalculator.calculateCooperativeSolitaireScore(won, difficulty);

        assertEquals(correctScore, calculatedScore);
        assertNotEquals(difficulty, calculatedScore);

        difficulty = 1;
        correctScore = 0;
        calculatedScore = cooperativeSolitaireScoreCalculator.calculateCooperativeSolitaireScore(won, difficulty);

        assertEquals(correctScore, calculatedScore);
        assertNotEquals(difficulty, calculatedScore);
    }
}
