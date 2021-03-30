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

package com.floatingpanda.scoreboard.model;

import android.app.Activity;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.repositories.GroupCategorySkillRatingRepository;
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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

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
public class GroupCategorySkillRatingRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private BgCategoryDao bgCategoryDao;
    private GroupDao groupDao;
    private MemberDao memberDao;
    private GroupMemberDao groupMemberDao;
    private GroupCategorySkillRatingDao groupCategorySkillRatingDao;
    private GroupCategorySkillRatingRepository groupCategorySkillRatingRepository;

    @Mock
    Activity activity;

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

        groupCategorySkillRatingRepository = new GroupCategorySkillRatingRepository(db);

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
    public void getGroupCategorySkillRatingsWithMemberDetailsByGroupIdAndCategoryId() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        List<GroupCategorySkillRatingWithMemberDetailsView> groupCategorySkillRatingsWithMemberDetailsCategory3 = LiveDataTestUtil.getValue(
                groupCategorySkillRatingRepository.getGroupCategorySkillRatingsWithMemberDetailsByGroupIdAndCategoryId(TestData.GROUP_1.getId(), TestData.BG_CATEGORY_3.getId()));

        assertThat(groupCategorySkillRatingsWithMemberDetailsCategory3.size(), is(6));
    }
}
