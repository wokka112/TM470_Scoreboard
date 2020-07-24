package com.floatingpanda.scoreboard.interfaces;

import com.floatingpanda.scoreboard.data.entities.Member;

/**
 * Interface providing access to removeGroupMember method to the group members list adapter.
 */
public interface RemoveGroupMemberInterface {
    void removeGroupMember(Member member);
}
