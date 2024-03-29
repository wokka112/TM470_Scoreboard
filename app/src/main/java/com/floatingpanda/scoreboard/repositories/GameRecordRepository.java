/*
ScoreBoard

Copyright © 2020 Adam Poole

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.floatingpanda.scoreboard.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.GroupCategoryRatingChange;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.calculators.Calculator;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.daos.GroupCategorySkillRatingDao;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.daos.PlayerSkillRatingChangeDao;
import com.floatingpanda.scoreboard.data.entities.GroupCategorySkillRating;
import com.floatingpanda.scoreboard.data.entities.GroupMonthlyScore;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.entities.PlayerSkillRatingChange;
import com.floatingpanda.scoreboard.data.entities.Score;
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
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayersAndRatingChanges;
import com.floatingpanda.scoreboard.data.relations.PlayerWithRatingChanges;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO write rest of javadoc comments
/**
 * A repository primarily for accessing the game records table in the database, but also providing
 * access to player team tables. Also inserts and deletes players, player teams, scores, group
 * monthly scores, player skill rating changes and group category skill rating changes into and
 * from the database.
 */
public class GameRecordRepository {

    private AppDatabase appDatabase;

    private GameRecordDao gameRecordDao;
    private PlayerTeamDao playerTeamDao;
    private PlayerDao playerDao;
    private GroupMonthlyScoreDao groupMonthlyScoreDao;
    private ScoreDao scoreDao;
    private BoardGameDao boardGameDao;
    private BgCategoryDao bgCategoryDao;
    private AssignedCategoryDao assignedCategoryDao;
    private GroupCategorySkillRatingDao groupCategorySkillRatingDao;
    private PlayerSkillRatingChangeDao playerSkillRatingChangeDao;
    private MemberDao memberDao;
    private LiveData<List<GameRecordWithPlayerTeamsAndPlayers>> allGameRecordsWithTeamsAndPlayers;

    public GameRecordRepository(Application application) {
        appDatabase = AppDatabase.getDatabase(application);

        gameRecordDao = appDatabase.gameRecordDao();
        playerTeamDao = appDatabase.playerTeamDao();
        playerDao = appDatabase.playerDao();
        groupMonthlyScoreDao = appDatabase.groupMonthlyScoreDao();
        scoreDao = appDatabase.scoreDao();
        boardGameDao = appDatabase.boardGameDao();
        bgCategoryDao = appDatabase.bgCategoryDao();
        assignedCategoryDao = appDatabase.assignedCategoryDao();
        groupCategorySkillRatingDao = appDatabase.groupCategorySkillRatingDao();
        playerSkillRatingChangeDao = appDatabase.playerSkillRatingChangeDao();
        memberDao = appDatabase.memberDao();

        allGameRecordsWithTeamsAndPlayers = gameRecordDao.getAllGameRecordsWithPlayerTeamsAndPlayers();
    }

