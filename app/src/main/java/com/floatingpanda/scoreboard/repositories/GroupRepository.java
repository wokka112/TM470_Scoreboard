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

package com.floatingpanda.scoreboard.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.relations.GroupWithMembers;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.daos.GroupMemberDao;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.GroupMember;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * A repository primarily for accessing the groups and group members tables in the database, but
 * also with some minor access to the game records table.
 *
 **/
public class GroupRepository {

    private GroupDao groupDao;
    private GroupMemberDao groupMemberDao;
    private GameRecordDao gameRecordDao;
    private LiveData<List<Group>> allGroups;

    public GroupRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        groupDao = db.groupDao();
        groupMemberDao = db.groupMemberDao();
        gameRecordDao = db.gameRecordDao();
        allGroups = groupDao.getAll();
    }

    public GroupRepository(AppDatabase db) {
        groupDao = db.groupDao();
        groupMemberDao = db.groupMemberDao();
        gameRecordDao = db.gameRecordDao();
        allGroups = groupDao.getAll();
    }

    /**
     * Returns a list of all the groups in the database.
     */
    public LiveData<List<Group>> getAll() {
        return allGroups;
    }

    /**
     * Returns a livedata group from the database, determined by groupId.
     * @param groupId an int id identifying a group in the database
     * @return
     */
    public LiveData<Group> getGroupById(int groupId) { return groupDao.findLiveDataById(groupId); }

    /**
     * Returns a livedata group from the database along with the members belonging to said group.
     * The group is determined by groupId.
     * @param groupId an int id identifying a group in the database
     * @return
     */
    public LiveData<GroupWithMembers> getGroupWithMembersByGroupId(int groupId) { return groupDao.findGroupWithMembersById(groupId); }

    /**
     * Returns a livedata list of members from a specific group from the database. The group is
     * determined by groupId.
     * @param groupId an int id identifying a group in the database
     * @return
     */
    public LiveData<List<Member>> getGroupMembersByGroupId(int groupId) {
        return groupMemberDao.findMembersOfASpecificGroupByGroupId(groupId);
    }

    /**
     * Inserts a new Group into the database. If the Group already exists in the database, no new
     * group is inserted.
     *
     * group should have an id of 0 so Room can autogenerate an id for it.
     *
     * group should have a unique name, i.e. no Group should already exist in the database
     * with the same name as group.
     * @param group a Group with a unique name and an id of 0
     */
    public void insert(Group group) {
        AppDatabase.getExecutorService().execute(() -> {
            groupDao.insert(group);
        });
    }

    /**
     * Updates a group in the database.
     *
     * The updated group should have the same id as the original group.
     * The updated group should either have the same name as the original group, or another unique
     * name that does not exist in the database.
     * @param group
     */
    public void update(Group group) {
        AppDatabase.getExecutorService().execute(() -> {
            groupDao.update(group);
        });
    }

    /**
     * Deletes a group from the database.
     *
     * This also deletes all related group monthly scores, scores, game records, skill ratings,
     * player teams and players from the database.
     * @param group
     */
    public void delete(Group group) {
        AppDatabase.getExecutorService().execute(() -> {
            groupDao.delete(group);
        });
    }

    /**
     * Adds a member to a group in the database. In other words, adds a new entry to the group
     * members table in the database, representing a member who belongs to a group.
     * @param groupMember the group-member relation entry to add to the group members table
     */
    public void insertGroupMember(GroupMember groupMember) {
        AppDatabase.getExecutorService().execute(() -> {
            groupMemberDao.insert(groupMember);
        });
    }

    /**
     * Adds a list of members to a group in the database. In other words, a list of new entries to
     * the group members table in the database, representing a list of members who belong to a group.
     * @param groupMembers
     */
    public void insertGroupMembers(List<GroupMember> groupMembers) {
        AppDatabase.getExecutorService().execute(() -> {
            groupMemberDao.insertAll(groupMembers.toArray(new GroupMember[groupMembers.size()]));
        });
    }

    /**
     * Removes a member from a group in the database. In other words, removes an entry from the
     * group members table in the database.
     * @param groupMember
     */
    public void removeGroupMember(GroupMember groupMember) {
        AppDatabase.getExecutorService().execute(() -> {
            groupMemberDao.delete(groupMember);
        });
    }

    /**
     * Returns the number of games played by a specific group, i.e. the number of game records
     * associated with a specific group. The group is determined by groupId.
     * @param groupId an int id identifying a group in the database.
     * @return
     */
    public int getGamesPlayed(int groupId) {
        Future future = AppDatabase.getExecutorService().submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int gamesPlayed = gameRecordDao.getNoOfGameRecordsByGroupId(groupId);
                return gamesPlayed;
            };
        });

        try{
            return (Integer) future.get();
        } catch (Exception e) {
            Log.e("GroupRepo.java", "Error getting games played. Exception: " + e);
            return -1;
        }
    }

    /**
     * Returns the number of members belonging to a specific group, i.e. the number of entries in
     * the group members table in the database for that specific group. The group is identified by
     * groupId.
     * @param groupId
     * @return
     */
    public int getNoOfGroupMembers(int groupId) {
        Future future = AppDatabase.getExecutorService().submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int noOfMembers = groupMemberDao.getNoOfGroupMembersByGroupId(groupId);
                return noOfMembers;
            };
        });

        try{
            return (Integer) future.get();
        } catch (Exception e) {
            Log.e("GroupRepo.java", "Error getting number of group members. Exception: " + e);
            return -1;
        }
    }

    /**
     * Tests whether a group with the name, groupName, exists in the database already. If it does,
     * return true. Otherwise, returns false.
     * @param groupName
     * @return
     */
    public boolean containsGroupName(String groupName) {
        Future future = AppDatabase.getExecutorService().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return groupDao.containsGroup(groupName);
            }
        });

        try {
            return (Boolean) future.get();
        } catch (Exception e) {
            Log.e("GroupRepos.java", "Could not get future for contains. Exception: " + e);
            return true;
        }
    }
}
