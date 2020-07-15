package com.floatingpanda.scoreboard.data.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity(tableName = "player_skill_rating_changes",
        foreignKeys = {@ForeignKey(entity = Player.class,
                parentColumns = "player_id",
                childColumns = "player_id",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = BgCategory.class,
                        parentColumns = "category_name",
                        childColumns = "category_name",
                        onDelete = ForeignKey.SET_NULL,
                        onUpdate = ForeignKey.CASCADE)})
public class PlayerSkillRatingChange implements Comparable<PlayerSkillRatingChange>{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "player_skill_rating_change_id")
    private int id;

    @ColumnInfo(name = "player_id")
    private int playerId;

    @ColumnInfo(name = "category_name")
    private String categoryName;

    @ColumnInfo(name = "old_rating")
    private double oldRating;

    @ColumnInfo(name = "rating_change")
    private double ratingChange;

    public PlayerSkillRatingChange(int id, int playerId, String categoryName, double oldRating, double ratingChange) {
        this.id = id;
        this.playerId = playerId;
        this.categoryName = categoryName;
        this.oldRating = oldRating;
        this.ratingChange = ratingChange;
    }

    @Ignore
    public PlayerSkillRatingChange(int playerId, String categoryName, double oldRating, double ratingChange) {
        this(0, playerId, categoryName, oldRating, ratingChange);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public double getOldRating() { return oldRating; }
    public void setOldRating(double oldRating) { this.oldRating = oldRating; }
    public double getRatingChange() { return ratingChange; }
    public void setRatingChange(double ratingChange) { this.ratingChange = ratingChange; }

    public double getNewRating() { return oldRating + ratingChange; }

    public double get2DpRoundedNewRating() {
        double newRating = getNewRating();
        BigDecimal bigDecimal = new BigDecimal(newRating).setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        PlayerSkillRatingChange playerSkillRatingChange = (PlayerSkillRatingChange) obj;

        return playerSkillRatingChange.getPlayerId() == this.getPlayerId()
                && playerSkillRatingChange.getCategoryName().equals(this.getCategoryName())
                && playerSkillRatingChange.getOldRating() == this.getOldRating()
                && playerSkillRatingChange.getRatingChange() == this.getRatingChange();
    }

    @Override
    public int compareTo(PlayerSkillRatingChange o) {
        return this.getCategoryName().compareTo(o.getCategoryName());
    }
}
