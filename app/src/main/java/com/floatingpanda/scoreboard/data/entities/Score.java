package com.floatingpanda.scoreboard.data.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "scores",
        foreignKeys = {@ForeignKey(entity = GroupMonthlyScore.class,
        parentColumns = "group_monthly_score_id",
        childColumns = "group_monthly_score_id",
        onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Member.class,
        parentColumns = "member_id",
        childColumns = "member_id",
        onDelete = ForeignKey.CASCADE)})
public class Score {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "score_id")
    private int id;

    @ColumnInfo(name = "group_monthly_score_id")
    private int groupMonthlyScoreId;

    @ColumnInfo(name = "member_id")
    private int memberId;

    private int score;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getGroupMonthlyScoreId() { return groupMonthlyScoreId; }
    public void setGroupMonthlyScoreId(int groupMonthlyScoreId) { this.groupMonthlyScoreId = groupMonthlyScoreId; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public int getScore() { return score; }

    /**
     * Sets score to the score passed as a parameter. If the score parameter is below 0, sets score
     * to 0 instead.
     * @param score the value to set score to
     */
    public void setScore(int score) {
        if (score < 0) {
            this.score = 0;
        } else {
            this.score = score;
        }
    }

    public Score(int id, int groupMonthlyScoreId, int memberId, int score) {
        this.id = id;
        this.groupMonthlyScoreId = groupMonthlyScoreId;
        this.memberId = memberId;
        this.score = score;
    }

    @Ignore
    public Score(int groupMonthlyScoreId, int memberId, int score) {
        this(0, groupMonthlyScoreId, memberId, score);
    }

    @Ignore
    public Score(int groupMonthlyScoreId, int memberId) {
        this(groupMonthlyScoreId, memberId, 0);
    }

    /**
     * Updates the score by adding scoreChange to the current score. You can reduce a score by
     * providing a negative number for scoreChange.
     * @param scoreChange the amount to change the score by
     */
    public void updateScore(int scoreChange) {
        setScore(getScore() + scoreChange);
    }

    /**
     * Resets the score to 0.
     */
    public void resetScore() {
        setScore(0);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Score score = (Score) obj;

        return (score.getGroupMonthlyScoreId() == this.getGroupMonthlyScoreId()
                && score.getMemberId() == this.getMemberId());
    }
}
