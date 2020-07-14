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
