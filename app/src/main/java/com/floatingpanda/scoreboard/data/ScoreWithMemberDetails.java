package com.floatingpanda.scoreboard.data;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.data.entities.Score;

import java.util.List;

public class ScoreWithMemberDetails implements Comparable<ScoreWithMemberDetails>{
    @Embedded
    public Score score;
    @Relation(
            parentColumn = "member_id",
            entityColumn = "member_id"
    )
    public Member member;

    public ScoreWithMemberDetails(Score score, Member member) {
        this.score = score;
        this.member = member;
    }

    public Score getScore() { return score; }
    public void setScore(Score score) { this.score = score; }
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    @Override
    public int compareTo(ScoreWithMemberDetails o) {
        if (this.getScore().getScore() == o.getScore().getScore()) {
            return this.getMember().getNickname().compareTo(o.getMember().getNickname());
        } else if (this.getScore().getScore() < o.getScore().getScore()) {
            return 1;
        } else {
            return -1;
        }
    }
}
