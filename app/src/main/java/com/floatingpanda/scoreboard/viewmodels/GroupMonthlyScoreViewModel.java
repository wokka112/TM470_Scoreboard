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

package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.repositories.GroupMonthlyScoreRepository;
import com.floatingpanda.scoreboard.data.relations.GroupMonthlyScoreWithScoresAndMemberDetails;

import java.util.List;

public class GroupMonthlyScoreViewModel extends AndroidViewModel {

    private GroupMonthlyScoreRepository groupMonthlyScoreRepository;
    private LiveData<List<GroupMonthlyScoreWithScoresAndMemberDetails>> groupMonthlyScoresWithScoresAndMemberDetails;

    public GroupMonthlyScoreViewModel(Application application) {
        super(application);
        groupMonthlyScoreRepository = new GroupMonthlyScoreRepository(application);
    }

    // Used for testing
    public GroupMonthlyScoreViewModel(Application application, AppDatabase db) {
        super(application);
        groupMonthlyScoreRepository = new GroupMonthlyScoreRepository(db);
    }

    public void initGroupMonthlyScoresWithScoresAndMemberDetails(int groupId) {
        groupMonthlyScoresWithScoresAndMemberDetails = groupMonthlyScoreRepository.getGroupsMonthlyScoresWithScoresAndMemberDetails(groupId);
    }

    public LiveData<List<GroupMonthlyScoreWithScoresAndMemberDetails>> getGroupMonthlyScoresWithScoresAndMemberDetails() {
        return groupMonthlyScoresWithScoresAndMemberDetails;
    }
}
