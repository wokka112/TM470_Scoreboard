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

    //TODO add test
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
