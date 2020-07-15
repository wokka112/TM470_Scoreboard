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
        GroupCategoryRatingChange player = new GroupCategoryRatingChange(1, 1, 1500);
        GroupCategoryRatingChange opponent = new GroupCategoryRatingChange(1, 2, 1500);

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
        GroupCategoryRatingChange player = new GroupCategoryRatingChange(1, 1, 1500);
        GroupCategoryRatingChange opponent = new GroupCategoryRatingChange(1, 1, 1500);

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
        GroupCategoryRatingChange player = new GroupCategoryRatingChange(1, 2, 1500);
        GroupCategoryRatingChange opponent = new GroupCategoryRatingChange(1, 1, 1500);

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