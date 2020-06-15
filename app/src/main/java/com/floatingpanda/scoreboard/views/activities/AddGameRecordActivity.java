package com.floatingpanda.scoreboard.views.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.floatingpanda.scoreboard.data.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.viewmodels.BoardGameViewModel;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//TODO Add in competitive, cooperative or solitaire choice.
//TODO Add in functionality so teams or no teams choice affects things.
public class AddGameRecordActivity extends AppCompatActivity {

    private final int CHOOSE_PLAYERS_REQUEST_CODE = 1;
    private final int CONFIRM_GAME_RECORD_REQUEST_CODE = 2;

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

    private BoardGameViewModel boardGameViewModel;

    private Group group;
    private String boardGameName;

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

        dateEditText = findViewById(R.id.add_game_record_datetime_edittext);
        timeEditText = findViewById(R.id.add_game_record_time_edittext);

        setDateEditText();
        setTimeEditText();

        group = (Group) getIntent().getExtras().get("GROUP");

        //Populate board game spinner
        boardGameViewModel = new ViewModelProvider(this).get(BoardGameViewModel.class);
        boardGameViewModel.getAllBoardGamesWithBgCategoriesAndPlayModes().observe(this, new Observer<List<BoardGameWithBgCategoriesAndPlayModes>>() {
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

    private GameRecord createGameRecord() {
        int difficulty = Integer.parseInt(difficultyTextView.getText().toString());
        //TODO change this so user inputs a date/time.
        Date date = calendar.getTime();
        Log.w("AddGameRecordAct.java", "Date: " + date.toString());
        PlayMode.PlayModeEnum playModePlayed = getPlayModePlayed();

        if (playModePlayed == PlayMode.PlayModeEnum.ERROR) {
            Toast.makeText(this, "Playmode ERROR returned", Toast.LENGTH_SHORT).show();
        }

        boolean won = false;
        if (playModePlayed == PlayMode.PlayModeEnum.COOPERATIVE
                || playModePlayed == PlayMode.PlayModeEnum.SOLITAIRE) {
            won = getWon();
            Log.w("AddGameRecordAct.java", "Got won: " + won);
        }

        boolean teams = getTeams();
        int noOfTeams = Integer.parseInt(playerCountEditText.getText().toString());

        GameRecord gameRecord = new GameRecord(group.getId(), boardGameName, difficulty, date, teams, playModePlayed, noOfTeams, won);
        return gameRecord;
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
        int year = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        dateEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
    }

    private void setTimeEditText() {
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        //TODO change to use some sort of formatting for the minutes.
        if (minute < 10) {
            timeEditText.setText(hourOfDay + ":0" + minute);
        } else {
            timeEditText.setText(hourOfDay + ":" + minute);
        }
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

    //TODO maybe move "disabling" entities (setting to alpha and making non-clickable) into its own helper method?

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

    private boolean getTeams() {
        switch (teamsRadioGroup.getCheckedRadioButtonId()) {
            case R.id.add_game_record_radio_button_teams:
                return true;
            case R.id.add_game_record_radio_button_no_teams:
                return false;
            default:
                Log.w("AddGameRecordAct.java", "Failed to return proper result when getting teams.");
                return false;
        }
    }

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

    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setDateEditText();
        }
    };

    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            setTimeEditText();
        }
    };
}
