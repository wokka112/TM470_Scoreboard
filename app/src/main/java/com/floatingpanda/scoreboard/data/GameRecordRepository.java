package com.floatingpanda.scoreboard.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.daos.PlayerDao;
import com.floatingpanda.scoreboard.data.daos.PlayerTeamDao;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;

import java.util.ArrayList;
import java.util.List;

public class GameRecordRepository {

    GameRecordDao gameRecordDao;
    PlayerTeamDao playerTeamDao;
    PlayerDao playerDao;
    LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> allGameRecordsWithTeamsAndPlayers;

    public GameRecordRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);

        gameRecordDao = db.gameRecordDao();
        playerTeamDao = db.playerTeamDao();
        playerDao = db.playerDao();

        allGameRecordsWithTeamsAndPlayers = gameRecordDao.getAllGameRecordsWithPlayerTeamsAndPlayers();
    }

    //Used for testing
    public GameRecordRepository(AppDatabase db) {
        gameRecordDao = db.gameRecordDao();
        playerTeamDao = db.playerTeamDao();
        playerDao = db.playerDao();

        allGameRecordsWithTeamsAndPlayers = gameRecordDao.getAllGameRecordsWithPlayerTeamsAndPlayers();
    }

    public LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> getAllGameRecordsWithTeamsAndPlayers() {
        return allGameRecordsWithTeamsAndPlayers;
    }

    public LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> getGameRecordWithTeamsAndPlayersViaGroupId(int groupId) {
        return gameRecordDao.findGameRecordsWithPlayerTeamsAndPlayersByGroupId(groupId);
    }

    public void addGameRecordAndPlayerTeams(GameRecord gameRecord, List<TeamOfPlayers> teamsOfPlayers) {
        AppDatabase.getExecutorService().execute(() -> {
            int recordId = (int) gameRecordDao.insert(gameRecord);

            //For each team of players, insert the team into the db, then insert the players
            for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
                //Insert player team and get id
                PlayerTeam playerTeam = new PlayerTeam(teamOfPlayers.getTeamNo(), recordId, teamOfPlayers.getPosition(), teamOfPlayers.getScore());
                if (playerTeamDao == null) {
                    Log.w("GameRecordRepo.java", "Null playerTeamDao");
                }

                int playerTeamId = (int) playerTeamDao.insert(playerTeam);

                //Create list of players to insert
                List<Player> players = new ArrayList<>();
                for (Member member : teamOfPlayers.getMembers()) {
                    //Update members score in the system.
                    //TODO update members score in the monthly scores database entries.

                    //Update skill ratings for this member.
                    //TODO update skill ratings in the skill ratings database entries.

                    //Add member as player in this game and on this team.
                    players.add(new Player(playerTeamId, member.getNickname()));
                }
                //Insert players
                playerDao.insertAll(players.toArray(new Player[players.size()]));
            }
        });
    }
}
