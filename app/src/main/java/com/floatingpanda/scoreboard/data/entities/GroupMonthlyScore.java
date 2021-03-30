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

package com.floatingpanda.scoreboard.data.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Represents a set of scores for a month for a group. The group the monthly scores are for is stored
 * in the groups table (Group class) and linked to via the groupId attribute, which is a foreign key
 * to the groups table. The scores scored by the group members for the month, including the members
 * who have scored them, are stored in the scores table (Score class) and linked to via a foreign
 * key in the scores table pointing to this table.
 */
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
