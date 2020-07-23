package com.floatingpanda.scoreboard.model;

import android.app.Activity;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.viewmodels.BoardGameAddEditViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class BoardGameAddEditViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private BgCategoryDao bgCategoryDao;
    private BoardGameDao boardGameDao;
    private BoardGameAddEditViewModel boardGameAddEditViewModel;

    @Mock
    Activity activity;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        bgCategoryDao = db.bgCategoryDao();
        boardGameDao = db.boardGameDao();
        boardGameAddEditViewModel = new BoardGameAddEditViewModel(ApplicationProvider.getApplicationContext(), db);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllBgCategoriesWhenNoneInserted() throws InterruptedException {
        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(boardGameAddEditViewModel.getAllBgCategories());

        assertTrue(bgCategories.isEmpty());
    }

    @Test
    public void getAllBgCategoriesWhenInserted() throws InterruptedException {
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));

        List<BgCategory> bgCategories = LiveDataTestUtil.getValue(boardGameAddEditViewModel.getAllBgCategories());
        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size()));
    }

    @Test
    public void getSelectedBgCategoriesWhenNoneSet() {
        List<BgCategory> bgCategories = boardGameAddEditViewModel.getSelectedBgCategories();

        assertTrue(bgCategories.isEmpty());
    }

    @Test
    public void setAndGetSelectedBgCategories() {
        boardGameAddEditViewModel.setSelectedBgCategories(TestData.BG_CATEGORIES);
        List<BgCategory> bgCategories = boardGameAddEditViewModel.getSelectedBgCategories();

        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size()));
    }

    @Test
    public void addSelectedBgCategory() {
        boardGameAddEditViewModel.addSelectedBgCategory(TestData.BG_CATEGORY_1);
        List<BgCategory> bgCategories = boardGameAddEditViewModel.getSelectedBgCategories();

        assertThat(bgCategories.size(), is(1));
        assertThat(bgCategories.get(0), is(TestData.BG_CATEGORY_1));
    }

    @Test
    public void removeSelectedBgCategory() {
        boardGameAddEditViewModel.setSelectedBgCategories(TestData.BG_CATEGORIES);
        List<BgCategory> bgCategories = boardGameAddEditViewModel.getSelectedBgCategories();

        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size()));
        assertTrue(bgCategories.contains(TestData.BG_CATEGORY_1));

        boardGameAddEditViewModel.removeSelectedBgCategory(TestData.BG_CATEGORY_1);
        bgCategories = boardGameAddEditViewModel.getSelectedBgCategories();

        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size() - 1));
        assertFalse(bgCategories.contains(TestData.BG_CATEGORY_1));
    }

    @Test
    public void setAndClearSelectedBgCategories() {
        boardGameAddEditViewModel.setSelectedBgCategories(TestData.BG_CATEGORIES);
        List<BgCategory> bgCategories = boardGameAddEditViewModel.getSelectedBgCategories();

        assertThat(bgCategories.size(), is(TestData.BG_CATEGORIES.size()));

        boardGameAddEditViewModel.clearSelectedBgCategories();
        bgCategories = boardGameAddEditViewModel.getSelectedBgCategories();

        assertTrue(bgCategories.isEmpty());
    }

    @Test
    public void getPlayModes() {
        //Test Case 1: Competitive only
        boolean competitive = true;
        boolean cooperative = false;
        boolean solitaire = false;
        List<PlayMode.PlayModeEnum> playModeEnums = boardGameAddEditViewModel.getPlayModes(competitive, cooperative, solitaire);

        assertThat(playModeEnums.size(), is(1));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.COMPETITIVE));

        //Test Case 2: Cooperative only
        competitive = false;
        cooperative = true;
        solitaire = false;
        playModeEnums = boardGameAddEditViewModel.getPlayModes(competitive, cooperative, solitaire);

        assertThat(playModeEnums.size(), is(1));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.COOPERATIVE));

        //Test Case 3: Solitaire only
        competitive = false;
        cooperative = false;
        solitaire = true;
        playModeEnums = boardGameAddEditViewModel.getPlayModes(competitive, cooperative, solitaire);

        assertThat(playModeEnums.size(), is(1));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.SOLITAIRE));

        //Test Case 4: Competitive and Cooperative
        competitive = true;
        cooperative = true;
        solitaire = false;
        playModeEnums = boardGameAddEditViewModel.getPlayModes(competitive, cooperative, solitaire);

        assertThat(playModeEnums.size(), is(2));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.COMPETITIVE));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.COOPERATIVE));

        //Test Case 5: Competitive and Solitaire
        competitive = true;
        cooperative = false;
        solitaire = true;
        playModeEnums = boardGameAddEditViewModel.getPlayModes(competitive, cooperative, solitaire);

        assertThat(playModeEnums.size(), is(2));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.COMPETITIVE));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.SOLITAIRE));

        //Test Case 6: Cooperative and Solitaire
        competitive = false;
        cooperative = true;
        solitaire = true;
        playModeEnums = boardGameAddEditViewModel.getPlayModes(competitive, cooperative, solitaire);

        assertThat(playModeEnums.size(), is(2));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.COOPERATIVE));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.SOLITAIRE));

        //Test Case 7: Competitive, Cooperative and Solitaire
        competitive = true;
        cooperative = true;
        solitaire = true;
        playModeEnums = boardGameAddEditViewModel.getPlayModes(competitive, cooperative, solitaire);

        assertThat(playModeEnums.size(), is(3));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.COMPETITIVE));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.COOPERATIVE));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.SOLITAIRE));

        //Test Case 8: None
        competitive = false;
        cooperative = false;
        solitaire = false;
        playModeEnums = boardGameAddEditViewModel.getPlayModes(competitive, cooperative, solitaire);

        assertThat(playModeEnums.size(), is(1));
        assertTrue(playModeEnums.contains(PlayMode.PlayModeEnum.ERROR));
    }

    @Test
    public void getTeamOptions() {
        //Test Case 1: No teams selected
        int checkBoxId = R.id.bgadd_no_teams_radiobutton;
        BoardGame.TeamOption teamOption = boardGameAddEditViewModel.getTeamOption(checkBoxId);

        assertThat(teamOption, is(BoardGame.TeamOption.NO_TEAMS));

        //Test Case 2: Teams allowed selected
        checkBoxId = R.id.bgadd_teams_allowed_radiobutton;
        teamOption = boardGameAddEditViewModel.getTeamOption(checkBoxId);

        assertThat(teamOption, is(BoardGame.TeamOption.TEAMS_AND_SOLOS_ALLOWED));

        //Test Case 3: Teams only selected
        checkBoxId = R.id.bgadd_teams_only_radiobutton;
        teamOption = boardGameAddEditViewModel.getTeamOption(checkBoxId);

        assertThat(teamOption, is(BoardGame.TeamOption.TEAMS_ONLY));

        //Test Case 4: None selected
        checkBoxId = 0;
        teamOption = boardGameAddEditViewModel.getTeamOption(checkBoxId);

        assertThat(teamOption, is(BoardGame.TeamOption.ERROR));
    }

    //Precondition that difficultyString, minPlayersString and maxPlayersString all be integers. I am not testing
    // for them not being integers.
    @Test
    public void addActivityInputsValid() {
        boardGameDao.insert(TestData.BOARD_GAME_1);

        Context context = ApplicationProvider.getApplicationContext();

        EditText bgNameEditText = new EditText(context);
        EditText difficultyEditText = new EditText(context);
        EditText minPlayersEditText = new EditText(context);
        EditText maxPlayersEditText = new EditText(context);
        RadioButton rightMostTeamOptionRadioButton = new RadioButton(context);
        CheckBox rightmostPlayModeCheckBox = new CheckBox(context);

        //Test Case 1: Invalid - bgName empty
        String bgName = "";
        String difficultyString = "3";
        String minPlayersString = "1";
        String maxPlayersString = "8";

        bgNameEditText.setText(bgName);
        difficultyEditText.setText(difficultyString);
        minPlayersEditText.setText(minPlayersString);
        maxPlayersEditText.setText(maxPlayersString);

        BoardGame.TeamOption teamOption = BoardGame.TeamOption.NO_TEAMS;
        List<PlayMode.PlayModeEnum> playModeEnums = new ArrayList<>();
        playModeEnums.add(PlayMode.PlayModeEnum.COMPETITIVE);
        boolean isValid = boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton,
                playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 2: Invalid - bgName contained in database
        bgName = TestData.BOARD_GAME_1.getBgName();
        bgNameEditText.setText(bgName);
        isValid = boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton,
                playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 3: Invalid - difficulty below 1
        bgName = "Valid name";
        bgNameEditText.setText(bgName);
        difficultyString = "0";
        difficultyEditText.setText(difficultyString);
        isValid = boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton,
                playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 4: Invalid - difficulty above 5
        difficultyString = "6";
        difficultyEditText.setText(difficultyString);
        isValid = boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton,
                playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 5: Invalid - minplayers below 0
        difficultyString = "3";
        difficultyEditText.setText(difficultyString);
        minPlayersString = "-1";
        minPlayersEditText.setText(minPlayersString);
        isValid = boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton,
                playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 6: Invalid - maxplayers below 0
        minPlayersString = "1";
        minPlayersEditText.setText(minPlayersString);
        maxPlayersString = "-1";
        maxPlayersEditText.setText(maxPlayersString);
        isValid = boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton,
                playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 7: Invalid - max players below minplayers
        minPlayersString = "3";
        minPlayersEditText.setText(minPlayersString);
        maxPlayersString = "2";
        maxPlayersEditText.setText(maxPlayersString);
        isValid = boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton,
                playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 8: Invalid - teamOption is set to TeamOption.ERROR
        minPlayersString = "1";
        minPlayersEditText.setText(minPlayersString);
        maxPlayersString = "8";
        maxPlayersEditText.setText(maxPlayersString);
        teamOption = BoardGame.TeamOption.ERROR;
        isValid = boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton,
                playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 9: Invalid - playModeEnums list only has PlayModeEnum.ERROR in it
        teamOption = BoardGame.TeamOption.NO_TEAMS;
        playModeEnums.clear();
        playModeEnums.add(PlayMode.PlayModeEnum.ERROR);
        isValid = boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton,
                playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 10: Invalid - playModeEnums list only has PlayModeEnum.ERROR in it alongside other play mode enums
        playModeEnums.clear();
        playModeEnums.add(PlayMode.PlayModeEnum.COMPETITIVE);
        playModeEnums.add(PlayMode.PlayModeEnum.ERROR);
        isValid = boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton,
                playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 11: Valid - everything valid and playModeEnums list has 1 valid play mode enum
        playModeEnums.clear();
        playModeEnums.add(PlayMode.PlayModeEnum.COMPETITIVE);
        isValid = boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton,
                playModeEnums, rightmostPlayModeCheckBox, true);

        assertTrue(isValid);

        //Test Case 12: Valid - everything valid and playModeEnums list has multiple valid play mode enums
        playModeEnums.add(PlayMode.PlayModeEnum.COOPERATIVE);
        isValid = boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton,
                playModeEnums, rightmostPlayModeCheckBox, true);

        assertTrue(isValid);

        //Test Case 13: Valid - everything valid and playModeEnums list is empty
        playModeEnums.clear();
        isValid = boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton,
                playModeEnums, rightmostPlayModeCheckBox, true);

        assertTrue(isValid);
    }

    //Precondition that difficultyString, minPlayersString and maxPlayersString all be integers. I am not testing
    // for them not being integers.
    @Test
    public void editActivityInputsValid() throws InterruptedException {
        boardGameDao.insert(TestData.BOARD_GAME_1);
        TimeUnit.MILLISECONDS.sleep(100);

        Context context = ApplicationProvider.getApplicationContext();

        EditText bgNameEditText = new EditText(context);
        EditText difficultyEditText = new EditText(context);
        EditText minPlayersEditText = new EditText(context);
        EditText maxPlayersEditText = new EditText(context);
        RadioButton rightMostTeamOptionRadioButton = new RadioButton(context);
        CheckBox rightmostPlayModeCheckBox = new CheckBox(context);

        //Test Case 1: Invalid - editedBgName empty
        String originalBgName = "Bloople";
        String editedBgName = "";
        String difficultyString = "3";
        String minPlayersString = "1";
        String maxPlayersString = "8";

        bgNameEditText.setText(editedBgName);
        difficultyEditText.setText(difficultyString);
        minPlayersEditText.setText(minPlayersString);
        maxPlayersEditText.setText(maxPlayersString);

        BoardGame.TeamOption teamOption = BoardGame.TeamOption.NO_TEAMS;
        List<PlayMode.PlayModeEnum> playModeEnums = new ArrayList<>();
        playModeEnums.add(PlayMode.PlayModeEnum.COMPETITIVE);
        boolean isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 2: Invalid - editedBgName contained in database
        editedBgName = TestData.BOARD_GAME_1.getBgName();
        bgNameEditText.setText(editedBgName);
        isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 3: Invalid - difficulty below 1
        editedBgName = "Valid name";
        bgNameEditText.setText(editedBgName);
        difficultyString = "0";
        difficultyEditText.setText(difficultyString);
        isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 4: Invalid - difficulty above 5
        difficultyString = "6";
        difficultyEditText.setText(difficultyString);
        isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 5: Invalid - minplayers below 0
        difficultyString = "3";
        difficultyEditText.setText(difficultyString);
        minPlayersString = "-1";
        minPlayersEditText.setText(minPlayersString);
        isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 6: Invalid - maxplayers below 0
        minPlayersString = "1";
        minPlayersEditText.setText(minPlayersString);
        maxPlayersString = "-1";
        maxPlayersEditText.setText(maxPlayersString);
        isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 7: Invalid - max players below minplayers
        minPlayersString = "3";
        minPlayersEditText.setText(minPlayersString);
        maxPlayersString = "2";
        maxPlayersEditText.setText(maxPlayersString);
        isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 8: Invalid - teamOption is set to TeamOption.ERROR
        minPlayersString = "1";
        minPlayersEditText.setText(minPlayersString);
        maxPlayersString = "8";
        maxPlayersEditText.setText(maxPlayersString);
        teamOption = BoardGame.TeamOption.ERROR;
        isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 9: Invalid - playModeEnums list only has PlayModeEnum.ERROR in it
        teamOption = BoardGame.TeamOption.NO_TEAMS;
        playModeEnums.clear();
        playModeEnums.add(PlayMode.PlayModeEnum.ERROR);
        isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 10: Invalid - playModeEnums list only has PlayModeEnum.ERROR in it alongside other play mode enums
        playModeEnums.clear();
        playModeEnums.add(PlayMode.PlayModeEnum.COMPETITIVE);
        playModeEnums.add(PlayMode.PlayModeEnum.ERROR);
        isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertFalse(isValid);

        //Test Case 11: Valid - everything valid and playModeEnums list has 1 valid play mode enum
        playModeEnums.clear();
        playModeEnums.add(PlayMode.PlayModeEnum.COMPETITIVE);
        isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertTrue(isValid);

        //Test Case 12: Valid - everything valid and playModeEnums list has multiple valid play mode enums
        playModeEnums.add(PlayMode.PlayModeEnum.COOPERATIVE);
        isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertTrue(isValid);

        //Test Case 13: Valid - everything valid and playModeEnums list is empty
        playModeEnums.clear();
        isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertTrue(isValid);

        //Test Case 14: Valid - everything valid, editedBgName is same as originalBgName and playModeEnums has 1 valid play mode enum
        editedBgName = "Bloople";
        bgNameEditText.setText(editedBgName);
        playModeEnums.add(PlayMode.PlayModeEnum.COMPETITIVE);
        isValid = boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText, difficultyEditText, minPlayersEditText,
                maxPlayersEditText, teamOption, rightMostTeamOptionRadioButton, playModeEnums, rightmostPlayModeCheckBox, true);

        assertTrue(isValid);
    }
}
