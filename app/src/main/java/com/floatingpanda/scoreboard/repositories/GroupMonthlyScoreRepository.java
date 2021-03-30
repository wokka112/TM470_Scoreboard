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
