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
 * View for adding board games to the database.
 */
public class BoardGameAddActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private BoardGameAddEditViewModel boardGameAddEditViewModel;

    private EditText bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, descriptionEditText,
        notesEditText, houseRulesEditText;
    private CheckBox competitiveCheckBox, cooperativeCheckBox, solitaireCheckBox;
    private RadioGroup teamOptionsRadioGroup;
    private RadioButton teamsOnlyRadioButton;
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
        teamsOnlyRadioButton = findViewById(R.id.bgadd_teams_only_radiobutton);

        chipGroup = findViewById(R.id.bgadd_categories_chip_group);

        multiSpinner = findViewById(R.id.bgadd_multi_spinner);
        multiSpinner.setAllText("Choose categories");

        final Button saveButton, cancelButton;

        boardGameAddEditViewModel.getAllBgCategories().observe(this, new Observer<List<BgCategory>>() {
            @Override
            public void onChanged(@NonNull final List<BgCategory> bgCategories) {
                adapter = new ArrayAdapter<BgCategory>(BoardGameAddActivity.this, android.R.layout.simple_spinner_item, bgCategories);
                multiSpinner.setAdapter(adapter, false, onSelectedListener);
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
                String bgName = bgNameEditText.getText().toString();
                String difficultyString = difficultyEditText.getText().toString();
                String minPlayersString = minPlayersEditText.getText().toString();
                String maxPlayersString = maxPlayersEditText.getText().toString();
                BoardGame.TeamOption teamOption = getTeamOption();
                List<PlayMode.PlayModeEnum> playModeEnums = getPlayModeEnums();

                if (!boardGameAddEditViewModel.addActivityInputsValid(bgNameEditText, difficultyEditText,
                        minPlayersEditText, maxPlayersEditText, teamOption, teamsOnlyRadioButton, playModeEnums,
                        solitaireCheckBox,false)) {
                    return;
                }

                int difficulty = Integer.parseInt(difficultyString);
                int minPlayers = Integer.parseInt(minPlayersString);
                int maxPlayers = Integer.parseInt(maxPlayersString);
                String description = descriptionEditText.getText().toString();
                String notes = notesEditText.getText().toString();
                String houseRules = houseRulesEditText.getText().toString();

                BoardGame boardGame = new BoardGame(bgName, difficulty, minPlayers, maxPlayers, teamOption,
                        description, notes, houseRules);

                List<BgCategory> bgCategories = boardGameAddEditViewModel.getSelectedBgCategories();
                BoardGameWithBgCategories bgWithBgCategories = new BoardGameWithBgCategories(boardGame, bgCategories);

                BoardGameWithBgCategoriesAndPlayModes bgWithBgCategoriesAndPlayModes =
                        BoardGameWithBgCategoriesAndPlayModes.createUsingPlayModeEnumList(bgWithBgCategories, playModeEnums);

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, bgWithBgCategoriesAndPlayModes);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }

    /**
     * Adds a chip representing bgCategory to the view.
     * @param bgCategory a BgCategory
     */
    private void addChip(BgCategory bgCategory) {
        chipGroup.addView(createChip(bgCategory));
    }

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

        boardGameAddEditViewModel.addSelectedBgCategory(bgCategory);

        return chip;
    }

    /**
     * Clears all chips from the view.
     */
    private void clearChips() {
        chipGroup.removeAllViews();
        boardGameAddEditViewModel.clearSelectedBgCategories();
    }

    /**
     * Gets the list of playmodes the board game can be played in - competitive, cooperative,
     * solitaire.
     * @return a list of PlayMode.PlayModeEnum representing viable play modes for the board game.
     */
    private List<PlayMode.PlayModeEnum> getPlayModeEnums() {
        boolean competitive = competitiveCheckBox.isChecked();
        boolean cooperative = cooperativeCheckBox.isChecked();
        boolean solitaire = solitaireCheckBox.isChecked();

        return boardGameAddEditViewModel.getPlayModes(competitive, cooperative, solitaire);
    }

    /**
     * Returns the team options the board game can use - no teams, teams and solos, teams only.
     * @return a list of BoardGame.TeamOption representing how the game can be played with teams
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
