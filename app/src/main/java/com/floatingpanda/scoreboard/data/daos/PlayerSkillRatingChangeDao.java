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

import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerSkillRatingChange;

import java.util.List;

@Dao
public interface PlayerSkillRatingChangeDao {
    @Query("SELECT * FROM player_skill_rating_changes")
    LiveData<List<PlayerSkillRatingChange>> getAll();

    @Query("SELECT * FROM player_skill_rating_changes WHERE player_skill_rating_change_id LIKE :playerSkillRatingChangeId")
    LiveData<PlayerSkillRatingChange> getPlayerSkillRatingChangeByPlayerSkillRatingChangeId(int playerSkillRatingChangeId);

    @Query("SELECT * FROM player_skill_rating_changes WHERE player_id LIKE :playerId")
    LiveData<List<PlayerSkillRatingChange>> getPlayerSkillRatingChangesByPlayerId(int playerId);

    @Query("SELECT * FROM player_skill_rating_changes WHERE player_id LIKE :playerId AND category_name LIKE :categoryName")
    LiveData<PlayerSkillRatingChange> getPlayerSkillRatingChangeByPlayerIdAndCategoryName(int playerId, String categoryName);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(PlayerSkillRatingChange... playerSkillRatingChanges);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PlayerSkillRatingChange playerSkillRatingChange);

    @Query ("DELETE FROM player_skill_rating_changes")
    void deleteAll();

    @Delete
    void delete(PlayerSkillRatingChange playerSkillRatingChange);
}
