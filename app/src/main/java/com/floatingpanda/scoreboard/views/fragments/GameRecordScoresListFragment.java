package com.floatingpanda.scoreboard.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.floatingpanda.scoreboard.viewmodels.GameRecordViewModel;

import java.util.Calendar;
import java.util.List;

public class GameRecordScoresListFragment extends Fragment {

    private GameRecord gameRecord;
    private GameRecordViewModel gameRecordViewModel;

    private TextView boardGameNameTextView, dateTextView, timeTextView, playModeTextView, wonLostTextView, difficultyOutputTextView,
            playerCountHeaderTextView, playerCountOutputTextView;
    private RecyclerView recyclerView;

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

        setViews(gameRecord);

        gameRecordViewModel.getPlayerTeamsWithPlayers(gameRecord.getId()).observe(getViewLifecycleOwner(), new Observer<List<PlayerTeamWithPlayers>>() {
            @Override
            public void onChanged(List<PlayerTeamWithPlayers> playerTeamsWithPlayers) {
                setupAdapter(playerTeamsWithPlayers, recyclerView);
            }
        });

        /*
        Button deleteButton;

        deleteButton = findViewById(R.id.memberact_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDeleteActivity(member);
            }
        });
         */

        return rootView;
    }

    private void setViews(GameRecord gameRecord) {
        boardGameNameTextView.setText(gameRecord.getBoardGameName());

        String playModeString = getPlayModeString(gameRecord.getPlayModePlayed());
        playModeTextView.setText(playModeString);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(gameRecord.getDateTime());

        String date = getDateString(calendar);
        dateTextView.setText(date);

        String time = getTimeString(calendar);
        timeTextView.setText(time);

        if (gameRecord.getPlayModePlayed() == PlayMode.PlayModeEnum.COMPETITIVE) {
            wonLostTextView.setVisibility(View.GONE);
        } else {
            if (gameRecord.getWon()) {
                wonLostTextView.setText("WON");
            } else {
                wonLostTextView.setText("LOST");
            }
        }

        difficultyOutputTextView.setText(Integer.toString(gameRecord.getDifficulty()));

        if (gameRecord.getTeams()) {
            playerCountHeaderTextView.setText("Teams:");
        } else {
            playerCountHeaderTextView.setText("Players:");
        }

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
                return "Competitive";
            case COOPERATIVE:
                return "Cooperative";
            case SOLITAIRE:
                return "Solitaire";
            default:
                return "PLAYMODE ERROR";
        }
    }

    private String getDateString(Calendar calendar) {
        StringBuilder sb = new StringBuilder();

        sb.append(calendar.get(Calendar.DAY_OF_MONTH) + " ");
        sb.append(getMonthString(calendar) + " ");
        sb.append(calendar.get(Calendar.YEAR));

        return sb.toString();
    }

    private String getMonthString(Calendar calendar) {
        switch (calendar.get(Calendar.MONTH)) {
            case Calendar.JANUARY:
                return "January";
            case Calendar.FEBRUARY:
                return "February";
            case Calendar.MARCH:
                return "March";
            case Calendar.APRIL:
                return "April";
            case Calendar.MAY:
                return "May";
            case Calendar.JUNE:
                return "June";
            case Calendar.JULY:
                return "July";
            case Calendar.AUGUST:
                return "August";
            case Calendar.SEPTEMBER:
                return "September";
            case Calendar.OCTOBER:
                return "October";
            case Calendar.NOVEMBER:
                return "November";
            case Calendar.DECEMBER:
                return "December";
            default:
                return "MONTH ERROR";
        }
    }

    private String getTimeString(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }

    /*
    private void startDeleteActivity(Member member) {
        //TODO refactor this popup window into a method and find somewhere better to put it.
        AlertDialog.Builder builder = new AlertDialog.Builder(MemberActivity.this);
        builder.setTitle("Delete Member?")
                .setMessage("Are you sure you want to delete " + member.getNickname() + "?\n" +
                        "The member will be removed from winner lists, groups and game records, and " +
                        "their skill ratings will be deleted.\n" +
                        "This operation is irreversible.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMember(member);
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

    private void deleteMember(Member member) {
        memberViewModel.deleteMember(member);
        finish();
    }

     */
}
