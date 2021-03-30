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

import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayersAndRatingChanges;

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

    @Query("SELECT player_team_id FROM player_teams WHERE team_number LIKE :teamNumber AND record_id LIKE :recordId")
    int getNonLivePlayerTeamIdByTeamNumberAndRecordId(int teamNumber, int recordId);

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

    @Transaction
    @Query("SELECT * FROM player_teams")
    LiveData<List<PlayerTeamWithPlayersAndRatingChanges>> getAllPlayerTeamsWithPlayersAndRatingChanges();

    @Transaction
    @Query("SELECT * FROM player_teams WHERE player_team_id LIKE :playerTeamId ORDER BY position ASC, team_number ASC")
    LiveData<PlayerTeamWithPlayersAndRatingChanges> getPlayerTeamWithPlayersAndRatingChangesByPlayerTeamId(int playerTeamId);

    @Transaction
    @Query("SELECT * FROM player_teams WHERE record_id LIKE :recordId ORDER BY position ASC, team_number ASC")
    LiveData<List<PlayerTeamWithPlayersAndRatingChanges>> getPlayerTeamsWithPlayersAndRatingChangesByRecordId(int recordId);

    @Transaction
    @Query("SELECT * FROM player_teams WHERE record_id LIKE :recordId ORDER BY position ASC, team_number ASC")
    List<PlayerTeamWithPlayersAndRatingChanges> getNonLivePlayerTeamsWithPlayersAndRatingChangesByRecordId(int recordId);
}
