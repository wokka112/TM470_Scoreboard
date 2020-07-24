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

/**
 * A repository for accessing the board games table, assigned categories table, and play modes table
 * in the database.
 */
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

    /**
     * Returns all board games in the database along with their assigned categories.
     * @return
     */
    public LiveData<List<BoardGameWithBgCategories>> getAllBoardGamesWithBgCategories() {
        return allBoardGamesWithCategories;
    }

    /**
     * Returns a livedata list of all the board games in the database along with their assigned
     * categories and potential playmodes.
     * @return
     */
    public LiveData<List<BoardGameWithBgCategoriesAndPlayModes>> getAllBoardGamesWithBgCategoriesAndPlayModes() {
        return allBoardGamesWithCategoriesAndPlayModes;
    }

    /**
     * Returns a livedata board game with id bgId from the database, along with its assigned
     * categories and potential playmodes.
     * @param bgId
     * @return
     */
    public LiveData<BoardGameWithBgCategoriesAndPlayModes> getLiveDataBoardGameWithBgCategoriesAndPlayModes(int bgId) {
        return boardGameDao.findBoardGameWithBgCategoriesAndPlayModesById(bgId);
    }

    /**
     * Inserts a new board game, boardGame, into the database.
     *
     * boardGame should have an id of 0, because the database autogenerates ids.
     * boardGame should have a unique game name, otherwise it will not insert.
     * @param boardGame
     */
    public void insert(BoardGame boardGame) {
        AppDatabase.getExecutorService().execute(() -> {
            insertBoardGameWithoutBgCategories(boardGame);
        });
    }

    /**
     * Inserts a new board game with assigned categories into the database.
     *
     * The board game should have an id of 0, because the database autogenerates ids.
     * The board game should have a unique game name, otherwise it will not insert.
     * The assigned categories should already exist in the database and the correct id for each
     * category should be provided in the list of assigned categories.
     * @param boardGameWithBgCategories
     */
    public void insert(BoardGameWithBgCategories boardGameWithBgCategories) {
        AppDatabase.getExecutorService().execute(() -> {
            insertBoardGameWithBgCategories(boardGameWithBgCategories);
        });
    }

    /**
     * Inserts a new board game with assigned categories and potential playmodes into the database.
     *
     * The board game should have an id of 0, because the database autogenerates ids.
     * The board game should have a unique game name, otherwise it will not insert.
     * The assigned categories should already exist in the database and the correct id for each
     * category should be provided in the list of assigned categories.
     * The playmodes list should have at least one potential playmode in it.
     * @param boardGameWithBgCategoriesAndPlayModes
     */
    public void insert(BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes) {
        AppDatabase.getExecutorService().execute(() -> {
            insertBoardGameWithBgCategoriesAndPlayModes(boardGameWithBgCategoriesAndPlayModes);
        });
    }

    /**
     * Updates a board game that already exists in the database, as well as its assigned categories
     * and potential playmodes.
     *
     * The updated board game should have the original board game's id, ids cannot be updated.
     * The updated board game should have the original board game's name, or a unique name.
     * The assigned categories should already exist in the database.
     *
     * Any assigned categories or playmodes that have been removed from the updated version of the
     * board game will result in entries in the assigned categories and play modes tables being
     * deleted.
     * @param originalBoardGameWithBgCategoriesAndPlayModes the original board game with assigned categories and potential play modes
     * @param editedBoardGameWithBgCategoriesAndPlayModes the updated board game with updated lists of assigned categories and potential play modes.
     */
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

    /**
     * Deletes the board game, boardGame, from the database, along with any assigned categories table
     * entries and play modes table entries it is a part of. Any game record referring to the board
     * game's name will have the name turned to null.
     * @param boardGame
     */
    public void delete(BoardGame boardGame) {
        AppDatabase.getExecutorService().execute(() -> {
            boardGameDao.delete(boardGame);
        });
    }

    /**
     * Tests whether the database contains a board game with the name bgName. If it does, returns
     * true. Otherwise, returns false.
     * @param bgName
     * @return
     */
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

    /**
     * Inserts a new board game then inserts a new set of assigned category table entries to represent
     * the categories the board game belongs to.
     * @param boardGameWithBgCategories
     */
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

    /**
     * Inserts a new board game, then inserts a new set of assigned category table entries to
     * represent the categories the board game belongs to, and then inserts a new set of play mode
     * table entries to represent the play modes the board game can be played int.
     * @param boardGameWithBgCategoriesAndPlayModes
     */
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
            assignedCategories.add(new AssignedCategory(bgId, bgCategory.getId()));
        }

        assignedCategoryDao.insertAll(assignedCategories.toArray(new AssignedCategory[assignedCategories.size()]));
    }

    /**
     * Takes the play modes that exist in the list of original play modes but not in the edited list
     * of play modes, and creates a list of PlayModes to delete from the database.
     * @param originalPlayModes the board game to be updated's original play modes
     * @param editedPlayModes the board game to be updated's new play modes
     * @return a list of PlayModes ready to delete from the database
     */
    private List<PlayMode> getPlayModesToDelete(List<PlayMode> originalPlayModes, List<PlayMode> editedPlayModes) {
        List<PlayMode> playModesToDelete = new ArrayList<>(originalPlayModes);
        playModesToDelete.removeAll(editedPlayModes);

        return playModesToDelete;
    }

    /**
     * Takes the categories that exist in the list of original bg categories but not in edited list
     * of bg categories, and creates a list of assigned categories to delete from the database.
     * @param bgId the board game to be updated's id
     * @param originalBgCategories the board game to be updated's original bg categories
     * @param editedBgCategories the board game to be updated's new bg categories
     * @return a list of assigned categories ready to delete from the database
     */
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
