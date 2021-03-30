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
import androidx.room.Update;

import com.floatingpanda.scoreboard.data.entities.Member;

import java.util.List;

@Dao
public interface MemberDao {
    /**
     * @return live data list of all members from the database.
     */
    @Query("SELECT * FROM members ORDER BY nickname")
    LiveData<List<Member>> getAllLive();

    @Query("SELECT * FROM members ORDER BY nickname")
    List<Member> getAllNonLive();

    /**
     * @param id the database id of a member
     * @return a live data member from the database
     */
    @Query("SELECT * FROM members WHERE member_id LIKE :id")
    LiveData<Member> findLiveDataById(int id);

    /**
     * @param nickname a member's nickname
     * @return a live data member from the database
     */
    @Query("SELECT * FROM members WHERE nickname LIKE :nickname")
    LiveData<Member> findLiveDataByNickname(String nickname);

    /**
     * @param nickname a member's nickname
     * @return a normal member object (not live data) from the database
     */
    @Query("SELECT * FROM members WHERE nickname LIKE :nickname")
    Member findNonLiveDataByNickname(String nickname);

    @Query("SELECT member_id FROM members WHERE nickname LIKE :nickname")
    int getMemberIdByMemberNickname(String nickname);

    @Query("SELECT EXISTS(SELECT * FROM members WHERE nickname LIKE :nickname)")
    boolean containsMember(String nickname);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Member... members);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(Member member);

    @Update
    void update(Member member);

    @Query ("DELETE FROM members")
    void deleteAll();

    @Delete
    void delete(Member member);
}
