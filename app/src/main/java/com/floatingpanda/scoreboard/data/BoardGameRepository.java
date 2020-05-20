package com.floatingpanda.scoreboard.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class BoardGameRepository {

    private AssignedCategoriesDao assignedCategoriesDao;
    private BoardGameDao boardGameDao;
    private BgCategoryDao bgCategoryDao;
    private PlayModeDao playModeDao;
    private AssignedCategoriesDao acDao; // Used for testing purposes.
    private LiveData<List<BoardGame>> allBoardGames;
    private LiveData<List<AssignedCategories>> allAssignedCategories;
    private LiveData<List<BoardGamesAndBgCategories>> allBgsAndCategories;
    private LiveData<List<BgAndBgCategoriesAndPlayModes>> allBgsAndCategoriesAndPlayModes;

    public BoardGameRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);

        assignedCategoriesDao = db.assignedCategoriesDao();
        boardGameDao = db.boardGameDao();
        bgCategoryDao = db.bgCategoryDao();
        acDao = db.assignedCategoriesDao();
        playModeDao = db.playModeDao();

        allBoardGames = boardGameDao.getAll();
        allAssignedCategories = assignedCategoriesDao.getAll();
        allBgsAndCategories = boardGameDao.getAllBoardGamesAndBgCategories();
        allBgsAndCategoriesAndPlayModes = boardGameDao.getAllBgsAndCategoriesAndPlayModes();
    }

    public LiveData<List<BoardGamesAndBgCategories>> getAllBgsAndCategories() {
        return allBgsAndCategories;
    }

    public LiveData<List<BgAndBgCategoriesAndPlayModes>> getAllBgsAndCategoriesAndPlayModes() {
        return allBgsAndCategoriesAndPlayModes;
    }

    // Maybe make private and use only inside the repository? Won't work in main thread cause it accesses db.
    private BoardGame getBoardGame(String bgName) {
        return boardGameDao.findNonLiveDataByName(bgName);
    }

    public LiveData<BoardGamesAndBgCategories> getLiveDataBoardGameAndCategories(BoardGame boardGame) {
        return boardGameDao.findBoardGameAndBgCategoriesById(boardGame.getId());
    }

    public LiveData<BgAndBgCategoriesAndPlayModes> getLiveDataBgAndCategoriesAndPlayModes(BoardGame boardGame) {
        return boardGameDao.findBgAndCategoriesAndPlayModesById(boardGame.getId());
    }

    //Preconditions: playmodes is not empty.
    public void insert(BoardGame boardGame) {
        if (boardGame.getBgCategories().isEmpty()) {
            insertBoardGame(boardGame);
        } else {
            insertBoardGameWithCategories(boardGame);
        }
    }

    //TODO change this to just insert?
    //TODO change this to insert board game with categories list drawn from board game categories attribute.
    private void insertBoardGame(BoardGame boardGame) {
        AppDatabase.getExecutorService().execute(() -> {
            boardGameDao.insert(boardGame);

            List<PlayMode.PlayModeEnum> list = boardGame.getPlayModes();

            for (PlayMode.PlayModeEnum playModeEnum : list) {
                playModeDao.insert(new PlayMode(boardGame.getBgName(), playModeEnum));
            }
        });
    }

    private void insertPlayModes(String bgName, List<PlayMode.PlayModeEnum> playModes) {
        for (PlayMode.PlayModeEnum playModeEnum : playModes) {
            playModeDao.insert(new PlayMode(bgName, playModeEnum));
        }
    }

    private void insertAssignedCategories(int bgId, List<BgCategory> bgCategories) {
        List<AssignedCategories> assignedCategories = new ArrayList<>();
        for (BgCategory bgCategory : bgCategories) {
            //if boardGame holds categories then they must have ids, or else this won't work.
            Log.w("BoardGameRepo.java", "bgCategory id: " + bgCategory.getId());
            assignedCategories.add(new AssignedCategories(bgId, bgCategory.getId()));
        }

        assignedCategoriesDao.insertAll(assignedCategories.toArray(new AssignedCategories[assignedCategories.size()]));
    }

    //TODO create an insertAssignedCategories(BoardGame boardGame) method?

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

    public void update(BoardGame originalBoardGame, BoardGame editedBoardGame) {
        Log.w("BoardGameRepo.java","Original Bg: " + originalBoardGame.getId() + ", " + originalBoardGame.getBgName());
        Log.w("BoardGameRepo.java","Edited Bg: " + editedBoardGame.getId() + ", " + editedBoardGame.getBgName());
        AppDatabase.getExecutorService().execute(() -> {
            boardGameDao.update(editedBoardGame);

            List<PlayMode> playModesToDelete = getPlayModesToDelete(editedBoardGame.getBgName(), originalBoardGame.getPlayModes(),
                    editedBoardGame.getPlayModes());
            List<AssignedCategories> assignedCategoriesToDelete = getAssignedCategoriesToDelete(editedBoardGame.getId(),
                    originalBoardGame.getBgCategories(), editedBoardGame.getBgCategories());

            deletePlayModes(playModesToDelete);
            deleteAssignedCategories(assignedCategoriesToDelete);

            insertPlayModes(editedBoardGame.getBgName(), editedBoardGame.getPlayModes());
            insertAssignedCategories(editedBoardGame.getId(), editedBoardGame.getBgCategories());
        });
    }

    private List<PlayMode> getPlayModesToDelete(String bgName, List<PlayMode.PlayModeEnum> originalPlayModes, List<PlayMode.PlayModeEnum> editedPlayModes) {
        List<PlayMode.PlayModeEnum> playModeEnums = new ArrayList<>(originalPlayModes);
        playModeEnums.removeAll(editedPlayModes);

        List<PlayMode> playModesToDelete = new ArrayList<>();

        for (PlayMode.PlayModeEnum playModeEnum : playModeEnums) {
            playModesToDelete.add(new PlayMode(bgName, playModeEnum));
        }

        return playModesToDelete;
    }

    private List<AssignedCategories> getAssignedCategoriesToDelete(int bgId, List<BgCategory> originalBgCategories, List<BgCategory> editedBgCategories) {
        // Compare and delete ones in original but not in edited
        List<BgCategory> bgCategories = new ArrayList<>(originalBgCategories);
        originalBgCategories.removeAll(editedBgCategories);

        List<AssignedCategories> assignedCategoriesToDelete = new ArrayList<>();

        for (BgCategory bgCategory : bgCategories) {
            assignedCategoriesToDelete.add(new AssignedCategories(bgId, bgCategory.getId()));
        }

        return assignedCategoriesToDelete;
    }

    //TODO add testing to ensure assigned categories with this bg are deleted.
    // Need to find how to add appropriate asserts for this really.
    public void delete(BoardGame boardGame) {
        AppDatabase.getExecutorService().execute(() -> {
            //TODO remove log messages once done. They are for testing.
            List<AssignedCategories> ac = acDao.findByBgId(boardGame.getId());

            for (int i = 0; i < ac.size(); i++) {
                Log.w("BGRepos.java", "1 Bg: " + boardGame.getId() + ", AC: " + ac.get(i).getCategoryId());
            }

            boardGameDao.delete(boardGame);

            ac = acDao.findByBgId(boardGame.getId());

            if (ac.isEmpty()) {
                Log.w("BGRepos.java", "2 Empty");
            } else {
                for (int i = 0; i < ac.size(); i++) {
                    Log.w("BGRepos.java", "2 Bg: " + boardGame.getId() + ", AC: " + ac.get(i).getCategoryId());
                }
            }
        });
    }

    private void deletePlayModes(List<PlayMode> playModes) {
        for (PlayMode playMode : playModes) {
            playModeDao.delete(playMode);
        }
    }

    public void deleteAssignedCategories(List<AssignedCategories> assignedCategories) {
        for (AssignedCategories acs : assignedCategories) {
            assignedCategoriesDao.delete(acs);
        }
    }
}
