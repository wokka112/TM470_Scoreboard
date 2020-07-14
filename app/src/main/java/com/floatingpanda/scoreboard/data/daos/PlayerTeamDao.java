package com.floatingpanda.scoreboard.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;

import java.util.List;

@Dao
public interface PlayerTeamDao {
    @Query("SELECT * FROM player_teams")
    LiveData<List<PlayerTeam>> getAll();

    @Query("SELECT * FROM player_teams WHERE player_team_id LIKE :playerTeamId")
    LiveData<PlayerTeam> findLiveDataPlayerTeamByPlayerTeamId(int playerTeamId);

    @Query("SELECT * FROM player_teams WHERE record_id LIKE :recordId")
    LiveData<List<PlayerTeam>> findLiveDataPlayerTeamsByRecordId(int recordId);

    @Query("SELECT * FROM player_teams WHERE team_number LIKE :teamNumber AND record_id LIKE :recordId")
    LiveData<PlayerTeam> findLiveDataPlayerTeamByTeamNumberAndRecordId(int teamNumber, int recordId);

    @Query("SELECT * FROM player_teams WHERE team_number LIKE :teamNumber AND record_id LIKE :recordId")
    PlayerTeam findNonLiveDataPlayerTeamByTeamNumberAndRecordId(int teamNumber, int recordId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(PlayerTeam... playerTeams);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    long insert(PlayerTeam playerTeam);

    @Query ("DELETE FROM player_teams")
    void deleteAll();

    @Delete
    void delete(PlayerTeam playerTeam);

    @Transaction
    @Query("SELECT * FROM player_teams")
    public LiveData<List<PlayerTeamWithPlayers>> getAllPlayerTeamsWithPlayers();

    @Transaction
    @Query("SELECT * FROM player_teams WHERE player_team_id LIKE :playerTeamId")
    public LiveData<PlayerTeamWithPlayers> findPlayerTeamWithPlayersByPlayerTeamId(int playerTeamId);

    @Transaction
    @Query("SELECT * FROM player_teams WHERE record_id LIKE :recordId")
    public LiveData<List<PlayerTeamWithPlayers>> findPlayerTeamsWithPlayersByRecordId(int recordId);
}
