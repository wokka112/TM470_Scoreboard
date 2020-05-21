package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGameRepository;

import java.util.List;

public class BoardGameViewModel extends AndroidViewModel {

    private BoardGameRepository boardGameRepository;
    private LiveData<List<BoardGameWithBgCategories>> allBoardGamesWithBgCategories;
    private LiveData<List<BoardGameWithBgCategoriesAndPlayModes>> allBoardGamesWithBgCategoriesAndPlayModes;

    public BoardGameViewModel(Application application) {
        super(application);
        boardGameRepository = new BoardGameRepository(application);
        allBoardGamesWithBgCategories = boardGameRepository.getAllBoardGamesWithCategories();
        allBoardGamesWithBgCategoriesAndPlayModes = boardGameRepository.getAllBoardGamesWithCategoriesAndPlayModes();
    }

    public LiveData<List<BoardGameWithBgCategories>> getAllBoardGamesWithBgCategories() { return allBoardGamesWithBgCategories; }

    public LiveData<List<BoardGameWithBgCategoriesAndPlayModes>> getAllBoardgamesWithBgCategoriesAndPlayModes() { return allBoardGamesWithBgCategoriesAndPlayModes; }

    public LiveData<BoardGameWithBgCategoriesAndPlayModes> getBoardGameWithBgCategoriesAndPlayModes(BoardGame boardGame) {
        return boardGameRepository.getLiveDataBgAndCategoriesAndPlayModes(boardGame);
    }

    // Preconditions: - boardGame does not exist in the database.
    // Postconditions: - boardGame is added to the database.
    //                 - if boardGame has categories assigned to it that exist in the database, assigned_categories
    //                    tables will be added linking boardGame with this categories.
    public void addBoardGame(BoardGame boardGame) { boardGameRepository.insert(boardGame); }

    public void editBoardGame(BoardGame originalBoardGame, BoardGame editedBoardGame) { boardGameRepository.update(originalBoardGame, editedBoardGame); }

    public void deleteBoardGame(BoardGame boardGame) { boardGameRepository.delete(boardGame); }
}
