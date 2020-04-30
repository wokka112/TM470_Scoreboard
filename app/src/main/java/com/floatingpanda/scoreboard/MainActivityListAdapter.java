package com.floatingpanda.scoreboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.floatingpanda.scoreboard.mainactivityfragments.BoardGameListFragment;
import com.floatingpanda.scoreboard.mainactivityfragments.GroupListFragment;
import com.floatingpanda.scoreboard.mainactivityfragments.MemberListFragment;

public class MainActivityListAdapter extends FragmentStateAdapter {

    private final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Groups", "Members" };

    public MainActivityListAdapter(FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new GroupListFragment();
        } else if (position == 1) {
            return new MemberListFragment();
        } else {
            return new BoardGameListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }


}
