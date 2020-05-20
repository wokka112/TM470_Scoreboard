package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.BgAndBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGameRepository;
import com.floatingpanda.scoreboard.data.BoardGamesAndBgCategories;

import java.util.List;

public class BoardGameViewModel extends AndroidViewModel {

    private BoardGameRepository boardGameRepository;
    private LiveData<List<BgAndBgCategoriesAndPlayModes>> allBgsAndCategoriesAndPlayModes;

    public BoardGameViewModel(Application application) {
        super(application);
        boardGameRepository = new BoardGameRepository(application);
        allBgsAndCategoriesAndPlayModes = boardGameRepository.getAllBgsAndCategoriesAndPlayModes();
    }

    public LiveData<List<BgAndBgCategoriesAndPlayModes>> getAllBgsAndCategoriesAndPlayModes() { return allBgsAndCategoriesAndPlayModes; }

    public LiveData<BgAndBgCategoriesAndPlayModes> getLiveDataBoardGameAndCategoriesAndPlayModes(BoardGame boardGame) {
        return boardGameRepository.getLiveDataBgAndCategoriesAndPlayModes(boardGame);
    }

    // Preconditions: - boardGame does not exist in the database.
    // Postconditions: - boardGame is added to the database.
    //                 - if boardGame has categories assigned to it that exist in the database, assigned_categories
    //                    tables will be added linking boardGame with this categories.
    public void addBoardGame(BoardGame boardGame) { boardGameRepository.insert(boardGame); }

    public void deleteBoardGame(BoardGame boardGame) { boardGameRepository.delete(boardGame); }
}
