package com.floatingpanda.scoreboard.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.floatingpanda.scoreboard.views.fragments.BoardGameListFragment;
import com.floatingpanda.scoreboard.views.fragments.BgCategoryListFragment;
import com.floatingpanda.scoreboard.views.fragments.GroupListFragment;
import com.floatingpanda.scoreboard.views.fragments.MemberListFragment;

/**
 * ViewPager adapter for the main activity. Provides 4 fragments - groups, members, board games, and
 * board game categories.
 */
public class MainActivityAdapter extends FragmentStateAdapter {

    private final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Groups", "Members", "Board Games", "Board Game Categories" };

    public MainActivityAdapter(FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new GroupListFragment();
        } else if (position == 1) {
            return new MemberListFragment();
        } else if (position == 2) {
            return new BoardGameListFragment();
        } else {
            return new BgCategoryListFragment();
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
