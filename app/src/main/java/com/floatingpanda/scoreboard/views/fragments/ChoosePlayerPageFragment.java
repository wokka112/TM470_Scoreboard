package com.floatingpanda.scoreboard.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        Button nextButton = rootView.findViewById(R.id.choose_players_button);

        final ChoosePlayersListAdapter adapter = new ChoosePlayersListAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //TODO change to observe from fragment instead of calling activity??
        choosePlayerSharedViewModel = new ViewModelProvider(requireActivity()).get(ChoosePlayerSharedViewModel.class);
        choosePlayerSharedViewModel.getPotentialPlayers().observe(getViewLifecycleOwner(), new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> potentialPlayers) {
                Log.w("ChoosePlayPageFrag.java", "Got potential players.");
                Toast.makeText(getContext(), "Got potential players", Toast.LENGTH_SHORT).show();
                List<Member> teamPlayers = choosePlayerSharedViewModel.getTeamPlayers(teamNo);
                adapter.setTeamAndPotentialPlayers(teamPlayers, potentialPlayers);
            }
        });

        return rootView;
    }

    public void addPlayerToTeam(Member member) {
        choosePlayerSharedViewModel.addPlayerToTeam(teamNo, member);
    }

    public void removePlayerFromTeam(Member member) {
        choosePlayerSharedViewModel.removePlayerFromTeam(teamNo, member);
    }
}
