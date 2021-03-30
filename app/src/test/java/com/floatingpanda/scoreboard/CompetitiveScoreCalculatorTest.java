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
