package com.floatingpanda.scoreboard.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.ChoosePlayersListAdapter;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.ChoosePlayerInterface;
import com.floatingpanda.scoreboard.viewmodels.ChoosePlayerSharedViewModel;

import java.util.List;

public class ChoosePlayerPageFragment extends Fragment implements ChoosePlayerInterface {

    private ChoosePlayerSharedViewModel choosePlayerSharedViewModel;

    int teamNo;

    public ChoosePlayerPageFragment(int teamNo){
        super();
        this.teamNo = teamNo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_choose_players, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.choose_players_recyclerview);

        final ChoosePlayersListAdapter adapter = new ChoosePlayersListAdapter(getContext(), this, teamNo);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        choosePlayerSharedViewModel = new ViewModelProvider(requireActivity()).get(ChoosePlayerSharedViewModel.class);
        choosePlayerSharedViewModel.getObservablePotentialPlayers().observe(getViewLifecycleOwner(), new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> potentialPlayers) {
                List<Member> teamPlayers = choosePlayerSharedViewModel.getTeamPlayers(teamNo);
                //Log.w("ChoosePlayerPageFrag.java", "Got team players for team " + teamNo);
                for (Member member : teamPlayers) {
                    //Log.w("ChoosePlayerPageFrag.java", "Team Player: " + member);
                }
                adapter.setTeamAndPotentialPlayers(teamPlayers, potentialPlayers);
            }
        });

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
}
