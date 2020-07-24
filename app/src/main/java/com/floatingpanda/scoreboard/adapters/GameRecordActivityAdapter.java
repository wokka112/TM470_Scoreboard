package com.floatingpanda.scoreboard.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.views.fragments.BgCategoryListFragment;
import com.floatingpanda.scoreboard.views.fragments.BoardGameListFragment;
import com.floatingpanda.scoreboard.views.fragments.GameRecordScoresListFragment;
import com.floatingpanda.scoreboard.views.fragments.GameRecordSkillRatingsListFragment;
import com.floatingpanda.scoreboard.views.fragments.GroupListFragment;
import com.floatingpanda.scoreboard.views.fragments.MemberListFragment;

//TODO update fragments so they provide empty argument constructors. See favourited thing talking about
// solution for it.

/**
 * ViewPager adapter for the game record activity. Provides 2 fragments - one for general game record
 * details (date, time, game played, teams, scores, etc.), and one for skill rating changes (players
 * and how much their skills changed based on the game, if competitive).
 */
public class GameRecordActivityAdapter extends FragmentStateAdapter {
    private final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Game Record Details", "Skill Rating Changes" };
    private GameRecord gameRecord;

    public GameRecordActivityAdapter(FragmentActivity fragmentActivity, GameRecord gameRecord) {
        super(fragmentActivity);
        this.gameRecord = gameRecord;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new GameRecordScoresListFragment(gameRecord);
        } else {
            return new GameRecordSkillRatingsListFragment(gameRecord);
        }
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }

    public String getTabTitle(int position) {
        return tabTitles[position];
    }
}
