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

package com.floatingpanda.scoreboard.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.floatingpanda.scoreboard.data.relations.GroupMonthlyScoreWithScoresAndMemberDetails;
import com.floatingpanda.scoreboard.data.entities.GroupMonthlyScore;

import java.util.List;

@Dao
public interface GroupMonthlyScoreDao {
    @Query("SELECT * FROM group_monthly_scores ORDER BY year DESC, month DESC")
    LiveData<List<GroupMonthlyScore>> getAll();

    @Query("SELECT * FROM group_monthly_scores WHERE group_id LIKE :groupId ORDER BY year DESC, month DESC")
    LiveData<List<GroupMonthlyScore>> getGroupMonthlyScoresByGroupId(int groupId);

    @Query("SELECT * FROM group_monthly_scores WHERE group_id LIKE :groupId AND year Like :year ORDER BY month DESC")
    LiveData<List<GroupMonthlyScore>> getGroupMonthlyScoresByGroupIdAndYear(int groupId, int year);

    @Query("SELECT * FROM group_monthly_scores WHERE group_id LIKE :groupId AND year LIKE :year AND quarter LIKE :quarter ORDER BY month DESC")
    LiveData<List<GroupMonthlyScore>> getGroupMonthlyScoresByGroupIdAndYearAndQuarter(int groupId, int year, int quarter);

    @Query("SELECT * FROM group_monthly_scores WHERE group_id LIKE :groupId AND year LIKE :year AND month LIKE :month")
    LiveData<GroupMonthlyScore> getGroupMonthlyScoreByGroupIdAndYearAndMonth(int groupId, int year, int month);

    @Query("SELECT * FROM group_monthly_scores WHERE group_monthly_score_id LIKE :groupMonthlyScoreId")
    LiveData<GroupMonthlyScore> getGroupMonthlyScoreByGroupMonthlyScoreId(int groupMonthlyScoreId);

    @Query("SELECT group_monthly_score_id FROM group_monthly_scores WHERE group_id LIKE :groupId AND year LIKE :year AND month LIKE :month")
    int getGroupMonthlyScoreIdByGroupIdAndYearAndMonth(int groupId, int year, int month);

    @Query("SELECT EXISTS(SELECT * FROM group_monthly_scores WHERE group_id LIKE :groupId AND year LIKE :year AND month LIKE :month)")
    boolean containsGroupMonthlyScore(int groupId, int year, int month);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(GroupMonthlyScore... groupMonthlyScores);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    long insert(GroupMonthlyScore groupMonthlyScore);

    @Query ("DELETE FROM group_monthly_scores")
    void deleteAll();

    @Delete
    void delete(GroupMonthlyScore groupMonthlyScore);

    @Transaction
    @Query("SELECT * FROM group_monthly_scores ORDER BY year DESC, month DESC")
    public LiveData<List<GroupMonthlyScoreWithScoresAndMemberDetails>> getAllGroupMonthlyScoresWithScoresAndMemberDetails();

    @Transaction
    @Query("SELECT * FROM group_monthly_scores WHERE group_id LIKE :groupId ORDER BY year DESC, month DESC")
    public LiveData<List<GroupMonthlyScoreWithScoresAndMemberDetails>> getGroupMonthlyScoresWithScoresAndMemberDetailsByGroupId(int groupId);

    @Transaction
    @Query("SELECT * FROM group_monthly_scores WHERE group_id LIKE :groupId AND year LIKE :year ORDER BY month DESC")
    public LiveData<List<GroupMonthlyScoreWithScoresAndMemberDetails>> getGroupMonthlyScoresWithScoresAndMemberDetailsByGroupIdAndYear(int groupId, int year);

    @Transaction
    @Query("SELECT * FROM group_monthly_scores WHERE group_id LIKE :groupId AND year LIKE :year AND quarter LIKE :quarter ORDER BY month DESC")
    public LiveData<List<GroupMonthlyScoreWithScoresAndMemberDetails>> getGroupMonthlyScoresWithScoresAndMemberDetailsByGroupIdAndYearAndQuarter(int groupId, int year, int quarter);

    @Transaction
    @Query("SELECT * FROM group_monthly_scores WHERE group_id LIKE :groupId AND year LIKE :year AND month LIKE :month")
    public LiveData<GroupMonthlyScoreWithScoresAndMemberDetails> getGroupMonthlyScoreWithScoresAndMemberDetailsByGroupIdAndYearAndMonth(int groupId, int year, int month);
}
