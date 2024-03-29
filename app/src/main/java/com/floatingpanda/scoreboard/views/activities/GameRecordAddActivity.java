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

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.adapters.SearchableSpinnerAdapter;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.utils.AlertDialogHelper;
import com.floatingpanda.scoreboard.viewmodels.BoardGameViewModel;
import com.floatingpanda.scoreboard.viewmodels.GameRecordAddViewModel;
import com.floatingpanda.scoreboard.viewmodels.GameRecordViewModel;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//TODO refactor to follow MVVM architecture. Put most logic into GameRecordAddViewModel.
// May need to switch to using data binding in XML to support MVVM architecture here.

/**
 * View for adding game records to the database.
 */
public class GameRecordAddActivity extends AppCompatActivity {

    private final int CHOOSE_PLAYERS_REQUEST_CODE = 1;

    public static final String EXTRA_REPLY_PLAYERS = "com.floatingpanda.scoreboard.REPLY_PLAYERS";
    public static final String EXTRA_REPLY_GAME_RECORD = "com.floatingpanda.scoreboard.REPLY_GAME_RECORD";

    private Button addPlayersButton;
    private RadioGroup teamsRadioGroup, playModeRadioGroup, winLoseRadioGroup;
    private RadioButton teamsRadioButton, noTeamsRadioButton, compRadioButton, coopRadioButton, soliRadioButton,
            wonRadioButton, lostRadioButton;
    private TextView winLoseRadioGroupHeader, playerCountTextView, difficultyTextView;
    private EditText dateEditText, timeEditText, playerCountEditText;
    private SearchableSpinner boardGameSpinner;

    private Calendar calendar = Calendar.getInstance();

    private GameRecordAddViewModel gameRecordAddViewModel;

    private Group group;
    private String boardGameName;

