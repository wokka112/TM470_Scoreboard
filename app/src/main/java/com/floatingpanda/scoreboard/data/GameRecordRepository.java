package com.floatingpanda.scoreboard.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.daos.GroupMonthlyScoreDao;
import com.floatingpanda.scoreboard.data.daos.PlayerDao;
import com.floatingpanda.scoreboard.data.daos.PlayerTeamDao;
import com.floatingpanda.scoreboard.data.daos.ScoreDao;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GameRecordRepository {

    private GameRecordDao gameRecordDao;
    private PlayerTeamDao playerTeamDao;
    private PlayerDao playerDao;
    private GroupMonthlyScoreDao groupMonthlyScoreDao;
    private ScoreDao scoreDao;
    private LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> allGameRecordsWithTeamsAndPlayers;

    public GameRecordRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);

        gameRecordDao = db.gameRecordDao();
        playerTeamDao = db.playerTeamDao();
        playerDao = db.playerDao();
        groupMonthlyScoreDao = db.groupMonthlyScoreDao();
        scoreDao = db.scoreDao();

        allGameRecordsWithTeamsAndPlayers = gameRecordDao.getAllGameRecordsWithPlayerTeamsAndPlayers();
    }

    //Used for testing
    public GameRecordRepository(AppDatabase db) {
        gameRecordDao = db.gameRecordDao();
        playerTeamDao = db.playerTeamDao();
        playerDao = db.playerDao();
        groupMonthlyScoreDao = db.groupMonthlyScoreDao();
        scoreDao = db.scoreDao();

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

            //TODO make this work as a transaction.
            //For each team of players, insert the team into the db, then insert the players
            for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
                //Insert player team and get id
                PlayerTeam playerTeam = new PlayerTeam(teamOfPlayers.getTeamNo(), recordId, teamOfPlayers.getPosition(), teamOfPlayers.getScore());

                int playerTeamId = (int) playerTeamDao.insert(playerTeam);

                //Create list of players to insert
                List<Player> players = new ArrayList<>();
                for (Member member : teamOfPlayers.getMembers()) {
                    //Update members score in the system.
                    //TODO update members score in the monthly scores database entries.
                    //TODO move into its own method.
                    //Get group id, year, month.
                    int groupId = gameRecord.getGroupId();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(gameRecord.getDateTime());
                    //Get year
                    int year = calendar.get(Calendar.YEAR);
                    //Get month
                    int month = calendar.get(Calendar.MONTH) + 1;
                    // Get group monthly score id
                    int groupMonthlyScoreId = groupMonthlyScoreDao.getGroupMonthlyScoreIdByGroupIdAndYearAndMonth(groupId, year, month);
                    //  Get member id
                    int memberId = member.getId();
                    //   Update score for group monthly score id and member id with the new score.
                    scoreDao.addScore(groupMonthlyScoreId, memberId, teamOfPlayers.getScore());

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
