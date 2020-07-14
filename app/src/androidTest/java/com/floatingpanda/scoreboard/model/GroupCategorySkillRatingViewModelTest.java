package com.floatingpanda.scoreboard.model;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.database_views.GroupCategorySkillRatingWithMemberDetailsView;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.daos.GroupCategorySkillRatingDao;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.daos.GroupMemberDao;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.GroupCategorySkillRating;
import com.floatingpanda.scoreboard.data.entities.GroupMember;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.viewmodels.GroupCategorySkillRatingViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class GroupCategorySkillRatingViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private BgCategoryDao bgCategoryDao;
    private GroupDao groupDao;
    private MemberDao memberDao;
    private GroupMemberDao groupMemberDao;
    private GroupCategorySkillRatingDao groupCategorySkillRatingDao;
    private GroupCategorySkillRatingViewModel groupCategorySkillRatingViewModel;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        bgCategoryDao = db.bgCategoryDao();
        groupDao = db.groupDao();
        memberDao = db.memberDao();
        groupMemberDao = db.groupMemberDao();
        groupCategorySkillRatingDao = db.groupCategorySkillRatingDao();

        groupCategorySkillRatingViewModel = new GroupCategorySkillRatingViewModel(ApplicationProvider.getApplicationContext(), db);

        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllBgCategories() throws InterruptedException {
        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(groupCategorySkillRatingViewModel.getAllBgCategories());

        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size()));
    }

    @Test
    public void setAndGetGroupCategorySkillRatingsWithMemberDetails() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        groupCategorySkillRatingViewModel.setGroupAndSkillRatingCategory(TestData.GROUP_1.getId(), TestData.BG_CATEGORY_3.getId());
        List<GroupCategorySkillRatingWithMemberDetailsView> groupCategorySkillRatingWithMemberDetailsViews = LiveDataTestUtil.getValue(
                groupCategorySkillRatingViewModel.getGroupCategorySkillRatingsWithMemberDetails());

        assertThat(groupCategorySkillRatingWithMemberDetailsViews.size(), is(6));

        groupCategorySkillRatingViewModel.setGroupAndSkillRatingCategory(TestData.GROUP_2.getId(), TestData.BG_CATEGORY_1.getId());
        groupCategorySkillRatingWithMemberDetailsViews = LiveDataTestUtil.getValue(groupCategorySkillRatingViewModel.getGroupCategorySkillRatingsWithMemberDetails());

        assertThat(groupCategorySkillRatingWithMemberDetailsViews.size(), is(1));
    }
}