    private KeyListener editTextKeyListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game_record);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        dateEditText = findViewById(R.id.add_game_record_datetime_edittext);
        timeEditText = findViewById(R.id.add_game_record_time_edittext);

        editTextKeyListener = playerCountEditText.getKeyListener();

        setDateEditText();
        setTimeEditText();

        group = (Group) getIntent().getExtras().get("GROUP");

        gameRecordAddViewModel = new ViewModelProvider(this).get(GameRecordAddViewModel.class);
        gameRecordAddViewModel.getAllBoardGamesWithBgCategoriesAndPlayModes().observe(this, new Observer<List<BoardGameWithBgCategoriesAndPlayModes>>() {
            @Override
            public void onChanged(List<BoardGameWithBgCategoriesAndPlayModes> boardGamesWithBgCategoriesAndPlayModes) {
                boardGameSpinner.setAdapter(new SearchableSpinnerAdapter(getApplicationContext(), boardGamesWithBgCategoriesAndPlayModes));
            }
        });

        boardGameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes = (BoardGameWithBgCategoriesAndPlayModes) boardGameSpinner.getSelectedItem();
                setViews(boardGameWithBgCategoriesAndPlayModes);
                boardGameName = boardGameWithBgCategoriesAndPlayModes.getBoardGame().getBgName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePickerDialog();
            }
        });

        addPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean winLoseRadioGroupChecked = true;
                if (winLoseRadioGroup.getCheckedRadioButtonId() == -1) {
                    winLoseRadioGroupChecked = false;
                }

                if(!gameRecordAddViewModel.inputsValid(getApplicationContext(), playerCountEditText, getPlayModePlayed(), winLoseRadioGroupChecked, false)) {
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
                String playerCountText = getString(R.string.players_colon_header);
                if (checkedId == teamsRadioButton.getId()) {
                    playerCountText = getString(R.string.teams_colon_header);
                }
                playerCountTextView.setText(playerCountText);
            }
        });
    }

    private void startChoosePlayersActivity() {
        GameRecord gameRecord = createGameRecord();

        Intent intent = new Intent(this, ChoosePlayersActivity.class);
        intent.putExtra("GROUP", group);
        intent.putExtra("GAME_RECORD", gameRecord);
        startActivityForResult(intent, CHOOSE_PLAYERS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CHOOSE_PLAYERS_REQUEST_CODE) {
            ArrayList<TeamOfPlayers> teamsOfPlayers = (ArrayList) data.getExtras().get(ChoosePlayersActivity.EXTRA_REPLY);
            GameRecord gameRecord = createGameRecord();

            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY_PLAYERS, teamsOfPlayers);
            replyIntent.putExtra(EXTRA_REPLY_GAME_RECORD, gameRecord);
            setResult(RESULT_OK, replyIntent);
            finish();
        }
    }

    /**
     * Returns a new game record with details from the view's edittexts and an id of 0, ready for
     * insertion into the database.
     * @return
     */
    private GameRecord createGameRecord() {
        int difficulty = Integer.parseInt(difficultyTextView.getText().toString());
        Date date = calendar.getTime();

        PlayMode.PlayModeEnum playModePlayed = getPlayModePlayed();
        if (playModePlayed == PlayMode.PlayModeEnum.ERROR) {
            Toast.makeText(this, "Playmode ERROR returned", Toast.LENGTH_SHORT).show();
        }

        boolean won = false;
        if (playModePlayed == PlayMode.PlayModeEnum.COOPERATIVE
                || playModePlayed == PlayMode.PlayModeEnum.SOLITAIRE) {
            won = getWon();
        }

        boolean teams = getTeams();
        int noOfTeams = Integer.parseInt(playerCountEditText.getText().toString());

        GameRecord gameRecord = new GameRecord(group.getId(), boardGameName, difficulty, date, teams, playModePlayed, noOfTeams, won);
        return gameRecord;
    }

    private void openDatePickerDialog() {
        // Get Current Date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void openTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true);

        timePickerDialog.show();
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

    private void setDateEditText() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        dateEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
    }

    private void setTimeEditText() {
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (minute < 10) {
            timeEditText.setText(hourOfDay + ":0" + minute);
        } else {
            timeEditText.setText(hourOfDay + ":" + minute);
        }
    }

    /**
     * Sets the views to what should be interactable for competitive games.
     */
    private void setToCompetitive() {
        //Enable everything
        enableTeamsRadioGroup();
        makePlayerCountEditTextEditable();

        // Set teams radio button to correct one based on which is clickable
        // This is based on what the board game selected allows - teams only, no teams, either.
        if (!teamsRadioButton.isClickable()) {
            noTeamsRadioButton.setChecked(true);
        } else if (!noTeamsRadioButton.isClickable()) {
            teamsRadioButton.setChecked(true);
        }

        //Set player count to minimum number
        playerCountEditText.setText("2");

        //Hide win lose options.
        hideWinLoseRadioGroup();
    }

    /**
     * Sets the views to what should be interactable for cooperative games.
     */
    private void setToCooperative() {
        //Check teams as chosen
        teamsRadioButton.setChecked(true);

        //Lock so only teams can be chosen
        disableTeamsRadioGroup();

        //Set teams to 1
        playerCountEditText.setText("1");
        makePlayerCountEditTextUneditable();

        //Show win lose options
        displayWinLoseRadioGroup();
    }

    /**
     * Sets the views to what should be interactable for solitaire games.
     */
    private void setToSolitaire() {
        //Check no teams as chosen
        noTeamsRadioButton.setChecked(true);
        disableTeamsRadioGroup();

        //Set players to 1 and lock it
        playerCountEditText.setText("1");
        makePlayerCountEditTextUneditable();

        //Show win lose options
        displayWinLoseRadioGroup();
    }

    /**
     * Sets the potential play modes - competitive, cooperative and solitaire - that can be chosen
     * by the user, and makes their relevant radio buttons interactable.
     * @param potentialPlayModes
     */
    private void setPotentialPlayModes(List<PlayMode.PlayModeEnum> potentialPlayModes) {
        playModeRadioGroup.clearCheck();
        disablePlaymodeRadioButtons();

        if (potentialPlayModes.contains(PlayMode.PlayModeEnum.SOLITAIRE)) {
            enableSolitaireRadioButton();
            soliRadioButton.setChecked(true);
        }

        if (potentialPlayModes.contains(PlayMode.PlayModeEnum.COOPERATIVE)) {
            enableCooperativeRadioButton();
            coopRadioButton.setChecked(true);
        }

        if (potentialPlayModes.contains(PlayMode.PlayModeEnum.COMPETITIVE)) {
            enableCompetitiveRadioButton();
            compRadioButton.setChecked(true);
        }
    }

    /**
     * Sets the potential team settings that can be selected by the user - no teams or teams - and
     * makes their relevant radio buttons interactable.
     * @param potentialTeamSettings
     */
    private void setPotentialTeamSettings(BoardGame.TeamOption potentialTeamSettings) {
        teamsRadioGroup.clearCheck();
        disableTeamsRadioButton();
        disableNoTeamsRadioButton();

        switch (potentialTeamSettings) {
            case NO_TEAMS:
                noTeamsRadioButton.setChecked(true);
                enableNoTeamsRadioButton();
                break;
            case TEAMS_ONLY:
                teamsRadioButton.setChecked(true);
                enableTeamsRadioButton();
                break;
            case TEAMS_AND_SOLOS_ALLOWED:
                teamsRadioButton.setChecked(true);
                enableTeamsRadioButton();
                enableNoTeamsRadioButton();
                break;
            default:
                break;
        }
    }

    /**
     * Returns a playmode enum based on which playmode button is selected - competitive, cooperative
     * or solitaire.
     * @return
     */
    private PlayMode.PlayModeEnum getPlayModePlayed() {
        switch (playModeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.add_game_record_radio_button_competitive:
                return PlayMode.PlayModeEnum.COMPETITIVE;
            case R.id.add_game_record_radio_button_cooperative:
                return PlayMode.PlayModeEnum.COOPERATIVE;
            case R.id.add_game_record_radio_button_solitaire:
                return PlayMode.PlayModeEnum.SOLITAIRE;
            default:
                return PlayMode.PlayModeEnum.ERROR;
        }
    }

    /**
     * Returns true or false depending on whether the no teams or the teams button is checked. If
     * teams is checked, returns true. If no teams is checked returns false.
     *
     * In the case there is an error and neither are checked, returns false and logs an error message.
     * @return
     */
    private boolean getTeams() {
        switch (teamsRadioGroup.getCheckedRadioButtonId()) {
            case R.id.add_game_record_radio_button_teams:
                return true;
            case R.id.add_game_record_radio_button_no_teams:
                return false;
            default:
                Log.e("AddGameRecordAct.java", "Failed to return proper result when getting teams.");
                return false;
        }
    }

    /**
     * Returns a boolean representing whether the team in a cooperative or solitaire game lost or
     * won. If the team won, returns true. If the team lost, returns false.
     *
     * If neither option is selected, which is possible, returns false and logs a warning.
     * @return
     */
    private boolean getWon() {
        switch (winLoseRadioGroup.getCheckedRadioButtonId()) {
            case R.id.add_game_record_radio_button_won:
                return true;
            case R.id.add_game_record_radio_button_lost:
                return false;
            default:
                Log.w("AddGameRecordAct.java", "Failed to return proper result when getting won.");
                return false;
        }
    }

    private void enableSolitaireRadioButton() {
        soliRadioButton.setClickable(true);
        soliRadioButton.setAlpha(1.0f);
    }

    private void enableCooperativeRadioButton() {
        coopRadioButton.setClickable(true);
        coopRadioButton.setAlpha(1.0f);
    }

    private void enableCompetitiveRadioButton() {
        compRadioButton.setClickable(true);
        compRadioButton.setAlpha(1.0f);
    }

    private void disablePlaymodeRadioButtons() {
        disableSolitaireRadioButton();
        disableCooperativeRadioButton();
        disableCompetitiveRadioButton();
    }

    private void disableSolitaireRadioButton() {
        soliRadioButton.setClickable(false);
        soliRadioButton.setAlpha(0.5f);
    }

    private void disableCooperativeRadioButton() {
        coopRadioButton.setClickable(false);
        coopRadioButton.setAlpha(0.5f);
    }

    private void disableCompetitiveRadioButton() {
        compRadioButton.setClickable(false);
        compRadioButton.setAlpha(0.5f);
    }

    private void enableTeamsRadioButton() {
        teamsRadioButton.setClickable(true);
        teamsRadioButton.setAlpha(1.0f);
    }

    private void enableNoTeamsRadioButton() {
        noTeamsRadioButton.setClickable(true);
        noTeamsRadioButton.setAlpha(1.0f);
    }

    private void disableTeamsRadioButton() {
        teamsRadioButton.setClickable(false);
        teamsRadioButton.setAlpha(0.5f);
    }

    private void disableNoTeamsRadioButton() {
        noTeamsRadioButton.setClickable(false);
        noTeamsRadioButton.setAlpha(0.5f);
    }

    private void makePlayerCountEditTextEditable() {
        playerCountEditText.setKeyListener(editTextKeyListener);
        playerCountEditText.setFocusable(true);
        playerCountEditText.setFocusableInTouchMode(true);
        playerCountEditText.requestFocus();
        playerCountEditText.setAlpha(1.0f);
    }

    private void makePlayerCountEditTextUneditable() {
        playerCountEditText.setKeyListener(null);
        playerCountEditText.setFocusable(false);
        playerCountEditText.clearFocus();
        playerCountEditText.setAlpha(0.5f);
    }

    private void hideWinLoseRadioGroup() {
        winLoseRadioGroupHeader.setVisibility(View.GONE);
        winLoseRadioGroup.setVisibility(View.GONE);
    }

    private void displayWinLoseRadioGroup() {
        winLoseRadioGroupHeader.setVisibility(View.VISIBLE);
        winLoseRadioGroup.setVisibility(View.VISIBLE);
        winLoseRadioGroup.clearCheck();
    }

    private void enableTeamsRadioGroup() {
        teamsRadioGroup.setClickable(true);
        teamsRadioGroup.setAlpha(1.0f);
        enableNoTeamsRadioButton();
        enableTeamsRadioButton();
    }

    private void disableTeamsRadioGroup() {
        teamsRadioGroup.setClickable(false);
        teamsRadioGroup.setAlpha(0.5f);
        disableNoTeamsRadioButton();
        disableTeamsRadioButton();
    }

    /**
     * Used for picking dates in the date edit text, which brings up a date picker dialog with this
     * listener attached when tapped.
     */
    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setDateEditText();
        }
    };

    /**
     * Used for picking times in the time edit text, which brings up a time picker dialog with this
     * listener attached when tapped.
     */
    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            setTimeEditText();
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
