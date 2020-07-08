package com.floatingpanda.scoreboard.model;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.daos.GroupMemberDao;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.GroupMember;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.viewmodels.ChoosePlayerSharedViewModel;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ChoosePlayerSharedViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private MemberDao memberDao;
    private GroupDao groupDao;
    private GroupMemberDao groupMemberDao;
    private ChoosePlayerSharedViewModel choosePlayerSharedViewModel;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        memberDao = db.memberDao();
        groupDao = db.groupDao();
        groupMemberDao = db.groupMemberDao();
        choosePlayerSharedViewModel = new ChoosePlayerSharedViewModel(ApplicationProvider.getApplicationContext(), db);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getObservablePotentialPlayersWhenNotInitialised() throws InterruptedException {
        List<Member> potentialPlayers = choosePlayerSharedViewModel.getPotentialPlayers();
        List<Member> observablePotentialPlayers = LiveDataTestUtil.getValue(choosePlayerSharedViewModel.getObservablePotentialPlayers());

        assertTrue(potentialPlayers.isEmpty());
        //Because potential players has not been initialised observablePotentialPlayers, which is a list drawn from a MutableLiveData<List<Member>> object,
        // should be null because the mutable live data shouldn't hold anything yet.
        assertNull(observablePotentialPlayers);
    }

    @Test
    public void getObservablePotentialPlayersWhenInitialised() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));

        List<Member> group3Members = LiveDataTestUtil.getValue(groupMemberDao.findMembersOfASpecificGroupByGroupId(TestData.GROUP_3.getId()));

        assertThat(group3Members.size(), is(2));
        assertTrue(group3Members.contains(TestData.MEMBER_1));
        assertTrue(group3Members.contains(TestData.MEMBER_3));

        choosePlayerSharedViewModel.initialisePotentialPlayers(TestData.GROUP_3.getId());
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> potentialPlayers = choosePlayerSharedViewModel.getPotentialPlayers();
        List<Member> observablePotentialPlayers = LiveDataTestUtil.getValue(choosePlayerSharedViewModel.getObservablePotentialPlayers());

        assertThat(potentialPlayers.size(), is(group3Members.size()));
        assertTrue(potentialPlayers.contains(TestData.MEMBER_1));
        assertTrue(potentialPlayers.contains(TestData.MEMBER_3));

        assertThat(observablePotentialPlayers.size(), is(group3Members.size()));
        assertTrue(observablePotentialPlayers.contains(TestData.MEMBER_1));
        assertTrue(observablePotentialPlayers.contains(TestData.MEMBER_3));
    }

    @Test
    public void getTeamOfMembersBeforeSettingOne() {
        int teamNo = 1;
        TeamOfPlayers teamOfPlayers = choosePlayerSharedViewModel.getTeamOfMembers(teamNo);

        assertNotNull(teamOfPlayers);
        assertThat(teamOfPlayers.getTeamNo(), is(teamNo));
        assertThat(teamOfPlayers.getPosition(), is(teamNo));
        assertTrue(teamOfPlayers.getMembers().isEmpty());
    }

    @Test
    public void getTeamMemberListBeforeSettingOne() {
        int teamNo = 1;
        List<Member> teamMemberList = choosePlayerSharedViewModel.getTeamMemberList(1);

        assertTrue(teamMemberList.isEmpty());
    }

    @Test
    public void addPlayerToTeamThenGetTeamMemberList() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));

        choosePlayerSharedViewModel.initialisePotentialPlayers(TestData.GROUP_3.getId());
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> group3Members = LiveDataTestUtil.getValue(groupMemberDao.findMembersOfASpecificGroupByGroupId(TestData.GROUP_3.getId()));
        assertThat(group3Members.size(), is(2));
        assertTrue(group3Members.contains(TestData.MEMBER_1));
        assertTrue(group3Members.contains(TestData.MEMBER_3));

        List<Member> potentialPlayers = choosePlayerSharedViewModel.getPotentialPlayers();
        assertThat(potentialPlayers.size(), is(group3Members.size()));
        assertTrue(potentialPlayers.contains(TestData.MEMBER_1));
        assertTrue(potentialPlayers.contains(TestData.MEMBER_3));

        int teamNo = 1;
        choosePlayerSharedViewModel.addPlayerToTeam(1, TestData.MEMBER_1);

        List<Member> team1MemberList = choosePlayerSharedViewModel.getTeamMemberList(1);
        assertThat(team1MemberList.size(), is(1));
        assertThat(team1MemberList.get(0), is(TestData.MEMBER_1));

        potentialPlayers = choosePlayerSharedViewModel.getPotentialPlayers();
        assertThat(potentialPlayers.size(), is(group3Members.size() - 1));
        assertFalse(potentialPlayers.contains(TestData.MEMBER_1));
        assertTrue(potentialPlayers.contains(TestData.MEMBER_3));
    }

    @Test
    public void removePlayerFromTeamThenGetTeamMemberList() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));

        choosePlayerSharedViewModel.initialisePotentialPlayers(TestData.GROUP_3.getId());
        TimeUnit.MILLISECONDS.sleep(100);

        int teamNo = 1;
        choosePlayerSharedViewModel.addPlayerToTeam(1, TestData.MEMBER_1);

        List<Member> team1MemberList = choosePlayerSharedViewModel.getTeamMemberList(1);
        assertThat(team1MemberList.size(), is(1));
        assertThat(team1MemberList.get(0), is(TestData.MEMBER_1));

        List<Member> potentialPlayers = choosePlayerSharedViewModel.getPotentialPlayers();
        assertThat(potentialPlayers.size(), is(1));
        assertFalse(potentialPlayers.contains(TestData.MEMBER_1));

        choosePlayerSharedViewModel.removePlayerFromTeam(1, TestData.MEMBER_1);

        team1MemberList = choosePlayerSharedViewModel.getTeamMemberList(1);
        assertTrue(team1MemberList.isEmpty());

        potentialPlayers = choosePlayerSharedViewModel.getPotentialPlayers();
        assertThat(potentialPlayers.size(), is(2));
        assertTrue(potentialPlayers.contains(TestData.MEMBER_1));
    }

    @Test
    public void getTeamPositionBeforeSettingOne() {
        int teamNo1 = 1;
        int teamPosition1 = choosePlayerSharedViewModel.getTeamPosition(teamNo1);

        assertThat(teamPosition1, is(teamNo1));

        int teamNo2 = 4;
        int teamPosition2 = choosePlayerSharedViewModel.getTeamPosition(teamNo2);

        assertThat(teamPosition2, is(teamNo2));
    }

    @Test
    public void setTeamPositionAndThenGetTeamPosition() {
        int teamNo1 = 1;
        int teamPosition1 = choosePlayerSharedViewModel.getTeamPosition(teamNo1);

        assertThat(teamPosition1, is(teamNo1));

        int newPosition1 = 3;
        choosePlayerSharedViewModel.setTeamPosition(teamNo1, newPosition1);
        teamPosition1 = choosePlayerSharedViewModel.getTeamPosition(teamNo1);

        assertThat(teamPosition1, is(newPosition1));

        int teamNo2 = 4;
        int newPosition2 = 8;
        choosePlayerSharedViewModel.setTeamPosition(teamNo2,newPosition2);

        int teamPosition2 = choosePlayerSharedViewModel.getTeamPosition(teamNo2);
        assertThat(teamPosition2, is(newPosition2));
    }

    @Test
    public void addingTeamMembersToTeamThenUpdatingObservablePotentialPlayers() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));

        choosePlayerSharedViewModel.initialisePotentialPlayers(TestData.GROUP_3.getId());
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> group3Members = LiveDataTestUtil.getValue(groupMemberDao.findMembersOfASpecificGroupByGroupId(TestData.GROUP_3.getId()));
        assertThat(group3Members.size(), is(2));
        assertTrue(group3Members.contains(TestData.MEMBER_1));
        assertTrue(group3Members.contains(TestData.MEMBER_3));

        List<Member> potentialPlayers = choosePlayerSharedViewModel.getPotentialPlayers();
        assertThat(potentialPlayers.size(), is(group3Members.size()));
        assertTrue(potentialPlayers.contains(TestData.MEMBER_1));
        assertTrue(potentialPlayers.contains(TestData.MEMBER_3));

        List<Member> observablePotentialPlayers = LiveDataTestUtil.getValue(choosePlayerSharedViewModel.getObservablePotentialPlayers());
        assertThat(observablePotentialPlayers.size(), is(group3Members.size()));
        assertTrue(observablePotentialPlayers.contains(TestData.MEMBER_1));
        assertTrue(observablePotentialPlayers.contains(TestData.MEMBER_3));

        choosePlayerSharedViewModel.addPlayerToTeam(1, TestData.MEMBER_1);

        potentialPlayers = choosePlayerSharedViewModel.getPotentialPlayers();
        assertThat(potentialPlayers.size(), is(group3Members.size() - 1));
        assertFalse(potentialPlayers.contains(TestData.MEMBER_1));
        assertTrue(potentialPlayers.contains(TestData.MEMBER_3));

        //TODO sort this out. Potential players changes observable potential players anyway.
        observablePotentialPlayers = LiveDataTestUtil.getValue(choosePlayerSharedViewModel.getObservablePotentialPlayers());
        assertThat(observablePotentialPlayers.size(), is(group3Members.size() - 1));
        assertFalse(observablePotentialPlayers.contains(TestData.MEMBER_1));
        assertTrue(observablePotentialPlayers.contains(TestData.MEMBER_3));

        choosePlayerSharedViewModel.updateObservablePotentialPlayers();
        TimeUnit.MILLISECONDS.sleep(100);

        potentialPlayers = choosePlayerSharedViewModel.getPotentialPlayers();
        assertThat(potentialPlayers.size(), is(group3Members.size() - 1));
        assertFalse(potentialPlayers.contains(TestData.MEMBER_1));
        assertTrue(potentialPlayers.contains(TestData.MEMBER_3));

        observablePotentialPlayers = LiveDataTestUtil.getValue(choosePlayerSharedViewModel.getObservablePotentialPlayers());
        assertThat(observablePotentialPlayers.size(), is(group3Members.size() - 1));
        assertFalse(observablePotentialPlayers.contains(TestData.MEMBER_1));
        assertTrue(observablePotentialPlayers.contains(TestData.MEMBER_3));
    }
}
