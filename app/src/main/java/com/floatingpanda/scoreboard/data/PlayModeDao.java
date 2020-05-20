package com.floatingpanda.scoreboard.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlayModeDao {
    @Query("SELECT * FROM play_modes")
    LiveData<List<PlayMode>> getAll();

    @Query("SELECT * FROM play_modes WHERE parent_bg_name LIKE :bgName")
    List<PlayMode> findByBgName(String bgName);

    @Query("SELECT * FROM play_modes WHERE play_mode_enum like :playModeEnum")
    List<PlayMode> findByPlayModeEnum(PlayMode.PlayModeEnum playModeEnum);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(PlayMode... playModes);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(PlayMode playMode);

    @Query ("DELETE FROM play_modes")
    void deleteAll();

    @Delete
    void delete(PlayMode playMode);
}
