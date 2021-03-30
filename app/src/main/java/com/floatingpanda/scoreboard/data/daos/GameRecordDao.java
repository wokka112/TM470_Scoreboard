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
