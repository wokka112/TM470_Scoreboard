package com.floatingpanda.scoreboard.data.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "group_monthly_scores", indices = {@Index(value = {"group_id", "year", "quarter", "month"},
        unique = true)},
        foreignKeys = {@ForeignKey(entity = Group.class,
                parentColumns = "group_id",
                childColumns = "group_id",
                onDelete = ForeignKey.CASCADE)})
public class GroupMonthlyScore {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "group_monthly_score_id")
    private int id;

    @ColumnInfo(name = "group_id")
    private int groupId;

    private int year;

    private int quarter;

    private int month;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getQuarter() { return quarter; }
    public void setQuarter(int quarter) { this.quarter = quarter; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public GroupMonthlyScore(int id,  int groupId, int year, int quarter, int month) {
        this.id = id;
        this.groupId = groupId;
        this.year = year;
        this.quarter = quarter;
        this.month = month;
    }

    @Ignore
    public GroupMonthlyScore(int groupId, int year, int quarter, int month) {
        this(0, groupId, year, quarter, month);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        GroupMonthlyScore groupMonthlyScore = (GroupMonthlyScore) obj;

        return (groupMonthlyScore.getGroupId() == this.getGroupId()
                && groupMonthlyScore.getYear() == this.getYear()
                && groupMonthlyScore.getQuarter() == this.getQuarter()
                && groupMonthlyScore.getMonth() == this.getMonth());
    }
}
