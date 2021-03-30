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

package com.floatingpanda.scoreboard.db;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.database_views.GroupCategorySkillRatingWithMemberDetailsView;
import com.floatingpanda.scoreboard.data.daos.GroupCategorySkillRatingDao;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.daos.GroupMemberDao;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.GroupCategorySkillRating;
import com.floatingpanda.scoreboard.data.entities.GroupMember;
import com.floatingpanda.scoreboard.data.entities.Member;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class GroupCategorySkillRatingDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private BgCategoryDao bgCategoryDao;
    private GroupDao groupDao;
    private MemberDao memberDao;
    private GroupMemberDao groupMemberDao;
    private GroupCategorySkillRatingDao groupCategorySkillRatingDao;

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
    public void getAllWhenNoneInserted() throws InterruptedException {
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getAll());

        assertTrue(groupCategorySkillRatings.isEmpty());
    }

    @Test
    public void getAllWhenAllInserted() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getAll());

        assertThat(groupCategorySkillRatings.size(), is(TestData.GROUP_CATEGORY_SKILL_RATINGS.size()));
    }

    @Test
    public void getGroupsSkillRatingsByGroupIdWhenNoneInserted() throws InterruptedException {
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getGroupsSkillRatingsByGroupId(TestData.GROUP_1.getId()));

        assertTrue(groupCategorySkillRatings.isEmpty());
    }

    @Test
    public void getGroupsSkillRatingsByGroupIdWhenAllInserted() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getGroupsSkillRatingsByGroupId(TestData.GROUP_1.getId()));

        //For group 1, category 1 has 6 ratings, category 2 has 6 ratings, and category 3 has 8 ratings.
        assertThat(groupCategorySkillRatings.size(), is(6 + 6 + 8));
    }

    @Test
    public void getGroupMembersSkillRatingsWhenNoneInserted() throws InterruptedException {
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(
                groupCategorySkillRatingDao.getGroupMembersSkillRatingsByGroupIdAndMemberId(TestData.GROUP_1.getId(), TestData.GROUP_MEMBER_1.getMemberId()));

        assertTrue(groupCategorySkillRatings.isEmpty());
    }

    @Test
    public void getGroupMembersSkillRatingsWhenAllInserted() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(
                groupCategorySkillRatingDao.getGroupMembersSkillRatingsByGroupIdAndMemberId(TestData.GROUP_1.getId(), TestData.GROUP_MEMBER_1.getMemberId()));

        //Group member 1 in group 1 has 3 ratings
        assertThat(groupCategorySkillRatings.size(), is(3));
    }

    @Test
    public void getGroupsCategorySkillRatingsByGroupIdAndCategoryIdWhenNoneInserted() throws InterruptedException {
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(
                groupCategorySkillRatingDao.getGroupsCategorysSkillRatingsByGroupIdAndCategoryId(TestData.GROUP_1.getId(), TestData.BG_CATEGORY_1.getId()));

        assertTrue(groupCategorySkillRatings.isEmpty());
    }

    @Test
    public void getGroupsCategorySkillRatingsByGroupIdAndCategoryIdWhenAllInserted() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(
                groupCategorySkillRatingDao.getGroupsCategorysSkillRatingsByGroupIdAndCategoryId(TestData.GROUP_1.getId(), TestData.BG_CATEGORY_1.getId()));

        //Group 1 has 6 ratings for category 1.
        assertThat(groupCategorySkillRatings.size(), is(6));
    }

    @Test
    public void getSpecificGroupCategorySkillRatingByGroupCategorySkillRatingIdWhenNoneInserted() throws InterruptedException {
        GroupCategorySkillRating groupCategorySkillRating = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getGroupCategorySkillRatingById(TestData.GROUP_CATEGORY_SKILL_RATING_1.getId()));

        assertNull(groupCategorySkillRating);
    }

    @Test
    public void getSpecificGroupCategorySkillRatingByGroupCategorySkillRatingIdWhenAllInserted() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        GroupCategorySkillRating groupCategorySkillRating = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getGroupCategorySkillRatingById(TestData.GROUP_CATEGORY_SKILL_RATING_1.getId()));

        assertThat(groupCategorySkillRating, is(TestData.GROUP_CATEGORY_SKILL_RATING_1));
    }

    @Test
    public void getSpecificGroupCategorySkillRatingByGroupIdAndCategoryIdAndMemberIdWhenNoneInserted() throws InterruptedException {
        GroupCategorySkillRating groupCategorySkillRating = LiveDataTestUtil.getValue(
                groupCategorySkillRatingDao.getGroupCategorySkillRatingByGroupIdAndCategoryIdAndMemberId(TestData.GROUP_CATEGORY_SKILL_RATING_1.getGroupId(),
                        TestData.GROUP_CATEGORY_SKILL_RATING_1.getCategoryId(), TestData.GROUP_CATEGORY_SKILL_RATING_1.getMemberId()));

        assertNull(groupCategorySkillRating);
    }

    @Test
    public void getSpecificGroupCategorySkillRatingByGroupIdAndCategoryIdAndMemberIdWhenAllInserted() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        GroupCategorySkillRating groupCategorySkillRating = LiveDataTestUtil.getValue(
                groupCategorySkillRatingDao.getGroupCategorySkillRatingByGroupIdAndCategoryIdAndMemberId(TestData.GROUP_CATEGORY_SKILL_RATING_1.getGroupId(),
                        TestData.GROUP_CATEGORY_SKILL_RATING_1.getCategoryId(), TestData.GROUP_CATEGORY_SKILL_RATING_1.getMemberId()));

        assertThat(groupCategorySkillRating, is(TestData.GROUP_CATEGORY_SKILL_RATING_1));
    }

    @Test
    public void getAllAfterInsertingOneGroupCategorySkillRating() throws InterruptedException {
        groupCategorySkillRatingDao.insert(TestData.GROUP_CATEGORY_SKILL_RATING_1);
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getAll());

        assertThat(groupCategorySkillRatings.size(), is(1));
        assertThat(groupCategorySkillRatings.get(0), is (TestData.GROUP_CATEGORY_SKILL_RATING_1));
    }

    @Test
    public void getAllAfterInsertingAllAndDeletingAll() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getAll());

        assertThat(groupCategorySkillRatings.size(), is(TestData.GROUP_CATEGORY_SKILL_RATINGS.size()));

        groupCategorySkillRatingDao.deleteAll();
        TimeUnit.MILLISECONDS.sleep(100);

        groupCategorySkillRatings = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getAll());

        assertTrue(groupCategorySkillRatings.isEmpty());
    }

    @Test
    public void getAllAfterInsertingAllAndDeletingOne() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getAll());

        assertThat(groupCategorySkillRatings.size(), is(TestData.GROUP_CATEGORY_SKILL_RATINGS.size()));

        groupCategorySkillRatingDao.delete(TestData.GROUP_CATEGORY_SKILL_RATING_1);
        TimeUnit.MILLISECONDS.sleep(100);

        groupCategorySkillRatings = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getAll());

        assertThat(groupCategorySkillRatings.size(), is(TestData.GROUP_CATEGORY_SKILL_RATINGS.size() - 1));
        assertFalse(groupCategorySkillRatings.contains(TestData.GROUP_CATEGORY_SKILL_RATING_1));
    }

    @Test
    public void insertAllAndUpdateOneSkillRatingByGroupCategorySkillRatingId() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        GroupCategorySkillRating groupCategorySkillRating = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getGroupCategorySkillRatingById(TestData.GROUP_CATEGORY_SKILL_RATING_1.getId()));

        assertThat(groupCategorySkillRating, is(TestData.GROUP_CATEGORY_SKILL_RATING_1));

        double addSkillRating = 15.00;
        groupCategorySkillRatingDao.addSkillRatingFromSingleGame(TestData.GROUP_CATEGORY_SKILL_RATING_1.getId(), addSkillRating);
        TimeUnit.MILLISECONDS.sleep(100);

        GroupCategorySkillRating updatedGroupCategorySkillRating = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getGroupCategorySkillRatingById(TestData.GROUP_CATEGORY_SKILL_RATING_1.getId()));

        assertThat(updatedGroupCategorySkillRating.getId(), is(groupCategorySkillRating.getId()));
        assertThat(updatedGroupCategorySkillRating.getSkillRating(), is(groupCategorySkillRating.getSkillRating() + addSkillRating));
        assertThat(updatedGroupCategorySkillRating.getGamesRated(), is(groupCategorySkillRating.getGamesRated() + 1));
    }

    @Test
    public void getGroupSkillRatingsWithMemberDetailsByCategoryIdAndGroupId() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        List<GroupCategorySkillRatingWithMemberDetailsView> groupCategorySkillRatingsWithMemberDetailsCategory3 = LiveDataTestUtil.getValue(
                groupCategorySkillRatingDao.getGroupSkillRatingsWithMemberDetailsByCategoryIdAndGroupId(TestData.BG_CATEGORY_3.getId(), TestData.GROUP_1.getId()));

        assertThat(groupCategorySkillRatingsWithMemberDetailsCategory3.size(), is(6));

        groupMemberDao.insert(new GroupMember(TestData.GROUP_1.getId(), TestData.MEMBER_2.getId()));
        TimeUnit.MILLISECONDS.sleep(100);

        groupCategorySkillRatingsWithMemberDetailsCategory3 = LiveDataTestUtil.getValue(
                groupCategorySkillRatingDao.getGroupSkillRatingsWithMemberDetailsByCategoryIdAndGroupId(TestData.BG_CATEGORY_3.getId(), TestData.GROUP_1.getId()));

        assertThat(groupCategorySkillRatingsWithMemberDetailsCategory3.size(), is(7));

        String correctNickname = TestData.MEMBER_2.getNickname();
        double correctSkillRating = TestData.GROUP_CATEGORY_SKILL_RATING_14.getSkillRating();
        int correctGamesRated = TestData.GROUP_CATEGORY_SKILL_RATING_14.getGamesRated();

        boolean containsCorrectNickname = false;
        boolean containsCorrectSkillRating = false;
        boolean containsCorrectGamesRated = false;
        for(GroupCategorySkillRatingWithMemberDetailsView groupCategorySkillRatingWithMemberDetailsView : groupCategorySkillRatingsWithMemberDetailsCategory3) {
            String nickname = groupCategorySkillRatingWithMemberDetailsView.getNickname();
            double skillRating = groupCategorySkillRatingWithMemberDetailsView.getSkillRating();
            int gamesRated = groupCategorySkillRatingWithMemberDetailsView.getGamesRated();

            if (nickname.equals(correctNickname)) {
                containsCorrectNickname = true;
            }

            if (skillRating == correctSkillRating) {
                containsCorrectSkillRating = true;
            }

            if (gamesRated == correctGamesRated) {
                containsCorrectGamesRated = true;
            }
        }

        assertTrue(containsCorrectNickname);
        assertTrue(containsCorrectSkillRating);
        assertTrue(containsCorrectGamesRated);
    }

    @Test
    public void testDatabaseContainsGroupCategorySkillRatingByGroupIdAndCategoryIdAndMemberId() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getAll());

        assertThat(groupCategorySkillRatings.size(), is(TestData.GROUP_CATEGORY_SKILL_RATINGS.size()));

        assertTrue(groupCategorySkillRatingDao.containsGroupCategorySkillRating(TestData.GROUP_1.getId(), TestData.BG_CATEGORY_1.getId(), TestData.MEMBER_1.getId()));
        assertFalse(groupCategorySkillRatingDao.containsGroupCategorySkillRating(TestData.GROUP_2.getId(), TestData.BG_CATEGORY_1.getId(), TestData.MEMBER_1.getId()));
    }

    @Test
    public void getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        List<GroupCategorySkillRating> groupCategorySkillRatings = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getAll());

        assertThat(groupCategorySkillRatings.size(), is(TestData.GROUP_CATEGORY_SKILL_RATINGS.size()));

        double rating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(TestData.GROUP_1.getId(), TestData.BG_CATEGORY_1.getId(), TestData.MEMBER_1.getId());
        assertThat(rating, is(TestData.GROUP_CATEGORY_SKILL_RATING_1.getSkillRating()));
    }

    @Test
    public void insertAllAndUpdateOneSkillRatingByGroupIdAndCategoryIdAndMemberId() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        GroupCategorySkillRating groupCategorySkillRating = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getGroupCategorySkillRatingById(TestData.GROUP_CATEGORY_SKILL_RATING_1.getId()));

        assertThat(groupCategorySkillRating, is(TestData.GROUP_CATEGORY_SKILL_RATING_1));

        int groupId = TestData.GROUP_CATEGORY_SKILL_RATING_1.getGroupId();
        int categoryId = TestData.GROUP_CATEGORY_SKILL_RATING_1.getCategoryId();
        int memberId = TestData.GROUP_CATEGORY_SKILL_RATING_1.getMemberId();
        double addSkillRating = 15.00;
        groupCategorySkillRatingDao.addSkillRatingFromSingleGame(groupId, categoryId, memberId, addSkillRating);
        TimeUnit.MILLISECONDS.sleep(100);

        GroupCategorySkillRating updatedGroupCategorySkillRating = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getGroupCategorySkillRatingById(TestData.GROUP_CATEGORY_SKILL_RATING_1.getId()));

        assertThat(updatedGroupCategorySkillRating.getId(), is(groupCategorySkillRating.getId()));
        assertThat(updatedGroupCategorySkillRating.getSkillRating(), is(groupCategorySkillRating.getSkillRating() + addSkillRating));
        assertThat(updatedGroupCategorySkillRating.getGamesRated(), is(groupCategorySkillRating.getGamesRated() + 1));
    }

    @Test
    public void removeSkillRatingFromSingleGame() throws InterruptedException {
        groupCategorySkillRatingDao.insertAll(TestData.GROUP_CATEGORY_SKILL_RATINGS.toArray(new GroupCategorySkillRating[TestData.GROUP_CATEGORY_SKILL_RATINGS.size()]));
        GroupCategorySkillRating groupCategorySkillRating = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getGroupCategorySkillRatingById(TestData.GROUP_CATEGORY_SKILL_RATING_1.getId()));

        assertThat(groupCategorySkillRating, is(TestData.GROUP_CATEGORY_SKILL_RATING_1));

        int groupId = TestData.GROUP_CATEGORY_SKILL_RATING_1.getGroupId();
        int categoryId = TestData.GROUP_CATEGORY_SKILL_RATING_1.getCategoryId();
        int memberId = TestData.GROUP_CATEGORY_SKILL_RATING_1.getMemberId();
        double removeSkillRating = 12.15;
        groupCategorySkillRatingDao.removeSkillRatingFromSingleGame(groupId, categoryId, memberId, removeSkillRating);
        TimeUnit.MILLISECONDS.sleep(100);

        GroupCategorySkillRating updatedGroupCategorySkillRating = LiveDataTestUtil.getValue(groupCategorySkillRatingDao.getGroupCategorySkillRatingById(TestData.GROUP_CATEGORY_SKILL_RATING_1.getId()));

        assertThat(updatedGroupCategorySkillRating.getId(), is(groupCategorySkillRating.getId()));
        assertThat(updatedGroupCategorySkillRating.getSkillRating(), is(groupCategorySkillRating.getSkillRating() - removeSkillRating));
        assertThat(updatedGroupCategorySkillRating.getGamesRated(), is(groupCategorySkillRating.getGamesRated() - 1));
    }
}
