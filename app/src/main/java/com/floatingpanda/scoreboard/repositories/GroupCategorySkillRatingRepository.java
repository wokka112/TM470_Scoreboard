package com.floatingpanda.scoreboard.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.database_views.GroupCategorySkillRatingWithMemberDetailsView;
import com.floatingpanda.scoreboard.data.daos.GroupCategorySkillRatingDao;

import java.util.List;

public class GroupCategorySkillRatingRepository {
    private GroupCategorySkillRatingDao groupCategorySkillRatingDao;
    private LiveData<List<GroupCategorySkillRatingWithMemberDetailsView>> groupCategorySkillRatingsWithMemberDetails;

    public GroupCategorySkillRatingRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);

        groupCategorySkillRatingDao = db.groupCategorySkillRatingDao();
    }

    public GroupCategorySkillRatingRepository(AppDatabase db) {
        groupCategorySkillRatingDao = db.groupCategorySkillRatingDao();
    }

    public LiveData<List<GroupCategorySkillRatingWithMemberDetailsView>> getGroupCategorySkillRatingsWithMemberDetailsByGroupIdAndCategoryId(int groupId, int categoryId) {
        return groupCategorySkillRatingDao.getGroupSkillRatingsWithMemberDetailsByCategoryIdAndGroupId(categoryId, groupId);
    }
}
