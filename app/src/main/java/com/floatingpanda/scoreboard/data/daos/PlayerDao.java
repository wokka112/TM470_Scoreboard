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

import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.data.relations.PlayerWithRatingChanges;

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

    @Query("SELECT player_id FROM players WHERE player_team_id LIKE :playerTeamId AND member_nickname LIKE :memberNickname")
    int getPlayerIdByPlayerTeamIdAndMemberNickname(int playerTeamId, String memberNickname);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Player... players);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Player player);

    @Query ("DELETE FROM players")
    void deleteAll();

    @Delete
    void delete(Player player);

    @Transaction
    @Query("SELECT * FROM players")
    LiveData<List<PlayerWithRatingChanges>> getAllPlayersWithRatingChanges();

    @Transaction
    @Query("SELECT * FROM players WHERE player_id LIKE :playerId")
    LiveData<PlayerWithRatingChanges> getPlayerWithRatingChangesByPlayerId(int playerId);
}
