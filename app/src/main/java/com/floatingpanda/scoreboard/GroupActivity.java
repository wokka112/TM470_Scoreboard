package com.floatingpanda.scoreboard;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group);

        ImageView imageView = findViewById(R.id.banner);
        ViewPager2 viewPager2 = (ViewPager2) findViewById(R.id.viewpager2);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        imageView.setImageResource(R.drawable.ic_launcher_background);

        viewPager2.setAdapter(createGroupActivityListAdapter());

        //TODO set tab names to correct names (Groups, Members, Board Games)
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText("TAB " + (position + 1))
        ).attach();

    }

    private GroupActivityListAdapter createGroupActivityListAdapter() {
        GroupActivityListAdapter adapter = new GroupActivityListAdapter(this);
        return adapter;
    }

}
