package com.floatingpanda.scoreboard.views.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.GameRecordDetailsCompetitiveScoreListAdapter;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.GameRecordDetailsCooperativeScoreListAdapter;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.utils.DateStringCreator;
import com.floatingpanda.scoreboard.viewmodels.GameRecordViewModel;
import com.floatingpanda.scoreboard.views.activities.GameRecordActivity;
import com.floatingpanda.scoreboard.views.activities.MemberActivity;

import java.util.Calendar;
import java.util.List;

public class GameRecordScoresListFragment extends Fragment {

    private GameRecord gameRecord;
    private GameRecordViewModel gameRecordViewModel;

    private TextView boardGameNameTextView, dateTextView, timeTextView, playModeTextView, wonLostTextView, difficultyOutputTextView,
            playerCountHeaderTextView, playerCountOutputTextView;
    private RecyclerView recyclerView;
    private Button deleteButton;

    public GameRecordScoresListFragment(GameRecord gameRecord) {
        this.gameRecord = gameRecord;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game_record_scores, container, false);
        gameRecordViewModel = new ViewModelProvider(this).get(GameRecordViewModel.class);

        boardGameNameTextView = rootView.findViewById(R.id.activity_game_record_details_board_game_name);
        dateTextView = rootView.findViewById(R.id.activity_game_record_details_date);
        timeTextView = rootView.findViewById(R.id.activity_game_record_details_time);
        playModeTextView = rootView.findViewById(R.id.activity_game_record_details_playmode);
        wonLostTextView = rootView.findViewById(R.id.activity_game_record_details_won_lost);
        difficultyOutputTextView = rootView.findViewById(R.id.activity_game_record_details_difficulty_output);
        playerCountHeaderTextView = rootView.findViewById(R.id.activity_game_record_details_player_count_header);
        playerCountOutputTextView = rootView.findViewById(R.id.activity_game_record_details_player_count_output);

        recyclerView = rootView.findViewById(R.id.activity_game_record_details_recyclerview);

        deleteButton = rootView.findViewById(R.id.activity_game_record_details_delete_button);

        setViews(gameRecord);

        gameRecordViewModel.getPlayerTeamsWithPlayers(gameRecord.getId()).observe(getViewLifecycleOwner(), new Observer<List<PlayerTeamWithPlayers>>() {
            @Override
            public void onChanged(List<PlayerTeamWithPlayers> playerTeamsWithPlayers) {
                setupAdapter(playerTeamsWithPlayers, recyclerView);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDeleteActivity(gameRecord);
            }
        });

        return rootView;
    }

    private void startDeleteActivity(GameRecord gameRecord) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Game Record?")
                .setMessage("Are you sure you want to delete this record?\n" +
                        "This will change player skill ratings and may decrease the accuracy of the " +
                        "skill rating system if this is not the latest record.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGameRecord(gameRecord);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .create()
                .show();
    }

    private void deleteGameRecord(GameRecord gameRecord) {
        gameRecordViewModel.deleteGameRecord(gameRecord);
        getActivity().finish();
    }

    private void setViews(GameRecord gameRecord) {
        boardGameNameTextView.setText(gameRecord.getBoardGameName());

        String playModeString = getPlayModeString(gameRecord.getPlayModePlayed());
        playModeTextView.setText(playModeString);

        DateStringCreator dateStringCreator = new DateStringCreator(gameRecord.getDateTime());

        String date = dateStringCreator.getDayOfMonthString() + " " + dateStringCreator.getEnglishMonthNameString() + " " + dateStringCreator.getYearString();
        dateTextView.setText(date);

        String time = dateStringCreator.getHourOfDayString() + ":" + dateStringCreator.getMinuteString();
        timeTextView.setText(time);

        if (gameRecord.getPlayModePlayed() == PlayMode.PlayModeEnum.COMPETITIVE) {
            wonLostTextView.setVisibility(View.GONE);
        } else {
            String wonLostString = getString(R.string.won);
            if (!gameRecord.getWon()) {
                wonLostString = getString(R.string.lost);
            }
            wonLostTextView.setText(wonLostString);
        }

        difficultyOutputTextView.setText(Integer.toString(gameRecord.getDifficulty()));

        String playerCountHeaderText = getString(R.string.players_colon_header);
        if (gameRecord.getTeams()) {
            playerCountHeaderText = getString(R.string.teams_colon_header);
        }
        playerCountHeaderTextView.setText(playerCountHeaderText);

        playerCountOutputTextView.setText(Integer.toString(gameRecord.getNoOfTeams()));
    }

    private void setupAdapter(List<PlayerTeamWithPlayers> playerTeamsWithPlayers, RecyclerView recyclerView) {
        RecyclerView.Adapter adapter;

        if (gameRecord.getPlayModePlayed() == PlayMode.PlayModeEnum.COMPETITIVE) {
            GameRecordDetailsCompetitiveScoreListAdapter competitiveAdapter = new GameRecordDetailsCompetitiveScoreListAdapter(getActivity(), gameRecord.getTeams());
            competitiveAdapter.setPlayerTeamsWithPlayers(playerTeamsWithPlayers);
            adapter = competitiveAdapter;
        } else {
            GameRecordDetailsCooperativeScoreListAdapter cooperativeAdapter = new GameRecordDetailsCooperativeScoreListAdapter(getActivity());
            cooperativeAdapter.setPlayerTeamsWithPlayers(playerTeamsWithPlayers);
            adapter = cooperativeAdapter;
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private String getPlayModeString(PlayMode.PlayModeEnum playModeEnum) {
        switch (playModeEnum) {
            case COMPETITIVE:
                return getString(R.string.competitive);
            case COOPERATIVE:
                return getString(R.string.cooperative);
            case SOLITAIRE:
                return getString(R.string.solitaire);
            default:
                return getString(R.string.play_mode_error);
        }
    }
}
