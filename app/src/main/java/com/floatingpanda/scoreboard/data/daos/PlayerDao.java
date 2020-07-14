package com.floatingpanda.scoreboard.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.floatingpanda.scoreboard.data.entities.Player;

import java.util.List;

@Dao
public interface PlayerDao {
    @Query("SELECT * FROM players")
    LiveData<List<Player>> getAll();

    @Query("SELECT * FROM players WHERE player_id LIKE :playerId")
    LiveData<Player> findLiveDataPlayerByPlayerId(int playerId);

    @Query("SELECT * FROM players WHERE player_team_id LIKE :playerTeamId")
    LiveData<List<Player>> findLiveDataPlayersByPlayerTeamId(int playerTeamId);

    @Query("SELECT * FROM players WHERE member_nickname LIKE :memberNickname")
    LiveData<List<Player>> findLiveDataPlayersByMemberNickname(String memberNickname);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Player... players);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Player player);

    @Query ("DELETE FROM players")
    void deleteAll();

    @Delete
    void delete(Player player);
}
