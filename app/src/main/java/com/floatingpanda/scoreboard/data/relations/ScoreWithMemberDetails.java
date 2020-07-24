package com.floatingpanda.scoreboard.data.relations;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.entities.Score;

/**
 * A relation that combines a single score with the member who earned that score.
 */
public class ScoreWithMemberDetails implements Comparable<ScoreWithMemberDetails>, Parcelable {
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

    public ScoreWithMemberDetails(Parcel source) {
        score = source.readParcelable(Score.class.getClassLoader());
        member = source.readParcelable(Member.class.getClassLoader());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(score, 0);
        dest.writeParcelable(member, 0);
    }

    public static final Creator<ScoreWithMemberDetails> CREATOR = new Creator<ScoreWithMemberDetails>() {
        @Override
        public ScoreWithMemberDetails[] newArray(int size) {
            return new ScoreWithMemberDetails[size];
        }

        @Override
        public ScoreWithMemberDetails createFromParcel(Parcel source) {
            return new ScoreWithMemberDetails(source);
        }
    };
}
