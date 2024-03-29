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

package com.floatingpanda.scoreboard.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.viewmodels.BoardGameAddEditViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.thomashaertel.widget.MultiSpinner;

import java.util.List;

/**
 * View for editing board games in the database.
 */
public class BoardGameEditActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY_ORIGINAL = "com.floatingpanda.scoreboard.REPLY_ORIGINAL";
    public static final String EXTRA_REPLY_EDITED = "com.floatingpanda.scoreboard.REPLY_EDITED";

    private BoardGameAddEditViewModel boardGameAddEditViewModel;

    private BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes;

    private EditText bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, descriptionEditText,
            notesEditText, houseRulesEditText;
    private CheckBox competitiveCheckBox, cooperativeCheckBox, solitaireCheckBox;
    private RadioGroup teamOptionsRadioGroup;
    private RadioButton noTeamsRadioButton, teamsOrSoloRadioButton, teamsOnlyRadioButton;
    private ChipGroup chipGroup;
    private MultiSpinner multiSpinner;
    private ArrayAdapter<BgCategory> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_board_game);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        boardGameAddEditViewModel = new ViewModelProvider(this).get(BoardGameAddEditViewModel.class);

        bgNameEditText = findViewById(R.id.bgadd_name_edittext);
        difficultyEditText = findViewById(R.id.bgadd_difficulty_edittext);
        minPlayersEditText = findViewById(R.id.bgadd_min_players_edittext);
        maxPlayersEditText = findViewById(R.id.bgadd_max_players_edittext);
        descriptionEditText = findViewById(R.id.bgadd_description_edittext);
        notesEditText = findViewById(R.id.bgadd_notes_edittext);
        houseRulesEditText = findViewById(R.id.bgadd_house_rules_edittext);

        competitiveCheckBox = findViewById(R.id.bgadd_checkbox_competitive);
        cooperativeCheckBox = findViewById(R.id.bgadd_checkbox_cooperative);
        solitaireCheckBox = findViewById(R.id.bgadd_checkbox_solitaire);

        teamOptionsRadioGroup = findViewById(R.id.bgadd_team_options_radiogroup);

        noTeamsRadioButton = findViewById(R.id.bgadd_no_teams_radiobutton);
        teamsOrSoloRadioButton = findViewById(R.id.bgadd_teams_allowed_radiobutton);
        teamsOnlyRadioButton = findViewById(R.id.bgadd_teams_only_radiobutton);

        chipGroup = findViewById(R.id.bgadd_categories_chip_group);

        multiSpinner = findViewById(R.id.bgadd_multi_spinner);
        multiSpinner.setAllText("Choose categories");

        boardGameWithBgCategoriesAndPlayModes = (BoardGameWithBgCategoriesAndPlayModes) getIntent().getExtras().get("BOARD_GAME_WITH_CATEGORIES_AND_PLAY_MODES");
        boardGameAddEditViewModel.setSelectedBgCategories(boardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().getBgCategories());

        setViews(boardGameWithBgCategoriesAndPlayModes);

        final Button saveButton, cancelButton;

        boardGameAddEditViewModel.getAllBgCategories().observe(this, new Observer<List<BgCategory>>() {
            @Override
            public void onChanged(@NonNull final List<BgCategory> bgCategories) {
                adapter = new ArrayAdapter<BgCategory>(BoardGameEditActivity.this, android.R.layout.simple_spinner_item, bgCategories);
                multiSpinner.setAdapter(adapter, false, onSelectedListener);
                setMultiSpinnerSelected(boardGameAddEditViewModel.getSelectedBgCategories());
            }
        });

        saveButton = findViewById(R.id.bgadd_button_save);
        cancelButton = findViewById(R.id.bgadd_button_cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String originalBgName = boardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().getBoardGame().getBgName();
                String bgName = bgNameEditText.getText().toString();
                String difficultyString = difficultyEditText.getText().toString();
                String minPlayersString = minPlayersEditText.getText().toString();
                String maxPlayersString = maxPlayersEditText.getText().toString();
                List<PlayMode.PlayModeEnum> playModeEnums = getPlayModeEnums();
                BoardGame.TeamOption teamOption = getTeamOption();

                if(!boardGameAddEditViewModel.editActivityInputsValid(originalBgName, bgNameEditText,
                        difficultyEditText, minPlayersEditText, maxPlayersEditText, teamOption, teamsOnlyRadioButton, playModeEnums,
                        solitaireCheckBox, false)) {
                    return;
                }

                int bgId = boardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().getBoardGame().getId();
                int difficulty = Integer.parseInt(difficultyString);
                int minPlayers = Integer.parseInt(minPlayersString);
                int maxPlayers = Integer.parseInt(maxPlayersString);
                String description = descriptionEditText.getText().toString();
                String notes = notesEditText.getText().toString();
                String houseRules = houseRulesEditText.getText().toString();
                List<BgCategory> bgCategories = boardGameAddEditViewModel.getSelectedBgCategories();

                BoardGame boardGame = new BoardGame(bgId, bgName, difficulty, minPlayers, maxPlayers, teamOption, description,
                        houseRules, notes);

                BoardGameWithBgCategories boardGameWithBgCategories = new BoardGameWithBgCategories(boardGame, bgCategories);

                BoardGameWithBgCategoriesAndPlayModes editedBoardGameWithBgCategoriesAndPlayModes =
                        BoardGameWithBgCategoriesAndPlayModes.createUsingPlayModeEnumList(boardGameWithBgCategories, playModeEnums);

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY_ORIGINAL, boardGameWithBgCategoriesAndPlayModes);
                replyIntent.putExtra(EXTRA_REPLY_EDITED, editedBoardGameWithBgCategoriesAndPlayModes);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }

    /**
     * Sets the views to the details from boardGame, including setting up the chips for the bg
     * categories.
     * @param boardGameWithBgCategoriesAndPlayModes a BoardGame with bg categories and play modes
     */
    private void setViews(BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes) {
        BoardGame boardGame = boardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().getBoardGame();

        bgNameEditText.setText(boardGame.getBgName());
        difficultyEditText.setText(Integer.toString(boardGame.getDifficulty()));
        minPlayersEditText.setText(Integer.toString(boardGame.getMinPlayers()));
        maxPlayersEditText.setText(Integer.toString(boardGame.getMaxPlayers()));
        descriptionEditText.setText(boardGame.getDescription());
        notesEditText.setText(boardGame.getNotes());
        houseRulesEditText.setText(boardGame.getHouseRules());
        setTeamOptionsRadioGroup(boardGame.getTeamOptions());

        setPlayModeCheckBoxes(boardGameWithBgCategoriesAndPlayModes.getPlayModeEnums());
        setChipGroupChips(boardGameWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().getBgCategories());
    }

    /**
     * Sets the multispinner to show the bg categories in the selectedBgCategories as selected. This
     * is reflected in the multispinner with a tick in a checkbox next to the appropriate categories,
     * and behind the scenes as an array of booleans with "true" in any positions where a selected
     * bgCategory is.
     * @param selectedBgCategories
     */
    private void setMultiSpinnerSelected(List<BgCategory> selectedBgCategories) {
        boolean[] selected = getMultiSpinnerSelectedForSelectedBgCategories(selectedBgCategories);
        multiSpinner.setSelected(selected);
    }

    /**
     * Returns a list of booleans the size of the multispinner's bg categories list. This list of
     * booleans contains "true" in the positions where a bg category from the selectedBgCategories
     * list exists.
     * @param selectedBgCategories a list of bg categories selected for the board game
     * @return
     */
    private boolean[] getMultiSpinnerSelectedForSelectedBgCategories(List<BgCategory> selectedBgCategories) {
        boolean[] selected = new boolean[adapter.getCount()];

        for (int i = 0; i < selectedBgCategories.size(); i++) {
            int position = adapter.getPosition(selectedBgCategories.get(i));
            selected[position] = true;
        }

        return selected;
    }

    /**
     * Sets the checkboxes to reflect the play modes that the board game supports.
     * @param playModes a list of PlayModeEnum's that represents the play modes the board game supports
     */
    private void setPlayModeCheckBoxes(List<PlayMode.PlayModeEnum> playModes) {
        if (playModes.contains(PlayMode.PlayModeEnum.COMPETITIVE)) {
            competitiveCheckBox.setChecked(true);
        }

        if (playModes.contains(PlayMode.PlayModeEnum.COOPERATIVE)) {
            cooperativeCheckBox.setChecked(true);
        }

        if (playModes.contains(PlayMode.PlayModeEnum.SOLITAIRE)) {
            solitaireCheckBox.setChecked(true);
        }
    }

    /**
     * Sets one of the radio buttons in the radiogroup on to reflect the team options that the board
     * game supports.
     * @param teamOption a TeamOption enum that represents the team options for the board game
     */
    private void setTeamOptionsRadioGroup(BoardGame.TeamOption teamOption) {
        switch (teamOption) {
            case NO_TEAMS:
                noTeamsRadioButton.setChecked(true);
                break;
            case TEAMS_AND_SOLOS_ALLOWED:
                teamsOrSoloRadioButton.setChecked(true);
                break;
            case TEAMS_ONLY:
                teamsOnlyRadioButton.setChecked(true);
                break;
            case ERROR:
                Log.w("BoardGameEditAct.java", "Team option: Error");
                break;
            default:
                break;
        }
    }

    /**
     * Sets the chips in the chip group to represent the categories passed as bgCategories. Also
     * adds these categories to the list of selected categories for the board game.
     * @param bgCategories a list of board game categories
     */
    private void setChipGroupChips(List<BgCategory> bgCategories) {
        clearChips();
        for (BgCategory bgCategory : bgCategories) {
            addChip(bgCategory);
        }
    }

    /**
     * Adds a chip to the chip group to represent the category passed as bgCategory. Also sets the
     * category passed as bgCategory to be a selected category for the board game.
     * @param bgCategory a board game category
     */
    private void addChip(BgCategory bgCategory) {
        chipGroup.addView(createChip(bgCategory));
    }

    /**
     * Creates a chip to add to the view for the BgCategory, bgCategory.
     * @param bgCategory
     * @return
     */
    private Chip createChip(BgCategory bgCategory) {
        Chip chip = new Chip(chipGroup.getContext());
        chip.setText((bgCategory.getCategoryName()));
        chip.setCloseIconVisible(true);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardGameAddEditViewModel.removeSelectedBgCategory(bgCategory);
                chipGroup.removeView(chip);

                int position = adapter.getPosition(bgCategory);
                boolean[] selected = multiSpinner.getSelected();
                selected[position] = false;

                multiSpinner.setSelected(selected);
            }
        });

        return chip;
    }

    /**
     * Clears the chips from the chip group and removes all the selected board game categories.
     */
    private void clearChips() {
        chipGroup.removeAllViews();
        boardGameAddEditViewModel.clearSelectedBgCategories();
    }

    /**
     * Returns a list of PlayModeEnum enums to represent the play mode checkboxes that are checked
     * in the activity.
     * @return a list of PlayModeEnum representing the checked play mode checkboxes
     */
    private List<PlayMode.PlayModeEnum> getPlayModeEnums() {
        boolean competitive = competitiveCheckBox.isChecked();
        boolean cooperative = cooperativeCheckBox.isChecked();
        boolean solitaire = solitaireCheckBox.isChecked();

        return boardGameAddEditViewModel.getPlayModes(competitive, cooperative, solitaire);
    }

    /**
     * Returns a TeamOption enum to represent the team option radio button that is checked in the
     * activity.
     * @return a TeamOption enum representing the checked radio group radio button
     */
    private BoardGame.TeamOption getTeamOption() {
        int checkboxId = teamOptionsRadioGroup.getCheckedRadioButtonId();

        return boardGameAddEditViewModel.getTeamOption(checkboxId);
    }

    /**
     * Listener for the multispinner. When items are selected from the multispinner and ok is
     * clicked on the spinner, the chips are cleared from the view and new chips are added to it to
     * represent the selected categories.
     */
    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            clearChips();
            for (int i = 0; i < selected.length; i++) {
                if (selected[i] == true) {
                    BgCategory bgCategory = adapter.getItem(i);
                    addChip(bgCategory);
                    boardGameAddEditViewModel.addSelectedBgCategory(bgCategory);
                }
            }

            // Sets text to appear on spinner instead of selected categories appearing on it.
            multiSpinner.setAllText("Choose categories");
        }
    };

    /**
     * Sets the back arrow in the taskbar to go back to the previous activity.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
