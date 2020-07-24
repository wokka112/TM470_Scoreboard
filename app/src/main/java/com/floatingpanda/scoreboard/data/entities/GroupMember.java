package com.floatingpanda.scoreboard.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

/**
 * Represents group members, i.e. members in the app who belong to a group. This is a simple table
 * that holds many-to-many relationships between groups and members, representing which members are
 * parts of which groups. The group is linked to via the groupId attribute, which is a foreign key.
 * The members are linked to via the memberId attribute, which is a foreign key.
 */
@Entity(tableName = "group_members", primaryKeys = {"group_id", "member_id"},
        foreignKeys = {@ForeignKey(entity = Group.class,
                parentColumns = "group_id",
                childColumns = "group_id",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Member.class,
                        parentColumns = "member_id",
                        childColumns = "member_id",
                        onDelete = ForeignKey.CASCADE)})
public class GroupMember {

    @NonNull
    @ColumnInfo(name = "group_id")
    private int groupId;

    @NonNull
    @ColumnInfo(name = "member_id")
    private int memberId;

    public GroupMember(int groupId, int memberId) {
        this.groupId = groupId;
        this.memberId = memberId;
    }

    @Ignore
    public GroupMember(GroupMember groupMember) {
        this.groupId = groupMember.getGroupId();
        this.memberId = groupMember.getMemberId();
    }

    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        GroupMember groupMember = (GroupMember) obj;

        return (groupMember.getGroupId() == this.getGroupId()
                && groupMember.getMemberId() == this.getMemberId());
    }
}
