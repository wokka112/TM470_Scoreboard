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
import com.floatingpanda.scoreboard.data.entities.PlayMode;
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
        TeamOfPlayers teamOfPlayers = choosePlayerSharedViewModel.getTeamOfPlayers(teamNo);

        assertNotNull(teamOfPlayers);
        assertThat(teamOfPlayers.getTeamNo(), is(teamNo));
        assertThat(teamOfPlayers.getPosition(), is(teamNo));
        assertTrue(teamOfPlayers.getMembers().isEmpty());
    }

    @Test
    public void getTeamMemberListBeforeSettingOne() {
        int teamNo = 1;
        List<Member> teamMemberList = choosePlayerSharedViewModel.getTeamOfPlayersMemberList(1);

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

        List<Member> team1MemberList = choosePlayerSharedViewModel.getTeamOfPlayersMemberList(1);
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

        List<Member> team1MemberList = choosePlayerSharedViewModel.getTeamOfPlayersMemberList(1);
        assertThat(team1MemberList.size(), is(1));
        assertThat(team1MemberList.get(0), is(TestData.MEMBER_1));

        List<Member> potentialPlayers = choosePlayerSharedViewModel.getPotentialPlayers();
        assertThat(potentialPlayers.size(), is(1));
        assertFalse(potentialPlayers.contains(TestData.MEMBER_1));

        choosePlayerSharedViewModel.removePlayerFromTeam(1, TestData.MEMBER_1);

        team1MemberList = choosePlayerSharedViewModel.getTeamOfPlayersMemberList(1);
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

    @Test
    public void testIsValidTeam() {
        int teamNo = 1;
        int position = 1;
        boolean teams = true;

        Context context = ApplicationProvider.getApplicationContext();

        //Test 1 - Invalid, no members in team 1.
        choosePlayerSharedViewModel.createEmptyTeam(teamNo, position);

        boolean isValid = choosePlayerSharedViewModel.isValidTeam(context, teamNo, teams, true);
        assertFalse(isValid);

        //Test 2 - Invalid, team 2 does not exist, it hence has no members.
        teamNo = 2;

        isValid = choosePlayerSharedViewModel.isValidTeam(context, teamNo, teams, true);
        assertFalse(isValid);

        //Test 3 - Invalid, playing solitaire and team has more than 1 member.
        teamNo = 1;
        teams = false;
        choosePlayerSharedViewModel.addPlayerToTeam(teamNo, TestData.MEMBER_1);
        choosePlayerSharedViewModel.addPlayerToTeam(teamNo, TestData.MEMBER_2);

        isValid = choosePlayerSharedViewModel.isValidTeam(context, teamNo, teams, true);
        assertFalse(isValid);

        //Test 4 - Valid, team 1 has 1 member and is playing solitaire.
        choosePlayerSharedViewModel.removePlayerFromTeam(teamNo, TestData.MEMBER_2);

        isValid = choosePlayerSharedViewModel.isValidTeam(context, teamNo, teams, true);
        assertTrue(isValid);

        //Test 5 - Valid, team 1 has 1 member and is playing with teams (coop or competitive)
        teams = true;

        isValid = choosePlayerSharedViewModel.isValidTeam(context, teamNo, teams, true);
        assertTrue(isValid);

        //Test 6 - Valid, team 1 has 2 members and is playing with teams
        choosePlayerSharedViewModel.addPlayerToTeam(teamNo, TestData.MEMBER_2);

        isValid = choosePlayerSharedViewModel.isValidTeam(context, teamNo, teams, true);
        assertTrue(isValid);
    }

    @Test
    public void testAreValidTeams() {
        int teamNo1 = 1;
        int position1 = 1;
        int teamNo2 = 2;
        int position2 = 2;
        PlayMode.PlayModeEnum playModePlayed = PlayMode.PlayModeEnum.COMPETITIVE;

        Context context = ApplicationProvider.getApplicationContext();

        //Test 1 - Invalid, competitive and no teams.
        boolean isValid = choosePlayerSharedViewModel.areValidTeams(context, playModePlayed, true);
        assertFalse(isValid);

        //Test 2 - Invalid, cooperative and no teams.
        playModePlayed = PlayMode.PlayModeEnum.COOPERATIVE;

        isValid = choosePlayerSharedViewModel.areValidTeams(context, playModePlayed, true);
        assertFalse(isValid);

        //Test 3 - Invalid, solitaire and no teams.
        playModePlayed = PlayMode.PlayModeEnum.SOLITAIRE;

        isValid = choosePlayerSharedViewModel.areValidTeams(context, playModePlayed, true);
        assertFalse(isValid);

        //Test 4 - Invalid, competitive and only 1 team.
        playModePlayed = PlayMode.PlayModeEnum.COMPETITIVE;
        choosePlayerSharedViewModel.createEmptyTeam(teamNo1, position1);
        choosePlayerSharedViewModel.addPlayerToTeam(teamNo1, TestData.MEMBER_1);

        isValid = choosePlayerSharedViewModel.areValidTeams(context, playModePlayed, true);
        assertFalse(isValid);

        //Test 5 - Valid, cooperative and only 1 team.
        playModePlayed = PlayMode.PlayModeEnum.COOPERATIVE;

        isValid = choosePlayerSharedViewModel.areValidTeams(context, playModePlayed, true);
        assertTrue(isValid);

        //Test 6 - Valid, solitaire and only 1 team.
        playModePlayed = PlayMode.PlayModeEnum.SOLITAIRE;

        isValid = choosePlayerSharedViewModel.areValidTeams(context, playModePlayed, true);
        assertTrue(isValid);

        //Test 7 - Invalid, solitaire and more than 1 team.
        choosePlayerSharedViewModel.createEmptyTeam(teamNo2, position2);
        choosePlayerSharedViewModel.addPlayerToTeam(teamNo2, TestData.MEMBER_2);

        isValid = choosePlayerSharedViewModel.areValidTeams(context, playModePlayed, true);
        assertFalse(isValid);

        //Test 8 - Invalid, cooperative and more than 1 team.
        playModePlayed = PlayMode.PlayModeEnum.COOPERATIVE;

        isValid = choosePlayerSharedViewModel.areValidTeams(context, playModePlayed, true);
        assertFalse(isValid);

        //Test 9 - Valid, competitive and more than 1 team.
        playModePlayed = PlayMode.PlayModeEnum.COMPETITIVE;

        isValid = choosePlayerSharedViewModel.areValidTeams(context, playModePlayed, true);
        assertTrue(isValid);
    }
}
