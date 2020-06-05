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
public interface PlayerTeamDao {
    @Query("SELECT * FROM player_teams")
    LiveData<List<PlayerTeam>> getAll();

    @Query("SELECT * FROM player_teams WHERE record_id LIKE :recordId")
    LiveData<List<PlayerTeam>> findLiveDataPlayerTeamsByRecordId(int recordId);

    @Query("SELECT * FROM player_teams WHERE team_number LIKE :teamNumber AND record_id LIKE :recordId")
    LiveData<PlayerTeam> findLiveDataPlayerTeamByTeamNumberAndRecordId(int teamNumber, int recordId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(PlayerTeam... playerTeams);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(PlayerTeam playerTeam);

    @Query ("DELETE FROM player_teams")
    void deleteAll();

    @Delete
    void delete(PlayerTeam playerTeam);
}
