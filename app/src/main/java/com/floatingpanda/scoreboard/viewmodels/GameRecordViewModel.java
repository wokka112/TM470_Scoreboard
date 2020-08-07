package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.calculators.Calculator;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayersAndRatingChanges;
import com.floatingpanda.scoreboard.data.relations.PlayerWithRatingChanges;
import com.floatingpanda.scoreboard.repositories.GameRecordRepository;
import com.floatingpanda.scoreboard.data.relations.GameRecordWithPlayerTeamsAndPlayers;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.PlayMode;

import java.util.ArrayList;
import java.util.List;

public class GameRecordViewModel extends AndroidViewModel {

    private GameRecordRepository gameRecordRepository;
    private LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> allGameRecordsWithTeamsAndPlayers;
    private LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> groupsGameRecordsWithTeamsAndPlayers;
    private GameRecord sharedGameRecord;

    public GameRecordViewModel(Application application) {
        super(application);
        gameRecordRepository = new GameRecordRepository(application);
        allGameRecordsWithTeamsAndPlayers = gameRecordRepository.getAllGameRecordsWithTeamsAndPlayers();
        groupsGameRecordsWithTeamsAndPlayers = new MutableLiveData<>();
    }

    // Used for testing
    public GameRecordViewModel(Application application, AppDatabase db) {
        super(application);
        gameRecordRepository = new GameRecordRepository(db);
        allGameRecordsWithTeamsAndPlayers = gameRecordRepository.getAllGameRecordsWithTeamsAndPlayers();
        groupsGameRecordsWithTeamsAndPlayers = new MutableLiveData<>();
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

    public LiveData<List<PlayerTeamWithPlayers>> getPlayerTeamsWithPlayers(int recordId) {
        return gameRecordRepository.getPlayerTeamsWithPlayersViaRecordId(recordId);
    }

    public LiveData<List<PlayerTeamWithPlayersAndRatingChanges>> getPlayerTeamsWithPlayersAndRatingChangesByRecordId(int recordId) {
        return gameRecordRepository.getPlayerTeamsWithPlayersAndRatingChangesByRecordId(recordId);
    }

    public List<PlayerWithRatingChanges> extractPlayersWithRatingChanges(List<PlayerTeamWithPlayersAndRatingChanges> playerTeamsWithPlayersAndRatingChanges) {
        List<PlayerWithRatingChanges> playersWithRatingChanges = new ArrayList<>();

        for (PlayerTeamWithPlayersAndRatingChanges playerTeamWithPlayersAndRatingChanges : playerTeamsWithPlayersAndRatingChanges) {
            playersWithRatingChanges.addAll(playerTeamWithPlayersAndRatingChanges.getPlayersWithRatingChanges());
        }

        return playersWithRatingChanges;
    }

    public void addGameRecordAndPlayers(GameRecord gameRecord, List<TeamOfPlayers> teamsOfPlayers) {
        //Add game record, player team and players, then assign skill rating changes and score changes
        gameRecordRepository.addGameRecordAndPlayerTeams(gameRecord, teamsOfPlayers);
    }

    public void deleteGameRecord(GameRecord gameRecord) {
        gameRecordRepository.deleteGameRecord(gameRecord);
    }

    //TODO write tests
    public void setSharedGameRecord(GameRecord sharedGameRecord) {
        this.sharedGameRecord = sharedGameRecord;
    }

    public GameRecord getSharedGameRecord() {
        return sharedGameRecord;
    }
}
