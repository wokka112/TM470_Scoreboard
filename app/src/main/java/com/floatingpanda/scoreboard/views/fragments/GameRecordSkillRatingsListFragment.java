package com.floatingpanda.scoreboard.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.GameRecordDetailsSkillRatingListAdapter;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayersAndRatingChanges;
import com.floatingpanda.scoreboard.data.relations.PlayerWithRatingChanges;
import com.floatingpanda.scoreboard.viewmodels.GameRecordViewModel;

import java.util.List;

public class GameRecordSkillRatingsListFragment extends Fragment {

    private GameRecord gameRecord;
    private GameRecordViewModel gameRecordViewModel;

    public GameRecordSkillRatingsListFragment(GameRecord gameRecord) { this.gameRecord = gameRecord; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);

        final GameRecordDetailsSkillRatingListAdapter adapter = new GameRecordDetailsSkillRatingListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        gameRecordViewModel = new ViewModelProvider(this).get(GameRecordViewModel.class);

        gameRecordViewModel.getPlayerTeamsWithPlayersAndRatingChangesByRecordId(gameRecord.getId()).observe(getViewLifecycleOwner(), new Observer<List<PlayerTeamWithPlayersAndRatingChanges>>() {
            @Override
            public void onChanged(List<PlayerTeamWithPlayersAndRatingChanges> playerTeamsWithPlayersAndRatingChanges) {
                List<PlayerWithRatingChanges> playersWithRatingChanges =
                        gameRecordViewModel.extractPlayersWithRatingChanges(playerTeamsWithPlayersAndRatingChanges);

                adapter.setPlayersWithRatingChanges(playersWithRatingChanges);
            }
        });

        return rootView;
    }
}
