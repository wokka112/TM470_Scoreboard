package com.floatingpanda.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BgCategoryRepository;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGameRepository;

import java.util.ArrayList;
import java.util.List;

public class BoardGameAddActivity extends AppCompatActivity {
    //TODO maybe remove this EXTRA_REPLY thing and simply change to a string??
    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private BoardGameRepository boardGameRepository;
    private EditText bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, descriptionEditText,
        notesEditText, houseRulesEditText;
    private CheckBox competitiveCheckBox, cooperativeCheckBox, solitaireCheckBox;
    private RadioGroup teamOptionsRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board_game);

        boardGameRepository = new BoardGameRepository(getApplication());

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

        final Button browseButton, cameraButton, saveButton, cancelButton;

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
                Toast.makeText(BoardGameAddActivity.this, "Cancel pressed",
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
                List<BgCategory> bgCategories = getBgCategories();
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

                /*
                //TODO remove popup warnings and instead direct people to the edit text in error and
                // inform them what they need to do to fix it?
                if (TextUtils.isEmpty(categoryEditText.getText())) {
                    AlertDialogHelper.popupWarning("You must enter a name for the category.", BgCategoryAddActivity.this);
                    return;
                }

                //TODO change bgcategoryrepository contains method to simply take a string bgcategoryname.
                String bgCategoryName = categoryEditText.getText().toString();
                BgCategory bgCategory = new BgCategory(bgCategoryName);

                Log.w("BgCatAddAct.java", "Includes category: " + bgCategoryRepository.contains(bgCategory));
                if (bgCategoryRepository.contains(bgCategory)) {
                    AlertDialogHelper.popupWarning("A category with that name already exists. " +
                            "You must enter a unique category name.", BgCategoryAddActivity.this);
                    return;
                }

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, bgCategory);
                setResult(RESULT_OK, replyIntent);
                finish();
                */
            }
        });
    }

    private BoardGame.PlayMode getPlayMode() {
        boolean competitive = competitiveCheckBox.isChecked();
        boolean cooperative = cooperativeCheckBox.isChecked();
        boolean solitaire = solitaireCheckBox.isChecked();

        BoardGame.PlayMode playMode = BoardGame.PlayMode.ERROR;

        if (competitive && cooperative && solitaire) {
            playMode = BoardGame.PlayMode.COMPETITIVE_OR_COOPERATIVE_OR_SOLITAIRE;
        } else if (cooperative && solitaire) {
            playMode = BoardGame.PlayMode.COOPERATIVE_OR_SOLITAIRE;
        } else if (competitive && solitaire) {
            playMode = BoardGame.PlayMode.COMPETITIVE_OR_SOLITAIRE;
        } else if (competitive && cooperative) {
            playMode = BoardGame.PlayMode.COMPETITIVE_OR_COOPERATIVE;
        } else if (competitive) {
            playMode = BoardGame.PlayMode.COMPETITIVE;
        } else if (cooperative) {
            playMode = BoardGame.PlayMode.COOPERATIVE;
        } else if (solitaire) {
            playMode = BoardGame.PlayMode.SOLITAIRE;
        }

        return playMode;
    }

    private List<BgCategory> getBgCategories() {
        List<BgCategory> bgCategories = new ArrayList<>();
        return bgCategories;
    }

    private BoardGame.TeamOption getTeamOption() {
        int id = teamOptionsRadioGroup.getCheckedRadioButtonId();

        switch(id) {
            case R.id.bgadd_no_teams_radiobutton:
                return BoardGame.TeamOption.NO_TEAMS;
            case R.id.bgadd_teams_allowed_radiobutton:
                return BoardGame.TeamOption.TEAMS_OR_SOLOS;
            case R.id.bgadd_teams_only_radiobutton:
                return BoardGame.TeamOption.TEAMS_OR_SOLOS;
            default:
                return BoardGame.TeamOption.ERROR;
        }
    }
}
