package com.floatingpanda.scoreboard.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class GroupRepository {

    private GroupDao groupDao;
    private LiveData<List<Group>> allGroups;

    public GroupRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        GroupDao groupDao = db.groupDao();
        allGroups = groupDao.getAll();
    }

    public LiveData<List<Group>> getAllGroups() {
        return allGroups;
    }

    public void insert(Group group) {
        AppDatabase.getExecutorService().execute(() -> {
            groupDao.insert(group);
        });
    }
}
