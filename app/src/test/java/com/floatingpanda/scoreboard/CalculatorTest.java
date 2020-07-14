package com.floatingpanda.scoreboard;

import com.floatingpanda.scoreboard.calculators.Calculator;
import com.floatingpanda.scoreboard.data.entities.Member;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CalculatorTest {
    @Test
    public void testCalculateCooperativeSolitaireScores() {
        Calculator calculator = new Calculator();

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
        teamsOfPlayers.add(new TeamOfPlayers(1, 1, -1, new ArrayList<Member>()));

        for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
            assertEquals(-1, teamOfPlayers.getScore());
        }

        int difficulty = 5;
        boolean won = true;

        calculator.calculateCooperativeSolitaireScores(difficulty, teamsOfPlayers, won);

        int correctScore = difficulty;
        for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
            assertEquals(correctScore, teamOfPlayers.getScore());
            assertNotEquals(-1, teamOfPlayers.getScore());
        }

        won = false;

        int oldScore = teamsOfPlayers.get(0).getScore();
        calculator.calculateCooperativeSolitaireScores(difficulty, teamsOfPlayers, won);

        correctScore = 0;
        for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
            assertEquals(correctScore, teamOfPlayers.getScore());
            assertNotEquals(oldScore, teamOfPlayers.getScore());
        }
    }

    @Test
    public void testCalculateCompetitiveScores() {
        Calculator calculator = new Calculator();

        List<TeamOfPlayers> teamsOfPlayers = new ArrayList<>();
        teamsOfPlayers.add(new TeamOfPlayers(1, 1, -1, new ArrayList<Member>()));
        teamsOfPlayers.add(new TeamOfPlayers(2, 2, -1, new ArrayList<Member>()));
        teamsOfPlayers.add(new TeamOfPlayers(3, 2, -1, new ArrayList<Member>()));
        teamsOfPlayers.add(new TeamOfPlayers(4, 4, -1, new ArrayList<Member>()));

        for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
            assertEquals(-1, teamOfPlayers.getScore());
        }

        int difficulty = 5;

        calculator.calculateCompetitiveScores(difficulty, teamsOfPlayers);

        for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
            int correctScore = difficulty * (teamsOfPlayers.size() + 1 - teamOfPlayers.getPosition());

            assertEquals(correctScore, teamOfPlayers.getScore());
            assertNotEquals(0, teamOfPlayers.getScore());
        }
    }

    @Test
    public void testCalculateCategoryPairwiseEloRatings() {
        List<GroupCategoryRatingChange> groupCategoryRatingChanges = new ArrayList<>();
        groupCategoryRatingChanges.add(new GroupCategoryRatingChange(1, 1, 1500));
        groupCategoryRatingChanges.add(new GroupCategoryRatingChange(1, 1, 1400));
        groupCategoryRatingChanges.add(new GroupCategoryRatingChange(1, 3, 1700));
        groupCategoryRatingChanges.add(new GroupCategoryRatingChange(1, 4, 1200));

        Calculator calculator = new Calculator();
        calculator.calculateCategoryPairwiseEloRatings(groupCategoryRatingChanges, 30);

        //Testing player 1 - Rating change should be roughly +7.7
        assertEquals(7.7, groupCategoryRatingChanges.get(0).getEloRatingChange(), 0.5);

        //Testing player 2 - Rating chage should be roughly +12.3
        assertEquals(12.3, groupCategoryRatingChanges.get(1).getEloRatingChange(), 0.5);

        //Testing player 3 - Rating change should be roughly -15.3
        assertEquals(-15.3, groupCategoryRatingChanges.get(2).getEloRatingChange(), 0.5);

        //Testing player 4 - Rating change should be roughly -4.7
        assertEquals(-4.7, groupCategoryRatingChanges.get(3).getEloRatingChange(), 0.5);
    }

    /*
    public void calculateCategoryPairwiseEloRatings(List<CategoryPairwiseEloRatingChange> categoryPairwiseEloRatingChanges, int kValue) {
        for (int i = 0; i < categoryPairwiseEloRatingChanges.size(); i++) {
            CategoryPairwiseEloRatingChange playerIRating = categoryPairwiseEloRatingChanges.get(i);

            for (int j = i +1; j < categoryPairwiseEloRatingChanges.size(); j++) {
                CategoryPairwiseEloRatingChange playerJRating = categoryPairwiseEloRatingChanges.get(j);

                //Sort out rating change for i and store it.
                double iRatingChange = eloSkillRatingCalculator.calculateSkillRatingChange(playerIRating, playerJRating, kValue);
                playerIRating.increaseEloRatingChange(iRatingChange);
                //Sort out rating change for j and store it.
                double jRatingChange = eloSkillRatingCalculator.calculateSkillRatingChange(playerJRating, playerIRating, kValue);
                playerJRating.increaseEloRatingChange(jRatingChange);
            }
        }
    }
     */
}
