package com.floatingpanda.scoreboard.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.ScoreWinnerListAdapter;
import com.floatingpanda.scoreboard.data.relations.GroupMonthlyScoreWithScoresAndMemberDetails;
import com.floatingpanda.scoreboard.data.relations.ScoreWithMemberDetails;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;
import com.floatingpanda.scoreboard.viewmodels.GroupMonthlyScoreViewModel;
import com.floatingpanda.scoreboard.views.activities.GroupMonthlyScoreActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ScoreWinnerListFragment extends Fragment implements DetailAdapterInterface {
    private Group group;
    private GroupMonthlyScoreViewModel groupMonthlyScoreViewModel;

    public ScoreWinnerListFragment(Group group) {
        this.group = group;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_with_fab, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        final ScoreWinnerListAdapter adapter = new ScoreWinnerListAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupMonthlyScoreViewModel = new ViewModelProvider(this).get(GroupMonthlyScoreViewModel.class);
        groupMonthlyScoreViewModel.initGroupMonthlyScoresWithScoresAndMemberDetails(group.getId());

        groupMonthlyScoreViewModel.getGroupMonthlyScoresWithScoresAndMemberDetails().observe(getViewLifecycleOwner(), new Observer<List<GroupMonthlyScoreWithScoresAndMemberDetails>>() {
            @Override
            public void onChanged(List<GroupMonthlyScoreWithScoresAndMemberDetails> groupMonthlyScoresWithScoresAndMemberDetails) {
                adapter.setGroupMonthlyScoresWithScoresAndMemberDetails(groupMonthlyScoresWithScoresAndMemberDetails);
            }
        });

        return rootView;
    }

    @Override
    public void viewDetails(Object object) {
        GroupMonthlyScoreWithScoresAndMemberDetails groupMonthlyScoreWithScoresAndMemberDetails = (GroupMonthlyScoreWithScoresAndMemberDetails) object;
        List<ScoreWithMemberDetails> scoresWithMemberDetails = groupMonthlyScoreWithScoresAndMemberDetails.getScoresWithMemberDetails();

        Intent detailsIntent = new Intent(getContext(), GroupMonthlyScoreActivity.class);
        detailsIntent.putExtra("MONTH", groupMonthlyScoreWithScoresAndMemberDetails.getGroupMonthlyScore().getMonth());
        detailsIntent.putExtra("YEAR", groupMonthlyScoreWithScoresAndMemberDetails.getGroupMonthlyScore().getYear());
        detailsIntent.putExtra("SCORES", (ArrayList<ScoreWithMemberDetails>) scoresWithMemberDetails);
        startActivity(detailsIntent);
    }
}
