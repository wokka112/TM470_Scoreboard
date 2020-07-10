package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.BgCategoryRepository;
import com.floatingpanda.scoreboard.data.GroupCategorySkillRatingRepository;
import com.floatingpanda.scoreboard.data.GroupCategorySkillRatingWithMemberDetailsView;
import com.floatingpanda.scoreboard.data.GroupMonthlyScoreRepository;
import com.floatingpanda.scoreboard.data.GroupMonthlyScoreWithScoresAndMemberDetails;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.GroupCategorySkillRating;

import java.util.List;

public class GroupCategorySkillRatingViewModel extends AndroidViewModel {

    private GroupCategorySkillRatingRepository groupCategorySkillRatingRepository;
    private BgCategoryRepository bgCategoryRepository;
    private LiveData<List<BgCategory>> allBgCategories;
    private MediatorLiveData<List<GroupCategorySkillRatingWithMemberDetailsView>> groupCategorySkillRatingsWithMemberDetails;
    private LiveData<List<GroupCategorySkillRatingWithMemberDetailsView>> currentSource;

    public GroupCategorySkillRatingViewModel(Application application) {
        super(application);
        groupCategorySkillRatingRepository = new GroupCategorySkillRatingRepository(application);
        bgCategoryRepository = new BgCategoryRepository(application);
        allBgCategories = bgCategoryRepository.getAll();
        groupCategorySkillRatingsWithMemberDetails = new MediatorLiveData<List<GroupCategorySkillRatingWithMemberDetailsView>>();
    }

    //Used for testing
    public GroupCategorySkillRatingViewModel(Application application, AppDatabase db) {
        super(application);
        groupCategorySkillRatingRepository = new GroupCategorySkillRatingRepository(db);
        bgCategoryRepository = new BgCategoryRepository(db);
        allBgCategories = bgCategoryRepository.getAll();
        groupCategorySkillRatingsWithMemberDetails = new MediatorLiveData<List<GroupCategorySkillRatingWithMemberDetailsView>>();
    }

    public void setGroupAndSkillRatingCategory(int groupId, int categoryId) {
        if (currentSource != null) {
            groupCategorySkillRatingsWithMemberDetails.removeSource(currentSource);
        }

        currentSource = groupCategorySkillRatingRepository.getGroupCategorySkillRatingsWithMemberDetailsByGroupIdAndCategoryId(groupId, categoryId);
        groupCategorySkillRatingsWithMemberDetails.addSource(currentSource, value -> groupCategorySkillRatingsWithMemberDetails.setValue(value));
    }

    public LiveData<List<GroupCategorySkillRatingWithMemberDetailsView>> getGroupCategorySkillRatingsWithMemberDetails() {
        return groupCategorySkillRatingsWithMemberDetails;
    }

    public LiveData<List<BgCategory>> getAllBgCategories() {
        return allBgCategories;
    }
}
