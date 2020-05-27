package com.floatingpanda.scoreboard.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class GroupRepository {

    private GroupDao groupDao;
    private LiveData<List<Group>> allGroups;

    public GroupRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        groupDao = db.groupDao();
        allGroups = groupDao.getAll();
    }

    public GroupRepository(AppDatabase db) {
        groupDao = db.groupDao();
        allGroups = groupDao.getAll();
    }

    /**
     * @return live data list of all groups from the database
     */
    public LiveData<List<Group>> getAll() {
        return allGroups;
    }

    public LiveData<Group> getGroupById(int groupId) { return groupDao.findLiveDataById(groupId); }

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

    //TODO make empty string an illegal argument as well?
    public boolean contains(String groupName) throws IllegalArgumentException {
        if(groupName == null) {
            throw new IllegalArgumentException("null groupName passed to contains method.");
        }

        Future future = AppDatabase.getExecutorService().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Group databaseGroup = groupDao.findNonLiveDataByName(groupName);
                return databaseGroup != null;
            };
        });

        try {
            return (Boolean) future.get();
        } catch (Exception e) {
            return false;
        }
    }
}
