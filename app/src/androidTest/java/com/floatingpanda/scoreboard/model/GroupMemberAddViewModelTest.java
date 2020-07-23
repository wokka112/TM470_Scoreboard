package com.floatingpanda.scoreboard.model;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.daos.GroupMemberDao;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.viewmodels.GroupMemberAddViewModel;
import com.floatingpanda.scoreboard.viewmodels.GroupMemberViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class GroupMemberAddViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private MemberDao memberDao;
    private GroupMemberAddViewModel groupMemberAddViewModel;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        memberDao = db.memberDao();
        groupMemberAddViewModel = new GroupMemberAddViewModel(ApplicationProvider.getApplicationContext(), db);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllMembersLiveData() throws InterruptedException {
        List<Member> allMembersLiveData = LiveDataTestUtil.getValue(groupMemberAddViewModel.getAllMembersLiveData());
        assertTrue(allMembersLiveData.isEmpty());

        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        allMembersLiveData = LiveDataTestUtil.getValue(groupMemberAddViewModel.getAllMembersLiveData());
        assertThat(allMembersLiveData.size(), is(TestData.MEMBERS.size()));
    }

    @Test
    public void getAllMembersWithoutSetting() {
        List<Member> allMembers = groupMemberAddViewModel.getAllMembers();
        assertNull(allMembers);
    }

    @Test
    public void setAndGetAllMembers() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        List<Member> allMembersLiveData = LiveDataTestUtil.getValue(groupMemberAddViewModel.getAllMembersLiveData());
        assertThat(allMembersLiveData.size(), is(TestData.MEMBERS.size()));

        groupMemberAddViewModel.setAllMembers(allMembersLiveData);

        List<Member> allMembers = groupMemberAddViewModel.getAllMembers();
        assertThat(allMembers.size(), is(TestData.MEMBERS.size()));
    }

    @Test
    public void getGroupMembersWithoutSetting() {
        List<Member> groupMembers = groupMemberAddViewModel.getGroupMembers();
        assertNull(groupMembers);
    }

    @Test
    public void setAndGetGroupMembers() {
        List<Member> members = new ArrayList<>();
        members.add(TestData.MEMBER_1);
        members.add(TestData.MEMBER_2);

        groupMemberAddViewModel.setGroupMembers(members);

        List<Member> groupMembers = groupMemberAddViewModel.getGroupMembers();
        assertThat(groupMembers.size(), is(members.size()));
    }

    @Test
    public void getNonGroupMembersWithoutSettingAllMembersAndGroupMembers() {
        List<Member> nonGroupMembers = groupMemberAddViewModel.getNonGroupMembers();
        assertNull(nonGroupMembers);
    }

    @Test
    public void getNonGroupMembersWithoutSettingAllMembers() {
        List<Member> groupMembers = new ArrayList<>();
        groupMembers.add(TestData.MEMBER_1);
        groupMembers.add(TestData.MEMBER_2);

        groupMemberAddViewModel.setGroupMembers(groupMembers);

        List<Member> nonGroupMembers = groupMemberAddViewModel.getNonGroupMembers();
        assertNull(nonGroupMembers);
    }

    @Test
    public void getNonGroupMembersWithoutSettingGroupMembers() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        List<Member> allMembersLiveData = LiveDataTestUtil.getValue(groupMemberAddViewModel.getAllMembersLiveData());
        groupMemberAddViewModel.setAllMembers(allMembersLiveData);

        List<Member> nonGroupMembers = groupMemberAddViewModel.getNonGroupMembers();
        assertNull(nonGroupMembers);
    }

    @Test
    public void getNonGroupMembersAfterSettingAllMembersAndGroupMembers() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        List<Member> allMembersLiveData = LiveDataTestUtil.getValue(groupMemberAddViewModel.getAllMembersLiveData());
        groupMemberAddViewModel.setAllMembers(allMembersLiveData);

        List<Member> groupMembers = new ArrayList<>();
        groupMembers.add(TestData.MEMBER_1);
        groupMembers.add(TestData.MEMBER_2);

        groupMemberAddViewModel.setGroupMembers(groupMembers);

        List<Member> nonGroupMembers = groupMemberAddViewModel.getNonGroupMembers();
        assertThat(nonGroupMembers.size(), is(allMembersLiveData.size() - groupMembers.size()));
    }

    @Test
    public void getSelectedMembersWhenNoneAdded() {
        List<Member> selectedMembers = groupMemberAddViewModel.getSelectedMembers();
        assertTrue(selectedMembers.isEmpty());
    }

    @Test
    public void addSelectedMembersThenGetSelectedMembers() {
        groupMemberAddViewModel.addSelectedMember(TestData.MEMBER_1);
        groupMemberAddViewModel.addSelectedMember(TestData.MEMBER_2);

        List<Member> selectedMembers = groupMemberAddViewModel.getSelectedMembers();
        assertThat(selectedMembers.size(), is(2));
    }

    @Test
    public void addDuplicateMembersToSelectedMembersThenGetSelectedMembers() {
        groupMemberAddViewModel.addSelectedMember(TestData.MEMBER_1);
        groupMemberAddViewModel.addSelectedMember(TestData.MEMBER_2);
        groupMemberAddViewModel.addSelectedMember(TestData.MEMBER_1);

        List<Member> selectedMembers = groupMemberAddViewModel.getSelectedMembers();
        assertThat(selectedMembers.size(), is(2));
    }

    @Test
    public void addAndRemoveSelectedMembers() {
        groupMemberAddViewModel.addSelectedMember(TestData.MEMBER_1);
        groupMemberAddViewModel.addSelectedMember(TestData.MEMBER_2);

        List<Member> selectedMembers = groupMemberAddViewModel.getSelectedMembers();
        assertThat(selectedMembers.size(), is(2));

        groupMemberAddViewModel.removeSelectedMember(TestData.MEMBER_2);
        selectedMembers = groupMemberAddViewModel.getSelectedMembers();
        assertThat(selectedMembers.size(), is(1));
        assertThat(selectedMembers.get(0), is(TestData.MEMBER_1));
    }

    @Test
    public void addMemberToDatabase() throws InterruptedException {
        Member member = LiveDataTestUtil.getValue(memberDao.findLiveDataById(TestData.MEMBER_1.getId()));
        assertNull(member);

        memberDao.insert(TestData.MEMBER_1);

        member = LiveDataTestUtil.getValue(memberDao.findLiveDataById(TestData.MEMBER_1.getId()));
        assertThat(member, is(TestData.MEMBER_1));
    }
}
