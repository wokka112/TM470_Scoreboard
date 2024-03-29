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
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.entities.GroupMember;
import com.floatingpanda.scoreboard.data.daos.GroupMemberDao;
import com.floatingpanda.scoreboard.repositories.GroupRepository;
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
public class GroupRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private GroupDao groupDao;
    private MemberDao memberDao;
    private GroupMemberDao groupMemberDao;
    private GroupRepository groupRepository;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        groupDao = db.groupDao();
        memberDao = db.memberDao();
        groupMemberDao = db.groupMemberDao();
        groupRepository = new GroupRepository(db);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllGroupsWhenNoneInserted() throws  InterruptedException {
        List<Group> groups = LiveDataTestUtil.getValue(groupRepository.getAll());

        assertTrue(groups.isEmpty());
    }

    @Test
    public void getAllGroupsWhenInserted() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        List<Group> groups = LiveDataTestUtil.getValue(groupRepository.getAll());

        assertFalse(groups.isEmpty());
        assertThat(groups.size(), is(TestData.GROUPS.size()));
    }

    @Test
    public void getSpecificGroupById() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        List<Group> groups = LiveDataTestUtil.getValue(groupRepository.getAll());

        assertFalse(groups.isEmpty());
        assertThat(groups.size(), is(TestData.GROUPS.size()));

        Group group = LiveDataTestUtil.getValue(groupRepository.getGroupById(TestData.GROUP_2.getId()));

        assertNotNull(group);
        assertThat(group, is(TestData.GROUP_2));
    }

    @Test
    public void insertGroup() throws InterruptedException {
        List<Group> groups = LiveDataTestUtil.getValue(groupRepository.getAll());

        assertTrue(groups.isEmpty());

        groupDao.insert(TestData.GROUP_1);
        TimeUnit.MILLISECONDS.sleep(100);

        groups = LiveDataTestUtil.getValue(groupRepository.getAll());

        assertThat(groups.size(), is(1));
        assertThat(groups.get(0), is(TestData.GROUP_1));
    }

    @Test
    public void insertGroupsAndUpdateSpecificGroup() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        List<Group> groups = LiveDataTestUtil.getValue(groupRepository.getAll());

        assertThat(groups.size(), is(TestData.GROUPS.size()));

        Group group = LiveDataTestUtil.getValue(groupDao.findLiveDataById(TestData.GROUP_2.getId()));

        assertNotNull(group);
        assertThat(group, is(TestData.GROUP_2));

        Group editedGroup = new Group(group);
        assertThat(editedGroup, is(TestData.GROUP_2));

        editedGroup.setGroupName("Changed");

        assertThat(editedGroup, is(not(TestData.GROUP_2)));
        assertThat(editedGroup.getId(), is(TestData.GROUP_2.getId()));

        groupRepository.update(editedGroup);
        TimeUnit.MILLISECONDS.sleep(100);

        groups = LiveDataTestUtil.getValue(groupRepository.getAll());

        assertThat(groups.size(), is(TestData.GROUPS.size()));

        group = LiveDataTestUtil.getValue(groupDao.findLiveDataById(TestData.GROUP_2.getId()));

        assertNotNull(group);
        assertThat(group, is(not(TestData.GROUP_2)));
        assertThat(group, is(editedGroup));
    }

    @Test
    public void insertGroupsAndDeleteSpecificGroup() throws  InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        List<Group> groups = LiveDataTestUtil.getValue(groupRepository.getAll());

        assertThat(groups.size(), is(TestData.GROUPS.size()));
        assertTrue(groups.contains(TestData.GROUP_2));

        groupRepository.delete(TestData.GROUP_2);
        TimeUnit.MILLISECONDS.sleep(100);

        groups = LiveDataTestUtil.getValue(groupRepository.getAll());

        assertThat(groups.size(), is(TestData.GROUPS.size() - 1));
        assertFalse(groups.contains(TestData.GROUP_2));
    }

    @Test
    public void insertGroupMember() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        List<GroupMember> groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());
        assertTrue(groupMembers.isEmpty());

        groupRepository.insertGroupMember(TestData.GROUP_MEMBER_1);
        TimeUnit.MILLISECONDS.sleep(100);

        groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());
        assertThat(groupMembers.size(), is(1));
        assertThat(groupMembers.get(0), is(TestData.GROUP_MEMBER_1));
    }

    @Test
    public void insertGroupMembers() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        List<GroupMember> groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());
        assertTrue(groupMembers.isEmpty());

        List<GroupMember> groupMembersToAdd = new ArrayList<>();
        groupMembersToAdd.add(TestData.GROUP_MEMBER_1);
        groupMembersToAdd.add(TestData.GROUP_MEMBER_2);

        groupRepository.insertGroupMembers(groupMembersToAdd);
        TimeUnit.MILLISECONDS.sleep(100);

        groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());
        assertThat(groupMembers.size(), is(2));
        assertTrue(groupMembers.contains(TestData.GROUP_MEMBER_1));
        assertTrue(groupMembers.contains(TestData.GROUP_MEMBER_2));
    }

    @Test
    public void deleteGroupMember() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        groupMemberDao.insertAll(TestData.GROUP_MEMBERS.toArray(new GroupMember[TestData.GROUP_MEMBERS.size()]));

        List<GroupMember> groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());
        assertThat(groupMembers.size(), is(TestData.GROUP_MEMBERS.size()));
        assertTrue(groupMembers.contains(TestData.GROUP_MEMBER_2));

        groupRepository.removeGroupMember(TestData.GROUP_MEMBER_2);
        TimeUnit.MILLISECONDS.sleep(100);

        groupMembers = LiveDataTestUtil.getValue(groupMemberDao.getAll());
        assertThat(groupMembers.size(), is(TestData.GROUP_MEMBERS.size() - 1));
        assertFalse(groupMembers.contains(TestData.GROUP_MEMBER_2));
    }

    @Test
    public void testContains() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        List<Group> groups = LiveDataTestUtil.getValue(groupRepository.getAll());

        assertThat(groups.size(), is(TestData.GROUPS.size()));

        //Test Case 1: Contains - group name that exists in database entered.
        String groupName = TestData.GROUP_1.getGroupName();
        boolean contains = groupRepository.containsGroupName(groupName);

        assertTrue(contains);

        //Test Case 2: Does not contain - group name that doesn't exist in database entered.
        groupName = "Test group name";
        contains = groupRepository.containsGroupName(groupName);

        assertFalse(contains);

        //Test Case 3: Does not contain - empty group name entered.
        groupName = "";
        contains = groupRepository.containsGroupName(groupName);

        assertFalse(contains);
    }
}
