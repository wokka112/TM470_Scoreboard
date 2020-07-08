package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.calculators.Calculator;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.GameRecordRepository;
import com.floatingpanda.scoreboard.data.GameRecordWithPlayerTeamsAndPlayers;
import com.floatingpanda.scoreboard.data.GroupRepository;
import com.floatingpanda.scoreboard.data.MemberRepository;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.PlayMode;

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

    //TODO setup tests for this.
    public void addGameRecordAndPlayers(GameRecord gameRecord, List<TeamOfPlayers> teamsOfPlayers) {
        Calculator calculator = new Calculator();
        //Calculate and assign scores
        if (gameRecord.getPlayModePlayed() == PlayMode.PlayModeEnum.COMPETITIVE) {
            calculator.calculateCompetitiveScores(gameRecord.getDifficulty(), teamsOfPlayers);
        } else {
            calculator.calculateCooperativeSolitaireScores(gameRecord.getDifficulty(), teamsOfPlayers, gameRecord.getWon());
        }

        //Calculate skill ratings

        //Add game record, player team and players, then assign skill rating changes and score changes
        gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
    }
}
