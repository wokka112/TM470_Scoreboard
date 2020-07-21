package com.floatingpanda.scoreboard.viewmodels;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.utils.AlertDialogHelper;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.repositories.GroupRepository;

import java.util.List;
import java.util.concurrent.Future;

public class GroupViewModel extends AndroidViewModel {

    private GroupRepository groupRepository;
    private LiveData<List<Group>> allGroups;

    public GroupViewModel(Application application) {
        super(application);
        groupRepository = new GroupRepository(application);
        allGroups = groupRepository.getAll();
    }

    public GroupViewModel(Application application, AppDatabase db) {
        super(application);
        groupRepository = new GroupRepository(db);
        allGroups = groupRepository.getAll();
    }

    /**
     * @return live data list of all groups from the database
     */
    public LiveData<List<Group>> getAllGroups() { return allGroups; }

    public LiveData<Group> getLiveDataGroupById(int groupId) { return groupRepository.getGroupById(groupId); }

    public int getGamesPlayedByGroup(int groupId) {
        return groupRepository.getGamesPlayed(groupId);
    }

    public int getNoOfMembersInGroup(int groupId) {
        return groupRepository.getNoOfGroupMembers(groupId);
    }

    // Precondition: Group with group's name or id should not exist in database.
    // Postcondition: new Group exists in the database.
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
    public void addGroup(Group group) { groupRepository.insert(group); }

    public void editGroup(Group group) { groupRepository.update(group); }

    public void deleteGroup(Group group) { groupRepository.delete(group); }

    public boolean addActivityInputsValid(Activity activity, String groupName, boolean testing) {
        return editActivityInputsValid(activity, "", groupName, testing);
    }

    public boolean editActivityInputsValid(Activity activity, String originalGroupName, String groupName, boolean testing) {
        if (groupName.isEmpty()) {
            if(!testing) {
                AlertDialogHelper.popupWarning("You must enter a name for the group.", activity);
            }
            return false;
        }

        if (!groupName.equals(originalGroupName)
                && groupRepository.contains(groupName)) {
            if(!testing) {
                AlertDialogHelper.popupWarning("You must enter a unique name for the group", activity);
            }
            return false;
        }

        return true;
    }
}
