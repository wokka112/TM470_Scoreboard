package com.floatingpanda.scoreboard.interfaces;

import com.floatingpanda.scoreboard.data.entities.Member;

public interface SelectedMemberInterface {
    public void addSelectedMember(Member member);
    public void removeSelectedMember(Member member);
}
