package com.floatingpanda.scoreboard.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.ChoosePlayersListAdapter;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.ChoosePlayerInterface;
import com.floatingpanda.scoreboard.viewmodels.ChoosePlayerSharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChoosePlayerPageFragment extends Fragment implements ChoosePlayerInterface {

    private Spinner spinner;
    private TextView teamTextView;

    private ChoosePlayerSharedViewModel choosePlayerSharedViewModel;

    private int teamNo;
    private int noOfTeams;

    public ChoosePlayerPageFragment(int teamNo, int noOfTeams){
        super();
        this.teamNo = teamNo;
        this.noOfTeams = noOfTeams;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_choose_players, container, false);

        spinner = rootView.findViewById(R.id.viewpager_layout_position_spinner);
        teamTextView = rootView.findViewById(R.id.viewpager_layout_team_textview);

        RecyclerView recyclerView = rootView.findViewById(R.id.choose_players_recyclerview);

        final ChoosePlayersListAdapter adapter = new ChoosePlayersListAdapter(getContext(), this, teamNo);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        choosePlayerSharedViewModel = new ViewModelProvider(requireActivity()).get(ChoosePlayerSharedViewModel.class);
        choosePlayerSharedViewModel.getObservablePotentialPlayers().observe(getViewLifecycleOwner(), new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> potentialPlayers) {
                List<Member> teamPlayers = choosePlayerSharedViewModel.getTeamMemberList(teamNo);
                /*
                Log.w("ChoosePlayerPageFrag.java", "Got team players for team " + teamNo);
                for (Member member : teamPlayers) {
                    Log.w("ChoosePlayerPageFrag.java", "Team Player: " + member);
                }
                 */
                adapter.setTeamAndPotentialPlayers(teamPlayers, potentialPlayers);
            }
        });

        int initialPosition = teamNo;
        choosePlayerSharedViewModel.createEmptyTeam(teamNo, initialPosition);
        //TODO style the area where the teamTextView is so it's a different colour to the viewpager of players.
        teamTextView.setText("Team " + teamNo);

        spinner.setAdapter(createPositionSpinnerAdapter());
        spinner.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int teamPosition = (int) parent.getItemAtPosition(position);
                choosePlayerSharedViewModel.setTeamPosition(teamNo, teamPosition);
                Log.w("ChoosePlayersAct.java", "Listener: Team " + teamNo + " position set to " + teamPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //TODO Look into whether fragments are destroyed and remade as you move along pager.
        // If they are then this will reset teams positions, which is a nuisance. Maybe look into setting
        // this to match the value of the team's position in the teamMembers list.
        Log.w("ChoosePlayerPageFrag.java", "Triggered " + this.teamNo);
        int currentPosition = choosePlayerSharedViewModel.getTeamPosition(teamNo);
        //Each position is at an index of (position - 1), so we set the spinner to the correct index for
        // the team's position, which is set either at the start of the fragment or when a user sets it.
        spinner.setSelection(currentPosition - 1);

        return rootView;
    }

    public void addPlayerToTeam(Member member) {
        //Log.w("ChoosePlayerPageFrag.java", "Adding member to team: " + member);
        choosePlayerSharedViewModel.addPlayerToTeam(teamNo, member);
    }

    public void removePlayerFromTeam(Member member) {
        //Log.w("ChoosePlayerPageFrag.java", "Removing member from team: " + member);
        choosePlayerSharedViewModel.removePlayerFromTeam(teamNo, member);
    }

    public void updatePosition() {

    }

    private ArrayAdapter<Integer> createPositionSpinnerAdapter() {
        List<Integer> positionList = new ArrayList<Integer>();

        for (int i = 1; i <= noOfTeams; i++) {
            positionList.add(i);
        }

        ArrayAdapter<Integer> positionsAdapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_item, positionList);
        return positionsAdapter;
    }


}
