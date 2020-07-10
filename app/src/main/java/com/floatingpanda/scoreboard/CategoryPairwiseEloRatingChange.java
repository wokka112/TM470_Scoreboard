package com.floatingpanda.scoreboard;

import com.floatingpanda.scoreboard.interfaces.EloRateable;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CategoryPairwiseEloRatingChange implements EloRateable {

    private int position;
    private int categoryId;
    private double oldEloRating;
    private double eloRatingChange;

    public CategoryPairwiseEloRatingChange(int categoryId, int position, double oldEloRating) {
        this.categoryId = categoryId;
        this.position = position;
        this.oldEloRating = oldEloRating;
        this.eloRatingChange = 0;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    @Override
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public double getOldEloRating() { return oldEloRating; }
    public void setOldEloRating(double oldEloRating) { this.oldEloRating = oldEloRating; }

    @Override
    public double getEloRating() { return oldEloRating; }

    public double getEloRatingChange() { return eloRatingChange; }
    public void setEloRatingChange(double eloRatingChange) { this.eloRatingChange = eloRatingChange; }

    public void increaseEloRatingChange(double additionalEloRatingChange) { this.eloRatingChange += additionalEloRatingChange; }

    public void roundEloRatingChange2Dp() {
        BigDecimal bigDecimal = new BigDecimal(eloRatingChange).setScale(2, RoundingMode.HALF_UP);
        setEloRatingChange(bigDecimal.doubleValue());
    }
}
