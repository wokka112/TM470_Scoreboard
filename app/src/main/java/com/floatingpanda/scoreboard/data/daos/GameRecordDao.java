package com.floatingpanda.scoreboard.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.relations.GameRecordWithPlayerTeamsAndPlayers;

import java.util.List;

@Dao
public interface GameRecordDao {
    @Query("SELECT * FROM game_records")
    LiveData<List<GameRecord>> getAll();

    @Query("SELECT * FROM game_records WHERE record_id LIKE :recordId")
    LiveData<GameRecord> findLiveDataGameRecordByRecordId(int recordId);

    @Query("SELECT * FROM game_records WHERE group_id LIKE :groupId AND bg_name LIKE :bgName AND date_time LIKE :dateTimeStamp")
    GameRecord findNonLiveDataGameRecordByRecordId(int groupId, String bgName, Long dateTimeStamp);

    @Query("SELECT * FROM game_records WHERE group_id LIKE :groupId")
    LiveData<List<GameRecord>> findLiveDataGameRecordsByGroupId(int groupId);

    @Query("SELECT * FROM game_records WHERE bg_name LIKE :bgName")
    LiveData<List<GameRecord>> findLiveDataGameRecordsByBoardGameName(String bgName);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(GameRecord... gameRecords);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    long insert(GameRecord gameRecord);

    @Query ("DELETE FROM game_records")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM game_records WHERE group_id LIKE :groupId")
    int getNoOfGameRecordsByGroupId(int groupId);

    @Delete
    void delete(GameRecord gameRecord);

    @Transaction
    @Query("SELECT * FROM game_records ORDER BY date_time DESC")
    public LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> getAllGameRecordsWithPlayerTeamsAndPlayers();

    @Transaction
    @Query("SELECT * FROM game_records WHERE record_id LIKE :recordId")
    public LiveData<GameRecordWithPlayerTeamsAndPlayers> findGameRecordWithPlayerTeamsAndPlayersByRecordId(int recordId);

    @Transaction
    @Query("SELECT * FROM game_records WHERE group_id LIKE :groupId ORDER BY date_time DESC")
    public LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> findGameRecordsWithPlayerTeamsAndPlayersByGroupId(int groupId);
}
