package com.floatingpanda.scoreboard.data.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.floatingpanda.scoreboard.data.entities.GroupMonthlyScore;
import com.floatingpanda.scoreboard.data.entities.Score;

import java.util.Collections;
import java.util.List;

/**
 * A relation that combines a single group monthly score with a list of the group's scores and the
 * details of the members who got those scores.
 */
public class GroupMonthlyScoreWithScoresAndMemberDetails{
    @Embedded
    public GroupMonthlyScore groupMonthlyScore;
    @Relation(
            entity = Score.class,
            parentColumn = "group_monthly_score_id",
            entityColumn = "group_monthly_score_id"
    )
    List<ScoreWithMemberDetails> scoresWithMemberDetails;

    public GroupMonthlyScoreWithScoresAndMemberDetails(GroupMonthlyScore groupMonthlyScore, List<ScoreWithMemberDetails> scoresWithMemberDetails) {
        this.groupMonthlyScore = groupMonthlyScore;
        this.scoresWithMemberDetails = scoresWithMemberDetails;
        sortScores();
    }

    public GroupMonthlyScore getGroupMonthlyScore() { return groupMonthlyScore; }
    public void setGroupMonthlyScore(GroupMonthlyScore groupMonthlyScore) { this.groupMonthlyScore = groupMonthlyScore; }
    public List<ScoreWithMemberDetails> getScoresWithMemberDetails() { return scoresWithMemberDetails; }
    public void setScoresWithMemberDetails(List<ScoreWithMemberDetails> scoresWithMemberDetails) { this.scoresWithMemberDetails = scoresWithMemberDetails; }

    public void sortScores() {
        Collections.sort(scoresWithMemberDetails);
    }
}
