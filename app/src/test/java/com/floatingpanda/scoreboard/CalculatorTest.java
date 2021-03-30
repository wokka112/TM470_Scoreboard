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
        groupCategoryRatingChanges.add(new GroupCategoryRatingChange(1, 1, "Strategy", 1, 1500));
        groupCategoryRatingChanges.add(new GroupCategoryRatingChange(1, 1, "Strategy", 1, 1400));
        groupCategoryRatingChanges.add(new GroupCategoryRatingChange(1, 1, "Strategy", 3, 1700));
        groupCategoryRatingChanges.add(new GroupCategoryRatingChange(1, 1, "Strategy", 4, 1200));

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
}
