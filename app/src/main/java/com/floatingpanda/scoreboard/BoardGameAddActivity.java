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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BgCategoryRepository;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGameRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class BoardGameAddActivity extends AppCompatActivity {
    //TODO maybe remove this EXTRA_REPLY thing and simply change to a string??
    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private final int EDIT_ASSIGNED_CATEGORIES = 1;

    private List<BgCategory> allBgCategories;
    private List<BgCategory> selectedBgCategories;

    private BoardGameRepository boardGameRepository;
    private BgCategoryRepository bgCategoryRepository;

    private TextView categoriesTextView;
    private EditText bgNameEditText, difficultyEditText, minPlayersEditText, maxPlayersEditText, descriptionEditText,
        notesEditText, houseRulesEditText;
    private CheckBox competitiveCheckBox, cooperativeCheckBox, solitaireCheckBox;
    private RadioGroup teamOptionsRadioGroup;
    private ChipGroup chipGroup;

    SearchableSpinner searchableSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board_game);

        boardGameRepository = new BoardGameRepository(getApplication());
        bgCategoryRepository = new BgCategoryRepository(getApplication());

        categoriesTextView = findViewById(R.id.bgadd_categories_output);

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

        searchableSpinner = findViewById(R.id.bgAdd_searchable_spinner);

        final Button browseButton, cameraButton, saveButton, cancelButton;

        List<BgCategory> bgCategories = new ArrayList<>();
        bgCategories.add(new BgCategory("Strategy"));
        bgCategories.add(new BgCategory("Luck"));
        bgCategories.add(new BgCategory("Ameritrash"));
        bgCategories.add(new BgCategory("Ben smells"));

        setBgCategories(bgCategories);
        setAllBgCategories(bgCategories);

        setSearchableSpinnerList();

        searchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.w("BoardGameAddAct.java", "Spinner position " + position + ": " + parent.getItemAtPosition(position).getClass());
                Log.w("BoardGameAddAct.java", "Spinner position " + position + ": " + parent.getItemAtPosition(position).toString());
                //TODO move this?
                BgCategory bgCategory = (BgCategory) parent.getItemAtPosition(position);

                selectedBgCategories.add(bgCategory);
                addChip(bgCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
            }
        });
    }

    //TODO This is adding logic into the presentation layer I fear. I need to look into moving this elsewhere,
    // but I don't want more viewmodels and stuff. Maybe I should consider the add/edit activities are more than
    // just views...

    private void setBgCategories(List<BgCategory> bgCategories) {
        this.selectedBgCategories = new ArrayList<BgCategory>(bgCategories);
        //TODO remove printBgCategories(), it is for testing purposes.
        printBgCategories();
        //categoriesTextView.setText(createCategoriesString(this.bgCategories));
        setChipGroupChips(bgCategories);
    }

    private void removeBgCategory(BgCategory bgCategory) {
        this.selectedBgCategories.remove(bgCategory);
    }

    private String createCategoriesString(List<BgCategory> categoriesList) {
        if (categoriesList == null || categoriesList.isEmpty()) {
            return "None";
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;
        sb.append(categoriesList.get(i));
        i++;

        while (i < categoriesList.size()) {
            sb.append(", ");
            sb.append(categoriesList.get(i).getCategoryName());
            i++;
        }

        return sb.toString();
    }

    private List<BgCategory> getBgCategories() {
        return selectedBgCategories;
    }

    //TODO remove this method, it is for testing purposes.
    private void printBgCategories() {
        Log.w("BoardGameAddAct.java", "Printing categories:");
        int i = 1;
        for (BgCategory bgCategory : selectedBgCategories) {
            Log.w("BoardGameAddAct.java", "BgCategory " + i + ": " + bgCategory.getCategoryName());
        }
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

    private void setChipGroupChips(List<BgCategory> bgCategories) {
        chipGroup.removeAllViews();
        for (BgCategory bgCategory : bgCategories) {
            addChip(bgCategory);
        }
    }

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
                //TODO remove this log message and printbgcategories, they are for testing purposes.
                Log.w("BoardGameAddAct.java", "Deleting: " + bgCategory.getCategoryName());
                removeBgCategory(bgCategory);
                printBgCategories();
                chipGroup.removeView(chip);
            }
        });

        return chip;
    }

    private void setAllBgCategories(List<BgCategory> bgCategories) {
        this.allBgCategories = bgCategories;
    }

    private List<BgCategory> getAllBgCategories() {
        return this.allBgCategories;
    }

    private void setSearchableSpinnerList() {

        ArrayAdapter<BgCategory> adapter = new ArrayAdapter<> (
                this, android.R.layout.simple_spinner_item, getAllBgCategories()
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchableSpinner.setAdapter(adapter);
    }

}
