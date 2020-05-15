package com.floatingpanda.scoreboard.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Database;

import java.util.ArrayList;
import java.util.List;

public class BoardGameRepository {

    private AssignedCategoriesDao assignedCategoriesDao;
    private BoardGameDao boardGameDao;
    private BgCategoryDao bgCategoryDao;
    private LiveData<List<BoardGame>> allBoardGames;
    private LiveData<List<AssignedCategories>> allAssignedCategories;
    private LiveData<List<BoardGamesAndBgCategories>> allBgsAndCategories;

    public BoardGameRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);

        assignedCategoriesDao = db.assignedCategoriesDao();
        boardGameDao = db.boardGameDao();
        bgCategoryDao = db.bgCategoryDao();

        allBoardGames = boardGameDao.getAll();
        allAssignedCategories = assignedCategoriesDao.getAll();
        allBgsAndCategories = boardGameDao.getAllBoardGamesAndBgCategories();
    }

    public LiveData<List<BoardGamesAndBgCategories>> getAllBgsAndCategories() {
        return allBgsAndCategories;
    }

    // Maybe make private and use only inside the repository? Won't work in main thread cause it accesses db.
    public BoardGame getBoardGame(String bgName) {
        return boardGameDao.findNonLiveDataByName(bgName);
    }

    public LiveData<BoardGamesAndBgCategories> getLiveDataBoardGameAndCategories(BoardGame boardGame) {
        return boardGameDao.findBoardGameAndBgCategoriesById(boardGame.getId());
    }

    public void insert(BoardGame boardGame) {
        if (boardGame.getBgCategories().isEmpty()) {
            insertBoardGame(boardGame);
        } else {
            insertBoardGameWithCategories(boardGame);
        }
    }

    //TODO change this to just insert?
    //TODO change this to insert board game with categories list drawn from board game categories attribute.
    public void insertBoardGame(BoardGame boardGame) {
        AppDatabase.getExecutorService().execute(() -> {
            boardGameDao.insert(boardGame);
        });
    }

    public void insertAssignedCategories(List<AssignedCategories> assignedCategories) {
        AppDatabase.getExecutorService().execute(() -> {
            assignedCategoriesDao.insertAll(assignedCategories.toArray(new AssignedCategories[assignedCategories.size()]));
        });
    }

    //TODO create an insertAssignedCategories(BoardGame boardGame) method?

    // Preconditions: - boardGame does not exist in database.
    //                - boardGame has categories assigned to it.
    //                - the categories stored in boardGame's categories list exist in the database and have
    //                   the correct id assigned to them from the database.
    // Postconditions: - boardGame will exist in database.
    //                 - a set of assigned_categories tables linking boardGame with its categories will exist
    //                    in the table.
    public void insertBoardGameWithCategories(BoardGame boardGame) {
        AppDatabase.getExecutorService().execute(() -> {
            boardGameDao.insert(boardGame);

            //TODO move this code into a private helper method?
            int bgId = getBoardGame(boardGame.getBgName()).getId();
            List<AssignedCategories> assignedCategories = new ArrayList<>();
            for (BgCategory bgCategory : boardGame.getBgCategories()) {
                //if boardGame holds categories then they must have ids, or else this won't work.
                Log.w("BoardGameRepo.java", "bgCategory id: " + bgCategory.getId());
                assignedCategories.add(new AssignedCategories(bgId, bgCategory.getId()));
            }

            assignedCategoriesDao.insertAll(assignedCategories.toArray(new AssignedCategories[assignedCategories.size()]));
        });
    }

    /*
    private LiveData<List<BoardGameWithBGCategories>> allBoardGamesWithCategories;

    public BoardGameRepository(Application application) {
        ScoreBoardRoomDatabase db = ScoreBoardRoomDatabase.getDatabase(application);
        boardGameDao = db.boardGameDao();
        allBoardGamesWithCategories = boardGameDao.getBoardGamesWithBGCategories();
    }

    public LiveData<List<BoardGameWithBGCategories>> getAllBoardGamesWithCategories() {
        return allBoardGamesWithCategories;
    }

    /*
    public void insert(Member member) {
        ScoreBoardRoomDatabase.getExecutorService().execute(() -> {
            memberDao.insert(member);
        });
    }
     */
}
