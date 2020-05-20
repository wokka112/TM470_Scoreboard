package com.floatingpanda.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.google.android.material.chip.ChipGroup;
import com.thomashaertel.widget.MultiSpinner;

import java.util.List;

public class BoardGameEditActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY_ORIGINAL_BG = "com.floatingpanda.scoreboard.REPLY_ORIGINAL_BG";
    public static final String EXTRA_REPLY_EDITED_BG = "com.floatingpanda.scoreboard.REPLY_EDITED_BG";

    private BoardGameAddEditViewModel boardGameAddEditViewModel;

    private EditText bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, descriptionEditText,
            notesEditText, houseRulesEditText;
    private CheckBox competitiveCheckBox, cooperativeCheckBox, solitaireCheckBox;
    private RadioGroup teamOptionsRadioGroup;
    private RadioButton noTeamsRadioButton, teamsOrSoloRadioButton, teamsOnlyRadioButton;
    private ChipGroup chipGroup;
    private MultiSpinner multiSpinner;

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

        noTeamsRadioButton = findViewById(R.id.bgadd_no_teams_radiobutton);
        teamsOrSoloRadioButton = findViewById(R.id.bgadd_teams_allowed_radiobutton);
        teamsOnlyRadioButton = findViewById(R.id.bgadd_teams_only_radiobutton);

        chipGroup = findViewById(R.id.bgadd_categories_chip_group);

        multiSpinner = findViewById(R.id.bgadd_multi_spinner);
        multiSpinner.setAllText("Choose categories");

        BoardGame boardGame = (BoardGame) getIntent().getExtras().get("BOARDGAME");

        setDetails(boardGame);

        final Button browseButton, cameraButton, saveButton, cancelButton;

        boardGameAddEditViewModel.getAllBgCategories().observe(this, new Observer<List<BgCategory>>() {
            @Override
            public void onChanged(@NonNull final List<BgCategory> bgCategories) {
                boardGameAddEditViewModel.setAllBgCategoriesNotLive(bgCategories);
                setupMultiSpinnerList();
                //TODO work out where to put this setselected.
                multiSpinner.setSelected(boardGameAddEditViewModel.getSelected(boardGame.getBgCategories()));
            }
        });

        browseButton = findViewById(R.id.bgadd_button_browse);
        cameraButton = findViewById(R.id.bgadd_button_camera);
        saveButton = findViewById(R.id.bgadd_button_save);
        cancelButton = findViewById(R.id.bgadd_button_cancel);

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BoardGameEditActivity.this, "Browse pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BoardGameEditActivity.this, "Camera pressed",
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

                //TODO add in warnings for empty or non-unique edittexts.

                String bgName = bgNameEditText.getText().toString();
                int difficulty = Integer.parseInt(difficultyEditText.getText().toString());
                int minPlayers = Integer.parseInt(minPlayersEditText.getText().toString());
                int maxPlayers = Integer.parseInt(maxPlayersEditText.getText().toString());
                BoardGame.TeamOption teamOption = getTeamOption();
                String description = descriptionEditText.getText().toString();
                String notes = notesEditText.getText().toString();
                String houseRules = houseRulesEditText.getText().toString();
                String imgFilePath = "TBA";
                List<BgCategory> bgCategories = boardGameAddEditViewModel.getSelectedBgCategories();
                List<PlayMode.PlayModeEnum> playModes = getPlayModes();

                //TODO look into a different way of doing this? Maybe use a constructor that takes an
                // id rather than setting the id.
                BoardGame editedBoardGame = new BoardGame(bgName, difficulty, minPlayers, maxPlayers, teamOption,
                        description, notes, houseRules, imgFilePath, bgCategories, playModes);
                editedBoardGame.setId(boardGame.getId());

                Log.w("BoardGameEditAct.java", "Original Bg: " + boardGame.getId() + ", " + boardGame.getBgName());
                Log.w("BoardGameEditAct.java", "Edited Bg: " + editedBoardGame.getId() + ", " + editedBoardGame.getBgName());

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY_ORIGINAL_BG, boardGame);
                replyIntent.putExtra(EXTRA_REPLY_EDITED_BG, editedBoardGame);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }

    /**
     * Sets the views to the details from boardGame.
     * @param boardGame a BoardGame
     */
    private void setDetails(BoardGame boardGame) {
        bgNameEditText.setText(boardGame.getBgName());
        difficultyEditText.setText(Integer.toString(boardGame.getDifficulty()));
        minPlayersEditText.setText(Integer.toString(boardGame.getMinPlayers()));
        maxPlayersEditText.setText(Integer.toString(boardGame.getMaxPlayers()));
        descriptionEditText.setText(boardGame.getDescription());
        notesEditText.setText(boardGame.getNotes());
        houseRulesEditText.setText(boardGame.getHouseRules());
        setPlayModeCheckBoxes(boardGame.getPlayModes());
        setTeamOptionsRadioGroup(boardGame.getTeamOptions());
        setChipGroupChips(boardGame.getBgCategories());
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
            case TEAMS_OR_SOLOS:
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
        chipGroup.addView(boardGameAddEditViewModel.createChip(multiSpinner, chipGroup, bgCategory));
    }

    /**
     * Clears the chips from the chip group and removes all the selected board game categories.
     */
    private void clearChips() {
        chipGroup.removeAllViews();
        boardGameAddEditViewModel.clearSelectedBgCategories();
    }

    /**
     * Sets up the multispinner list to hold all the board game categories in the app.
     */
    private void setupMultiSpinnerList() {
        multiSpinner.setAdapter(boardGameAddEditViewModel.getAdapter(this), false, onSelectedListener);
    }

    /**
     * Returns a list of PlayModeEnum enums to represent the play mode checkboxes that are checked
     * in the activity.
     * @return a list of PlayModeEnum representing the checked play mode checkboxes
     */
    private List<PlayMode.PlayModeEnum> getPlayModes() {
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
                Log.w("BoardGameEditAct.Java", "Selected " + i + ":" + selected[i]);
                if (selected[i] == true) {
                    BgCategory bgCategory = boardGameAddEditViewModel.getAdapterItem(i);
                    addChip(bgCategory);
                }
            }

            // Sets text to appear on spinner instead of selected categories appearing on it.
            multiSpinner.setAllText("Choose categories");
        }
    };
}
