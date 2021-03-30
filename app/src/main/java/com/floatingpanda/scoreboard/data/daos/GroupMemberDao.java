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

    @Query("SELECT * FROM members INNER JOIN group_members " +
            "WHERE group_members.group_id LIKE :groupId " +
            "AND group_members.member_id LIKE members.member_id " +
            "ORDER BY nickname")
    LiveData<List<Member>> findMembersOfASpecificGroupByGroupId(int groupId);

    @Query("SELECT * FROM members INNER JOIN group_members " +
            "WHERE group_members.group_id LIKE :groupId " +
            "AND group_members.member_id LIKE members.member_id")
    List<Member> findNonLiveMembersOfASpecificGroupByGroupId(int groupId);
}
