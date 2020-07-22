package com.floatingpanda.scoreboard.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategoriesAndPlayModes;

import java.util.List;

@Dao
public interface BoardGameDao {
    @Query("SELECT * FROM boardgames ORDER BY bg_name")
    LiveData<List<BoardGame>> getAllLive();

    @Query("SELECT * FROM boardgames ORDER BY bg_name")
    List<BoardGame> getAllNonLive();

    @Query("SELECT * FROM boardgames WHERE bg_id LIKE :bgId")
    LiveData<BoardGame> findLiveDataById(int bgId);

    @Query("SELECT * FROM boardgames WHERE bg_name LIKE :bgName")
    LiveData<BoardGame> findLiveDataByName(String bgName);

    @Query("SELECT * FROM boardgames WHERE bg_name LIKE :bgName")
    BoardGame findNonLiveDataByName(String bgName);

    @Query("SELECT bg_id FROM boardgames WHERE bg_name LIKE :bgName")
    int findBoardGameIdByBoardGameName(String bgName);

    @Query("SELECT EXISTS(SELECT * FROM boardgames WHERE bg_name LIKE :bgName)")
    boolean containsBoardGame(String bgName);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(BoardGame... boardGames);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(BoardGame boardGame);

    @Update
    void update(BoardGame boardGame);

    @Query ("DELETE FROM boardgames")
    void deleteAll();

    @Delete
    void delete(BoardGame boardGame);

    @Transaction
    @Query("SELECT * FROM boardgames ORDER BY bg_name")
    public LiveData<List<BoardGameWithBgCategories>> getAllBoardGamesWithBgCategories();

    @Transaction
    @Query("SELECT * FROM boardgames WHERE bg_id LIKE :bgId")
    public LiveData<BoardGameWithBgCategories> findBoardGameWithBgCategoriesById(int bgId);

    @Transaction
    @Query("SELECT * FROM boardgames ORDER BY bg_name")
    public LiveData<List<BoardGameWithBgCategoriesAndPlayModes>> getAllBoardGamesWithBgCategoriesAndPlayModes();

    @Transaction
    @Query("SELECT * FROM boardgames WHERE bg_id like :bgId")
    public LiveData<BoardGameWithBgCategoriesAndPlayModes> findBoardGameWithBgCategoriesAndPlayModesById(int bgId);
}
