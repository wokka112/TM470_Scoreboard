package com.floatingpanda.scoreboard.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.floatingpanda.scoreboard.views.fragments.GroupDetailsFragment;
import com.floatingpanda.scoreboard.views.fragments.GameRecordListFragment;
import com.floatingpanda.scoreboard.views.fragments.GroupMemberListFragment;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.views.fragments.GroupSkillRatingListFragment;
import com.floatingpanda.scoreboard.views.fragments.ScoreWinnerListFragment;

public class GroupActivityAdapter extends FragmentStateAdapter {

    private final int PAGE_COUNT = 5;
    private String tabTitles[] = new String[] { };
    private Group group;

    public GroupActivityAdapter(FragmentActivity fragmentActivity, Group group) {
        super(fragmentActivity);
        this.group = group;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new GameRecordListFragment(group);
        } else if (position == 1) {
            return new ScoreWinnerListFragment(group);
        } else if (position == 2) {
            return new GroupMemberListFragment(group);
        } else if (position == 3) {
            return new GroupSkillRatingListFragment(group);
        } else {
            return new GroupDetailsFragment(group);
        }
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }
}
