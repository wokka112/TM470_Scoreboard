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

import com.floatingpanda.scoreboard.data.relations.ScoreWithMemberDetails;
import com.floatingpanda.scoreboard.data.entities.Score;

import java.util.List;

@Dao
public interface ScoreDao {
    @Query("SELECT * FROM scores ORDER BY score DESC")
    LiveData<List<Score>> getAll();

    @Query("SELECT * FROM scores WHERE group_monthly_score_id LIKE :groupMonthlyScoreId ORDER BY score DESC")
    LiveData<List<Score>> getAllGroupsMonthlyScores(int groupMonthlyScoreId);

    @Query("SELECT * FROM scores WHERE group_monthly_score_id LIKE :groupMonthlyScoreId AND member_id LIKE :memberId")
    LiveData<Score> getGroupMembersMonthlyScore(int groupMonthlyScoreId, int memberId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Score... scores);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(Score score);

    @Query ("DELETE FROM scores")
    void deleteAll();

    @Delete
    void delete(Score score);

    @Query("UPDATE scores SET score = score + :addScore WHERE group_monthly_score_id LIKE :groupMonthlyScoreId AND member_id LIKE :memberId")
    void addScore(int groupMonthlyScoreId, int memberId, int addScore);

    @Query("UPDATE scores SET score = score - :removeScore WHERE group_monthly_score_id LIKE :groupMonthlyScoreId AND member_id LIKE :memberId")
    void removeScore(int groupMonthlyScoreId, int memberId, int removeScore);

    @Query("SELECT EXISTS(SELECT * FROM scores WHERE group_monthly_score_id LIKE :groupMonthlyScoreId AND member_id LIKE :memberId)")
    boolean containsScore(int groupMonthlyScoreId, int memberId);

    @Transaction
    @Query("SELECT * FROM scores ORDER BY score DESC")
    public LiveData<List<ScoreWithMemberDetails>> getAllScoresWithMemberDetails();

    @Transaction
    @Query("SELECT * FROM scores WHERE group_monthly_score_id LIKE :groupMonthlyScoreId ORDER BY score DESC")
    public LiveData<List<ScoreWithMemberDetails>> getAllGroupsMonthlyScoresWithMemberDetails(int groupMonthlyScoreId);

    @Transaction
    @Query("SELECT * FROM scores WHERE group_monthly_score_id LIKE :groupMonthlyScoreId AND member_id LIKE :memberId")
    public LiveData<ScoreWithMemberDetails> getGroupMembersMonthlyScoreWithMemberDetails(int groupMonthlyScoreId, int memberId);
}
