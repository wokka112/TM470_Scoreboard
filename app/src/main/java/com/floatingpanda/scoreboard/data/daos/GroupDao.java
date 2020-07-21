package com.floatingpanda.scoreboard.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.relations.GroupWithMembers;

import java.util.List;

@Dao
public interface GroupDao {
    @Query("SELECT * FROM groups ORDER BY group_name")
    LiveData<List<Group>> getAll();

    @Query("SELECT * FROM groups WHERE group_id LIKE :groupId")
    LiveData<Group> findLiveDataById(int groupId);

    @Query("SELECT * FROM groups WHERE group_name LIKE :groupName")
    Group findNonLiveDataByName(String groupName);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Group... groups);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(Group group);

    @Query ("DELETE FROM groups")
    void deleteAll();

    @Update
    void update(Group group);

    @Delete
    void delete(Group group);

    @Transaction
    @Query("SELECT * FROM groups WHERE group_id LIKE :groupId")
    public LiveData<GroupWithMembers> findGroupWithMembersById(int groupId);
}
