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

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

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
public class GroupViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private GroupDao groupDao;
    private GroupViewModel groupViewModel;

    @Mock
    Activity activity;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        groupDao = db.groupDao();
        groupViewModel = new GroupViewModel(ApplicationProvider.getApplicationContext(), db);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllGroupsWhenNoneInsert() throws InterruptedException {
        List<Group> groups = LiveDataTestUtil.getValue(groupViewModel.getAllGroups());

        assertTrue(groups.isEmpty());
    }

    @Test
    public void getAllGroupsWhenGroupsInserted() throws  InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        List<Group> groups = LiveDataTestUtil.getValue(groupViewModel.getAllGroups());

        assertThat(groups.size(), is(TestData.GROUPS.size()));
    }

    @Test
    public void getLiveDataGroupByIdWhenNoneInserted() throws InterruptedException {
        Group group = LiveDataTestUtil.getValue(groupViewModel.getLiveDataGroupById(TestData.GROUP_1.getId()));

        assertNull(group);
    }

    @Test
    public void getLiveDataGroupByIdWhenGroupsInserted() throws  InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        Group group = LiveDataTestUtil.getValue(groupViewModel.getLiveDataGroupById(TestData.GROUP_2.getId()));

        assertNotNull(group);
        assertThat(group, is(TestData.GROUP_2));
    }

    @Test
    public void testAddGroup() throws InterruptedException {
        List<Group> groups = LiveDataTestUtil.getValue(groupViewModel.getAllGroups());

        assertTrue(groups.isEmpty());

        groupViewModel.addGroup(TestData.GROUP_1);
        TimeUnit.MILLISECONDS.sleep(100);

        groups = LiveDataTestUtil.getValue(groupViewModel.getAllGroups());

        assertThat(groups.size(), is(1));
        assertThat(groups.get(0), is(TestData.GROUP_1));
    }

    @Test
    public void testEditGroup() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        Group group = LiveDataTestUtil.getValue(groupViewModel.getLiveDataGroupById(TestData.GROUP_2.getId()));

        assertNotNull(group);
        assertThat(group, is(TestData.GROUP_2));

        Group editedGroup = new Group(group);

        assertThat(editedGroup, is(TestData.GROUP_2));

        String groupName = "Changed";
        editedGroup.setGroupName(groupName);

        assertThat(editedGroup, is(not(TestData.GROUP_2)));
        assertThat(editedGroup.getId(), is(TestData.GROUP_2.getId()));

        groupViewModel.editGroup(editedGroup);
        TimeUnit.MILLISECONDS.sleep(100);

        group = LiveDataTestUtil.getValue(groupViewModel.getLiveDataGroupById(TestData.GROUP_2.getId()));

        assertThat(group, is(not(TestData.GROUP_2)));
        assertThat(group, is(editedGroup));
        assertThat(group.getId(), is(TestData.GROUP_2.getId()));
    }

    @Test
    public void testDeleteGroup() throws InterruptedException {
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        List<Group> groups = LiveDataTestUtil.getValue(groupViewModel.getAllGroups());

        assertThat(groups.size(), is(TestData.GROUPS.size()));
        assertTrue(groups.contains(TestData.GROUP_2));

        groupViewModel.deleteGroup(TestData.GROUP_2);
        TimeUnit.MILLISECONDS.sleep(100);

        groups = LiveDataTestUtil.getValue(groupViewModel.getAllGroups());

        assertThat(groups.size(), is(TestData.GROUPS.size() - 1));
        assertFalse(groups.contains(TestData.GROUP_2));
    }

    @Test
    public void testAddActivityInputsValid() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();
        EditText groupNameEditText = new EditText(context);

        //Test Case 1: Valid - String input that doesn't exist in database and is not empty
        String groupName = "Group name";
        groupNameEditText.setText(groupName);
        boolean isValid = groupViewModel.addActivityInputsValid(groupNameEditText, true);

        assertTrue(isValid);

        //Test Case 2: Invalid - empty String input
        groupName = "";
        groupNameEditText.setText(groupName);
        isValid = groupViewModel.addActivityInputsValid(groupNameEditText, true);

        assertFalse(isValid);

        //Test Case 3: Invalid - String input that already exists in category database
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        TimeUnit.MILLISECONDS.sleep(100);

        Group group = groupDao.findNonLiveDataByName(TestData.GROUP_2.getGroupName());

        assertTrue(group != null);

        groupName = TestData.GROUP_2.getGroupName();
        groupNameEditText.setText(groupName);
        isValid = groupViewModel.addActivityInputsValid(groupNameEditText, true);

        assertFalse(isValid);
    }

    @Test
    public void testEditActivityInputsValid() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();
        EditText groupNameEditText = new EditText(context);

        //Test Case 1: Valid - String input that is the same as the original group name
        String originalGroupName = "Original";
        String editedGroupName = "Original";
        groupNameEditText.setText(editedGroupName);
        boolean isValid = groupViewModel.editActivityInputsValid(originalGroupName, groupNameEditText, true);

        assertTrue(isValid);

        //Test Case 2: Valid - String input that doesn't exist in database and is not empty
        editedGroupName = "Group name";
        groupNameEditText.setText(editedGroupName);
        isValid = groupViewModel.editActivityInputsValid(originalGroupName, groupNameEditText, true);

        assertTrue(isValid);

        //Test Case 3: Invalid - empty String input
        editedGroupName = "";
        groupNameEditText.setText(editedGroupName);
        isValid = groupViewModel.editActivityInputsValid(originalGroupName, groupNameEditText, true);

        assertFalse(isValid);

        //Test Case 4: Invalid - String input that already exists in category database
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        TimeUnit.MILLISECONDS.sleep(100);

        Group group = groupDao.findNonLiveDataByName(TestData.GROUP_2.getGroupName());

        assertTrue(group != null);

        editedGroupName = TestData.GROUP_2.getGroupName();
        groupNameEditText.setText(editedGroupName);
        isValid = groupViewModel.editActivityInputsValid(originalGroupName, groupNameEditText, true);

        assertFalse(isValid);
    }
}
