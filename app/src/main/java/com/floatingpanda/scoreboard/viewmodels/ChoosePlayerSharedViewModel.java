package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.MemberRepository;
import com.floatingpanda.scoreboard.data.entities.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoosePlayerSharedViewModel extends AndroidViewModel {

    private MemberRepository memberRepository;

    private MutableLiveData<List<Member>> potentialPlayers;
    private Map<Integer, List<Member>> selectedPlayers;

    public ChoosePlayerSharedViewModel(Application application) {
        super(application);
        memberRepository = new MemberRepository(application);
        if (selectedPlayers == null) {
            selectedPlayers = new HashMap<Integer, List<Member>>();
        }

        if (potentialPlayers == null) {
            potentialPlayers = new MutableLiveData<List<Member>>();
        }
    }

    // Used for testing.
    public ChoosePlayerSharedViewModel(Application application, AppDatabase db) {
        super(application);
        memberRepository = new MemberRepository(db);
        selectedPlayers = new HashMap<Integer, List<Member>>();

        if (potentialPlayers == null) {
            potentialPlayers = new MutableLiveData<List<Member>>();
        }
    }

    public void initialisePotentialPlayers(int groupId) {
        Log.w("ChoosePlShVM.java", "Called initialise.");
        LiveData<List<Member>> liveData = memberRepository.getLiveMembersOfAGroupByGroupId(groupId);

        liveData.observeForever(new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> members) {
                Log.w("ChooselShVM.java", "Initialise has retrieved members");
                potentialPlayers.postValue(members);
                liveData.removeObserver(this);
            }
        });
    }

    public MutableLiveData<List<Member>> getPotentialPlayers() {
        return potentialPlayers;
    }

    public void addPlayerToTeam(int teamNo, Member player) {
        List<Member> players = selectedPlayers.get(teamNo);

        if (players == null) {
            players = new ArrayList<Member>();
        }

        players.add(player);
        selectedPlayers.put(teamNo, players);
        List<Member> newPotentialPlayers = potentialPlayers.getValue();
        printList(newPotentialPlayers);
        newPotentialPlayers.remove(player);
        potentialPlayers.setValue(newPotentialPlayers);
    }

    public void removePlayerFromTeam(int teamNo, Member player) {
        List<Member> players = selectedPlayers.get(teamNo);

        if (players == null) {
            return;
        }

        players.remove(player);
        selectedPlayers.put(teamNo, players);
        List<Member> newPotentialPlayers = potentialPlayers.getValue();
        printList(newPotentialPlayers);
        newPotentialPlayers.add(player);
        potentialPlayers.setValue(newPotentialPlayers);
    }

    public List<Member> getTeamPlayers(int teamNo) {
        List<Member> teamPlayers = selectedPlayers.get(teamNo);

        if (teamPlayers == null) {
            return new ArrayList<Member>();
        }

        return new ArrayList<Member>(teamPlayers);
    }

    private void printList(List<Member> list) {
        for (Member member : list) {
            Log.w("ChoosePlayerSharedVM.java", "Member: " + member);
        }
    }
}
