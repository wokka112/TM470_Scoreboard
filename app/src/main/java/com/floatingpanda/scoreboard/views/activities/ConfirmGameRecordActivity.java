package com.floatingpanda.scoreboard.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.adapters.CompetitiveConfirmPlayersListAdapter;
import com.floatingpanda.scoreboard.adapters.CoopSoliConfirmPlayersListAdapter;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.PlayMode;

import java.util.ArrayList;
import java.util.List;

import static com.floatingpanda.scoreboard.data.entities.PlayMode.PlayModeEnum.COMPETITIVE;

public class ConfirmGameRecordActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private TextView gameTextView, dateTextView, timeTextView, playModeTextView, teamSettingOrWinLoseTextView,
            teamCountTextView;
    private Button confirmButton, cancelButton;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_game_record);

        gameTextView = findViewById(R.id.activity_confirm_record_game_textview);
        dateTextView = findViewById(R.id.activity_confirm_record_date_textview);
        timeTextView = findViewById(R.id.activity_confirm_record_time_textview);
        playModeTextView = findViewById(R.id.activity_confirm_record_play_mode_textview);
        teamSettingOrWinLoseTextView = findViewById(R.id.activity_confirm_record_team_setting_OR_win_lose_textview);
        teamCountTextView = findViewById(R.id.activity_confirm_record_team_count_textview);

        confirmButton = findViewById(R.id.activity_confirm_record_confirm_button);
        cancelButton = findViewById(R.id.activity_confirm_record_cancel_button);

        recyclerView = findViewById(R.id.activity_confirm_record_recyclerview);

        GameRecord gameRecord = (GameRecord) getIntent().getExtras().get("GAME_RECORD");
        List<TeamOfPlayers> teamsOfPlayers = (ArrayList) getIntent().getExtras().get("TEAMS_OF_PLAYERS");

        gameTextView.setText(gameRecord.getBoardGameName());
        dateTextView.setText(gameRecord.getDateTime().toString());

        PlayMode.PlayModeEnum playModePlayed = gameRecord.getPlayModePlayed();
        setPlayModePlayedTextView(playModePlayed);

        if(playModePlayed == COMPETITIVE) {
            if (gameRecord.getTeams()) {
                teamSettingOrWinLoseTextView.setText("Teams");
            } else {
                teamSettingOrWinLoseTextView.setText("No Teams");
            }
        }

        if (gameRecord.getTeams()) {
            teamCountTextView.setText(gameRecord.getNoOfTeams() + " Teams");
        } else {
            teamCountTextView.setText(gameRecord.getNoOfTeams() + " Players");
        }

        if (gameRecord.getPlayModePlayed() == COMPETITIVE) {
            final CompetitiveConfirmPlayersListAdapter adapter = new CompetitiveConfirmPlayersListAdapter(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter.setPlayerTeams(teamsOfPlayers);
        } else {
            final CoopSoliConfirmPlayersListAdapter adapter = new CoopSoliConfirmPlayersListAdapter(this, gameRecord.getWon());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter.setPlayerTeams(teamsOfPlayers);
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, (ArrayList) teamsOfPlayers);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setPlayModePlayedTextView(PlayMode.PlayModeEnum playModePlayed) {
        switch (playModePlayed) {
            case COMPETITIVE:
                playModeTextView.setText("Competitive");
                break;
            case COOPERATIVE:
                playModeTextView.setText("Cooperative");
                break;
            case SOLITAIRE:
                playModeTextView.setText("Solitaire");
                break;
            default:
                playModeTextView.setText("Error");
        }
    }
}
