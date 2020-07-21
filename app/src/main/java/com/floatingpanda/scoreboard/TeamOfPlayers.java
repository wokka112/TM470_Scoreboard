package com.floatingpanda.scoreboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.Scoreable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamOfPlayers implements Parcelable, Comparable<TeamOfPlayers>, Scoreable {

    private int teamNo;
    private int position;
    private int score;
    private List<Member> members;
    private List<GroupCategoryRatingChange> groupCategoryRatingChanges;

    public TeamOfPlayers(int teamNo, int position, int score, List<Member> members) {
        this.teamNo = teamNo;
        this.position = position;
        this.score = score;
        this.members = members;
        this.groupCategoryRatingChanges = new ArrayList<>();
    }

    public TeamOfPlayers(int teamNo, int position) {
        this(teamNo, position, -1, new ArrayList<Member>());
    }

    public TeamOfPlayers(Parcel source) {
        teamNo = source.readInt();
        position = source.readInt();
        score = source.readInt();
        members = source.readArrayList(Member.class.getClassLoader());
        groupCategoryRatingChanges = source.readArrayList(GroupCategoryRatingChange.class.getClassLoader());
    }

    public int getTeamNo() { return teamNo; }
    public void setTeamNo(int teamNo) { this.teamNo = teamNo; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public List<Member> getMembers() { return new ArrayList<>(members); }
    public void setMembers(List<Member> members) { this.members = members; }
    public List<GroupCategoryRatingChange> getGroupCategoryRatingChanges() { return new ArrayList<>(groupCategoryRatingChanges); }
    public void setGroupCategoryRatingChanges(List<GroupCategoryRatingChange> groupCategoryRatingChanges) { this.groupCategoryRatingChanges = groupCategoryRatingChanges; }

    public void addPlayer(Member member) {
        if (!this.members.contains(member)) {
            this.members.add(member);
        }
    }

    public void removePlayer(Member member) {
        this.members.remove(member);
    }

    public void addGroupCategoryRatingChange(GroupCategoryRatingChange groupCategoryRatingChange) { this.groupCategoryRatingChanges.add(groupCategoryRatingChange); }

    public void removeGroupCategoryRatingChange(GroupCategoryRatingChange groupCategoryRatingChange) { groupCategoryRatingChanges.remove(groupCategoryRatingChange); }

    @Override
    public int compareTo(TeamOfPlayers other) {
        if (this.getPosition() == other.getPosition()) {
            if (this.getTeamNo() == other.getTeamNo()) {
                return 0;
            } else if (this.getTeamNo() > other.getTeamNo()) {
                return 1;
            } else {
                return -1;
            }
        } else if (this.getPosition() > other.getPosition()) {
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
        dest.writeInt(teamNo);
        dest.writeInt(position);
        dest.writeInt(score);
        dest.writeList(members);
        dest.writeList(groupCategoryRatingChanges);
    }

    public static final Creator<TeamOfPlayers> CREATOR = new Creator<TeamOfPlayers>() {
        @Override
        public TeamOfPlayers[] newArray(int size) {
            return new TeamOfPlayers[size];
        }

        @Override
        public TeamOfPlayers createFromParcel(Parcel source) {
            return new TeamOfPlayers(source);
        }
    };
}
