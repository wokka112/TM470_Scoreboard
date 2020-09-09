package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
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
    private int noOfTeams;
    private int currentTeamNo;

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

    public TeamOfPlayers getTeamOfPlayers(int teamNo) {
        TeamOfPlayers teamOfPlayers = selectedPlayers.get(teamNo);

        if (teamOfPlayers == null) {
            teamOfPlayers = new TeamOfPlayers(teamNo, teamNo);
            selectedPlayers.put(teamNo, teamOfPlayers);
        }

        return teamOfPlayers;
    }

    public List<Member> getTeamOfPlayersMemberList(int teamNo) {
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

    public List<TeamOfPlayers> getTeamsOfPlayers() {
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
            selectedPlayers.put(teamNo, new TeamOfPlayers(teamNo, initialPosition));
        }
    }

    public int getNoOfTeams() {
        return noOfTeams;
    }

    public void setNoOfTeams(int noOfTeams) {
        this.noOfTeams = noOfTeams;
    }

    public int getCurrentTeamNo() { return currentTeamNo; }

    public void setCurrentTeamNo(int currentTeamNo) { this.currentTeamNo = currentTeamNo; }

    //TODO Change validity tests to return enums and then use them to determine toast to show? That way the application context is
    // not being plugged into the viewmodel, and it doesn't need to be aware of the view or what needs to be done in it.
    // Or is this method okay? Could be rather spaghetti code-y.
    // Maybe I could separate validity tests into individual methods, then run them from a core method in the view and then I can
    // know which toast to show based on which one comes back as false. Although at that point I may as well just keep the validity
    // test code in the view activity.
    public boolean isValidTeam(Context applicationContext, int teamNo, boolean playingAsTeams, boolean testing) {
        TeamOfPlayers teamOfPlayers = getTeamOfPlayers(teamNo);

        if(teamOfPlayers.getMembers().isEmpty()) {
            if (!testing) {
                Toast.makeText(applicationContext, "You need to have at least 1 player on the team.", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        if(playingAsTeams == false
                && teamOfPlayers.getMembers().size() > 1) {
            if (!testing) {
                Toast.makeText(applicationContext, "You can only have 1 player per team in cooperative and solitaire games.", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        return true;
    }

    //Testing of individual teams done by "isValidTeam". Thus, solitaire and coop games should already have had only 1 player for the team enforced.
    // Competitive games should already have had at least 1 player per team ensured.
    public boolean areValidTeams(Context applicationContext, PlayMode.PlayModeEnum playModePlayed, boolean testing) {
        List<TeamOfPlayers> teamsOfPlayers = getTeamsOfPlayers();

        if (teamsOfPlayers.isEmpty()) {
            if (!testing) {
                Toast.makeText(applicationContext, "You need to have at least one team of members.", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        if (playModePlayed == PlayMode.PlayModeEnum.COMPETITIVE) {
            if (teamsOfPlayers.size() < 2) {
                if (!testing) {
                    Toast.makeText(applicationContext, "A competitive game must have more than one team or player.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        }

        if (playModePlayed == PlayMode.PlayModeEnum.COOPERATIVE) {
            if (teamsOfPlayers.size() > 1) {
                if (!testing) {
                    Toast.makeText(applicationContext, "A cooperative game cannot have more than one team.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        }

        if (playModePlayed == PlayMode.PlayModeEnum.SOLITAIRE) {
            if (teamsOfPlayers.size() > 1) {
                if (!testing) {
                    Toast.makeText(applicationContext, "A solitaire game cannot have more than one team.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        }

        return true;
    }
}
