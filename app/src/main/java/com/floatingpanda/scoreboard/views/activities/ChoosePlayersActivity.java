package com.floatingpanda.scoreboard.views.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.ChoosePlayersPagerAdapter;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.viewmodels.ChoosePlayerSharedViewModel;
import com.floatingpanda.scoreboard.viewmodels.GameRecordAddViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class ChoosePlayersActivity extends AppCompatActivity {

    private ChoosePlayerSharedViewModel choosePlayerSharedViewModel;

    private Group group;
    private List<Member> potentialPlayers;
    private int numOfTeams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_layout);

        group = (Group) getIntent().getExtras().get("GROUP");
        numOfTeams = (int) getIntent().getExtras().get("NUM_OF_TEAMS");

        choosePlayerSharedViewModel = new ViewModelProvider(this).get(ChoosePlayerSharedViewModel.class);
        choosePlayerSharedViewModel.initialisePotentialPlayers(group.getId());

        ViewPager2 viewPager2 = findViewById(R.id.view_pager);
        viewPager2.setAdapter(new ChoosePlayersPagerAdapter(this, numOfTeams));

    }
}
