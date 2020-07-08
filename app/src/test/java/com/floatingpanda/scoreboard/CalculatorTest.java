package com.floatingpanda.scoreboard;

import com.floatingpanda.scoreboard.calculators.Calculator;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.Scoreable;

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
}
