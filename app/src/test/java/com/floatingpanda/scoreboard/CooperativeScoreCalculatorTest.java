/*
ScoreBoard

Copyright © 2020 Adam Poole

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
