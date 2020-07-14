package com.floatingpanda.scoreboard.views.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.GameRecordActivityAdapter;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.viewmodels.GameRecordViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class GameRecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager2 = findViewById(R.id.viewpager2);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        GameRecord gameRecord = (GameRecord) getIntent().getExtras().get("GAME_RECORD");
        GameRecordActivityAdapter adapter = new GameRecordActivityAdapter(this, gameRecord);

        viewPager2.setAdapter(adapter);

        //TODO set tab names to correct names
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText("TAB " + (position + 1))
        ).attach();
    }


}
