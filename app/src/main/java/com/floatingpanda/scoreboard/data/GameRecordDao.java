package com.floatingpanda.scoreboard.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GameRecordDao {
    @Query("SELECT * FROM game_records")
    LiveData<List<GameRecord>> getAll();

    @Query("SELECT * FROM game_records WHERE record_id LIKE :recordId")
    LiveData<GameRecord> findLiveDataGameRecordByRecordId(int recordId);

    @Query("SELECT * FROM game_records WHERE group_id LIKE :groupId")
    LiveData<List<GameRecord>> findLiveDataGameRecordsByGroupId(int groupId);

    @Query("SELECT * FROM game_records WHERE bg_name LIKE :bgName")
    LiveData<List<GameRecord>> findLiveDataGameRecordsByBoardGameName(String bgName);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(GameRecord... gameRecords);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(GameRecord gameRecord);

    @Query ("DELETE FROM game_records")
    void deleteAll();

    @Delete
    void delete(GameRecord gameRecord);
}
