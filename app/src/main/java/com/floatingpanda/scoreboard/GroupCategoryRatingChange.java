package com.floatingpanda.scoreboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.EloRateable;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GroupCategoryRatingChange implements Parcelable, EloRateable {

    private int teamNo;
    private int categoryId;
    private int finishingPosition;
    private double avgEloRating;
    private double eloRatingChange;

    public GroupCategoryRatingChange(int teamNo, int categoryId, int finishingPosition, double avgEloRating) {
        this.teamNo = teamNo;
        this.categoryId = categoryId;
        this.finishingPosition = finishingPosition;
        this.avgEloRating = avgEloRating;
        this.eloRatingChange = 0.0;
    }

    public GroupCategoryRatingChange(int teamNo, int categoryId, int finishingPosition) {
        this(teamNo, categoryId, finishingPosition, 0.0);
    }

    public GroupCategoryRatingChange(Parcel source) {
        teamNo = source.readInt();
        categoryId = source.readInt();
        finishingPosition = source.readInt();
        avgEloRating = source.readDouble();
        eloRatingChange = source.readDouble();
    }

    public int getTeamNo() { return teamNo; }
    public void setTeamNo(int teamNo) { this.teamNo = teamNo; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    @Override
    public int getFinishingPosition() { return finishingPosition; }
    public void setFinishingPosition(int finishingPosition) { this.finishingPosition = finishingPosition; }

    public double getAvgEloRating() { return avgEloRating; }
    public void setAvgEloRating(double avgEloRating) { this.avgEloRating = avgEloRating; }

    @Override
    public double getEloRating() { return avgEloRating; }

    public double getEloRatingChange() { return eloRatingChange; }
    public void setEloRatingChange(double eloRatingChange) { this.eloRatingChange = eloRatingChange; }

    public void increaseEloRatingChange(double additionalEloRatingChange) { this.eloRatingChange += additionalEloRatingChange; }

    public void roundEloRatingChange2Dp() {
        BigDecimal bigDecimal = new BigDecimal(eloRatingChange).setScale(2, RoundingMode.HALF_UP);
        setEloRatingChange(bigDecimal.doubleValue());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(teamNo);
        dest.writeInt(categoryId);
        dest.writeInt(finishingPosition);
        dest.writeDouble(avgEloRating);
        dest.writeDouble(eloRatingChange);
    }

    public static final Creator<GroupCategoryRatingChange> CREATOR = new Creator<GroupCategoryRatingChange>() {
        @Override
        public GroupCategoryRatingChange[] newArray(int size) {
            return new GroupCategoryRatingChange[size];
        }

        @Override
        public GroupCategoryRatingChange createFromParcel(Parcel source) {
            return new GroupCategoryRatingChange(source);
        }
    };
}
