package com.floatingpanda.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.PlayMode;
import com.floatingpanda.scoreboard.viewmodels.BoardGameAddEditViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.thomashaertel.widget.MultiSpinner;

import java.util.ArrayList;
import java.util.List;

//TODO add in a clear categories button?

//TODO look into if I can get the close chip stuff working with removing the selected elements from the spinner.

//TODO keep searchable spinner comment stuff for when I make the gamerecord creation. It could be useful for
// finding and adding board games. Same for players.

public class BoardGameAddActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private BoardGameAddEditViewModel boardGameAddEditViewModel;

    private EditText bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, descriptionEditText,
        notesEditText, houseRulesEditText;
    private CheckBox competitiveCheckBox, cooperativeCheckBox, solitaireCheckBox;
    private RadioGroup teamOptionsRadioGroup;
    private ChipGroup chipGroup;
    private MultiSpinner multiSpinner;
    private ArrayAdapter<BgCategory> adapter;

    //private SearchableSpinner searchableSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_board_game);

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

        chipGroup = findViewById(R.id.bgadd_categories_chip_group);

        multiSpinner = findViewById(R.id.bgadd_multi_spinner);
        multiSpinner.setAllText("Choose categories");

        final Button browseButton, cameraButton, saveButton, cancelButton;

        boardGameAddEditViewModel.getAllBgCategories().observe(this, new Observer<List<BgCategory>>() {
            @Override
            public void onChanged(@NonNull final List<BgCategory> bgCategories) {
                adapter = new ArrayAdapter<BgCategory>(BoardGameAddActivity.this, android.R.layout.simple_spinner_item, bgCategories);
                multiSpinner.setAdapter(adapter, false, onSelectedListener);
            }
        });

        /*
        //searchableSpinner.setPositiveButton("Ok");


        searchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }

                Log.w("BoardGameAddAct.java", "Spinner position " + position + ": " + parent.getItemAtPosition(position).getClass());
                Log.w("BoardGameAddAct.java", "Spinner position " + position + ": " + parent.getItemAtPosition(position).toString());
                //TODO move this?
                BgCategory bgCategory = (BgCategory) parent.getItemAtPosition(position);
                addChip(bgCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        */

        browseButton = findViewById(R.id.bgadd_button_browse);
        cameraButton = findViewById(R.id.bgadd_button_camera);
        saveButton = findViewById(R.id.bgadd_button_save);
        cancelButton = findViewById(R.id.bgadd_button_cancel);

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BoardGameAddActivity.this, "Browse pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BoardGameAddActivity.this, "Camera pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

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

                if (!boardGameAddEditViewModel.addActivityInputsValid(BoardGameAddActivity.this, bgName, difficultyString,
                        minPlayersString, maxPlayersString)) {
                    return;
                }

                int difficulty = Integer.parseInt(difficultyString);
                int minPlayers = Integer.parseInt(minPlayersString);
                int maxPlayers = Integer.parseInt(maxPlayersString);
                BoardGame.TeamOption teamOption = getTeamOption();
                String description = descriptionEditText.getText().toString();
                String notes = notesEditText.getText().toString();
                String houseRules = houseRulesEditText.getText().toString();
                String imgFilePath = "TBA";
                List<BgCategory> bgCategories = boardGameAddEditViewModel.getSelectedBgCategories();
                List<PlayMode.PlayModeEnum> playModes = getPlayModes();

                BoardGame boardGame = new BoardGame(bgName, difficulty, minPlayers, maxPlayers, teamOption,
                        description, notes, houseRules, imgFilePath, bgCategories, playModes);

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, boardGame);
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

                //TODO look into making this better? Feels very clunky and long.
                int position = adapter.getPosition(bgCategory);
                boolean[] selected = multiSpinner.getSelected();
                selected[position] = false;

                multiSpinner.setSelected(selected);
            }
        });

        //TODO move out of here into the views or somewhere else?
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
    private List<PlayMode.PlayModeEnum> getPlayModes() {
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

     /*
    private void setSearchableSpinnerList() {
        searchableSpinner.setAdapter(viewModel.getSpinnerListAdapter(this));
    }
    */
}
