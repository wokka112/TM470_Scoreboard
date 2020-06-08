package com.floatingpanda.scoreboard.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.floatingpanda.scoreboard.data.entities.GroupMember;

import java.util.List;

@Dao
public interface GroupMemberDao {
    @Query("SELECT * FROM group_members")
    LiveData<List<GroupMember>> getAll();

    //Used for testing purposes.
    @Query("SELECT * FROM group_members WHERE group_id LIKE :groupId")
    List<GroupMember> findNonLiveDataByGroupId(int groupId);

    //Used for testing purposes
    @Query("SELECT * FROM group_members WHERE member_id like :memberId")
    List<GroupMember> findNonLiveDataByMemberId(int memberId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(GroupMember... groupMembers);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(GroupMember groupMember);

    @Query ("DELETE FROM group_members")
    void deleteAll();

    @Delete
    void delete(GroupMember groupMember);
}