    //Used for testing
    public GameRecordRepository(AppDatabase db) {
        appDatabase = db;

        gameRecordDao = appDatabase.gameRecordDao();
        playerTeamDao = appDatabase.playerTeamDao();
        playerDao = appDatabase.playerDao();
        groupMonthlyScoreDao = appDatabase.groupMonthlyScoreDao();
        scoreDao = appDatabase.scoreDao();
        boardGameDao = appDatabase.boardGameDao();
        bgCategoryDao = appDatabase.bgCategoryDao();
        assignedCategoryDao = appDatabase.assignedCategoryDao();
        groupCategorySkillRatingDao = appDatabase.groupCategorySkillRatingDao();
        playerSkillRatingChangeDao = appDatabase.playerSkillRatingChangeDao();
        memberDao = appDatabase.memberDao();

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

    public LiveData<List<PlayerTeamWithPlayersAndRatingChanges>> getPlayerTeamsWithPlayersAndRatingChangesByRecordId(int recordId) {
        return playerTeamDao.getPlayerTeamsWithPlayersAndRatingChangesByRecordId(recordId);
    }

    public void addGameRecordAndPlayerTeams(GameRecord gameRecord, List<TeamOfPlayers> teamsOfPlayers) {
        AppDatabase.getExecutorService().execute(() -> {
            appDatabase.runInTransaction(new Runnable() {
                @Override
                public void run() {
                    //Insert game record and get id
                    int recordId = (int) gameRecordDao.insert(gameRecord);
                    gameRecord.setId(recordId);

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
                }
            });
        });
    }

    public void deleteGameRecord(GameRecord gameRecord) {
        AppDatabase.getExecutorService().execute(() -> {
            appDatabase.runInTransaction(new Runnable() {
                @Override
                public void run() {
                    List<PlayerTeamWithPlayersAndRatingChanges> playerTeamsWithPlayersAndRatingChanges =
                            playerTeamDao.getNonLivePlayerTeamsWithPlayersAndRatingChangesByRecordId(gameRecord.getId());

                    removeScores(gameRecord, playerTeamsWithPlayersAndRatingChanges);

                    undoSkillRatingChanges(gameRecord, playerTeamsWithPlayersAndRatingChanges);

                    gameRecordDao.delete(gameRecord);
                }
            });
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

        int groupMonthlyScoreId;
        if (!groupMonthlyScoreDao.containsGroupMonthlyScore(groupId, year, month)) {
            //4 quarters, 12 months in a year. Each quarter consists of 3 months.
            //To get the correct quarter, divide the month by 3 and remove any numbers after the decimal
            // place. If there was a remainder, add 1 to the value to get the correct quarter.
            int quarter = month / 3;
            //If there's a remainder, then the month belongs to the quarter above the current quarter value.
            int remainder = month % 3;
            if (remainder != 0) {
                quarter += 1;
            }

            groupMonthlyScoreId = (int) groupMonthlyScoreDao.insert(new GroupMonthlyScore(groupId, year, quarter, month));
        } else {
            //Get group monthly score id
            groupMonthlyScoreId = groupMonthlyScoreDao.getGroupMonthlyScoreIdByGroupIdAndYearAndMonth(groupId, year, month);
        }

        //Get member id
        int memberId = member.getId();
        //Update score for group monthly score id and member id with the new score.
        if (!scoreDao.containsScore(groupMonthlyScoreId, memberId)) {
            scoreDao.insert(new Score(groupMonthlyScoreId, memberId, score));
        } else {
            scoreDao.addScore(groupMonthlyScoreId, memberId, score);
        }
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
            int playerTeamId = playerTeamDao.getNonLivePlayerTeamIdByTeamNumberAndRecordId(teamOfPlayers.getTeamNo(), gameRecord.getId());
            for (Member member : teamOfPlayers.getMembers()) {
                int playerId = playerDao.getPlayerIdByPlayerTeamIdAndMemberNickname(playerTeamId, member.getNickname());

                recordMemberSkillRatingChanges(groupId, member.getId(), playerId, teamOfPlayers.getGroupCategoryRatingChanges());
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
            String categoryName = bgCategoryDao.getNonLiveCategoryNameByCategoryId(categoryId);

            //Calculate each teams avg elo rating from their players' ratings
            for (TeamOfPlayers teamOfPlayers : teamsOfPlayers) {
                double avgRating = calculateTeamAverageRating(groupId, categoryId, teamOfPlayers.getMembers());

                //Add the elo rating change with the team's avg elo rating to the category's list of elo rating changes
                GroupCategoryRatingChange groupCategoryRatingChange = new GroupCategoryRatingChange(teamOfPlayers.getTeamNo(), categoryId, categoryName, teamOfPlayers.getPosition(), avgRating);
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
            double eloRating = getMemberCategorySkillRating(groupId, categoryId, member.getId());
            totalRating += eloRating;
        }
        return totalRating / members.size();
    }

    public double getMemberCategorySkillRating(int groupId, int categoryId, int memberId) {
        double eloRating;
        if (!groupCategorySkillRatingDao.containsGroupCategorySkillRating(groupId, categoryId, memberId)) {
            eloRating = 1500.0;
            groupCategorySkillRatingDao.insert(new GroupCategorySkillRating(groupId, memberId, categoryId, eloRating));
        } else {
            eloRating = groupCategorySkillRatingDao.getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(groupId, categoryId, memberId);
        }

        return eloRating;
    }

    public void recordMemberSkillRatingChanges(int groupId, int memberId, int playerId, List<GroupCategoryRatingChange> groupCategoryRatingChanges) {
        for (GroupCategoryRatingChange groupCategoryRatingChange : groupCategoryRatingChanges) {
            String categoryName = groupCategoryRatingChange.getCategoryName();
            double oldRating = getMemberCategorySkillRating(groupId, groupCategoryRatingChange.getCategoryId(), memberId);
            double ratingChange = groupCategoryRatingChange.getEloRatingChange();

            Log.w("GameRecordRepository.java", "PlayerId: " + playerId + " Category Name: " + categoryName);

            playerSkillRatingChangeDao.insert(new PlayerSkillRatingChange(playerId, categoryName, oldRating, ratingChange));
        }
    }

    public void updateMemberSkillRatings(int groupId, int memberId, List<GroupCategoryRatingChange> groupCategoryRatingChanges) {
        //Go through category rating changes for group and update member's category skill ratings
        for (GroupCategoryRatingChange groupCategoryRatingChange : groupCategoryRatingChanges) {
            int categoryId = groupCategoryRatingChange.getCategoryId();
            double addSkillRating = groupCategoryRatingChange.getEloRatingChange();
            groupCategorySkillRatingDao.addSkillRatingFromSingleGame(groupId, categoryId, memberId, addSkillRating);
        }
    }

    public void removeScores(GameRecord gameRecord, List<PlayerTeamWithPlayersAndRatingChanges> playerTeamsWithPlayersAndRatingChanges) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(gameRecord.getDateTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        //SCORE STUFF
        for (PlayerTeamWithPlayersAndRatingChanges playerTeamWithPlayersAndRatingChanges : playerTeamsWithPlayersAndRatingChanges) {
            int groupMonthlyScoreId = groupMonthlyScoreDao.getGroupMonthlyScoreIdByGroupIdAndYearAndMonth(gameRecord.getGroupId(), year, month);
            int score = playerTeamWithPlayersAndRatingChanges.getPlayerTeam().getScore();
            for (PlayerWithRatingChanges playerWithRatingChanges : playerTeamWithPlayersAndRatingChanges.getPlayersWithRatingChanges()) {
                String memberNickname = playerWithRatingChanges.getPlayer().getMemberNickname();
                int memberId = memberDao.getMemberIdByMemberNickname(memberNickname);
                scoreDao.removeScore(groupMonthlyScoreId, memberId, score);
            }
        }
    }

    public void undoSkillRatingChanges(GameRecord gameRecord, List<PlayerTeamWithPlayersAndRatingChanges> playerTeamsWithPlayersAndRatingChanges) {
        List<PlayerWithRatingChanges> playersWithRatingChanges = new ArrayList<>();
        for(PlayerTeamWithPlayersAndRatingChanges playerTeamWithPlayersAndRatingChanges : playerTeamsWithPlayersAndRatingChanges) {
            playersWithRatingChanges.addAll(playerTeamWithPlayersAndRatingChanges.getPlayersWithRatingChanges());
        }

        for (PlayerWithRatingChanges playerWithRatingChanges : playersWithRatingChanges) {
            String memberNickname = playerWithRatingChanges.getPlayer().getMemberNickname();
            int memberId = memberDao.getMemberIdByMemberNickname(memberNickname);
            for (PlayerSkillRatingChange playerSkillRatingChange : playerWithRatingChanges.getPlayerSkillRatingChanges()) {
                int categoryId = bgCategoryDao.getCategoryIdByCategoryName(playerSkillRatingChange.getCategoryName());
                double ratingChange = playerSkillRatingChange.getRatingChange();
                groupCategorySkillRatingDao.removeSkillRatingFromSingleGame(gameRecord.getGroupId(), categoryId, memberId, ratingChange);
            }
        }
    }
}
