package com.floatingpanda.scoreboard.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.SearchableSpinnerAdapter;
import com.floatingpanda.scoreboard.data.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.viewmodels.BoardGameViewModel;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.List;

//TODO Add in competitive, cooperative or solitaire choice.
//TODO Add in functionality so teams or no teams choice affects things.
public class AddGameRecordActivity extends AppCompatActivity {

    private Button addPlayersButton;
    private RadioGroup teamsRadioGroup, playModeRadioGroup, winLoseRadioGroup;
    private RadioButton teamsRadioButton, noTeamsRadioButton, compRadioButton, coopRadioButton, soliRadioButton,
            wonRadioButton, lostRadioButton;
    private TextView winLoseRadioGroupHeader, playerCountTextView, difficultyTextView;
    private EditText playerCountEditText;
    private SearchableSpinner boardGameSpinner;

    private BoardGameViewModel boardGameViewModel;

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game_record);

        addPlayersButton = findViewById(R.id.add_game_record_add_players_button);

        teamsRadioGroup = findViewById(R.id.add_game_record_teams_setting_radio_group);
        teamsRadioButton = findViewById(R.id.add_game_record_radio_button_teams);
        noTeamsRadioButton = findViewById(R.id.add_game_record_radio_button_no_teams);

        playModeRadioGroup = findViewById(R.id.add_game_record_play_mode_radio_group);
        compRadioButton = findViewById(R.id.add_game_record_radio_button_competitive);
        coopRadioButton = findViewById(R.id.add_game_record_radio_button_cooperative);
        soliRadioButton = findViewById(R.id.add_game_record_radio_button_solitaire);

        winLoseRadioGroup = findViewById(R.id.add_game_record_win_lose_radio_group);
        wonRadioButton = findViewById(R.id.add_game_record_radio_button_won);
        lostRadioButton = findViewById(R.id.add_game_record_radio_button_lost);
        winLoseRadioGroupHeader = findViewById(R.id.add_game_record_win_lose_textview);

        playerCountEditText = findViewById(R.id.add_game_record_player_count_edittext);
        playerCountTextView = findViewById(R.id.add_game_record_player_count_header_textview);

        difficultyTextView = findViewById(R.id.add_game_record_difficulty_output);

        boardGameSpinner = findViewById(R.id.add_game_record_bg_spinner);

        //Populate board game spinner
        boardGameViewModel = new ViewModelProvider(this).get(BoardGameViewModel.class);
        boardGameViewModel.getAllBoardGamesWithBgCategoriesAndPlayModes().observe(this, new Observer<List<BoardGameWithBgCategoriesAndPlayModes>>() {
            @Override
            public void onChanged(List<BoardGameWithBgCategoriesAndPlayModes> boardGamesWithBgCategoriesAndPlayModes) {
                boardGameSpinner.setAdapter(new SearchableSpinnerAdapter(getApplicationContext(), boardGamesWithBgCategoriesAndPlayModes));
            }
        });

        //User selects board game from spinner and activates listener.
        boardGameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setViews((BoardGameWithBgCategoriesAndPlayModes) boardGameSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Get all details
        //Hit next and then setup fragments, one for each team.

        group = (Group) getIntent().getExtras().get("GROUP");

        addPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inputsValid()) {
                    return;
                }

                startChoosePlayersActivity();
            }
        });

        playModeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == compRadioButton.getId()) {
                    setToCompetitive();
                }

                if (checkedId == coopRadioButton.getId()) {
                    setToCooperative();
                }

                if (checkedId == soliRadioButton.getId()) {
                    setToSolitaire();
                }
            }
        });

        teamsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == teamsRadioButton.getId()) {
                    playerCountTextView.setText("Teams");
                }

                if (checkedId == noTeamsRadioButton.getId()) {
                    playerCountTextView.setText("Players");
                }
            }
        });
    }

    private void startChoosePlayersActivity() {
        int numOfTeams = Integer.parseInt(playerCountEditText.getText().toString());
        boolean solitaire = isSolitaire();

        Intent intent = new Intent(this, ChoosePlayersActivity.class);
        intent.putExtra("GROUP", group);
        intent.putExtra("NUM_OF_TEAMS", numOfTeams);
        intent.putExtra("SOLITAIRE_BOOL", solitaire);
        startActivity(intent);
    }

    public boolean inputsValid() {
        //TODO add in validity tests - empty string, players chosen, win/loss chosen if necessary, player count greater than 2 if competitive selected.
        if (playerCountEditText.getText().toString().isEmpty()) {
            Log.w("AddGameRecordAct.java", "Player count is empty.");
            return false;
        }

        return true;
    }

    private boolean isSolitaire() {
        if (playModeRadioGroup.getCheckedRadioButtonId() == R.id.add_game_record_radio_button_solitaire) {
            return true;
        }

        return false;
    }

    private void setViews(BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes) {
        //Set default playmode to first available option.
        List<PlayMode.PlayModeEnum> potentialPlayModes = boardGameWithBgCategoriesAndPlayModes.getPlayModeEnums();
        setPotentialPlayModes(potentialPlayModes);

        //Set team option to first available team option.
        BoardGame.TeamOption potentialTeamSettings = boardGameWithBgCategoriesAndPlayModes.getBoardGame().getTeamOptions();
        setPotentialTeamSettings(potentialTeamSettings);

        //Set difficulty to board game difficulty.
        difficultyTextView.setText(Integer.toString(boardGameWithBgCategoriesAndPlayModes.getBoardGame().getDifficulty()));
    }

    private void setPotentialPlayModes(List<PlayMode.PlayModeEnum> potentialPlayModes) {
        playModeRadioGroup.clearCheck();
        soliRadioButton.setClickable(false);
        soliRadioButton.setAlpha(0.5f);
        coopRadioButton.setClickable(false);
        coopRadioButton.setAlpha(0.5f);
        compRadioButton.setClickable(false);
        compRadioButton.setAlpha(0.5f);

        if (potentialPlayModes.contains(PlayMode.PlayModeEnum.SOLITAIRE)) {
            soliRadioButton.setClickable(true);
            soliRadioButton.setAlpha(1.0f);
            soliRadioButton.setChecked(true);
        }

        if (potentialPlayModes.contains(PlayMode.PlayModeEnum.COOPERATIVE)) {
            coopRadioButton.setClickable(true);
            coopRadioButton.setAlpha(1.0f);
            coopRadioButton.setChecked(true);
        }

        if (potentialPlayModes.contains(PlayMode.PlayModeEnum.COMPETITIVE)) {
            compRadioButton.setClickable(true);
            compRadioButton.setAlpha(1.0f);
            compRadioButton.setChecked(true);
        }
    }

    private void setPotentialTeamSettings(BoardGame.TeamOption potentialTeamSettings) {
        teamsRadioGroup.clearCheck();
        teamsRadioButton.setClickable(false);
        teamsRadioButton.setAlpha(0.5f);
        noTeamsRadioButton.setClickable(false);
        noTeamsRadioButton.setAlpha(0.5f);

        switch (potentialTeamSettings) {
            case NO_TEAMS:
                noTeamsRadioButton.setChecked(true);
                noTeamsRadioButton.setClickable(true);
                noTeamsRadioButton.setAlpha(1.0f);
                break;
            case TEAMS_ONLY:
                teamsRadioButton.setChecked(true);
                teamsRadioButton.setClickable(true);
                teamsRadioButton.setAlpha(1.0f);
                break;
            case TEAMS_AND_SOLOS_ALLOWED:
                teamsRadioButton.setChecked(true);
                teamsRadioButton.setClickable(true);
                noTeamsRadioButton.setClickable(true);
                teamsRadioButton.setAlpha(1.0f);
                noTeamsRadioButton.setAlpha(1.0f);
                break;
            default:
                break;
        }
    }

    //TODO maybe move "disabling" (setting to alpha and making non-clickable) into its own helper method?

    private void setToCompetitive() {
        //Unlock everything.
        teamsRadioGroup.setClickable(true);
        playerCountEditText.setClickable(true);
        playerCountEditText.setFocusable(true);

        //TODO this feels like spaghetti code, look into it.
        if (!teamsRadioButton.isClickable()) {
            noTeamsRadioButton.setChecked(true);
        } else if (!noTeamsRadioButton.isClickable()) {
            teamsRadioButton.setChecked(true);
        }

        //Visually represent this
        teamsRadioGroup.setAlpha(1.0f);
        playerCountEditText.setAlpha(1.0f);

        //Hide win lose options.
        winLoseRadioGroupHeader.setVisibility(View.GONE);
        winLoseRadioGroup.setVisibility(View.GONE);
    }

    private void setToCooperative() {
        //Check teams as chosen
        teamsRadioButton.setChecked(true);

        //Lock so only teams can be chosen
        teamsRadioGroup.setClickable(false);

        //Visually represent this
        teamsRadioGroup.setAlpha(0.5f);

        //Set teams to 1
        playerCountEditText.setText("1");
        playerCountEditText.setFocusable(false);
        playerCountEditText.setClickable(false);
        playerCountEditText.clearFocus();
        playerCountEditText.setAlpha(0.5f);

        //Show win lose options
        winLoseRadioGroupHeader.setVisibility(View.VISIBLE);
        winLoseRadioGroup.setVisibility(View.VISIBLE);
        winLoseRadioGroup.clearCheck();
    }

    private void setToSolitaire() {
        //Check no teams as chosen
        noTeamsRadioButton.setChecked(true);

        //Lock so only no teams can be chosen
        teamsRadioGroup.setClickable(false);

        //Visually represent this
        teamsRadioGroup.setAlpha(0.5f);

        //Set players to 1 and lock it
        playerCountEditText.setText("1");
        playerCountEditText.setFocusable(false);
        playerCountEditText.setClickable(false);
        playerCountEditText.clearFocus();
        playerCountEditText.setAlpha(0.5f);

        //Show win lose options
        winLoseRadioGroupHeader.setVisibility(View.VISIBLE);
        winLoseRadioGroup.setVisibility(View.VISIBLE);
        winLoseRadioGroup.clearCheck();
    }
}
