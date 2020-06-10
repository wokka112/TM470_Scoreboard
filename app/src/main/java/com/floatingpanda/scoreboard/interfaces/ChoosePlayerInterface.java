package com.floatingpanda.scoreboard.interfaces;

import com.floatingpanda.scoreboard.data.entities.Member;

public interface ChoosePlayerInterface {
    void addPlayerToTeam(Member member);
    void removePlayerFromTeam(Member member);
}
