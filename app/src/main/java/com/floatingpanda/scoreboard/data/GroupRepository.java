package com.floatingpanda.scoreboard.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class GroupRepository {

    private GroupDao groupDao;
    private LiveData<List<Group>> allGroups;

    public GroupRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        groupDao = db.groupDao();
        allGroups = groupDao.getAll();
    }

    /**
     * @return live data list of all groups from the database
     */
    public LiveData<List<Group>> getAll() {
        return allGroups;
    }

    public LiveData<Group> getGroupById(int groupId) { return groupDao.findById(groupId); }

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
}
