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
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.views.fragments.BgCategoryListFragment;
import com.floatingpanda.scoreboard.views.fragments.BoardGameListFragment;
import com.floatingpanda.scoreboard.views.fragments.GameRecordScoresListFragment;
import com.floatingpanda.scoreboard.views.fragments.GameRecordSkillRatingsListFragment;
import com.floatingpanda.scoreboard.views.fragments.GroupListFragment;
import com.floatingpanda.scoreboard.views.fragments.MemberListFragment;

/**
 * ViewPager adapter for the game record activity. Provides 2 fragments - one for general game record
 * details (date, time, game played, teams, scores, etc.), and one for skill rating changes (players
 * and how much their skills changed based on the game, if competitive).
 */
public class GameRecordActivityAdapter extends FragmentStateAdapter {
    private final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Game Record Details", "Skill Rating Changes" };

    public GameRecordActivityAdapter(FragmentActivity fragmentActivity, GameRecord gameRecord) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new GameRecordScoresListFragment();
        } else {
            return new GameRecordSkillRatingsListFragment();
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
