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

public class GameRecordActivityAdapter extends FragmentStateAdapter {
    private final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "General Details", "Skill Ratings" };
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
}
