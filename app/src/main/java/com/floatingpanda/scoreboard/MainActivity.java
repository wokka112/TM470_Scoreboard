package com.floatingpanda.scoreboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.floatingpanda.scoreboard.adapters.MainActivityAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ViewPager2 viewPager2 = findViewById(R.id.viewpager2);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        viewPager2.setAdapter(createMainActivityListAdapter());

        //TODO set tab names to correct names (Groups, Members, Board Games, Game Categories)
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText("TAB " + (position + 1))
        ).attach();
    }

    private MainActivityAdapter createMainActivityListAdapter() {
        MainActivityAdapter adapter = new MainActivityAdapter(this);
        return adapter;
    }
}
