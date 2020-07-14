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
