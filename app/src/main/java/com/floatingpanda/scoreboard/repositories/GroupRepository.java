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
     * @return live data list of all groups from the database
     */
    public LiveData<List<Group>> getAll() {
        return allGroups;
    }

    public LiveData<Group> getGroupById(int groupId) { return groupDao.findLiveDataById(groupId); }

    public LiveData<GroupWithMembers> getGroupWithMembersByGroupId(int groupId) { return groupDao.findGroupWithMembersById(groupId); }

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

    public void update(Group group) {
        AppDatabase.getExecutorService().execute(() -> {
            groupDao.update(group);
        });
    }

    public void delete(Group group) {
        AppDatabase.getExecutorService().execute(() -> {
            groupDao.delete(group);
        });
    }

    public void insertGroupMember(GroupMember groupMember) {
        AppDatabase.getExecutorService().execute(() -> {
            groupMemberDao.insert(groupMember);
        });
    }

    public void insertGroupMembers(List<GroupMember> groupMembers) {
        AppDatabase.getExecutorService().execute(() -> {
            groupMemberDao.insertAll(groupMembers.toArray(new GroupMember[groupMembers.size()]));
        });
    }

    public void removeGroupMember(GroupMember groupMember) {
        AppDatabase.getExecutorService().execute(() -> {
            groupMemberDao.delete(groupMember);
        });
    }

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
