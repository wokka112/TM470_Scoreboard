package com.floatingpanda.scoreboard.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.adapters.GameRecordListAdapter;
import com.floatingpanda.scoreboard.data.GameRecordWithPlayerTeamsAndPlayers;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.viewmodels.GameRecordViewModel;
import com.floatingpanda.scoreboard.views.activities.AddGameRecordActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class GameRecordListFragment extends Fragment {

    private final int ADD_GAME_RECORD_REQUEST_CODE = 1;

    private Group group;
    private GameRecordViewModel gameRecordViewModel;

    public GameRecordListFragment(Group group) {
        this.group = group;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_main, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        //TODO implement basic functionality using preset test game records.
        //TODO once basic functioanlity is in, get in basic game record creation functionality
        // - adding a game record with players, teams and positions, but no scores or skill ratings yet.
        //TODO once that's done, start on the mediator
        // - first of all sort out the score calculations as that's the simplest thing.
        // - then move on to skill rating as that's much more complex to deal with.

        final GameRecordListAdapter adapter = new GameRecordListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        gameRecordViewModel = new ViewModelProvider(this).get(GameRecordViewModel.class);
        gameRecordViewModel.initGameRecordWithPlayerTeamsAndPlayers(group.getId());

        gameRecordViewModel.getGroupsGameRecordsWithTeamsAndPlayers().observe(getViewLifecycleOwner(), new Observer<List<GameRecordWithPlayerTeamsAndPlayers>>() {
            @Override
            public void onChanged(List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers) {
                adapter.setGameRecordsWithPlayerTeamsAndPlayers(gameRecordsWithPlayerTeamsAndPlayers);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddGameRecord();
            }
        });

        return rootView;
    }

    private void startAddGameRecord() {
        Intent intent = new Intent(getContext(), AddGameRecordActivity.class);
        intent.putExtra("GROUP", group);
        startActivityForResult(intent, ADD_GAME_RECORD_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_GAME_RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            GameRecord gameRecord = (GameRecord) data.getExtras().get(AddGameRecordActivity.EXTRA_REPLY_GAME_RECORD);
            List<TeamOfPlayers> teamsOfPlayers = (ArrayList) data.getExtras().get(AddGameRecordActivity.EXTRA_REPLY_PLAYERS);

            gameRecordViewModel.addGameRecordAndPlayers(gameRecord, teamsOfPlayers);
        }
    }
}
