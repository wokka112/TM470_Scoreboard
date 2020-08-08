package com.floatingpanda.scoreboard.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;
import com.floatingpanda.scoreboard.views.activities.GroupMonthlyScoreActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A view fragment showing, for a specific group, a list of the months, in descending order, and the
 * top three scores attained by the group's members for each month. These top three scores are
 * ranked (1st, 2nd, 3rd) to show how they place relative to one another, and include the nickname
 * of the group member who got the score.
 */
public class ScoreWinnerListFragment extends Fragment implements DetailAdapterInterface {

    //The group view model is used to get the groupId, which is supplied by the calling activity -
    // GroupActivity - to this and other fragments.
    private GroupViewModel groupViewModel;
    private GroupMonthlyScoreViewModel groupMonthlyScoreViewModel;
    private TextView noMonthlyScoresTextView;

    public ScoreWinnerListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_with_fab, container, false);

        noMonthlyScoresTextView = rootView.findViewById(R.id.no_element_exists_textview);
        noMonthlyScoresTextView.setText(R.string.no_monthly_scores);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        final ScoreWinnerListAdapter adapter = new ScoreWinnerListAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupViewModel = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);
        int groupId = groupViewModel.getSharedGroupId();

        groupMonthlyScoreViewModel = new ViewModelProvider(this).get(GroupMonthlyScoreViewModel.class);
        groupMonthlyScoreViewModel.initGroupMonthlyScoresWithScoresAndMemberDetails(groupId);

        groupMonthlyScoreViewModel.getGroupMonthlyScoresWithScoresAndMemberDetails().observe(getViewLifecycleOwner(), new Observer<List<GroupMonthlyScoreWithScoresAndMemberDetails>>() {
            @Override
            public void onChanged(List<GroupMonthlyScoreWithScoresAndMemberDetails> groupMonthlyScoresWithScoresAndMemberDetails) {
                if (groupMonthlyScoresWithScoresAndMemberDetails == null
                        || groupMonthlyScoresWithScoresAndMemberDetails.isEmpty()) {
                    noMonthlyScoresTextView.setVisibility(View.VISIBLE);
                } else {
                    noMonthlyScoresTextView.setVisibility(View.GONE);
                }

                adapter.setGroupMonthlyScoresWithScoresAndMemberDetails(groupMonthlyScoresWithScoresAndMemberDetails);
            }
        });

        return rootView;
    }

    /**
     * Starts the GroupMonthlyScoreActivity to view a whole month of scores in more detail.
     *
     * object must be a GroupMonthlyScore, and the group monthly score must exist in the database.
     *
     * Part of the DetailAdapterInterface.
     * @param object
     */
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
