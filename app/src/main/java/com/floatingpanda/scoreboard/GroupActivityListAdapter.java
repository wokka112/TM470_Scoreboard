package com.floatingpanda.scoreboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.floatingpanda.scoreboard.groupactivityfragments.GroupDetailsFragment;
import com.floatingpanda.scoreboard.groupactivityfragments.RecordListFragment;
import com.floatingpanda.scoreboard.groupactivityfragments.WinnerListFragment;
import com.floatingpanda.scoreboard.mainactivityfragments.MemberListFragment;

public class GroupActivityListAdapter extends FragmentStateAdapter {

    private final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Groups", "Members" };

    public GroupActivityListAdapter(FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        /*
        if (position == 0) {
            return new RecordListFragment();
        } else if (position == 1) {
            return new WinnersListFragment();
        } else if (position == 2) {
            return new MemberListFragment();
        } else {
            return new GroupDetailsFragment();
        }
        */

        if (position == 0) {
            return new RecordListFragment();
        } else if (position == 1) {
            return new WinnerListFragment();
        } else if (position ==2) {
            return new MemberListFragment();
        } else {
            return new GroupDetailsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }
}
