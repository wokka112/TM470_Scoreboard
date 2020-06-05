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
public interface PlayerDao {
    @Query("SELECT * FROM players")
    LiveData<List<Player>> getAll();

    @Query("SELECT * FROM players WHERE player_id LIKE :playerId")
    LiveData<Player> findLiveDataPlayerByPlayerId(int playerId);

    @Query("SELECT * FROM players WHERE team_number LIKE :teamNumber AND record_id LIKE :recordId")
    LiveData<List<Player>> findLiveDataPlayersByTeamNumberAndRecordId(int teamNumber, int recordId);

    @Query("SELECT * FROM players WHERE record_id LIKE :recordId")
    LiveData<List<Player>> findLiveDataPlayersByRecordId(int recordId);

    @Query("SELECT * FROM players WHERE member_nickname LIKE :memberNickname")
    LiveData<List<Player>> findLiveDataPlayersByMemberNickname(String memberNickname);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Player... players);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(Player player);

    @Query ("DELETE FROM players")
    void deleteAll();

    @Delete
    void delete(Player player);
}
