package com.floatingpanda.scoreboard.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Represents the scores of a member in a group for a specific month. The score is stored as score.
 * The member is linked to from the members table (Member class) via the memberId attribute, which
 * is a foreign key. The group monthly score, which details the group the score is from and the
 * month the score is for, is linked to from the group_monthly_scores table (GroupMonthlyScore
 * class) via the groupMonthlyScoreId attribute, which is a foreign key.
 */
@Entity(tableName = "scores", indices = {@Index(value = {"group_monthly_score_id", "member_id"},
        unique = true)},
        foreignKeys = {@ForeignKey(entity = GroupMonthlyScore.class,
        parentColumns = "group_monthly_score_id",
        childColumns = "group_monthly_score_id",
        onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Member.class,
        parentColumns = "member_id",
        childColumns = "member_id",
        onDelete = ForeignKey.CASCADE)})
public class Score implements Parcelable {

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

    @Ignore
    public Score(Parcel source) {
        this.id = source.readInt();
        this.groupMonthlyScoreId = source.readInt();
        this.memberId = source.readInt();
        this.score = source.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(groupMonthlyScoreId);
        dest.writeInt(memberId);
        dest.writeInt(score);
    }

    public static final Creator<Score> CREATOR = new Creator<Score>() {
        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }

        @Override
        public Score createFromParcel(Parcel source) {
            return new Score(source);
        }
    };
}
