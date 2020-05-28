package com.floatingpanda.scoreboard.model;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.Group;
import com.floatingpanda.scoreboard.data.GroupDao;
import com.floatingpanda.scoreboard.data.GroupMember;
import com.floatingpanda.scoreboard.data.GroupMemberDao;
import com.floatingpanda.scoreboard.data.GroupWithMembers;
import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.data.MemberDao;
import com.floatingpanda.scoreboard.viewmodels.GroupMemberViewModel;
import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
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
public class GroupMemberViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private GroupDao groupDao;
    private MemberDao memberDao;
    private GroupMemberDao groupMemberDao;
    private GroupMemberViewModel groupMemberViewModel;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        groupDao = db.groupDao();
        memberDao = db.memberDao();
        groupMemberDao = db.groupMemberDao();
        groupMemberViewModel = new GroupMemberViewModel(ApplicationProvider.getApplicationContext(), db);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void initialiseAndGetGroupWithMembers() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));

        //Group member 3's group has 2 members.
        groupMemberViewModel.initGroupWithMembers(TestData.GROUP_MEMBER_3.getGroupId());

        GroupWithMembers groupWithMembers = LiveDataTestUtil.getValue(groupMemberViewModel.getGroupWithMembers());
        assertNotNull(groupWithMembers);
        assertThat(groupWithMembers.getMembers().size(), is(2));
    }

    @Test
    public void addGroupMember() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        Group group = LiveDataTestUtil.getValue(groupDao.findLiveDataById(TestData.GROUP_1.getId()));
        assertNotNull(group);

        Member member = LiveDataTestUtil.getValue(memberDao.findLiveDataById(TestData.MEMBER_2.getId()));
        assertNotNull(member);

        groupMemberViewModel.addGroupMember(group, member);
        TimeUnit.MILLISECONDS.sleep(100);

        List<GroupMember> groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());
        assertThat(groupMembers.size(), is(1));

        GroupMember groupMember = new GroupMember(group.getId(), member.getId());
        assertThat(groupMembers.get(0), is(groupMember));
    }

    @Test
    public void addGroupMembers() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        Group group = LiveDataTestUtil.getValue(groupDao.findLiveDataById(TestData.GROUP_1.getId()));
        List<Member> members = LiveDataTestUtil.getValue(memberDao.getAllLive());

        assertNotNull(group);
        assertThat(members.size(), is(TestData.MEMBERS.size()));

        List<GroupMember> groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());
        assertTrue(groupMembers.isEmpty());

        groupMemberViewModel.addGroupMembers(group, members);
        TimeUnit.MILLISECONDS.sleep(100);

        groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());
        assertThat(groupMembers.size(), is(3));

        for (GroupMember groupMember : groupMembers) {
            assertThat(groupMember.getGroupId(), is(group.getId()));
        }

        List<Integer> memberIds = new ArrayList<>();
        for (GroupMember groupMember : groupMembers) {
            memberIds.add(groupMember.getMemberId());
        }

        for (Member member : members) {
            assertTrue(memberIds.contains(member.getId()));
        }
    }

    @Test
    public void removeGroupMember() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));

        List<GroupMember> groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());
        assertThat(groupMembers.size(), is(TestData.GROUP_MEMBERS.size()));
        assertTrue(groupMembers.contains(TestData.GROUP_MEMBER_2));

        Group group = LiveDataTestUtil.getValue(groupDao.findLiveDataById(TestData.GROUP_MEMBER_2.getGroupId()));
        Member member = LiveDataTestUtil.getValue(memberDao.findLiveDataById(TestData.GROUP_MEMBER_2.getMemberId()));

        groupMemberViewModel.removeGroupMember(group, member);
        TimeUnit.MILLISECONDS.sleep(100);

        groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());
        assertThat(groupMembers.size(), is(TestData.GROUP_MEMBERS.size() - 1));
        assertFalse(groupMembers.contains(TestData.GROUP_MEMBER_2));
    }
}
