package com.floatingpanda.scoreboard.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BoardGameDao {
    @Query("SELECT * FROM boardgames")
    LiveData<List<BoardGame>> getAllLive();

    @Query("SELECT * FROM boardgames")
    List<BoardGame> getAllNonLive();

    @Query("SELECT * FROM boardgames WHERE bg_id LIKE :bgId")
    LiveData<BoardGame> findLiveDataById(int bgId);

    @Query("SELECT * FROM boardgames WHERE bg_name LIKE :bgName")
    LiveData<BoardGame> findLiveDataByName(String bgName);

    //TODO maybe change this to find by id? Or add that in?
    @Query("SELECT * FROM boardgames WHERE bg_name LIKE :bgName")
    BoardGame findNonLiveDataByName(String bgName);

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
    @Query("SELECT * FROM boardgames")
    public LiveData<List<BoardGameWithBgCategories>> getAllBoardGamesAndBgCategories();

    @Transaction
    @Query("SELECT * FROM boardgames WHERE bg_id LIKE :bgId")
    public LiveData<BoardGameWithBgCategories> findBoardGameAndBgCategoriesById(int bgId);

    @Transaction
    @Query("SELECT * FROM boardgames")
    public LiveData<List<BoardGameWithBgCategoriesAndPlayModes>> getAllBgsAndCategoriesAndPlayModes();

    @Transaction
    @Query("SELECT * FROM boardgames WHERE bg_id like :bgId")
    public LiveData<BoardGameWithBgCategoriesAndPlayModes> findBgAndCategoriesAndPlayModesById(int bgId);
}
