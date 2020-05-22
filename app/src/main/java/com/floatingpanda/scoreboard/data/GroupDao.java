package com.floatingpanda.scoreboard.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GroupDao {
    @Query("SELECT * FROM groups")
    LiveData<List<Group>> getAll();

    @Query("SELECT * FROM groups WHERE group_id LIKE :groupId")
    LiveData<Group> findById(int groupId);

    @Query("SELECT * FROM groups WHERE group_name LIKE :groupName")
    LiveData<Group> findByName(String groupName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Group... groups);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(Group group);

    @Query ("DELETE FROM groups")
    void deleteAll();

    @Delete
    void delete(Group group);
}
