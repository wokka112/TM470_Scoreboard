package com.floatingpanda.scoreboard.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.relations.GroupMonthlyScoreWithScoresAndMemberDetails;
import com.floatingpanda.scoreboard.data.daos.GroupMonthlyScoreDao;
import com.floatingpanda.scoreboard.data.daos.ScoreDao;

import java.util.List;

/**
 * A repository for accessing the group monthly scores table in the database.
 */
public class GroupMonthlyScoreRepository {

    private GroupMonthlyScoreDao groupMonthlyScoreDao;

    public GroupMonthlyScoreRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);

        groupMonthlyScoreDao = db.groupMonthlyScoreDao();
    }

    //Used for testing
    public GroupMonthlyScoreRepository(AppDatabase db) {
        groupMonthlyScoreDao = db.groupMonthlyScoreDao();
    }

    /**
     * Returns a livedata list of all the monthly scores for a group along with the scores attained
     * in each month, and the details of the members who attained each score. The group to which the
     * scores belong is determined by groupId.
     * @param groupId an int id identifying a group in the database
     * @return
     */
    public LiveData<List<GroupMonthlyScoreWithScoresAndMemberDetails>> getGroupsMonthlyScoresWithScoresAndMemberDetails(int groupId) {
        return groupMonthlyScoreDao.getGroupMonthlyScoresWithScoresAndMemberDetailsByGroupId(groupId);
    }
}
