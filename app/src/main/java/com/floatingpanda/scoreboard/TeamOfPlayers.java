package com.floatingpanda.scoreboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.floatingpanda.scoreboard.data.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class TeamOfPlayers implements Parcelable, Comparable<TeamOfPlayers> {

    private int teamNo;
    private int place;
    private int score;
    //TODO use a hashset instead? Then it limits it so each member has to be unique.
    private List<Member> members;

    public TeamOfPlayers(int teamNo, int place, int score, List<Member> members) {
        this.teamNo = teamNo;
        this.place = place;
        this.score = score;
        this.members = members;
    }

    public TeamOfPlayers(int teamNo, int place) {
        this(teamNo, place, -1, new ArrayList<Member>());
    }

    public TeamOfPlayers(Parcel source) {
        teamNo = source.readInt();
        place = source.readInt();
        score = source.readInt();
        members = source.readArrayList(Member.class.getClassLoader());
    }

    public int getTeamNo() { return teamNo; }
    public void setTeamNo(int teamNo) { this.teamNo = teamNo; }
    public int getPlace() { return place; }
    public void setPlace(int place) { this.place = place; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public List<Member> getMembers() { return new ArrayList<>(members); }
    public void setMembers(List<Member> members) { this.members = members; }

    public void addPlayer(Member member) {
        if (!this.members.contains(member)) {
            this.members.add(member);
        }
    }

    public void removePlayer(Member member) {
        this.members.remove(member);
    }

    @Override
    public int compareTo(TeamOfPlayers other) {
        if (this.getPlace() == other.getPlace()) {
            if (this.getTeamNo() == other.getTeamNo()) {
                return 0;
            } else if (this.getTeamNo() > other.getTeamNo()) {
                return 1;
            } else {
                return -1;
            }
        } else if (this.getPlace() > other.getPlace()) {
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
        dest.writeInt(place);
        dest.writeInt(score);
        dest.writeList(members);
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
