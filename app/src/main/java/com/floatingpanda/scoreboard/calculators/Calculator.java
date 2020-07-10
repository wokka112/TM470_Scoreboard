package com.floatingpanda.scoreboard.calculators;

import com.floatingpanda.scoreboard.CategoryPairwiseEloRatingChange;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.interfaces.EloRateable;
import com.floatingpanda.scoreboard.interfaces.Scoreable;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    private CompetitiveScoreCalculator competitiveScoreCalculator;
    private CooperativeSolitaireScoreCalculator cooperativeSolitaireScoreCalculator;
    private EloSkillRatingCalculator eloSkillRatingCalculator;

    public Calculator() {
        competitiveScoreCalculator = new CompetitiveScoreCalculator();
        cooperativeSolitaireScoreCalculator = new CooperativeSolitaireScoreCalculator();
        eloSkillRatingCalculator = new EloSkillRatingCalculator();
    }

    /**
     * Takes a list of scoreables for a competitive game, calculates their scores and assigns the scoreables the new scores.
     * @param difficulty
     * @param scoreables
     */
    public void calculateCompetitiveScores(int difficulty, List<? extends Scoreable> scoreables) {
        for (Scoreable scoreable : scoreables) {
            int score = competitiveScoreCalculator.calculateCompetitiveScore(scoreable.getPosition(), scoreables.size(), difficulty);
            scoreable.setScore(score);
        }
    }

    /**
     * Takes a list of scoreables for a cooperative game, calculates their scores and assigns the scoreables the new scores.
     * @param difficulty
     * @param scoreables
     */
    public void calculateCooperativeSolitaireScores(int difficulty, List<? extends Scoreable> scoreables, boolean won) {
        for (Scoreable scoreable : scoreables) {
            int score = cooperativeSolitaireScoreCalculator.calculateCooperativeSolitaireScore(won, difficulty);
            scoreable.setScore(score);
        }
    }

    public void calculateCategoryPairwiseEloRatings(List<CategoryPairwiseEloRatingChange> categoryPairwiseEloRatingChanges, int twoPlayerkValue) {
        int kValue = (int) Math.ceil((double) twoPlayerkValue / (categoryPairwiseEloRatingChanges.size() - 1));

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

            playerIRating.roundEloRatingChange2Dp();
        }
    }
}
