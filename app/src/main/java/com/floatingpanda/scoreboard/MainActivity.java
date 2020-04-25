package com.floatingpanda.scoreboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ViewPager2 viewPager2 = (ViewPager2) findViewById(R.id.viewpager2);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        viewPager2.setAdapter(createCategoryAdapter());

        //TODO set tab names to correct names (Groups, Members, Board Games)
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText("TAB " + (position + 1))
        ).attach();

    }

    private MainActivityListAdapter createCategoryAdapter() {
        MainActivityListAdapter adapter = new MainActivityListAdapter(this);
        return adapter;
    }
}
