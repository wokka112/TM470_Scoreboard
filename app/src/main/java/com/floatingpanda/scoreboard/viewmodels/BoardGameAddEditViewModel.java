package com.floatingpanda.scoreboard.viewmodels;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.AlertDialogHelper;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BgCategoryRepository;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGameRepository;
import com.floatingpanda.scoreboard.data.PlayMode;

import java.util.ArrayList;
import java.util.List;

//TODO keep searchable spinner comment stuff for when I make the gamerecord creation. It could be useful for
// finding and adding board games. Same for players.

//TODO set up something to sort the bgcategory list into alphabetic order.

public class BoardGameAddEditViewModel extends AndroidViewModel {

    private BoardGameRepository boardGameRepository;
    private BgCategoryRepository bgCategoryRepository;
    private List<BgCategory> selectedBgCategories;
    private LiveData<List<BgCategory>> allBgCategoriesLiveData;

    public BoardGameAddEditViewModel(Application application) {
        super(application);
        boardGameRepository = new BoardGameRepository(application);
        bgCategoryRepository = new BgCategoryRepository(application);
        selectedBgCategories = new ArrayList<BgCategory>();
        allBgCategoriesLiveData = bgCategoryRepository.getAll();
    }

    // Used for testing.
    public BoardGameAddEditViewModel(Application application, AppDatabase db) {
        super(application);
        boardGameRepository = new BoardGameRepository(db);
        bgCategoryRepository = new BgCategoryRepository(db);
        selectedBgCategories = new ArrayList<BgCategory>();
        allBgCategoriesLiveData = bgCategoryRepository.getAll();
    }

    public LiveData<List<BgCategory>> getAllBgCategories() { return allBgCategoriesLiveData; }

    public List<BgCategory> getSelectedBgCategories() {
        return selectedBgCategories;
    }

    public void setSelectedBgCategories(List<BgCategory> bgCategories) {
        this.selectedBgCategories = new ArrayList<BgCategory>(bgCategories);
    }

    public void addSelectedBgCategory(BgCategory bgCategory) {
        this.selectedBgCategories.add(bgCategory);
    }

    public void removeSelectedBgCategory(BgCategory bgCategory) {
        this.selectedBgCategories.remove(bgCategory);
    }

    public void clearSelectedBgCategories() {
        this.selectedBgCategories.clear();
    }

    public List<PlayMode.PlayModeEnum> getPlayModes(boolean competitive, boolean cooperative, boolean solitaire) {
        List<PlayMode.PlayModeEnum> playModes = new ArrayList<>();

        boolean allFalse = true;
        if (competitive) {
            playModes.add(PlayMode.PlayModeEnum.COMPETITIVE);
            allFalse = false;
        }

        if (cooperative) {
            playModes.add(PlayMode.PlayModeEnum.COOPERATIVE);
            allFalse = false;
        }

        if (solitaire) {
            playModes.add(PlayMode.PlayModeEnum.SOLITAIRE);
            allFalse = false;
        }

        if (allFalse) {
            playModes.add(PlayMode.PlayModeEnum.ERROR);
        }

        return playModes;
    }

    public BoardGame.TeamOption getTeamOption(int checkboxId) {
        switch(checkboxId) {
            case R.id.bgadd_no_teams_radiobutton:
                return BoardGame.TeamOption.NO_TEAMS;
            case R.id.bgadd_teams_allowed_radiobutton:
                return BoardGame.TeamOption.TEAMS_AND_SOLOS_ALLOWED;
            case R.id.bgadd_teams_only_radiobutton:
                return BoardGame.TeamOption.TEAMS_ONLY;
            default:
                return BoardGame.TeamOption.ERROR;
        }
    }

