package com.floatingpanda.scoreboard.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

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

        assignedCategoryDao = db.assignedCategoriesDao();
        boardGameDao = db.boardGameDao();
        bgCategoryDao = db.bgCategoryDao();
        playModeDao = db.playModeDao();

        allBoardGames = boardGameDao.getAll();
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
            boardGameDao.insert(boardGame);

            insertPlayModes(boardGame.getBgName(), boardGame.getPlayModes());

            if (!boardGame.getBgCategories().isEmpty()) {
                insertAssignedCategories(boardGame.getId(), boardGame.getBgCategories());
            }
        });
    }

    public void update(BoardGame originalBoardGame, BoardGame editedBoardGame) {
        AppDatabase.getExecutorService().execute(() -> {
            boardGameDao.update(editedBoardGame);

            List<PlayMode> playModesToDelete = getPlayModesToDelete(editedBoardGame.getBgName(), originalBoardGame.getPlayModes(),
                    editedBoardGame.getPlayModes());
            List<AssignedCategory> assignedCategoriesToDelete = getAssignedCategoriesToDelete(editedBoardGame.getId(),
                    originalBoardGame.getBgCategories(), editedBoardGame.getBgCategories());

            deletePlayModes(playModesToDelete);
            deleteAssignedCategories(assignedCategoriesToDelete);

            insertPlayModes(editedBoardGame.getBgName(), editedBoardGame.getPlayModes());
            insertAssignedCategories(editedBoardGame.getId(), editedBoardGame.getBgCategories());
        });
    }

    //TODO add testing to ensure assigned categories with this bg are deleted.
    // Need to find how to add appropriate asserts for this really.
    public void delete(BoardGame boardGame) {
        AppDatabase.getExecutorService().execute(() -> {
            //TODO remove log messages once done. They are for testing.
            List<AssignedCategory> assignedCategories = assignedCategoryDao.findByBgId(boardGame.getId());

            for (int i = 0; i < assignedCategories.size(); i++) {
                Log.w("BGRepos.java", "1 Bg: " + boardGame.getId() + ", AC: " + assignedCategories.get(i).getCategoryId());
            }

            boardGameDao.delete(boardGame);

            assignedCategories = assignedCategoryDao.findByBgId(boardGame.getId());

            if (assignedCategories.isEmpty()) {
                Log.w("BGRepos.java", "2 Empty");
            } else {
                for (int i = 0; i < assignedCategories.size(); i++) {
                    Log.w("BGRepos.java", "2 Bg: " + boardGame.getId() + ", AC: " + assignedCategories.get(i).getCategoryId());
                }
            }
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

    //TODO remove these.
    private void insertBoardGameWithoutCategories(BoardGame boardGame) {
        AppDatabase.getExecutorService().execute(() -> {
            boardGameDao.insert(boardGame);

            insertPlayModes(boardGame.getBgName(), boardGame.getPlayModes());
        });
    }

    // Preconditions: - boardGame does not exist in database.
    //                - boardGame has categories assigned to it.
    //                - the categories stored in boardGame's categories list exist in the database and have
    //                   the correct id assigned to them from the database.
    // Postconditions: - boardGame will exist in database.
    //                 - a set of assigned_categories tables linking boardGame with its categories will exist
    //                    in the table.
    private void insertBoardGameWithCategories(BoardGame boardGame) {
        AppDatabase.getExecutorService().execute(() -> {
            boardGameDao.insert(boardGame);

            insertPlayModes(boardGame.getBgName(), boardGame.getPlayModes());
            insertAssignedCategories(boardGame.getId(), boardGame.getBgCategories());
        });
    }

    private void insertPlayModes(String bgName, List<PlayMode.PlayModeEnum> playModes) {
        for (PlayMode.PlayModeEnum playModeEnum : playModes) {
            playModeDao.insert(new PlayMode(bgName, playModeEnum));
        }
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

    private List<PlayMode> getPlayModesToDelete(String editedBgName, List<PlayMode.PlayModeEnum> originalPlayModes, List<PlayMode.PlayModeEnum> editedPlayModes) {
        List<PlayMode.PlayModeEnum> playModeEnums = new ArrayList<>(originalPlayModes);
        playModeEnums.removeAll(editedPlayModes);

        List<PlayMode> playModesToDelete = new ArrayList<>();

        for (PlayMode.PlayModeEnum playModeEnum : playModeEnums) {
            playModesToDelete.add(new PlayMode(editedBgName, playModeEnum));
        }

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
