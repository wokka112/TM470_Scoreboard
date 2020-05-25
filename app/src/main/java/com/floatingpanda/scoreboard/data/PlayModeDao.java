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

    @Query("SELECT * FROM play_modes WHERE bg_id LIKE :bgId")
    List<PlayMode> findNonLiveDataByBgId(int bgId);

    //Possibly useless.
    @Query("SELECT * FROM play_modes WHERE play_mode_enum like :playModeEnum")
    List<PlayMode> findNonLiveDataByPlayModeEnum(PlayMode.PlayModeEnum playModeEnum);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(PlayMode... playModes);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(PlayMode playMode);

    @Query ("DELETE FROM play_modes")
    void deleteAll();

    @Delete
    void delete(PlayMode playMode);
}
