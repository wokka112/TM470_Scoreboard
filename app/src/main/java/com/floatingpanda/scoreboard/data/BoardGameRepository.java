package com.floatingpanda.scoreboard.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class BoardGameRepository {

    private AssignedCategoryDao assignedCategoryDao;
    private BoardGameDao boardGameDao;
    private BgCategoryDao bgCategoryDao;
    private PlayModeDao playModeDao;
    private LiveData<List<BoardGame>> allBoardGames;
    private LiveData<List<AssignedCategory>> allAssignedCategories;
    private LiveData<List<BoardGameWithBgCategories>> allBoardGamesWithCategories;
    private LiveData<List<BoardGameWithBgCategoriesAndPlayModes>> allBoardGamesWithCategoriesAndPlayModes;

    public BoardGameRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);

        assignedCategoryDao = db.assignedCategoryDao();
        boardGameDao = db.boardGameDao();
        bgCategoryDao = db.bgCategoryDao();
        playModeDao = db.playModeDao();

        allBoardGames = boardGameDao.getAllLive();
        allAssignedCategories = assignedCategoryDao.getAll();
        allBoardGamesWithCategories = boardGameDao.getAllBoardGamesAndBgCategories();
        allBoardGamesWithCategoriesAndPlayModes = boardGameDao.getAllBgsAndCategoriesAndPlayModes();
    }

    public LiveData<List<BoardGameWithBgCategories>> getAllBoardGamesWithCategories() {
        return allBoardGamesWithCategories;
    }

    public LiveData<List<BoardGameWithBgCategoriesAndPlayModes>> getAllBoardGamesWithCategoriesAndPlayModes() {
        return allBoardGamesWithCategoriesAndPlayModes;
    }

    // Maybe make private and use only inside the repository? Won't work in main thread cause it accesses db.
    private BoardGame getBoardGame(String bgName) {
        return boardGameDao.findNonLiveDataByName(bgName);
    }

    public LiveData<BoardGameWithBgCategories> getLiveDataBoardGameAndCategories(BoardGame boardGame) {
        return boardGameDao.findBoardGameAndBgCategoriesById(boardGame.getId());
    }

    public LiveData<BoardGameWithBgCategoriesAndPlayModes> getLiveDataBgAndCategoriesAndPlayModes(BoardGame boardGame) {
        return boardGameDao.findBgAndCategoriesAndPlayModesById(boardGame.getId());
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

    //TODO add testing to ensure assigned categories with this bg are deleted.
    // Need to find how to add appropriate asserts for this really.
    public void delete(BoardGame boardGame) {
        AppDatabase.getExecutorService().execute(() -> {
            boardGameDao.delete(boardGame);
        });
    }

    public boolean contains(String bgName) {
        Future future = AppDatabase.getExecutorService().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                BoardGame databaseBoardGame = boardGameDao.findNonLiveDataByName(bgName);
                return databaseBoardGame != null;
            };
        });

        try {
            return (Boolean) future.get();
        } catch (Exception e) {
            Log.e("BoardGameRepos.java", "Exception: " + e);
            return false;
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

        int bgId = boardGameDao.findNonLiveDataByName(boardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().getBoardGame().getBgName()).getId();
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
