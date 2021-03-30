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

import com.floatingpanda.scoreboard.calculators.EloSkillRatingCalculator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * All tests use literal values calculated from an online Elo calculator available at
 * https://ratings.fide.com/calculator_rtd.phtml to ensure they work.
 */
public class EloSkillRatingCalculatorTest {

    @Test
    public void testEloSkillRatingWin() {
        GroupCategoryRatingChange player = new GroupCategoryRatingChange(1, 1, "Strategy", 1, 1500);
        GroupCategoryRatingChange opponent = new GroupCategoryRatingChange(1, 1, "Strategy", 2, 1500);

        EloSkillRatingCalculator eloSkillRatingCalculator = new EloSkillRatingCalculator();
        int kValue = 30;
        double skillRatingChange = eloSkillRatingCalculator.calculateSkillRatingChange(player, opponent, kValue);

        // Online Elo calculator available at
        assertEquals(15.0, skillRatingChange, 0.1);

        player.setAvgEloRating(1300.0);
        skillRatingChange = eloSkillRatingCalculator.calculateSkillRatingChange(player, opponent, kValue);

        assertEquals(22.8, skillRatingChange, 0.1);

        player.setAvgEloRating(1525);
        opponent.setAvgEloRating(1492);
        skillRatingChange = eloSkillRatingCalculator.calculateSkillRatingChange(player, opponent, kValue);

        assertEquals(13.5, skillRatingChange, 0.1);
    }

    @Test
    public void testEloSkillRatingDraw() {
        GroupCategoryRatingChange player = new GroupCategoryRatingChange(1, 1, "Strategy", 1, 1500);
        GroupCategoryRatingChange opponent = new GroupCategoryRatingChange(1, 1, "Strategy", 1, 1500);

        EloSkillRatingCalculator eloSkillRatingCalculator = new EloSkillRatingCalculator();
        int kValue = 30;
        double skillRatingChange = eloSkillRatingCalculator.calculateSkillRatingChange(player, opponent, kValue);

        // Online Elo calculator available at
        assertEquals(0, skillRatingChange, 0.0);

        player.setAvgEloRating(1300.0);
        skillRatingChange = eloSkillRatingCalculator.calculateSkillRatingChange(player, opponent, kValue);

        assertEquals(7.8, skillRatingChange, 0.1);

        player.setAvgEloRating(1525);
        opponent.setAvgEloRating(1492);
        skillRatingChange = eloSkillRatingCalculator.calculateSkillRatingChange(player, opponent, kValue);

        assertEquals(-1.5, skillRatingChange, 0.1);
    }

    @Test
    public void testEloSkillRatingLose() {
        GroupCategoryRatingChange player = new GroupCategoryRatingChange(1, 1, "Strategy", 2, 1500);
        GroupCategoryRatingChange opponent = new GroupCategoryRatingChange(1, 1, "Strategy", 1, 1500);

        EloSkillRatingCalculator eloSkillRatingCalculator = new EloSkillRatingCalculator();
        int kValue = 30;
        double skillRatingChange = eloSkillRatingCalculator.calculateSkillRatingChange(player, opponent, kValue);

        // Online Elo calculator available at
        assertEquals(-15.0, skillRatingChange, 0.1);

        player.setAvgEloRating(1300.0);
        skillRatingChange = eloSkillRatingCalculator.calculateSkillRatingChange(player, opponent, kValue);

        assertEquals(-7.2, skillRatingChange, 0.1);

        player.setAvgEloRating(1525);
        opponent.setAvgEloRating(1492);
        skillRatingChange = eloSkillRatingCalculator.calculateSkillRatingChange(player, opponent, kValue);

        assertEquals(-16.5, skillRatingChange, 0.1);
    }
}
