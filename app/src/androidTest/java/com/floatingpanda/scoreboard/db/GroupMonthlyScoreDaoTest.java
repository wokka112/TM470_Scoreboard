package com.floatingpanda.scoreboard.db;

import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.relations.GroupMonthlyScoreWithScoresAndMemberDetails;
import com.floatingpanda.scoreboard.data.relations.ScoreWithMemberDetails;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.daos.GroupMonthlyScoreDao;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.daos.ScoreDao;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.GroupMonthlyScore;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.entities.Score;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class GroupMonthlyScoreDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private GroupDao groupDao;
    private GroupMonthlyScoreDao groupMonthlyScoreDao;
    private ScoreDao scoreDao;
    private MemberDao memberDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        groupMonthlyScoreDao = db.groupMonthlyScoreDao();

        // Needed because group monthly scores in the db uses group ids as foreign keys.
        groupDao = db.groupDao();
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));

        memberDao = db.memberDao();
        scoreDao = db.scoreDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllGroupMonthlyScoresWhenNoneInserted() throws InterruptedException {
        List<GroupMonthlyScore> groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getAll());

        assertTrue(groupMonthlyScores.isEmpty());
    }

    @Test
    public void getAllGroupMonthlyScoresWhenAllInserted() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));

        List<GroupMonthlyScore> groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getAll());
        assertThat(groupMonthlyScores.size(), is(TestData.GROUP_MONTHLY_SCORES.size()));

        int previousYear = 3000;
        int previousMonth = 12;
        for (GroupMonthlyScore groupMonthlyScore : groupMonthlyScores) {
            assertTrue(groupMonthlyScore.getYear() <= previousYear);

            // If same year, month should be less than previous month.
            if (groupMonthlyScore.getYear() == previousYear) {
                assertTrue(groupMonthlyScore.getMonth() <= previousMonth);
            }
            // If different year, month can be any month.

            previousYear = groupMonthlyScore.getYear();
            previousMonth = groupMonthlyScore.getMonth();
        }
    }

    @Test
    public void getGroupMonthlyScoresByGroupIdWhenNoneInserted() throws InterruptedException {
        List<GroupMonthlyScore> groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoresByGroupId(TestData.GROUP_1.getId()));

        assertTrue(groupMonthlyScores.isEmpty());
    }

    @Test
    public void getGroupMonthlyScoresByGroupIdWhenAllInserted() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        // Group 1 has 4 group monthly scores associated with it
        List<GroupMonthlyScore> groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoresByGroupId(TestData.GROUP_1.getId()));

        assertThat(groupMonthlyScores.size(), is(4));

        int previousYear = 3000;
        int previousMonth = 12;
        for (GroupMonthlyScore groupMonthlyScore : groupMonthlyScores) {
            assertTrue(groupMonthlyScore.getYear() <= previousYear);

            // If same year, month should be less than previous month.
            if (groupMonthlyScore.getYear() == previousYear) {
                assertTrue(groupMonthlyScore.getMonth() <= previousMonth);
            }
            // If different year, month can be any month.

            previousYear = groupMonthlyScore.getYear();
            previousMonth = groupMonthlyScore.getMonth();
        }
    }

    @Test
    public void getGroupMonthlyScoresByGroupIdAndYearWhenNoneInserted() throws InterruptedException {
        int year = 2020;
        List<GroupMonthlyScore> groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoresByGroupIdAndYear(TestData.GROUP_1.getId(), year));

        assertTrue(groupMonthlyScores.isEmpty());
    }

    @Test
    public void getGroupMonthlyScoresByGroupIdAndYearWhenAllInserted() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        // Group 1 has 3 group monthly scores associated with it for the year 2020
        int year = 2020;
        List<GroupMonthlyScore> groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoresByGroupIdAndYear(TestData.GROUP_1.getId(), year));

        assertThat(groupMonthlyScores.size(), is(3));

        int previousMonth = 12;
        for (GroupMonthlyScore groupMonthlyScore : groupMonthlyScores) {
            assertTrue(groupMonthlyScore.getMonth() <= previousMonth);
            previousMonth = groupMonthlyScore.getMonth();
        }
    }

    @Test
    public void getGroupMonthlyScoresByGroupIdAndYearAndQuarterWhenNoneInserted() throws InterruptedException {
        int year = 2020;
        int quarter = 3;
        List<GroupMonthlyScore> groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoresByGroupIdAndYearAndQuarter(TestData.GROUP_1.getId(), year, quarter));

        assertTrue(groupMonthlyScores.isEmpty());
    }

    @Test
    public void getGroupMonthlyScoresByGroupIdAndYearAndQuarterWhenAllInserted() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        // Group 1 has 2 group monthly scores associated with it for the year 2020 and quarter 3
        int year = 2020;
        int quarter = 3;
        List<GroupMonthlyScore> groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoresByGroupIdAndYearAndQuarter(TestData.GROUP_1.getId(), year, quarter));

        assertThat(groupMonthlyScores.size(), is(2));

        int previousMonth = 12;
        for (GroupMonthlyScore groupMonthlyScore : groupMonthlyScores) {
            assertTrue(groupMonthlyScore.getMonth() <= previousMonth);
            previousMonth = groupMonthlyScore.getMonth();
        }
    }

    @Test
    public void getGroupMonthlyScoreByGroupIdAndYearAndMonthWhenNoneInserted() throws InterruptedException {
        int year = TestData.GROUP_MONTHLY_SCORE_2.getYear();
        int month = TestData.GROUP_MONTHLY_SCORE_2.getMonth();
        GroupMonthlyScore groupMonthlyScore = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoreByGroupIdAndYearAndMonth(TestData.GROUP_1.getId(), year, month));

        assertNull(groupMonthlyScore);
    }

    @Test
    public void getGroupMonthlyScoreByGroupIdAndYearAndMonthWhenAllInserted() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        int year = TestData.GROUP_MONTHLY_SCORE_2.getYear();
        int month = TestData.GROUP_MONTHLY_SCORE_2.getMonth();
        GroupMonthlyScore groupMonthlyScore = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoreByGroupIdAndYearAndMonth(TestData.GROUP_1.getId(), year, month));

        assertThat(groupMonthlyScore, is(TestData.GROUP_MONTHLY_SCORE_2));
    }

    @Test
    public void getGroupMonthlyScoreByGroupMonthlyScoreIdWhenNoneInserted() throws InterruptedException {
        GroupMonthlyScore groupMonthlyScore = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoreByGroupMonthlyScoreId(TestData.GROUP_MONTHLY_SCORE_1.getId()));

        assertNull(groupMonthlyScore);
    }

    @Test
    public void getGroupMonthlyScoreByGroupMonthlyScoreIdWhenAllInserted() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        GroupMonthlyScore groupMonthlyScore = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoreByGroupMonthlyScoreId(TestData.GROUP_MONTHLY_SCORE_1.getId()));

        assertThat(groupMonthlyScore, is(TestData.GROUP_MONTHLY_SCORE_1));
    }

    @Test
    public void getAllGroupMonthlyScoresAfterInsertingAMonthlyScore() throws InterruptedException {
        groupMonthlyScoreDao.insert(TestData.GROUP_MONTHLY_SCORE_2);
        List<GroupMonthlyScore> groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getAll());

        assertThat(groupMonthlyScores.size(), is(1));
        assertThat(groupMonthlyScores.get(0), is(TestData.GROUP_MONTHLY_SCORE_2));
    }

    @Test
    public void getAllGroupMonthlyScoresAfterInsertingAndDeletingAll() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        List<GroupMonthlyScore> groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getAll());

        assertThat(groupMonthlyScores.size(), is(TestData.GROUP_MONTHLY_SCORES.size()));

        groupMonthlyScoreDao.deleteAll();
        TimeUnit.MILLISECONDS.sleep(100);

        groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getAll());

        assertTrue(groupMonthlyScores.isEmpty());
    }

    @Test
    public void getAllGroupMonthlyScoresAfterInsertingAllAndDeletingOne() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        List<GroupMonthlyScore> groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getAll());

        assertThat(groupMonthlyScores.size(), is(TestData.GROUP_MONTHLY_SCORES.size()));

        groupMonthlyScoreDao.delete(TestData.GROUP_MONTHLY_SCORE_1);
        TimeUnit.MILLISECONDS.sleep(100);

        groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getAll());
        assertThat(groupMonthlyScores.size(), is(TestData.GROUP_MONTHLY_SCORES.size() - 1));
        assertFalse(groupMonthlyScores.contains(TestData.GROUP_MONTHLY_SCORE_1));
    }

    @Test
    public void getAllGroupMonthlyScoresAfterInsertingAllAndDeletingAnAssociatedGroup() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));

        List<GroupMonthlyScore> groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getAll());
        assertThat(groupMonthlyScores.size(), is(TestData.GROUP_MONTHLY_SCORES.size()));

        List<GroupMonthlyScore> group1MonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoresByGroupId(TestData.GROUP_1.getId()));
        assertThat(group1MonthlyScores.size(), is(4));

        groupDao.delete(TestData.GROUP_1);
        TimeUnit.MILLISECONDS.sleep(100);

        Group group1 = LiveDataTestUtil.getValue(groupDao.findLiveDataById(TestData.GROUP_1.getId()));
        assertNull(group1);

        groupMonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getAll());
        assertThat(groupMonthlyScores.size(), is(TestData.GROUP_MONTHLY_SCORES.size() - 4));

        group1MonthlyScores = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoresByGroupId(TestData.GROUP_1.getId()));
        assertTrue(group1MonthlyScores.isEmpty());
    }

    @Test
    public void getAllGroupMonthlyScoresWithScoresAndMemberDetailsWhenAllInserted() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        List<GroupMonthlyScoreWithScoresAndMemberDetails> groupMonthlyScoresWithScoresAndMemberDetails =
                LiveDataTestUtil.getValue(groupMonthlyScoreDao.getAllGroupMonthlyScoresWithScoresAndMemberDetails());
        assertThat(groupMonthlyScoresWithScoresAndMemberDetails.size(), is(TestData.GROUP_MONTHLY_SCORES.size()));

        testGroupMonthlyScoresWithScoresAndMemberDetails(groupMonthlyScoresWithScoresAndMemberDetails);
    }

    @Test
    public void getGroupMonthlyScoresWithScoresAndMemberDetailsByGroupId() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        //Group 1 has 4 monthly scores associated with it
        List<GroupMonthlyScoreWithScoresAndMemberDetails> groupMonthlyScoresWithScoresAndMemberDetails =
                LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoresWithScoresAndMemberDetailsByGroupId(TestData.GROUP_1.getId()));
        assertThat(groupMonthlyScoresWithScoresAndMemberDetails.size(), is(4));

        testGroupMonthlyScoresWithScoresAndMemberDetails(groupMonthlyScoresWithScoresAndMemberDetails);
    }

    @Test
    public void getGroupMonthlyScoresWithScoresAndMemberDetailsByGroupIdAndYear() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        //Group 1 has 3 monthly scores associated with it for the year 2020
        int groupId = TestData.GROUP_1.getId();
        int year = 2020;
        List<GroupMonthlyScoreWithScoresAndMemberDetails> groupMonthlyScoresWithScoresAndMemberDetails =
                LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoresWithScoresAndMemberDetailsByGroupIdAndYear(groupId, year));
        assertThat(groupMonthlyScoresWithScoresAndMemberDetails.size(), is(3));

        testGroupMonthlyScoresWithScoresAndMemberDetails(groupMonthlyScoresWithScoresAndMemberDetails);
    }

    @Test
    public void getGroupMonthlyScoresWithScoresAndMemberDetailsByGroupIdAndYearAndQuarter() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        //Group 1 has 2 monthly scores associated with it for year 2020 and quarter 3
        int groupId = TestData.GROUP_1.getId();
        int year = 2020;
        int quarter = 3;
        List<GroupMonthlyScoreWithScoresAndMemberDetails> groupMonthlyScoresWithScoresAndMemberDetails =
                LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoresWithScoresAndMemberDetailsByGroupIdAndYearAndQuarter(groupId, year, quarter));
        assertThat(groupMonthlyScoresWithScoresAndMemberDetails.size(), is(2));

        testGroupMonthlyScoresWithScoresAndMemberDetails(groupMonthlyScoresWithScoresAndMemberDetails);
    }

    @Test
    public void getGroupMonthlyScoreWithScoresAndMemberDetailsByGroupIdAndYearAndMonth() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        int groupId = TestData.GROUP_1.getId();
        int year = TestData.GROUP_MONTHLY_SCORE_3.getYear();
        int month = TestData.GROUP_MONTHLY_SCORE_3.getMonth();
        GroupMonthlyScoreWithScoresAndMemberDetails groupMonthlyScoreWithScoresAndMemberDetails =
                LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoreWithScoresAndMemberDetailsByGroupIdAndYearAndMonth(groupId, year, month));
        assertThat(groupMonthlyScoreWithScoresAndMemberDetails.getGroupMonthlyScore(), is(TestData.GROUP_MONTHLY_SCORE_3));
        assertThat(groupMonthlyScoreWithScoresAndMemberDetails.getScoresWithMemberDetails().size(), is (3));

        int previousScore = 100000;
        for (ScoreWithMemberDetails scoreWithMemberDetails : groupMonthlyScoreWithScoresAndMemberDetails.getScoresWithMemberDetails()) {
            assertTrue(scoreWithMemberDetails.getScore().getScore() <= previousScore);
            previousScore = scoreWithMemberDetails.getScore().getScore();
        }
    }

    @Test
    public void getGroupMonthlyScoreIdWhenAllInserted() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));

        int groupId = TestData.GROUP_MONTHLY_SCORE_2.getGroupId();
        int year = TestData.GROUP_MONTHLY_SCORE_2.getYear();
        int month = TestData.GROUP_MONTHLY_SCORE_2.getMonth();

        int groupMonthlyScoreId = groupMonthlyScoreDao.getGroupMonthlyScoreIdByGroupIdAndYearAndMonth(groupId, year, month);
        assertTrue(groupMonthlyScoreId == TestData.GROUP_MONTHLY_SCORE_2.getId());
        assertFalse(groupMonthlyScoreId == TestData.GROUP_MONTHLY_SCORE_3.getId());

        groupId = TestData.GROUP_MONTHLY_SCORE_3.getGroupId();
        year = TestData.GROUP_MONTHLY_SCORE_3.getYear();
        month = TestData.GROUP_MONTHLY_SCORE_3.getMonth();

        groupMonthlyScoreId = groupMonthlyScoreDao.getGroupMonthlyScoreIdByGroupIdAndYearAndMonth(groupId, year, month);
        assertTrue(groupMonthlyScoreId == TestData.GROUP_MONTHLY_SCORE_3.getId());
        assertFalse(groupMonthlyScoreId == TestData.GROUP_MONTHLY_SCORE_2.getId());
    }

    @Test
    public void testContainsGroupMonthlyScore() throws InterruptedException {
        boolean contains = groupMonthlyScoreDao.containsGroupMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getGroupId(),
                TestData.GROUP_MONTHLY_SCORE_1.getYear(), TestData.GROUP_MONTHLY_SCORE_1.getMonth());
        assertFalse(contains);

        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));

        contains = groupMonthlyScoreDao.containsGroupMonthlyScore(TestData.GROUP_MONTHLY_SCORE_1.getGroupId(),
                TestData.GROUP_MONTHLY_SCORE_1.getYear(), TestData.GROUP_MONTHLY_SCORE_1.getMonth());
        assertTrue(contains);
    }

    private void testGroupMonthlyScoresWithScoresAndMemberDetails(List<GroupMonthlyScoreWithScoresAndMemberDetails> groupMonthlyScoresWithScoresAndMemberDetails) {
        int previousYear = 3000;
        int previousMonth = 12;
        for (GroupMonthlyScoreWithScoresAndMemberDetails groupMonthlyScoreWithScoresAndMemberDetails : groupMonthlyScoresWithScoresAndMemberDetails) {
            GroupMonthlyScore groupMonthlyScore = groupMonthlyScoreWithScoresAndMemberDetails.getGroupMonthlyScore();

            assertTrue(groupMonthlyScore.getYear() <= previousYear);

            // If same year, month should be less than previous month.
            if (groupMonthlyScore.getYear() == previousYear) {
                assertTrue(groupMonthlyScore.getMonth() <= previousMonth);
            }
            // If different year, month can be any month.

            previousYear = groupMonthlyScore.getYear();
            previousMonth = groupMonthlyScore.getMonth();

            int previousScore = 100000;
            for (ScoreWithMemberDetails scoreWithMemberDetails : groupMonthlyScoreWithScoresAndMemberDetails.getScoresWithMemberDetails()){
                Log.w("GroupMonthScoreDaoTest.java", "Previous Score: " + previousScore + ", Current Score: " + scoreWithMemberDetails.getScore().getScore());
                assertTrue(scoreWithMemberDetails.getScore().getScore() <= previousScore);
                previousScore = scoreWithMemberDetails.getScore().getScore();
            }
        }
    }
}
