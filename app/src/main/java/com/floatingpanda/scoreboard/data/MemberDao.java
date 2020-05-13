package com.floatingpanda.scoreboard.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MemberDao {
    @Query("SELECT * FROM members")
    LiveData<List<Member>> getAll();

    @Query("SELECT * FROM members WHERE nickname LIKE :nickname")
    LiveData<Member> findLivedataByNickname(String nickname);

    @Query("SELECT * FROM members WHERE nickname LIKE :nickname")
    Member findNonLiveDataByNickname(String nickname);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Member... members);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(Member member);

    @Update
    void update(Member member);

    @Query ("DELETE FROM members")
    void deleteAll();

    @Delete
    void delete(Member member);
}
