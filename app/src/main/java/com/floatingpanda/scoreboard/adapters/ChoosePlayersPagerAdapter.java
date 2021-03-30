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

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.floatingpanda.scoreboard.views.fragments.ChoosePlayerPageFragment;

/**
 * Adapter for the choose players activity. Creates a page per team based on how many teams there are
 * in the game.
 */
public class ChoosePlayersPagerAdapter extends FragmentStateAdapter {
    int numOfTeams;

    public ChoosePlayersPagerAdapter(FragmentActivity fa, int numOfTeams) {
        super(fa);
        this.numOfTeams = numOfTeams;
    }

    public static final ChoosePlayerPageFragment newInstance(int position) {
        ChoosePlayerPageFragment fragment = new ChoosePlayerPageFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt("TEAM_NO", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Fragment createFragment(int position) {
        return newInstance(position + 1);
    }

    @Override
    public int getItemCount() {
        return numOfTeams;
    }
}
