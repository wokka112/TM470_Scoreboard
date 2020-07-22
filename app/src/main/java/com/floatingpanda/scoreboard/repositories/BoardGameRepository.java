package com.floatingpanda.scoreboard.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.daos.AssignedCategoryDao;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.daos.PlayModeDao;
import com.floatingpanda.scoreboard.data.entities.AssignedCategory;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.entities.PlayMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class BoardGameRepository {

    private AssignedCategoryDao assignedCategoryDao;
    private BoardGameDao boardGameDao;
    private PlayModeDao playModeDao;
    private LiveData<List<BoardGameWithBgCategories>> allBoardGamesWithCategories;
    private LiveData<List<BoardGameWithBgCategoriesAndPlayModes>> allBoardGamesWithCategoriesAndPlayModes;

    public BoardGameRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);

        assignedCategoryDao = db.assignedCategoryDao();
        boardGameDao = db.boardGameDao();
        playModeDao = db.playModeDao();

        allBoardGamesWithCategories = boardGameDao.getAllBoardGamesWithBgCategories();
        allBoardGamesWithCategoriesAndPlayModes = boardGameDao.getAllBoardGamesWithBgCategoriesAndPlayModes();
    }

    public BoardGameRepository(AppDatabase db) {
        assignedCategoryDao = db.assignedCategoryDao();
        boardGameDao = db.boardGameDao();
        playModeDao = db.playModeDao();

        allBoardGamesWithCategories = boardGameDao.getAllBoardGamesWithBgCategories();
        allBoardGamesWithCategoriesAndPlayModes = boardGameDao.getAllBoardGamesWithBgCategoriesAndPlayModes();
    }

    public LiveData<List<BoardGameWithBgCategories>> getAllBoardGamesWithBgCategories() {
        return allBoardGamesWithCategories;
    }

    public LiveData<List<BoardGameWithBgCategoriesAndPlayModes>> getAllBoardGamesWithBgCategoriesAndPlayModes() {
        return allBoardGamesWithCategoriesAndPlayModes;
    }

    public LiveData<BoardGameWithBgCategoriesAndPlayModes> getLiveDataBoardGameWithBgCategoriesAndPlayModes(int bgId) {
        return boardGameDao.findBoardGameWithBgCategoriesAndPlayModesById(bgId);
    }

    //Preconditions: playmodes is not empty.
    public void insert(BoardGame boardGame) {
        AppDatabase.getExecutorService().execute(() -> {
            insertBoardGameWithoutBgCategories(boardGame);
        });
    }

    public void insert(BoardGameWithBgCategories boardGameWithBgCategories) {
        AppDatabase.getExecutorService().execute(() -> {
            insertBoardGameWithBgCategories(boardGameWithBgCategories);
        });
    }

    public void insert(BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes) {
        AppDatabase.getExecutorService().execute(() -> {
            insertBoardGameWithBgCategoriesAndPlayModes(boardGameWithBgCategoriesAndPlayModes);
        });
    }

    public void update(BoardGameWithBgCategoriesAndPlayModes originalBoardGameWithBgCategoriesAndPlayModes,
                       BoardGameWithBgCategoriesAndPlayModes editedBoardGameWithBgCategoriesAndPlayModes) {
        AppDatabase.getExecutorService().execute(() -> {
            boardGameDao.update(editedBoardGameWithBgCategoriesAndPlayModes.getBoardGame());

            List<PlayMode> playModesToDelete = getPlayModesToDelete(originalBoardGameWithBgCategoriesAndPlayModes.getPlayModes(),
                    editedBoardGameWithBgCategoriesAndPlayModes.getPlayModes());
            deletePlayModes(playModesToDelete);

            List<AssignedCategory> assignedCategoriesToDelete = getAssignedCategoriesToDelete(editedBoardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().
                            getBoardGame().getId(),
                    originalBoardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().getBgCategories(),
                    editedBoardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().getBgCategories());
            deleteAssignedCategories(assignedCategoriesToDelete);

            insertPlayModes(editedBoardGameWithBgCategoriesAndPlayModes.getPlayModes());
            insertAssignedCategories(editedBoardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().getBoardGame().getId(),
                    editedBoardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().getBgCategories());
        });
    }

    public void delete(BoardGame boardGame) {
        AppDatabase.getExecutorService().execute(() -> {
            boardGameDao.delete(boardGame);
        });
    }

    public boolean containsBoardGameName(String bgName) {
        Future future = AppDatabase.getExecutorService().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return boardGameDao.containsBoardGame(bgName);
            }
        });

        try {
            return (Boolean) future.get();
        } catch (Exception e) {
            Log.e("BoardGameRepos.java", "Could not get future for contains. Exception: " + e);
            return true;
        }
    }

    private void insertBoardGameWithoutBgCategories(BoardGame boardGame) {
        boardGameDao.insert(boardGame);
    }

    // Preconditions: - boardGame does not exist in database.
    //                - boardGame has categories assigned to it.
    //                - the categories stored in boardGame's categories list exist in the database and have
    //                   the correct id assigned to them from the database.
    // Postconditions: - boardGame will exist in database.
    //                 - a set of assigned_categories tables linking boardGame with its categories will exist
    //                    in the table.
    private void insertBoardGameWithBgCategories(BoardGameWithBgCategories boardGameWithBgCategories) {
        insertBoardGameWithoutBgCategories(boardGameWithBgCategories.getBoardGame());

        int bgId = getBoardGameId(boardGameWithBgCategories.getBoardGame().getBgName());
        List<BgCategory> bgCategories = boardGameWithBgCategories.getBgCategories();
        List<AssignedCategory> assignedCategories = new ArrayList<>();
        for (BgCategory bgCategory : bgCategories) {
            assignedCategories.add(new AssignedCategory(bgId, bgCategory.getId()));
        }

        assignedCategoryDao.insertAll(assignedCategories.toArray(new AssignedCategory[assignedCategories.size()]));
    }

    private void insertBoardGameWithBgCategoriesAndPlayModes(BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes) {
        insertBoardGameWithBgCategories(boardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories());

        int bgId = getBoardGameId(boardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().getBoardGame().getBgName());
        List<PlayMode> playModes = boardGameWithBgCategoriesAndPlayModes.getPlayModes();

        for(int i = 0; i < playModes.size(); i++) {
            playModes.get(i).setBgId(bgId);
        }

        playModeDao.insertAll(playModes.toArray(new PlayMode[playModes.size()]));
    }

    private int getBoardGameId(String boardGameName) {
        BoardGame boardGame = boardGameDao.findNonLiveDataByName(boardGameName);
        return boardGame.getId();
    }

    private void insertPlayModes(List<PlayMode> playModes) {
        playModeDao.insertAll(playModes.toArray(new PlayMode[playModes.size()]));
    }

    private void insertAssignedCategories(int bgId, List<BgCategory> bgCategories) {
        List<AssignedCategory> assignedCategories = new ArrayList<>();
        for (BgCategory bgCategory : bgCategories) {
            //if boardGame holds categories then they must have ids, or else this won't work.
            Log.w("BoardGameRepo.java", "bgCategory id: " + bgCategory.getId());
            assignedCategories.add(new AssignedCategory(bgId, bgCategory.getId()));
        }

        assignedCategoryDao.insertAll(assignedCategories.toArray(new AssignedCategory[assignedCategories.size()]));
    }

    private List<PlayMode> getPlayModesToDelete(List<PlayMode> originalPlayModes, List<PlayMode> editedPlayModes) {
        List<PlayMode> playModesToDelete = new ArrayList<>(originalPlayModes);
        playModesToDelete.removeAll(editedPlayModes);

        return playModesToDelete;
    }

    private List<AssignedCategory> getAssignedCategoriesToDelete(int bgId, List<BgCategory> originalBgCategories, List<BgCategory> editedBgCategories) {
        // Compare and delete ones in original but not in edited
        List<BgCategory> bgCategories = new ArrayList<>(originalBgCategories);
        originalBgCategories.removeAll(editedBgCategories);

        List<AssignedCategory> assignedCategoriesToDelete = new ArrayList<>();

        for (BgCategory bgCategory : bgCategories) {
            assignedCategoriesToDelete.add(new AssignedCategory(bgId, bgCategory.getId()));
        }

        return assignedCategoriesToDelete;
    }

    private void deletePlayModes(List<PlayMode> playModes) {
        for (PlayMode playMode : playModes) {
            playModeDao.delete(playMode);
        }
    }

    private void deleteAssignedCategories(List<AssignedCategory> assignedCategories) {
        for (AssignedCategory assignedCategory : assignedCategories) {
            assignedCategoryDao.delete(assignedCategory);
        }
    }
}
