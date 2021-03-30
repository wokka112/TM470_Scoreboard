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
