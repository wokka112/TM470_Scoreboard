package com.floatingpanda.scoreboard.data;

import android.app.Application;

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
        allBgsAndCategories = boardGameDao.getBoardGamesAndBgCategories();
    }

    public LiveData<List<BoardGamesAndBgCategories>> getAllBgsWithCategories() {
        return allBgsAndCategories;
    }

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

    public void insertBgWithCategories(BoardGame boardGame, List<BgCategory> categories) {
        insertBoardGame(boardGame);

        String bgName = boardGame.getBgName();
        List<AssignedCategories> assignedCategories = new ArrayList<>();
        for (BgCategory category : categories) {
            assignedCategories.add(new AssignedCategories(bgName, category.getId()));
        }
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
