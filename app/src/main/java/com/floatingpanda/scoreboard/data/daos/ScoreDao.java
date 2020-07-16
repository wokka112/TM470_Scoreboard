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
