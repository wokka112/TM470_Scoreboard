package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.repositories.MemberRepository;
import com.floatingpanda.scoreboard.data.entities.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoosePlayerSharedViewModel extends AndroidViewModel {

    private MemberRepository memberRepository;

    private MutableLiveData<List<Member>> observablePotentialPlayers;
    private List<Member> potentialPlayers;
    private Map<Integer, TeamOfPlayers> selectedPlayers;

    public ChoosePlayerSharedViewModel(Application application) {
        super(application);
        memberRepository = new MemberRepository(application);

        if (selectedPlayers == null) {
            selectedPlayers = new HashMap<Integer, TeamOfPlayers>();
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

        if (selectedPlayers == null) {
            selectedPlayers = new HashMap<Integer, TeamOfPlayers>();
        }

        if (potentialPlayers == null) {
            potentialPlayers = new ArrayList<Member>();
        }

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

    public void updateObservablePotentialPlayers() {
        /*
        Log.w("ChoosePlayerShVM.java", "Updating observable potential players.");
        for (Member member : potentialPlayers) {
            Log.w("ChoosePlayerShVM.java", "Potential Player: " + member);
        }
         */

        //Activates notifyall.
        observablePotentialPlayers.setValue(potentialPlayers);
    }

    public void addPlayerToTeam(int teamNo, Member player) {
        TeamOfPlayers teamOfPlayers = selectedPlayers.get(teamNo);

        if (teamOfPlayers == null) {
            teamOfPlayers = new TeamOfPlayers(teamNo, teamNo);
        }

        teamOfPlayers.addPlayer(player);

        selectedPlayers.put(teamNo, teamOfPlayers);
        potentialPlayers.remove(player);
    }

    public void removePlayerFromTeam(int teamNo, Member player) {
        TeamOfPlayers teamOfPlayers = selectedPlayers.get(teamNo);

        if (teamOfPlayers == null) {
            return;
        }

        teamOfPlayers.removePlayer(player);

        selectedPlayers.put(teamNo, teamOfPlayers);
        potentialPlayers.add(player);
    }

    public TeamOfPlayers getTeamOfMembers(int teamNo) {
        TeamOfPlayers teamOfPlayers = selectedPlayers.get(teamNo);

        if (teamOfPlayers == null) {
            teamOfPlayers = new TeamOfPlayers(teamNo, teamNo);
            selectedPlayers.put(teamNo, teamOfPlayers);
        }

        return teamOfPlayers;
    }

    public List<Member> getTeamMemberList(int teamNo) {
        if (selectedPlayers.get(teamNo) == null) {
            selectedPlayers.put(teamNo, new TeamOfPlayers(teamNo, teamNo));
        }

        List<Member> teamMemberList = new ArrayList<>(selectedPlayers.get(teamNo).getMembers());

        return teamMemberList;
    }

    public void setTeamPosition(int teamNo, int position) {
        if (selectedPlayers.get(teamNo) == null) {
            selectedPlayers.put(teamNo, new TeamOfPlayers(teamNo, position));
        } else {
            selectedPlayers.get(teamNo).setPosition(position);
        }
    }

    public int getTeamPosition(int teamNo) {
        if (selectedPlayers.get(teamNo) == null) {
            selectedPlayers.put(teamNo, new TeamOfPlayers(teamNo, teamNo));
        }

        return selectedPlayers.get(teamNo).getPosition();
    }

    public List<Member> getPotentialPlayers() {
        return new ArrayList<>(potentialPlayers);
    }

    public List<TeamOfPlayers> getTeamsOfMembers() {
        List<TeamOfPlayers> teamsOfMembers = new ArrayList<>();
        for (int key : selectedPlayers.keySet()) {
            teamsOfMembers.add(selectedPlayers.get(key));
        }
        Collections.sort(teamsOfMembers);

        return teamsOfMembers;
    }

    /**
     * If no team exists with the key teamNo, creates an empty team with the key teamNo, team number
     * teamNo and position teamNo. If a team already exists with the key teamNo, does nothing.
     * @param teamNo
     */
    public void createEmptyTeam(int teamNo, int initialPosition) {
        if (selectedPlayers.get(teamNo) == null) {
            Log.w("ChoosePlayerShVM.java", "Created team " + teamNo + ", position: " + initialPosition);
            selectedPlayers.put(teamNo, new TeamOfPlayers(teamNo, initialPosition));
        }
    }

    private void printList(List<Member> list) {
        for (Member member : list) {
            Log.w("ChoosePlayerSharedVM.java", "Member: " + member);
        }
    }
}
