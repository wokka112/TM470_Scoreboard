package com.floatingpanda.scoreboard.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.GroupCategoryRatingChange;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.calculators.Calculator;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.daos.GroupCategorySkillRatingDao;
import com.floatingpanda.scoreboard.data.entities.GroupCategorySkillRating;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.relations.GameRecordWithPlayerTeamsAndPlayers;
import com.floatingpanda.scoreboard.data.daos.AssignedCategoryDao;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.daos.GroupMonthlyScoreDao;
import com.floatingpanda.scoreboard.data.daos.PlayerDao;
import com.floatingpanda.scoreboard.data.daos.PlayerTeamDao;
import com.floatingpanda.scoreboard.data.daos.ScoreDao;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameRecordRepository {

    private GameRecordDao gameRecordDao;
    private PlayerTeamDao playerTeamDao;
    private PlayerDao playerDao;
    private GroupMonthlyScoreDao groupMonthlyScoreDao;
    private ScoreDao scoreDao;
    private BoardGameDao boardGameDao;
    private AssignedCategoryDao assignedCategoryDao;
    private GroupCategorySkillRatingDao groupCategorySkillRatingDao;
    private LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> allGameRecordsWithTeamsAndPlayers;

    public GameRecordRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);

        gameRecordDao = db.gameRecordDao();
        playerTeamDao = db.playerTeamDao();
        playerDao = db.playerDao();
        groupMonthlyScoreDao = db.groupMonthlyScoreDao();
        scoreDao = db.scoreDao();
        boardGameDao = db.boardGameDao();
        assignedCategoryDao = db.assignedCategoryDao();
        groupCategorySkillRatingDao = db.groupCategorySkillRatingDao();

        allGameRecordsWithTeamsAndPlayers = gameRecordDao.getAllGameRecordsWithPlayerTeamsAndPlayers();
    }

    //Used for testing
    public GameRecordRepository(AppDatabase db) {
        gameRecordDao = db.gameRecordDao();
        playerTeamDao = db.playerTeamDao();
        playerDao = db.playerDao();
        groupMonthlyScoreDao = db.groupMonthlyScoreDao();
        scoreDao = db.scoreDao();
        boardGameDao = db.boardGameDao();
        assignedCategoryDao = db.assignedCategoryDao();
        groupCategorySkillRatingDao = db.groupCategorySkillRatingDao();

        allGameRecordsWithTeamsAndPlayers = gameRecordDao.getAllGameRecordsWithPlayerTeamsAndPlayers();
    }

    public LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> getAllGameRecordsWithTeamsAndPlayers() {
        return allGameRecordsWithTeamsAndPlayers;
    }

    public LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> getGameRecordWithTeamsAndPlayersViaGroupId(int groupId) {
        return gameRecordDao.findGameRecordsWithPlayerTeamsAndPlayersByGroupId(groupId);
    }

    public LiveData<List<PlayerTeamWithPlayers>> getPlayerTeamsWithPlayersViaRecordId(int recordId) {
        return playerTeamDao.findPlayerTeamsWithPlayersByRecordId(recordId);
    }

    public void addGameRecordAndPlayerTeams(GameRecord gameRecord, List<TeamOfPlayers> teamsOfPlayers) {
        //TODO make this work as a transaction.
        AppDatabase.getExecutorService().execute(() -> {
            //Insert game record and get id
            int recordId = (int) gameRecordDao.insert(gameRecord);

            //Calculate scores
            calculateScores(gameRecord, teamsOfPlayers);

            //For each team of players, insert the team into the db, then insert the players
            for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
                //Insert player team and get id
                PlayerTeam playerTeam = new PlayerTeam(teamOfPlayers.getTeamNo(), recordId, teamOfPlayers.getPosition(), teamOfPlayers.getScore());
                int playerTeamId = (int) playerTeamDao.insert(playerTeam);

                //Create list of players to insert
                List<Player> players = new ArrayList<>();
                for (Member member : teamOfPlayers.getMembers()) {
                    //Update members score in the system.
                    insertScore(gameRecord, member, teamOfPlayers.getScore());

                    //Add member as player in this game and on this team.
                    players.add(new Player(playerTeamId, member.getNickname()));
                }
                //Insert players
                playerDao.insertAll(players.toArray(new Player[players.size()]));
            }

            //If the game was played competitively, calculate and assign skill rating changes
            if (gameRecord.getPlayModePlayed() == PlayMode.PlayModeEnum.COMPETITIVE) {
                calculateAndAssignSkillRatingChanges(gameRecord, teamsOfPlayers);
            }
        });
    }

    // TURN PUBLIC FOR TESTING //

    //TODO turn these helper methods private before release.
    public void calculateScores(GameRecord gameRecord, List<TeamOfPlayers> teamsOfPlayers) {
        Calculator calculator = new Calculator();
        //Calculate and assign scores
        if (gameRecord.getPlayModePlayed() == PlayMode.PlayModeEnum.COMPETITIVE) {
            calculator.calculateCompetitiveScores(gameRecord.getDifficulty(), teamsOfPlayers);
        } else {
            calculator.calculateCooperativeSolitaireScores(gameRecord.getDifficulty(), teamsOfPlayers, gameRecord.getWon());
        }
    }

    public void insertScore(GameRecord gameRecord, Member member, int score) {
        //Get group id, year, month.
        int groupId = gameRecord.getGroupId();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(gameRecord.getDateTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        //Get group monthly score id
        int groupMonthlyScoreId = groupMonthlyScoreDao.getGroupMonthlyScoreIdByGroupIdAndYearAndMonth(groupId, year, month);
        //Get member id
        int memberId = member.getId();
        //Update score for group monthly score id and member id with the new score.
        scoreDao.addScore(groupMonthlyScoreId, memberId, score);
    }

    public void calculateAndAssignSkillRatingChanges(GameRecord gameRecord, List<TeamOfPlayers> teamsOfPlayers) {
        calculateAndAssignTeamsAverageRatings(gameRecord, teamsOfPlayers);
        Map<Integer, List<GroupCategoryRatingChange>> groupCategoryRatingChangeMap = createCategoryRatingChangesMap(teamsOfPlayers);

        Calculator calculator = new Calculator();
        //Calculate skill rating changes for each category.
        for (int key : groupCategoryRatingChangeMap.keySet()) {
            calculator.calculateCategoryPairwiseEloRatings(groupCategoryRatingChangeMap.get(key));
        }

        int groupId = gameRecord.getGroupId();
        for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
            for (Member member : teamOfPlayers.getMembers()) {
                updateMemberSkillRatings(groupId, member.getId(), teamOfPlayers.getGroupCategoryRatingChanges());
            }
        }
    }

    public void calculateAndAssignTeamsAverageRatings(GameRecord gameRecord, List<TeamOfPlayers> teamsOfPlayers) {
        //Get board game id
        int bgId = boardGameDao.findBoardGameIdByBoardGameName(gameRecord.getBoardGameName());
        //Get associated categories
        List<Integer> associatedCategoryIds = assignedCategoryDao.getAllCategoryIdsByBoardGameId(bgId);

        int groupId = gameRecord.getGroupId();
        //Get each category
        for (int categoryId : associatedCategoryIds) {
            //Calculate each teams avg elo rating from their players' ratings
            for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
                double avgRating = calculateTeamAverageRating(groupId, categoryId, teamOfPlayers.getMembers());

                //Add the elo rating change with the team's avg elo rating to the category's list of elo rating changes
                GroupCategoryRatingChange groupCategoryRatingChange = new GroupCategoryRatingChange(teamOfPlayers.getTeamNo(), categoryId, teamOfPlayers.getPosition(), avgRating);
                teamOfPlayers.addGroupCategoryRatingChange(groupCategoryRatingChange);
            }
        }
    }

    public Map<Integer, List<GroupCategoryRatingChange>> createCategoryRatingChangesMap(List<TeamOfPlayers> teamsOfPlayers) {
        Map<Integer, List<GroupCategoryRatingChange>> groupCategoryRatingChangeMap = new HashMap<>();

        for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
            for (GroupCategoryRatingChange groupCategoryRatingChange : teamOfPlayers.getGroupCategoryRatingChanges()) {
                int categoryId = groupCategoryRatingChange.getCategoryId();
                if (groupCategoryRatingChangeMap.get(categoryId) == null) {
                    groupCategoryRatingChangeMap.put(categoryId, new ArrayList<>());
                }

                groupCategoryRatingChangeMap.get(categoryId).add(groupCategoryRatingChange);
            }
        }

        return groupCategoryRatingChangeMap;
    }

    public double calculateTeamAverageRating(int groupId, int categoryId, List<Member> members) {
        double totalRating = 0.0;
        for (Member member : members) {
            double eloRating;
            if (!groupCategorySkillRatingDao.containsGroupCategorySkillRating(groupId, categoryId, member.getId())) {
                eloRating = 1500.0;
                groupCategorySkillRatingDao.insert(new GroupCategorySkillRating(groupId, member.getId(), categoryId, eloRating));
            } else {
                eloRating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(groupId, categoryId, member.getId());
            }

            totalRating += eloRating;

            //Add elorating as player's original rating to database here.
        }
        return totalRating / members.size();
    }

    public void updateMemberSkillRatings(int groupId, int memberId, List<GroupCategoryRatingChange> groupCategoryRatingChanges) {
        //Go through category rating changes for group and update member's category skill ratings
        for (GroupCategoryRatingChange groupCategoryRatingChange : groupCategoryRatingChanges) {
            int categoryId = groupCategoryRatingChange.getCategoryId();
            double addSkillRating = groupCategoryRatingChange.getEloRatingChange();
            groupCategorySkillRatingDao.addSkillRatingFromSingleGame(groupId, categoryId, memberId, addSkillRating);
        }
    }

    /*
    public Map<Integer, List<GroupCategoryRatingChange>> createCategoryRatingChangesMap(GameRecord gameRecord, List<TeamOfPlayers> teamsOfPlayers) {
        //Get board game id
        int bgId = boardGameDao.findBoardGameIdByBoardGameName(gameRecord.getBoardGameName());
        //Get associated categories
        List<Integer> associatedCategoryIds = assignedCategoryDao.getAllCategoryIdsByBoardGameId(bgId);

        //Create rating lists for each category.
        //Category id map
        Map<Integer, List<GroupCategoryRatingChange>> groupCategoryRatingChangeMap = new HashMap<>();

        int groupId = gameRecord.getGroupId();
        //Get each category
        for (int categoryId : associatedCategoryIds) {
            //Make a list of elo rating changes with avg ratings for each team for the category
            List<GroupCategoryRatingChange> groupCategoryRatingChanges = new ArrayList<>();

            //Calculate each teams avg elo rating from their players' ratings
            for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
                double avgRating = calculateTeamAverageRating(groupId, categoryId, teamOfPlayers.getMembers());

                //Add the elo rating change with the team's avg elo rating to the category's list of elo rating changes
                GroupCategoryRatingChange groupCategoryRatingChange = new GroupCategoryRatingChange(teamOfPlayers.getTeamNo(), categoryId, teamOfPlayers.getPosition(), avgRating);
                groupCategoryRatingChanges.add(groupCategoryRatingChange);

                //TODO this is switching the focus for this method, think on how to sort this out so this method isn't focusing on too much.
                // Then write this into tests.
                teamOfPlayers.addGroupCategoryRatingChange(groupCategoryRatingChange);
            }

            //Add the list of elo rating changes to the map for that category
            groupCategoryRatingChangeMap.put(categoryId, groupCategoryRatingChanges);
        }

        return groupCategoryRatingChangeMap;
    }

     */
}
