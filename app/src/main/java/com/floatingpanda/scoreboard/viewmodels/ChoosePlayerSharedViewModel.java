package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.MemberRepository;
import com.floatingpanda.scoreboard.data.entities.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoosePlayerSharedViewModel extends AndroidViewModel {

    private MemberRepository memberRepository;

    private MutableLiveData<List<Member>> observablePotentialPlayers;
    private List<Member> potentialPlayers;
    private Map<Integer, List<Member>> selectedPlayers;

    public ChoosePlayerSharedViewModel(Application application) {
        super(application);
        memberRepository = new MemberRepository(application);

        if (selectedPlayers == null) {
            selectedPlayers = new HashMap<Integer, List<Member>>();
        }

        if (potentialPlayers == null) {
            potentialPlayers = new ArrayList<Member>();
        }

        if (observablePotentialPlayers == null) {
            observablePotentialPlayers = new MutableLiveData<List<Member>>();
        }
    }

    // Used for testing.
    public ChoosePlayerSharedViewModel(Application application, AppDatabase db) {
        super(application);
        memberRepository = new MemberRepository(db);
        selectedPlayers = new HashMap<Integer, List<Member>>();

        if (observablePotentialPlayers == null) {
            observablePotentialPlayers = new MutableLiveData<List<Member>>();
        }
    }

    public void initialisePotentialPlayers(int groupId) {
        LiveData<List<Member>> liveData = memberRepository.getLiveMembersOfAGroupByGroupId(groupId);

        liveData.observeForever(new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> members) {
                potentialPlayers = members;
                observablePotentialPlayers.postValue(members);
                liveData.removeObserver(this);
            }
        });
    }

    public MutableLiveData<List<Member>> getObservablePotentialPlayers() {
        return observablePotentialPlayers;
    }

    public void addPlayerToTeam(int teamNo, Member player) {
        if (selectedPlayers.get(teamNo) == null) {
            selectedPlayers.put(teamNo, new ArrayList<Member>());
        }

        //Log.w("ChoosePlayerShVM.java", "Adding player to team " + teamNo + ": " + player);
        selectedPlayers.get(teamNo).add(player);
        potentialPlayers.remove(player);
    }

    public void removePlayerFromTeam(int teamNo, Member player) {
        if (selectedPlayers.get(teamNo) == null) {
            return;
        }

        //Log.w("ChoosePlayerShVM.java", "Removing player from team " + teamNo + ": " + player);
        selectedPlayers.get(teamNo).remove(player);
        potentialPlayers.add(player);
    }

    public List<Member> getTeamPlayers(int teamNo) {
        List<Member> teamPlayers = selectedPlayers.get(teamNo);

        if (teamPlayers == null) {
            return new ArrayList<Member>();
        }

        return new ArrayList<Member>(teamPlayers);
    }

    public void updateObservablePotentialPlayers() {
        //Log.w("ChoosePlayerShVM.java", "Updating observable potential players.");
        for (Member member : potentialPlayers) {
            //Log.w("ChoosePlayerShVM.java", "Potential Player: " + member);
        }
        observablePotentialPlayers.setValue(potentialPlayers);
    }

    private void printList(List<Member> list) {
        for (Member member : list) {
            Log.w("ChoosePlayerSharedVM.java", "Member: " + member);
        }
    }
}
