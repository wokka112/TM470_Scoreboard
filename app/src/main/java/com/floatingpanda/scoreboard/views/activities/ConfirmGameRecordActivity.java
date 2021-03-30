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
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.CompetitiveConfirmPlayersListAdapter;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.CoopSoliConfirmPlayersListAdapter;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.utils.DateStringCreator;

import java.util.ArrayList;
import java.util.List;

import static com.floatingpanda.scoreboard.data.entities.PlayMode.PlayModeEnum.COMPETITIVE;

/**
 * View for confirming game record details when creating a new game record. This view details the
 * settings chosen for the game record - board game, play mode played, teams, etc. - and gives the
 * user the option to confirm or cancel the game record creation.
 */
public class ConfirmGameRecordActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private TextView gameTextView, dateTextView, timeTextView, playModeTextView, teamSettingOrWinLoseTextView,
            teamCountHeaderTextView, teamCountOutputTextView;
    private Button confirmButton, cancelButton;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_game_record);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gameTextView = findViewById(R.id.activity_confirm_record_game_textview);
        dateTextView = findViewById(R.id.activity_confirm_record_date_textview);
        timeTextView = findViewById(R.id.activity_confirm_record_time_textview);
        playModeTextView = findViewById(R.id.activity_confirm_record_play_mode_textview);
        teamSettingOrWinLoseTextView = findViewById(R.id.activity_confirm_record_team_setting_OR_win_lose_textview);
        teamCountHeaderTextView = findViewById(R.id.activity_confirm_record_team_count_textview);
        teamCountOutputTextView = findViewById(R.id.activity_confirm_record_team_count_output);

        confirmButton = findViewById(R.id.activity_confirm_record_confirm_button);
        cancelButton = findViewById(R.id.activity_confirm_record_cancel_button);

        recyclerView = findViewById(R.id.activity_confirm_record_recyclerview);

        GameRecord gameRecord = (GameRecord) getIntent().getExtras().get("GAME_RECORD");
        List<TeamOfPlayers> teamsOfPlayers = (ArrayList) getIntent().getExtras().get("TEAMS_OF_PLAYERS");

        setViews(gameRecord);

        RecyclerView.Adapter adapter = createAdapter(gameRecord, teamsOfPlayers);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    private void setViews(GameRecord gameRecord) {
        gameTextView.setText(gameRecord.getBoardGameName());

        DateStringCreator dateStringCreator = new DateStringCreator(gameRecord.getDateTime());

        String dateText = dateStringCreator.getDayOfMonthString() + " " + dateStringCreator.getEnglishMonth3LetterString() + " " + dateStringCreator.getYearString();
        dateTextView.setText(dateText);

        String timeText = dateStringCreator.getHourOfDayString() + ":" + dateStringCreator.getMinuteString();
        timeTextView.setText(timeText);

        PlayMode.PlayModeEnum playModePlayed = gameRecord.getPlayModePlayed();
        setPlayModePlayedTextView(playModePlayed);

        if(playModePlayed == COMPETITIVE) {
            if (gameRecord.getTeams()) {
                teamSettingOrWinLoseTextView.setText(getString(R.string.teams));
            } else {
                teamSettingOrWinLoseTextView.setText(getString(R.string.no_teams));
            }
        }

        if (gameRecord.getTeams()) {
            teamCountHeaderTextView.setText(getString(R.string.teams_colon_header));
            teamCountOutputTextView.setText(Integer.toString(gameRecord.getNoOfTeams()));
        } else {
            teamCountHeaderTextView.setText(getString(R.string.players_colon_header));
            teamCountOutputTextView.setText(Integer.toString(gameRecord.getNoOfTeams()));
        }
    }

    /**
     * Returns an adapter for the recyclerview to use to list the game record details. If the
     * playmode selected is competitive, the competitive confirm players list adapter is returned,
     * which displays teams in different positions to show who beat who. If the playmode selected is
     * cooperative or solitaire, the coop soli confirm players list adapter is returned, which
     * displays whether the game was won or lost and the players who played.
     * @param gameRecord the game record being created
     * @param teamsOfPlayers the teams of players who played in said record
     * @return
     */
    private RecyclerView.Adapter createAdapter(GameRecord gameRecord, List<TeamOfPlayers> teamsOfPlayers) {
        if (gameRecord.getPlayModePlayed() == COMPETITIVE) {
            final CompetitiveConfirmPlayersListAdapter adapter = new CompetitiveConfirmPlayersListAdapter(this);
            adapter.setPlayerTeams(teamsOfPlayers);
            return adapter;
        } else {
            final CoopSoliConfirmPlayersListAdapter adapter = new CoopSoliConfirmPlayersListAdapter(this, gameRecord.getWon());
            adapter.setPlayerTeams(teamsOfPlayers);
            return adapter;
        }
    }

    /**
     * Sets the play mode textView to the correct value for the playmode that the game record was
     * played in.
     * @param playModePlayed
     */
    private void setPlayModePlayedTextView(PlayMode.PlayModeEnum playModePlayed) {
        switch (playModePlayed) {
            case COMPETITIVE:
                playModeTextView.setText(getString(R.string.competitive));
                break;
            case COOPERATIVE:
                playModeTextView.setText(getString(R.string.cooperative));
                break;
            case SOLITAIRE:
                playModeTextView.setText(getString(R.string.solitaire));
                break;
            default:
                playModeTextView.setText(getString(R.string.play_mode_error));
        }
    }

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
