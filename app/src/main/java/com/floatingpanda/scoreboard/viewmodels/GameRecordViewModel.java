package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.GameRecordRepository;
import com.floatingpanda.scoreboard.data.GameRecordWithPlayerTeamsAndPlayers;
import com.floatingpanda.scoreboard.data.GroupRepository;
import com.floatingpanda.scoreboard.data.MemberRepository;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Group;

import java.util.List;

public class GameRecordViewModel extends AndroidViewModel {

    private GameRecordRepository gameRecordRepository;
    private LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> allGameRecordsWithTeamsAndPlayers;
    private LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> groupsGameRecordsWithTeamsAndPlayers;

    public GameRecordViewModel(Application application) {
        super(application);
        gameRecordRepository = new GameRecordRepository(application);
        allGameRecordsWithTeamsAndPlayers = gameRecordRepository.getAllGameRecordsWithTeamsAndPlayers();
    }

    // Used for testing
    public GameRecordViewModel(Application application, AppDatabase db) {
        super(application);
        gameRecordRepository = new GameRecordRepository(db);
        allGameRecordsWithTeamsAndPlayers = gameRecordRepository.getAllGameRecordsWithTeamsAndPlayers();
    }

    public void initGameRecordWithPlayerTeamsAndPlayers(int groupId) {
        groupsGameRecordsWithTeamsAndPlayers = gameRecordRepository.getGameRecordWithTeamsAndPlayersViaGroupId(groupId);
    }

    public LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> getAllGameRecordsWithTeamsAndPlayers() {
        return allGameRecordsWithTeamsAndPlayers;
    }

    public LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> getGroupsGameRecordsWithTeamsAndPlayers() {
        return groupsGameRecordsWithTeamsAndPlayers;
    }

    public void addGameRecordAndPlayers(GameRecord gameRecord, List<TeamOfPlayers> teamsOfPlayers) {
        //Calculate and assign scores
        for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
            teamOfPlayers.setScore(30);
        }

        //Calculate skill ratings

        //Add game record, player team and players, then assign skill rating changes and score changes
        gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
    }
}
