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

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.GroupMember;
import com.floatingpanda.scoreboard.data.entities.Member;

import java.util.List;

/**
 * A relation that combines a single group with a list of members who are part of that group.
 */
public class GroupWithMembers implements Parcelable {
    @Embedded
    public Group group;
    @Relation(
            parentColumn = "group_id",
            entityColumn = "member_id",
            associateBy = @Junction(GroupMember.class)
    )
    public List<Member> members;

    public GroupWithMembers(Group group, List<Member> members) {
        this.group = group;
        this.members = members;
    }

    public GroupWithMembers(GroupWithMembers groupWithMembers) {
        this.group = groupWithMembers.getGroup();
        this.members = groupWithMembers.getMembers();
    }

    public GroupWithMembers(Parcel source) {
        this.group = source.readParcelable(Group.class.getClassLoader());
        this.members = source.readArrayList(Member.class.getClassLoader());
    }

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }
    public List<Member> getMembers() { return this.members; }
    public void setMembers(List<Member> members) { this.members = members; }

    public void addMember(Member member) {
        if (!this.members.contains(member)) {
            members.add(member);
        }
    }

    public void removeMember(Member member) {
        this.members.remove(member);
    }

    public Member getMember(int index) {
        return this.members.get(index);
    }

    public void clearMembers() {
        this.members.clear();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(group, 0);
        dest.writeList(members);
    }

    public static final Creator<GroupWithMembers> CREATOR = new Creator<GroupWithMembers>() {
        @Override
        public GroupWithMembers[] newArray(int size) {
            return new GroupWithMembers[size];
        }

        @Override
        public GroupWithMembers createFromParcel(Parcel source) {
            return new GroupWithMembers(source);
        }
    };
}
