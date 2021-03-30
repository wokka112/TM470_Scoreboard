/*
ScoreBoard

Copyright © 2020 Adam Poole

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
