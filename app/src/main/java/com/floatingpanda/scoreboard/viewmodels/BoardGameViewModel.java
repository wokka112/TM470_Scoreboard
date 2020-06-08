package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGameRepository;

import java.util.List;

public class BoardGameViewModel extends AndroidViewModel {

    private BoardGameRepository boardGameRepository;
    private LiveData<List<BoardGameWithBgCategories>> allBoardGamesWithBgCategories;
    private LiveData<List<BoardGameWithBgCategoriesAndPlayModes>> allBoardGamesWithBgCategoriesAndPlayModes;

    public BoardGameViewModel(Application application) {
        super(application);
        boardGameRepository = new BoardGameRepository(application);
        allBoardGamesWithBgCategories = boardGameRepository.getAllBoardGamesWithBgCategories();
        allBoardGamesWithBgCategoriesAndPlayModes = boardGameRepository.getAllBoardGamesWithBgCategoriesAndPlayModes();
    }

    // Used for testing
    public BoardGameViewModel(Application application, AppDatabase db) {
        super(application);
        boardGameRepository = new BoardGameRepository(db);
        allBoardGamesWithBgCategories = boardGameRepository.getAllBoardGamesWithBgCategories();
        allBoardGamesWithBgCategoriesAndPlayModes = boardGameRepository.getAllBoardGamesWithBgCategoriesAndPlayModes();
    }

    public LiveData<List<BoardGameWithBgCategories>> getAllBoardGamesWithBgCategories() { return allBoardGamesWithBgCategories; }

    public LiveData<List<BoardGameWithBgCategoriesAndPlayModes>> getAllBoardGamesWithBgCategoriesAndPlayModes() { return allBoardGamesWithBgCategoriesAndPlayModes; }

    public LiveData<BoardGameWithBgCategoriesAndPlayModes> getBoardGameWithBgCategoriesAndPlayModes(BoardGame boardGame) {
        return boardGameRepository.getLiveDataBoardGameWithBgCategoriesAndPlayModes(boardGame.getId());
    }

    public void addBoardGameWithBgCategoriesAndPlayModes(BoardGameWithBgCategoriesAndPlayModes bgWithBgCategoriesAndPlayModes) {
        boardGameRepository.insert(bgWithBgCategoriesAndPlayModes);
    }

    public void editBoardGameWithBgCategoriesAndPlayModes(BoardGameWithBgCategoriesAndPlayModes originalBoardGameWithBgCategoriesAndPlayModes,
                                                          BoardGameWithBgCategoriesAndPlayModes editedBoardGameWithBgCategoriesAndPlayModes) {
        boardGameRepository.update(originalBoardGameWithBgCategoriesAndPlayModes, editedBoardGameWithBgCategoriesAndPlayModes);
    }

    public void deleteBoardGame(BoardGame boardGame) { boardGameRepository.delete(boardGame); }

    public void deleteBoardGameWithBgCategoriesAndPlayModes(BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes) {
        //Deletes cascade in database, so deleting the board game alone is enough.
        boardGameRepository.delete(boardGameWithBgCategoriesAndPlayModes.getBoardGame());
    }
}
