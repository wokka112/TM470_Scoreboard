package com.floatingpanda.scoreboard.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.relations.GroupMonthlyScoreWithScoresAndMemberDetails;
import com.floatingpanda.scoreboard.data.daos.GroupMonthlyScoreDao;
import com.floatingpanda.scoreboard.data.daos.ScoreDao;

import java.util.List;

public class GroupMonthlyScoreRepository {

    private GroupMonthlyScoreDao groupMonthlyScoreDao;
    private ScoreDao scoreDao;

    public GroupMonthlyScoreRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);

        groupMonthlyScoreDao = db.groupMonthlyScoreDao();
        scoreDao = db.scoreDao();
    }

    //Used for testing
    public GroupMonthlyScoreRepository(AppDatabase db) {
        groupMonthlyScoreDao = db.groupMonthlyScoreDao();
        scoreDao = db.scoreDao();
    }

    public LiveData<List<GroupMonthlyScoreWithScoresAndMemberDetails>> getGroupsMonthlyScoresWithScoresAndMemberDetails(int groupId) {
        return groupMonthlyScoreDao.getGroupMonthlyScoresWithScoresAndMemberDetailsByGroupId(groupId);
    }
}
