package com.floatingpanda.scoreboard.db;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.entities.GroupMember;
import com.floatingpanda.scoreboard.data.daos.GroupMemberDao;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.daos.MemberDao;

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
public class GroupMemberDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private GroupDao groupDao;
    private MemberDao memberDao;
    private GroupMemberDao groupMemberDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        groupDao = db.groupDao();
        memberDao = db.memberDao();
        groupMemberDao = db.groupMemberDao();

        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllGroupMembersWhenNoneInserted() throws InterruptedException {
        List<GroupMember> groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());

        assertTrue(groupMembers.isEmpty());
    }

    @Test
    public void getAllGroupMembersWhenInserted() throws InterruptedException {
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        List<GroupMember> groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());

        assertThat(groupMembers.size(), is(TestData.GROUP_MEMBERS.size()));
    }

    @Test
    public void getNonLiveDataByGroupIdWhenNoneInserted() throws InterruptedException {
        List<GroupMember> groupMembers = groupMemberDao.findNonLiveDataByGroupId(TestData.GROUP_MEMBER_3.getGroupId());

        assertTrue(groupMembers.isEmpty());
    }

    @Test
    public void getNonLiveDataByMemberIdWhenNoneInserted() throws InterruptedException {
        List<GroupMember> groupMembers = groupMemberDao.findNonLiveDataByMemberId(TestData.GROUP_MEMBER_1.getMemberId());

        assertTrue(groupMembers.isEmpty());
    }

    @Test
    public void getNonLiveGroupMemberByGroupIdWhenGroupMembersInserted() throws InterruptedException {
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));

        //Group assigned to group member 3 has 2 members assigned to it, hence size should be 2.
        List<GroupMember> groupMembers = groupMemberDao.findNonLiveDataByGroupId(TestData.GROUP_MEMBER_3.getGroupId());

        assertThat(groupMembers.size(), is(2));
    }

    @Test
    public void getNonLiveGroupMemberByMemberIdWhenGroupMembersInserted() throws InterruptedException {
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));

        //Member assigned to group member 1 has 2 groups it's assigned to, hence size should be 2.
        List<GroupMember> groupMembers = groupMemberDao.findNonLiveDataByMemberId(TestData.GROUP_MEMBER_1.getMemberId());

        assertThat(groupMembers.size(), is(2));
    }

    @Test
    public void insertAndDeleteAllGroupMembers() throws InterruptedException {
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        List<GroupMember> groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());

        assertThat(groupMembers.size(), is(TestData.GROUP_MEMBERS.size()));

        groupMemberDao.deleteAll();
        TimeUnit.MILLISECONDS.sleep(100);

        groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());

        assertTrue(groupMembers.isEmpty());
    }

    @Test
    public void insertAllGroupMembersAndDeleteSpecificGroupMember() throws  InterruptedException {
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));
        List<GroupMember> allGroupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());

        assertThat(allGroupMembers.size(), is(TestData.GROUP_MEMBERS.size()));

        //Group member 1's group has only 1 member assigned to it, so only 1 entry in group members.
        List<GroupMember> specificGroupMembers = groupMemberDao.findNonLiveDataByGroupId(TestData.GROUP_MEMBER_1.getGroupId());

        assertThat(specificGroupMembers.size(), is(1));

        groupMemberDao.delete(TestData.GROUP_MEMBER_1);
        TimeUnit.MILLISECONDS.sleep(100);

        allGroupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());

        assertThat(allGroupMembers.size(), is(TestData.GROUP_MEMBERS.size() - 1));

        specificGroupMembers = groupMemberDao.findNonLiveDataByGroupId(TestData.GROUP_MEMBER_1.getGroupId());

        assertTrue(specificGroupMembers.isEmpty());
    }

    @Test
    public void insertAllGroupMembersAndDeleteAGroup() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));

        List<GroupMember> allGroupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());

        assertThat(allGroupMembers.size(), is(TestData.GROUP_MEMBERS.size()));

        //Group 2 should be associated with only 1 member so should have only 1 entry in group members.
        List<GroupMember> specificGroupMembers = groupMemberDao.findNonLiveDataByGroupId(TestData.GROUP_2.getId());

        assertThat(specificGroupMembers.size(), is(1));

        groupDao.delete(TestData.GROUP_2);
        TimeUnit.MILLISECONDS.sleep(100);

        //Test it's not in group db.
        Group group = LiveDataTestUtil.getValue(groupDao.findLiveDataById(TestData.GROUP_2.getId()));
        assertNull(group);

        //Test it's associated group member entry is gone.
        allGroupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());

        assertThat(allGroupMembers.size(), is(TestData.GROUP_MEMBERS.size() - 1));

        specificGroupMembers = groupMemberDao.findNonLiveDataByGroupId(TestData.GROUP_2.getId());

        assertTrue(specificGroupMembers.isEmpty());
    }

    @Test
    public void insertAllGroupMembersAndDeleteAMember() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));

        List<GroupMember> allGroupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());

        assertThat(allGroupMembers.size(), is(TestData.GROUP_MEMBERS.size()));

        //Member 2 should be associated with only 1 group so should have only 1 entry in group members.
        List<GroupMember> specificGroupMembers = groupMemberDao.findNonLiveDataByMemberId(TestData.MEMBER_2.getId());

        assertThat(specificGroupMembers.size(), is(1));

        memberDao.delete(TestData.MEMBER_2);
        TimeUnit.MILLISECONDS.sleep(100);

        //Test it's not in member db.
        Member member = LiveDataTestUtil.getValue(memberDao.findLiveDataById(TestData.MEMBER_2.getId()));
        assertNull(member);

        //Test it's associated group member entry is gone.
        allGroupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());

        assertThat(allGroupMembers.size(), is(TestData.GROUP_MEMBERS.size() - 1));

        specificGroupMembers = groupMemberDao.findNonLiveDataByMemberId(TestData.MEMBER_2.getId());

        assertTrue(specificGroupMembers.isEmpty());
    }
}
