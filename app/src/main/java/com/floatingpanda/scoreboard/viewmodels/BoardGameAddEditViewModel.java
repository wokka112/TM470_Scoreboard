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

import android.app.Activity;
import android.app.Application;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.utils.AlertDialogHelper;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.repositories.BgCategoryRepository;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.repositories.BoardGameRepository;
import com.floatingpanda.scoreboard.data.entities.PlayMode;

import java.util.ArrayList;
import java.util.List;

//TODO comments
/**
 * ViewModel providing data to the views for adding and editing board games in the database.
 */
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

    public boolean addActivityInputsValid(EditText bgNameEditText, EditText difficultyEditText, EditText minPlayersEditText,
                                          EditText maxPlayersEditText, BoardGame.TeamOption teamOption, RadioButton rightmostTeamOptionRadioButton,
                                          List<PlayMode.PlayModeEnum> playModeEnums, CheckBox rightmostPlayModeCheckBox, boolean testing) {
        return editActivityInputsValid("", bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption,
                rightmostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, testing);
    }

    public boolean editActivityInputsValid(String originalBgName, EditText bgNameEditText, EditText difficultyEditText, EditText minPlayersEditText,
                                           EditText maxPlayersEditText, BoardGame.TeamOption teamOption, RadioButton rightmostTeamOptionRadioButton,
                                           List<PlayMode.PlayModeEnum> playModeEnums, CheckBox rightmostPlayModeCheckBox, boolean testing) {
        String bgName = bgNameEditText.getText().toString();
        if (bgName.isEmpty()) {
            if (!testing) {
                bgNameEditText.setError("You must enter a board game name.");
                bgNameEditText.requestFocus();
            }
            return false;
        }

        if (!bgName.equals(originalBgName)
                && boardGameRepository.containsBoardGameName(bgName)) {
            if (!testing) {
                bgNameEditText.setError("A board game with this name already exists in the app. You must enter a unique board game name.");
                bgNameEditText.requestFocus();
            }
            return false;
        }

        String difficultyString = difficultyEditText.getText().toString();
        if (difficultyString.isEmpty()) {
            if (!testing) {
                difficultyEditText.setError("You must enter a difficulty between 1 and 5 (inclusive).");
                difficultyEditText.requestFocus();
            }
            return false;
        }

        int difficulty = Integer.parseInt(difficultyString);

        if (difficulty < 1 || difficulty >5) {
            if (!testing) {
                difficultyEditText.setError("You must enter a difficulty between 1 and 5 (inclusive).");
                difficultyEditText.requestFocus();
            }
            return false;
        }

        String minPlayersString = minPlayersEditText.getText().toString();
        if (minPlayersString.isEmpty()) {
            if (!testing) {
                minPlayersEditText.setError("You must enter a minimum number of players.");
                minPlayersEditText.requestFocus();
            }
            return false;
        }

        int minPlayers = Integer.parseInt(minPlayersString);

        if (minPlayers < 0) {
            if (!testing) {
                minPlayersEditText.setError("You must enter a number greater than 0.");
                minPlayersEditText.requestFocus();
            }
            return false;
        }

        String maxPlayersString = maxPlayersEditText.getText().toString();
        if (maxPlayersString.isEmpty()) {
            if (!testing) {
                maxPlayersEditText.setError("You must enter a maximum number of players.");
                maxPlayersEditText.requestFocus();
            }
            return false;
        }

        int maxPlayers = Integer.parseInt(maxPlayersString);

        if (maxPlayers < minPlayers || maxPlayers < 0) {
            if(!testing) {
                maxPlayersEditText.setError("You must enter a number greater than 0 and greater than the minimum number of players.");
                maxPlayersEditText.requestFocus();
            }
            return false;
        }

        if (teamOption.equals(BoardGame.TeamOption.ERROR)) {
            if(!testing) {
                rightmostTeamOptionRadioButton.setError("You must select a team option.");
                rightmostTeamOptionRadioButton.requestFocus();
                //AlertDialogHelper.popupWarning("You must select a team option.", activity);
            }
            return false;
        }

        if (playModeEnums.contains(PlayMode.PlayModeEnum.ERROR)) {
            if(!testing) {
                rightmostPlayModeCheckBox.setError("You must select at least one possible play mode");
                rightmostPlayModeCheckBox.requestFocus();
                //AlertDialogHelper.popupWarning("You must select at least one possible play mode.", activity);
            }
            return false;
        }

        return true;
    }
}
