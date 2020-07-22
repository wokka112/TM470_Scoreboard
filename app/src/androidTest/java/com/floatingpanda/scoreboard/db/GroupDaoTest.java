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
import com.floatingpanda.scoreboard.data.relations.GroupWithMembers;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.daos.MemberDao;

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
public class GroupDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private MemberDao memberDao;
    private GroupDao groupDao;
    private GroupMemberDao groupMemberDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        memberDao = db.memberDao();
        groupDao = db.groupDao();
        groupMemberDao = db.groupMemberDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getGroupsWhenNoneInserted() throws InterruptedException {
        List<Group> groups = LiveDataTestUtil.getValue(groupDao.getAll());

        assertTrue(groups.isEmpty());
    }

    @Test
    public void getGroupsWhenInserted() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        List<Group> groups = LiveDataTestUtil.getValue(groupDao.getAll());

        assertThat(groups.size(), is(TestData.GROUPS.size()));
    }

    @Test
    public void getLiveDataGroupByIdWhenNoneInserted() throws InterruptedException {
        Group group = LiveDataTestUtil.getValue(groupDao.findLiveDataById(TestData.GROUP_2.getId()));

        assertNull(group);
    }

    @Test
    public void getLiveDataGroupByIdWhenGroupsInserted() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        Group group = LiveDataTestUtil.getValue(groupDao.findLiveDataById(TestData.GROUP_2.getId()));

        assertNotNull(group);
        assertThat(group, is(TestData.GROUP_2));
    }

    @Test
    public void getNonLiveDataGroupByNameWhenNoneInserted() throws InterruptedException {
        Group group = LiveDataTestUtil.getValue(groupDao.findLiveDataById(TestData.GROUP_2.getId()));

        assertNull(group);
    }

    @Test
    public void getNonLiveDataGroupByNameWhenGroupsInserted() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        Group group = groupDao.findNonLiveDataByName(TestData.GROUP_2.getGroupName());

        assertNotNull(group);
        assertThat(group, is(TestData.GROUP_2));
    }

    @Test
    public void getGroupsWhenSpecificGroupInserted() throws InterruptedException {
        groupDao.insert(TestData.GROUP_1);
        List<Group> groups = LiveDataTestUtil.getValue(groupDao.getAll());

        assertThat(groups.size(), is(1));
        assertThat(groups.get(0), is(TestData.GROUP_1));
    }

    @Test
    public void getGroupsWhenSameGroupInsertedTwice() throws InterruptedException {
        groupDao.insert(TestData.GROUP_1);
        List<Group> groups = LiveDataTestUtil.getValue(groupDao.getAll());

        assertThat(groups.size(), is(1));
        assertThat(groups.get(0), is(TestData.GROUP_1));

        groupDao.insert(TestData.GROUP_1);
        TimeUnit.MILLISECONDS.sleep(100);

        groups = LiveDataTestUtil.getValue(groupDao.getAll());

        assertThat(groups.size(), is(not(2)));
        assertThat(groups.size(), is(1));
        assertThat(groups.get(0), is(TestData.GROUP_1));
    }

    @Test
    public void insertAllGroupsAndUpdateSpecificGroup() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        List<Group> groups = LiveDataTestUtil.getValue(groupDao.getAll());

        assertThat(groups.size(), is(TestData.GROUPS.size()));

        Group group = LiveDataTestUtil.getValue(groupDao.findLiveDataById(TestData.GROUP_2.getId()));

        assertNotNull(group);
        assertThat(group, is(TestData.GROUP_2));

        Group editedGroup = new Group(group);

        editedGroup.setGroupName("Changed");
        assertThat(editedGroup, is(not(group)));
        assertThat(editedGroup.getId(), is(group.getId()));
        assertThat(editedGroup.getGroupName(), is(not(group.getGroupName())));

        groupDao.update(editedGroup);
        TimeUnit.MILLISECONDS.sleep(100);

        groups = LiveDataTestUtil.getValue(groupDao.getAll());

        assertThat(groups.size(), is(TestData.GROUPS.size()));

        group = LiveDataTestUtil.getValue(groupDao.findLiveDataById(TestData.GROUP_2.getId()));

        assertNotNull(group);
        assertThat(group, is(not(TestData.GROUP_2)));
        assertThat(group, is(editedGroup));
    }

    @Test
    public void insertAndDeleteAllGroups() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        List<Group> groups = LiveDataTestUtil.getValue(groupDao.getAll());

        assertFalse(groups.isEmpty());
        assertThat(groups.size(), is(TestData.GROUPS.size()));

        groupDao.deleteAll();
        TimeUnit.MILLISECONDS.sleep(100);

        groups = LiveDataTestUtil.getValue(groupDao.getAll());

        assertTrue(groups.isEmpty());
    }

    @Test
    public void insertAllGroupsAndDeleteSpecificGroup() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        List<Group> groups = LiveDataTestUtil.getValue(groupDao.getAll());

        assertFalse(groups.isEmpty());
        assertThat(groups.size(), is(TestData.GROUPS.size()));
        assertTrue(groups.contains(TestData.GROUP_2));

        groupDao.delete(TestData.GROUP_2);
        TimeUnit.MILLISECONDS.sleep(100);

        groups = LiveDataTestUtil.getValue(groupDao.getAll());

        assertThat(groups.size(), is(TestData.GROUPS.size() - 1));
        assertFalse(groups.contains(TestData.GROUP_2));
    }

    @Test
    public void getLiveGroupWithMembersByGroupId() throws  InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));

        List<GroupMember> groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());
        assertThat(groupMembers.size(), is(TestData.GROUP_MEMBERS.size()));

        //group 3 should have 2 members - member 1 and member 3.
        GroupWithMembers groupWithMembers = LiveDataTestUtil.getValue(groupDao.findGroupWithMembersById(TestData.GROUP_3.getId()));

        assertNotNull(groupWithMembers);
        assertThat(groupWithMembers.getGroup(), is(TestData.GROUP_3));
        assertThat(groupWithMembers.getMembers().size(), is(2));

        List<Integer> memberIds = new ArrayList<>();
        for (Member member : groupWithMembers.getMembers()) {
            memberIds.add(member.getId());
        }

        assertThat(memberIds.size(), is(2));
        assertTrue(memberIds.contains(TestData.MEMBER_1.getId()));
        assertTrue(memberIds.contains(TestData.MEMBER_3.getId()));
    }

    @Test
    public void testContains() throws InterruptedException {
        boolean contains = groupDao.containsGroup(TestData.GROUP_1.getGroupName());
        assertFalse(contains);

        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        contains = groupDao.containsGroup(TestData.GROUP_1.getGroupName());
        assertTrue(contains);
    }
}
