package com.floatingpanda.scoreboard.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.floatingpanda.scoreboard.views.fragments.GroupDetailsFragment;
import com.floatingpanda.scoreboard.views.fragments.GroupListFragment;
import com.floatingpanda.scoreboard.views.fragments.GroupMemberListFragment;
import com.floatingpanda.scoreboard.views.fragments.MemberListFragment;
import com.floatingpanda.scoreboard.data.Group;

public class GroupActivityAdapter extends FragmentStateAdapter {

    private final int PAGE_COUNT = 4;
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
            return new GroupListFragment();
        } else if (position == 1) {
            return new MemberListFragment();
        } else if (position == 2) {
            return new GroupMemberListFragment(group);
        } else {
            return new GroupDetailsFragment(group);
        }
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }
}
