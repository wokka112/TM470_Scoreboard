package com.floatingpanda.scoreboard.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.floatingpanda.scoreboard.data.entities.GroupMember;
import com.floatingpanda.scoreboard.data.entities.Member;

import java.util.List;

@Dao
public interface GroupMemberDao {
    @Query("SELECT * FROM group_members")
    LiveData<List<GroupMember>> getAll();

    //Used for testing purposes.
    @Query("SELECT * FROM group_members WHERE group_id LIKE :groupId")
    List<GroupMember> findNonLiveDataByGroupId(int groupId);

    //Used for testing purposes
    @Query("SELECT * FROM group_members WHERE member_id LIKE :memberId")
    List<GroupMember> findNonLiveDataByMemberId(int memberId);

    @Query("SELECT COUNT(*) FROM group_members WHERE group_id LIKE :groupId")
    int getNoOfGroupMembersByGroupId(int groupId);

    @Query("SELECT COUNT(*) FROM group_members WHERE member_id LIKE :memberId")
    int getNoOfGroupsMemberIsPartOfByMemberId(int memberId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(GroupMember... groupMembers);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(GroupMember groupMember);

    @Query ("DELETE FROM group_members")
    void deleteAll();

    @Delete
    void delete(GroupMember groupMember);

    @Query("SELECT * FROM members JOIN group_members " +
            "WHERE group_members.group_id LIKE :groupId " +
            "AND group_members.member_id LIKE members.member_id")
    public LiveData<List<Member>> findMembersOfASpecificGroupByGroupId(int groupId);

    @Query("SELECT * FROM members JOIN group_members " +
            "WHERE group_members.group_id LIKE :groupId " +
            "AND group_members.member_id LIKE members.member_id")
    public List<Member> findNonLiveMembersOfASpecificGroupByGroupId(int groupId);
}
