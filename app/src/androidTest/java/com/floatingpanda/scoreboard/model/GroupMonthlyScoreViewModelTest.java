package com.floatingpanda.scoreboard.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
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
import com.floatingpanda.scoreboard.viewmodels.GroupMonthlyScoreViewModel;

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
public class GroupMonthlyScoreViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private GroupDao groupDao;
    private MemberDao memberDao;
    private GroupMonthlyScoreDao groupMonthlyScoreDao;
    private ScoreDao scoreDao;
    private GroupMonthlyScoreViewModel groupMonthlyScoreViewModel;

    @Mock
    Activity activity;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        groupDao = db.groupDao();
        memberDao = db.memberDao();
        groupMonthlyScoreDao = db.groupMonthlyScoreDao();
        scoreDao = db.scoreDao();

        groupMonthlyScoreViewModel = new GroupMonthlyScoreViewModel(ApplicationProvider.getApplicationContext(), db);

        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getGroupMonthlyScoresWithScoresAndMemberDetailsWhenNoneInserted() throws InterruptedException {
        groupMonthlyScoreViewModel.initGroupMonthlyScoresWithScoresAndMemberDetails(TestData.GROUP_1.getId());
        List<GroupMonthlyScoreWithScoresAndMemberDetails> groupMonthlyScoresWithScoresAndMemberDetails =
                LiveDataTestUtil.getValue(groupMonthlyScoreViewModel.getGroupMonthlyScoresWithScoresAndMemberDetails());

        assertTrue(groupMonthlyScoresWithScoresAndMemberDetails.isEmpty());
    }

    @Test
    public void getGroupMonthlyScoresWithScoresAndMemberDetailsWhenAllInserted() throws InterruptedException {
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));
        groupMonthlyScoreViewModel.initGroupMonthlyScoresWithScoresAndMemberDetails(TestData.GROUP_1.getId());
        List<GroupMonthlyScoreWithScoresAndMemberDetails> groupMonthlyScoresWithScoresAndMemberDetails =
                LiveDataTestUtil.getValue(groupMonthlyScoreViewModel.getGroupMonthlyScoresWithScoresAndMemberDetails());

        assertThat(groupMonthlyScoresWithScoresAndMemberDetails.size(), is(4));

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
