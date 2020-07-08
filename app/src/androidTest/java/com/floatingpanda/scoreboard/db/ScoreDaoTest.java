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
import com.floatingpanda.scoreboard.data.ScoreWithMemberDetails;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ScoreDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private GroupDao groupDao;
    private MemberDao memberDao;
    private GroupMonthlyScoreDao groupMonthlyScoreDao;
    private ScoreDao scoreDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        scoreDao = db.scoreDao();

        // Needed because scores in the db use group monthly score and member ids as foreign keys. Group
        // monthly scores in the db uses group ids as foreign keys.
        memberDao = db.memberDao();
        groupDao = db.groupDao();
        groupMonthlyScoreDao = db.groupMonthlyScoreDao();

        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        groupMonthlyScoreDao.insertAll(TestData.GROUP_MONTHLY_SCORES.toArray(new GroupMonthlyScore[TestData.GROUP_MONTHLY_SCORES.size()]));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllScoresWhenNoneInserted() throws InterruptedException {
        List<Score> scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertTrue(scores.isEmpty());
    }

    @Test
    public void getAllScoresWhenAllInserted() throws InterruptedException {
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        List<Score> scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertThat(scores.size(), is(TestData.SCORES.size()));

        int previousScore = 100000;
        for (Score score : scores) {
            assertTrue(score.getScore() <= previousScore);
            previousScore = score.getScore();
        }
    }

    @Test
    public void getAllGroupsMonthlyScoresByGroupMonthlyScoreIdWhenNoneInserted() throws InterruptedException {
        List<Score> scores = LiveDataTestUtil.getValue(scoreDao.getAllGroupsMonthlyScores(TestData.GROUP_MONTHLY_SCORE_2.getId()));
        assertTrue(scores.isEmpty());
    }

    @Test
    public void getAllGroupsMonthlyScoresByGroupMonthlyScoreIdWhenAllInserted() throws InterruptedException {
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        //Each monthly score except monthly score 5 has 3 scores associated with it.
        List<Score> scores = LiveDataTestUtil.getValue(scoreDao.getAllGroupsMonthlyScores(TestData.GROUP_MONTHLY_SCORE_2.getId()));
        assertThat(scores.size(), is(3));

        int previousScore = 100000;
        for (Score score : scores) {
            assertTrue(score.getScore() <= previousScore);
            previousScore = score.getScore();
        }
    }

    @Test
    public void getGroupMembersMonthlyScoreByGroupMonthlyScoreIdAndMemberIdWhenNoneInserted() throws InterruptedException {
        Score score = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_2.getId(), TestData.MEMBER_1.getId()));
        assertNull(score);
    }

    @Test
    public void getGroupMembersMonthlyScoreByGroupMonthlyScoreIdAndMemberIdWhenAllInserted() throws InterruptedException {
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        Score score = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_2.getId(), TestData.MEMBER_1.getId()));
        assertThat(score, is(TestData.SCORE_4));
        assertThat(score.getScore(), is(TestData.SCORE_4.getScore()));
        assertThat(score, is(not(TestData.SCORE_5)));
        assertThat(score, is(not(TestData.SCORE_1)));
    }

    @Test
    public void getGroupMembersMonthlyScoreByGroupMonthlyScoreIdAndMemberIdAfterAddingScoreToIt() throws InterruptedException {
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        Score score = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_2.getId(), TestData.MEMBER_1.getId()));
        assertThat(score, is(TestData.SCORE_4));
        assertThat(score.getScore(), is(TestData.SCORE_4.getScore()));
        assertThat(score, is(not(TestData.SCORE_5)));
        assertThat(score, is(not(TestData.SCORE_1)));

        int addScore = 10;
        scoreDao.addScore(score.getGroupMonthlyScoreId(), score.getMemberId(), addScore);

        score = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.GROUP_MONTHLY_SCORE_2.getId(), TestData.MEMBER_1.getId()));
        assertThat(score.getId(), is(TestData.SCORE_4.getId()));
        assertThat(score.getGroupMonthlyScoreId(), is(TestData.SCORE_4.getGroupMonthlyScoreId()));
        assertThat(score.getMemberId(), is(TestData.SCORE_4.getMemberId()));
        assertThat(score.getScore(), is(TestData.SCORE_4.getScore() + 10));
    }

    @Test
    public void getAllScoresWhenOneInserted() throws InterruptedException {
        scoreDao.insert(TestData.SCORE_4);

        List<Score> scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertThat(scores.size(), is(1));
    }

    @Test
    public void getAllScoresWhenAllInsertedAndDeleted() throws InterruptedException {
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        List<Score> scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertThat(scores.size(), is(TestData.SCORES.size()));

        scoreDao.deleteAll();
        TimeUnit.MILLISECONDS.sleep(100);

        scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertTrue(scores.isEmpty());
    }

    @Test
    public void getAllScoresWhenAllInsertedAndOneDeleted() throws InterruptedException {
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        List<Score> scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertThat(scores.size(), is(TestData.SCORES.size()));

        Score score = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.SCORE_5.getGroupMonthlyScoreId(), TestData.SCORE_5.getMemberId()));
        assertThat(score, is(TestData.SCORE_5));

        scoreDao.delete(TestData.SCORE_5);
        TimeUnit.MILLISECONDS.sleep(100);

        scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertThat(scores.size(), is(TestData.SCORES.size() - 1));

        score = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(TestData.SCORE_5.getGroupMonthlyScoreId(), TestData.SCORE_5.getMemberId()));
        assertNull(score);
    }

    @Test
    public void getAllScoresWhenAllInsertedAndAssociatedGroupMonthlyScoreIsDeleted() throws InterruptedException {
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        List<Score> scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertThat(scores.size(), is(TestData.SCORES.size()));

        //Group monthly score 2 has 3 scores associated with it.
        List<Score> monthlyScore2Scores = LiveDataTestUtil.getValue(scoreDao.getAllGroupsMonthlyScores(TestData.GROUP_MONTHLY_SCORE_2.getId()));
        assertThat(monthlyScore2Scores.size(), is(3));

        groupMonthlyScoreDao.delete(TestData.GROUP_MONTHLY_SCORE_2);
        TimeUnit.MILLISECONDS.sleep(100);

        GroupMonthlyScore groupMonthlyScore2 = LiveDataTestUtil.getValue(groupMonthlyScoreDao.getGroupMonthlyScoreByGroupIdAndYearAndMonth(TestData.GROUP_MONTHLY_SCORE_2.getGroupId(),
                TestData.GROUP_MONTHLY_SCORE_2.getYear(), TestData.GROUP_MONTHLY_SCORE_2.getMonth()));
        assertNull(groupMonthlyScore2);

        scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertThat(scores.size(), is(TestData.SCORES.size() - 3));

        monthlyScore2Scores = LiveDataTestUtil.getValue(scoreDao.getAllGroupsMonthlyScores(TestData.GROUP_MONTHLY_SCORE_2.getId()));
        assertTrue(monthlyScore2Scores.isEmpty());
    }

    @Test
    public void getAllScoresWhenAllInsertedAndAssociatedMemberisDeleted() throws InterruptedException {
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        List<Score> scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertThat(scores.size(), is(TestData.SCORES.size()));

        int groupMonthlyScoreId = TestData.SCORE_4.getGroupMonthlyScoreId();
        int memberId = TestData.SCORE_4.getMemberId();
        Score score = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(groupMonthlyScoreId, memberId));
        assertThat(score, is(TestData.SCORE_4));

        memberDao.delete(TestData.MEMBER_1);
        TimeUnit.MILLISECONDS.sleep(100);

        Member member = LiveDataTestUtil.getValue(memberDao.findLiveDataById(TestData.MEMBER_1.getId()));
        assertNull(member);

        //Member 1 has 5 scores associated with them
        scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertThat(scores.size(), is(TestData.SCORES.size() - 5));

        score = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScore(groupMonthlyScoreId, memberId));
        assertNull(score);
    }

    @Test
    public void getAllScoresWithMemberDetailsWhenAllInserted() throws InterruptedException {
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        List<Score> scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertThat(scores.size(), is(TestData.SCORES.size()));

        List<ScoreWithMemberDetails> scoresWithMemberDetails = LiveDataTestUtil.getValue(scoreDao.getAllScoresWithMemberDetails());
        assertThat(scoresWithMemberDetails.size(), is(TestData.SCORES.size()));

        ArrayList<Member> members = new ArrayList<>();
        ArrayList<Score> scores1 = new ArrayList<>();
        for (ScoreWithMemberDetails scoreWithMemberDetails : scoresWithMemberDetails) {
            members.add(scoreWithMemberDetails.getMember());
            scores1.add(scoreWithMemberDetails.getScore());
        }

        assertTrue(members.contains(TestData.MEMBER_1));
        assertTrue(members.contains(TestData.MEMBER_3));
        assertTrue(members.contains(TestData.MEMBER_4));
        assertTrue(members.contains(TestData.MEMBER_5));
        assertFalse(members.contains(TestData.MEMBER_2));

        int previousScore = 100000;
        for (Score score : scores1) {
            assertTrue(score.getScore() <= previousScore);
            previousScore = score.getScore();
        }
    }

    @Test
    public void getGroupsMonthlyScoresWithMemberDetailsWhenAllInserted() throws InterruptedException {
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        List<Score> scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertThat(scores.size(), is(TestData.SCORES.size()));

        List<ScoreWithMemberDetails> scoresWithMemberDetails = LiveDataTestUtil.getValue(scoreDao.getAllGroupsMonthlyScoresWithMemberDetails(TestData.GROUP_MONTHLY_SCORE_2.getId()));
        assertThat(scoresWithMemberDetails.size(), is(3));

        ArrayList<Member> members = new ArrayList<>();
        ArrayList<Score> scores1 = new ArrayList<>();
        for (ScoreWithMemberDetails scoreWithMemberDetails : scoresWithMemberDetails) {
            members.add(scoreWithMemberDetails.getMember());
            scores1.add(scoreWithMemberDetails.getScore());
        }

        assertTrue(members.contains(TestData.MEMBER_1));
        assertFalse(members.contains(TestData.MEMBER_3));
        assertTrue(members.contains(TestData.MEMBER_4));
        assertTrue(members.contains(TestData.MEMBER_5));

        int previousScore = 100000;
        for (Score score : scores1) {
            assertTrue(score.getScore() <= previousScore);
            previousScore = score.getScore();
        }
    }

    @Test
    public void getGroupMembersMonthlyScoreWithMemberDetailsWhenAllInserted() throws InterruptedException {
        scoreDao.insertAll(TestData.SCORES.toArray(new Score[TestData.SCORES.size()]));

        List<Score> scores = LiveDataTestUtil.getValue(scoreDao.getAll());
        assertThat(scores.size(), is(TestData.SCORES.size()));

        int groupMonthlyScoreId = TestData.SCORE_4.getGroupMonthlyScoreId();
        int memberId = TestData.SCORE_4.getMemberId();
        ScoreWithMemberDetails scoreWithMemberDetails = LiveDataTestUtil.getValue(scoreDao.getGroupMembersMonthlyScoreWithMemberDetails(groupMonthlyScoreId, memberId));
        assertThat(scoreWithMemberDetails.getMember(), is(TestData.MEMBER_1));
        assertThat(scoreWithMemberDetails.getScore(), is(TestData.SCORE_4));
    }
}
