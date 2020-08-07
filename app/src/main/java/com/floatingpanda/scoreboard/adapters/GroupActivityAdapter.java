package com.floatingpanda.scoreboard.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;
import com.floatingpanda.scoreboard.views.fragments.GroupDetailsFragment;
import com.floatingpanda.scoreboard.views.fragments.GameRecordListFragment;
import com.floatingpanda.scoreboard.views.fragments.GroupMemberListFragment;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.views.fragments.GroupSkillRatingListFragment;
import com.floatingpanda.scoreboard.views.fragments.ScoreWinnerListFragment;

//TODO update fragments so they provide empty argument constructors. See favourited thing talking about
// solution for it. This will stop the crash when turning horizontal.

/**
 * Viewpager adapter for group activity which provides 5 fragments - game records, monthly scores,
 * group members, skill ratings and group details.
 */
public class GroupActivityAdapter extends FragmentStateAdapter {

    private final int PAGE_COUNT = 5;
    private String tabTitles[] = new String[] { "Game Records", "Monthly Scores", "Group Members", "Skill Ratings", "Group Details"};
    private Group group;

    public GroupActivityAdapter(FragmentActivity fragmentActivity, Group group) {
        super(fragmentActivity);
        this.group = group;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new GameRecordListFragment();
        } else if (position == 1) {
            return new ScoreWinnerListFragment();
        } else if (position == 2) {
            return new GroupMemberListFragment();
        } else if (position == 3) {
            return new GroupSkillRatingListFragment();
        } else {
            return new GroupDetailsFragment();
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
