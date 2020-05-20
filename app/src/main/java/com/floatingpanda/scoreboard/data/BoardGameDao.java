package com.floatingpanda.scoreboard.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface BoardGameDao {
    @Query("SELECT * FROM boardgames")
    LiveData<List<BoardGame>> getAll();

    @Query("SELECT * FROM boardgames WHERE bg_name LIKE :bgName")
    LiveData<BoardGame> findByName(String bgName);

    //TODO maybe change this to find by id? Or add that in?
    @Query("SELECT * FROM boardgames WHERE bg_name LIKE :bgName")
    BoardGame findNonLiveDataByName(String bgName);

    @Query("SELECT * FROM boardgames WHERE bg_id LIKE :bgId")
    BoardGame findNonLiveDataById(int bgId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(BoardGame... boardGames);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(BoardGame boardGame);

    @Query ("DELETE FROM boardgames")
    void deleteAll();

    @Delete
    void delete(BoardGame boardGame);

    @Transaction
    @Query("SELECT * FROM boardgames")
    public LiveData<List<BoardGamesAndBgCategories>> getAllBoardGamesAndBgCategories();

    @Transaction
    @Query("SELECT * FROM boardgames WHERE bg_id LIKE :bgId")
    public LiveData<BoardGamesAndBgCategories> findBoardGameAndBgCategoriesById(int bgId);

    @Transaction
    @Query("SELECT * FROM boardgames")
    public LiveData<List<BgAndBgCategoriesAndPlayModes>> getAllBgsAndCategoriesAndPlayModes();

    @Transaction
    @Query("SELECT * FROM boardgames WHERE bg_id like :bgId")
    public LiveData<BgAndBgCategoriesAndPlayModes> findBgAndCategoriesAndPlayModesById(int bgId);
}
