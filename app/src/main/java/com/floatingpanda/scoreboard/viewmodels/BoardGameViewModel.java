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

package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.repositories.BoardGameRepository;

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
