package com.floatingpanda.scoreboard.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.floatingpanda.scoreboard.data.GroupMonthlyScoreWithScoresAndMemberDetails;
import com.floatingpanda.scoreboard.data.ScoreWithMemberDetails;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.GroupMonthlyScore;
import com.floatingpanda.scoreboard.data.entities.Player;

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
