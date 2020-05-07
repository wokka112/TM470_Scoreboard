package com.floatingpanda.scoreboard.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.floatingpanda.scoreboard.BoardGameListFragment;
import com.floatingpanda.scoreboard.BgCategoryListFragment;
import com.floatingpanda.scoreboard.GroupListFragment;
import com.floatingpanda.scoreboard.MemberListFragment;

public class MainActivityAdapter extends FragmentStateAdapter {

    private final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Groups", "Members" };

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


}
