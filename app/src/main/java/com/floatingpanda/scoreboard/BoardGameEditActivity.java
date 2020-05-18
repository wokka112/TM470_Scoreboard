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
import com.floatingpanda.scoreboard.viewmodels.BoardGameAddEditViewModel;
import com.google.android.material.chip.ChipGroup;
import com.thomashaertel.widget.MultiSpinner;

import java.util.List;

//TODO 1. Get board game.
// 2. Fill details.
// 3. Update selected categories.
// 4. Updated spinner's selected list.

public class BoardGameEditActivity extends AppCompatActivity {
    //TODO maybe remove this EXTRA_REPLY thing and simply change to a string??
    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private BoardGameAddEditViewModel viewModel;

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
        setContentView(R.layout.activity_add_board_game);

        viewModel = new ViewModelProvider(this).get(BoardGameAddEditViewModel.class);

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
        Log.w("BoardGameEditAct.java", "Bg: " + boardGame.getBgName());

        setDetails(boardGame);

        final Button browseButton, cameraButton, saveButton, cancelButton;

        viewModel.getAllBgCategories().observe(this, new Observer<List<BgCategory>>() {
            @Override
            public void onChanged(@NonNull final List<BgCategory> bgCategories) {
                //When list changed, refresh allbgcategories list and then take the selected list from it.
                Log.w("BoardGameAddAct.java", "Bgcategories: " + bgCategories);
                viewModel.setAllBgCategoriesNotLive(bgCategories);
                //setSearchableSpinnerList();
                setMultiSpinnerList();
                multiSpinner.setSelected(viewModel.getSelected(boardGame.getBgCategories()));
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
                Toast.makeText(BoardGameEditActivity.this, "Cancel pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //TODO add in warnings for empty or non-unique edittexts.

                String bgName = bgNameEditText.getText().toString();
                int difficulty = Integer.parseInt(difficultyEditText.getText().toString());
                int minPlayers = Integer.parseInt(minPlayersEditText.getText().toString());
                int maxPlayers = Integer.parseInt(maxPlayersEditText.getText().toString());
                List<BgCategory> bgCategories = viewModel.getSelectedBgCategories();
                BoardGame.PlayMode playMode = getPlayMode();
                BoardGame.TeamOption teamOption = getTeamOption();
                String description = descriptionEditText.getText().toString();
                String notes = notesEditText.getText().toString();
                String houseRules = houseRulesEditText.getText().toString();
                String imgFilePath = "TBA";

                BoardGame boardGame = new BoardGame(bgName, difficulty, minPlayers, maxPlayers, teamOption,
                        playMode, description, notes, houseRules, imgFilePath, bgCategories);

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, boardGame);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }

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

    private void setPlayModeCheckBoxes(BoardGame.PlayMode playMode) {
        switch (playMode) {
            case COMPETITIVE:
                Log.w("BoardGameEditAct.java", "PlayModeCheckBoxes: Competitive");
                competitiveCheckBox.setChecked(true);
                break;
            case COOPERATIVE:
                Log.w("BoardGameEditAct.java", "PlayModeCheckBoxes: Cooperative");
                cooperativeCheckBox.setChecked(true);
                break;
            case SOLITAIRE:
                Log.w("BoardGameEditAct.java", "PlayModeCheckBoxes: Solitaire");
                solitaireCheckBox.setChecked(true);
                break;
            case COMPETITIVE_OR_COOPERATIVE:
                Log.w("BoardGameEditAct.java", "PlayModeCheckBoxes: Competitive or Cooperative");
                competitiveCheckBox.setChecked(true);
                cooperativeCheckBox.setChecked(true);
                break;
            case COOPERATIVE_OR_SOLITAIRE:
                Log.w("BoardGameEditAct.java", "PlayModeCheckBoxes: Cooperative or Solitaire");
                cooperativeCheckBox.setChecked(true);
                solitaireCheckBox.setChecked(true);
                break;
            case COMPETITIVE_OR_SOLITAIRE:
                Log.w("BoardGameEditAct.java", "PlayModeCheckBoxes: Competitive or Solitaire");
                competitiveCheckBox.setChecked(true);
                solitaireCheckBox.setChecked(true);
                break;
            case COMPETITIVE_OR_COOPERATIVE_OR_SOLITAIRE:
                Log.w("BoardGameEditAct.java", "PlayModeCheckBoxes: Competitive or Cooperative or Solitaire");
                competitiveCheckBox.setChecked(true);
                cooperativeCheckBox.setChecked(true);
                solitaireCheckBox.setChecked(true);
                break;
            case ERROR:
                Log.w("BoardGameEditAct.java", "Play Mode: error");
                break;
            default:
                break;
        }
    }

    private void setTeamOptionsRadioGroup(BoardGame.TeamOption teamOption) {
        switch (teamOption) {
            case NO_TEAMS:
                noTeamsRadioButton.setChecked(true);
            case TEAMS_OR_SOLOS:
                teamsOrSoloRadioButton.setChecked(true);
            case TEAMS_ONLY:
                teamsOnlyRadioButton.setChecked(true);
            case ERROR:
                Log.w("BoardGameEditAct.java", "Team option: Error");
            default:
                break;
        }
    }

    // Deals with presentation, may be useful for the edit activity.
    private void setChipGroupChips(List<BgCategory> bgCategories) {
        clearChips();
        for (BgCategory bgCategory : bgCategories) {
            addChip(bgCategory);
        }
    }

    private void addChip(BgCategory bgCategory) {
        chipGroup.addView(viewModel.createChip(multiSpinner, chipGroup, bgCategory));
    }

    private void clearChips() {
        chipGroup.removeAllViews();
        viewModel.clearSelectedBgCategories();
    }

    private void clearSelected() {
        clearChips();
        multiSpinner.setSelected(new boolean[viewModel.getAdapter(this).getCount()]);
    }

    private void setMultiSpinnerList() {
        multiSpinner.setAdapter(viewModel.getAdapter(this), false, onSelectedListener);
    }

    private BoardGame.PlayMode getPlayMode() {
        boolean competitive = competitiveCheckBox.isChecked();
        boolean cooperative = cooperativeCheckBox.isChecked();
        boolean solitaire = solitaireCheckBox.isChecked();

        return viewModel.getPlayMode(competitive, cooperative, solitaire);
    }

    private BoardGame.TeamOption getTeamOption() {
        int checkboxId = teamOptionsRadioGroup.getCheckedRadioButtonId();

        return viewModel.getTeamOption(checkboxId);
    }

    //TODO remove this method, it is for testing purposes.
    private void printBgCategories(List<BgCategory> bgCategories) {
        Log.w("BoardGameAddAct.java", "Printing categories:");
        int i = 1;
        for (BgCategory bgCategory : bgCategories) {
            Log.w("BoardGameAddAct.java", "BgCategory " + i + ": " + bgCategory.getCategoryName());
        }
    }

    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            clearChips();
            boolean noneSelected = true;
            for (int i = 0; i < selected.length; i++) {
                Log.w("BoardGameEditAct.Java", "Selected " + i + ":" + selected[i]);
                if (selected[i] == true) {
                    BgCategory bgCategory = viewModel.getAdapterItem(i);
                    addChip(bgCategory);
                    noneSelected = false;
                }
            }

            // Sets text to appear on spinner instead of selected categories appearing on it.
            multiSpinner.setAllText("Choose categories");
        }
    };
}
