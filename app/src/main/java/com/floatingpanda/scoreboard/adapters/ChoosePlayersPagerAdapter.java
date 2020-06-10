package com.floatingpanda.scoreboard.adapters;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.floatingpanda.scoreboard.views.fragments.ChoosePlayerPageFragment;

public class ChoosePlayersPagerAdapter extends FragmentStateAdapter {
    int numOfTeams;

    public ChoosePlayersPagerAdapter(FragmentActivity fa, int numOfTeams) {
        super(fa);
        this.numOfTeams = numOfTeams;
    }

    @Override
    public Fragment createFragment(int position) {
        return new ChoosePlayerPageFragment(position + 1);
    }

    @Override
    public int getItemCount() {
        return numOfTeams;
    }
}
