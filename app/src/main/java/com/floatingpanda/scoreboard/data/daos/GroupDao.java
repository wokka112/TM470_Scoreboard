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

    @Query("SELECT EXISTS(SELECT * FROM groups WHERE group_name LIKE :groupName)")
    boolean containsGroup(String groupName);

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
