package com.floatingpanda.scoreboard.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.database_views.GroupCategorySkillRatingWithMemberDetailsView;
import com.floatingpanda.scoreboard.data.daos.GroupCategorySkillRatingDao;

import java.util.List;

/**
 * A repository for accessing the group category skill rating table in the database.
 */
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

    /**
     * Returns a livedata list of database views which hold category skill ratings and associated
     * member details for a specific group, determined by groupId, and a specific board game
     * category, determined by categoryId.
     * @param groupId an int id identifying a group in the database
     * @param categoryId an int id identifying a bg category in the database
     * @return
     */
    public LiveData<List<GroupCategorySkillRatingWithMemberDetailsView>> getGroupCategorySkillRatingsWithMemberDetailsByGroupIdAndCategoryId(int groupId, int categoryId) {
        return groupCategorySkillRatingDao.getGroupSkillRatingsWithMemberDetailsByCategoryIdAndGroupId(categoryId, groupId);
    }
}