    //TODO move this into a validator class??
    //TODO maybe add enums or some other method for dealing with this so I don't have to make popups in the viewmodel and use a hackey
    // testing boolean to control it for testing.
    public boolean addActivityInputsValid(Activity activity, String bgName, String difficultyString, String minPlayersString,
                                          String maxPlayersString, BoardGame.TeamOption teamOption, List<PlayMode.PlayModeEnum> playModeEnums, boolean testing) {
        return editActivityInputsValid(activity, "", bgName, difficultyString, minPlayersString, maxPlayersString, teamOption, playModeEnums, testing);
    }

    public boolean editActivityInputsValid(Activity activity, String originalBgName, String bgName, String difficultyString, String minPlayersString,
                                           String maxPlayersString, BoardGame.TeamOption teamOption, List<PlayMode.PlayModeEnum> playModeEnums, boolean testing) {
        if (bgName.isEmpty()) {
            if (!testing) {
                AlertDialogHelper.popupWarning("You must enter a unique name for the board game.", activity);
            }
            return false;
        }

        if (!bgName.equals(originalBgName)
                && boardGameRepository.contains(bgName)) {
            if (!testing) {
                AlertDialogHelper.popupWarning("You must enter a unique name for the board game.", activity);
            }
            return false;
        }

        if (difficultyString.isEmpty()) {
            if (!testing) {
                AlertDialogHelper.popupWarning("You must enter a difficulty between 1 and 5 (inclusive).", activity);
            }
            return false;
        }

        int difficulty = Integer.parseInt(difficultyString);

        if (difficulty < 1 || difficulty >5) {
            if (!testing) {
                AlertDialogHelper.popupWarning("You must enter a difficulty between 1 and 5 (inclusive).", activity);
            }
            return false;
        }

        if (minPlayersString.isEmpty()) {
            if (!testing) {
                AlertDialogHelper.popupWarning("You must enter a minimum number of players for the game.", activity);
            }
            return false;
        }

        int minPlayers = Integer.parseInt(minPlayersString);

        if (minPlayers < 0) {
            if (!testing) {
                AlertDialogHelper.popupWarning("Minimum players must be greater than 0.", activity);
            }
            return false;
        }

        if (maxPlayersString.isEmpty()) {
            if (!testing) {
                AlertDialogHelper.popupWarning("You must enter a maximum number of players for the game.", activity);
            }
            return false;
        }

        int maxPlayers = Integer.parseInt(maxPlayersString);

        if (maxPlayers < minPlayers || maxPlayers < 0) {
            if(!testing) {
                AlertDialogHelper.popupWarning("Maximum players must be greater than 0 and greater than minimum players.", activity);
            }
            return false;
        }

        if (teamOption.equals(BoardGame.TeamOption.ERROR)) {
            if(!testing) {
                AlertDialogHelper.popupWarning("You must select a team option.", activity);
            }
            return false;
        }

        if (playModeEnums.contains(PlayMode.PlayModeEnum.ERROR)) {
            if(!testing) {
                AlertDialogHelper.popupWarning("You must select at least one possible play mode.", activity);
            }
            return false;
        }

        return true;
    }

    //Stuff for the searchable spinner list. This could be useful for when I'm making the board game
    // selection element in the game record creation/editing activity.

    /*
    private void setupAdapter(Context context) {
        List<BgCategory> bgCategories = new ArrayList<BgCategory>(getAllBgCategoriesNotLive());
        bgCategories.removeAll(getSelectedBgCategories());

        adapter = new SpinnerAdapter(context, bgCategories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public SpinnerAdapter getSpinnerListAdapter(Context context) {
        if (adapter == null) {
            setupAdapter(context);
        }

        return adapter;
    }
     */

    /*
    private void setupAdapter(Context context) {
        List<BgCategory> bgCategories = new ArrayList<BgCategory>(getAllBgCategoriesNotLive());
        bgCategories.removeAll(getSelectedBgCategories());

        adapter = new ArrayAdapter<BgCategory> (
                context, android.R.layout.simple_spinner_item, bgCategories
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public ArrayAdapter<BgCategory> getSpinnerListAdapter(Context context) {
        if (adapter == null) {
            setupAdapter(context);
        }

        return adapter;
    }

     */
}
