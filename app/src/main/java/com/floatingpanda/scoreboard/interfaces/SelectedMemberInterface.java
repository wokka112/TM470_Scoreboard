package com.floatingpanda.scoreboard.interfaces;

import com.floatingpanda.scoreboard.data.entities.Member;

/**
 * Interface that provides access to methods add and remove members from a list of selected members.
 */
public interface SelectedMemberInterface {
    void addSelectedMember(Member member);
    void removeSelectedMember(Member member);
}
