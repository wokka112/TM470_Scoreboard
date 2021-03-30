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
import androidx.lifecycle.MediatorLiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.repositories.BgCategoryRepository;
import com.floatingpanda.scoreboard.repositories.GroupCategorySkillRatingRepository;
import com.floatingpanda.scoreboard.data.database_views.GroupCategorySkillRatingWithMemberDetailsView;
import com.floatingpanda.scoreboard.data.entities.BgCategory;

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
