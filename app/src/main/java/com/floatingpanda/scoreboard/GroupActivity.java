package com.floatingpanda.scoreboard;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.floatingpanda.scoreboard.adapters.GroupActivityAdapter;
import com.floatingpanda.scoreboard.adapters.MainActivityAdapter;
import com.floatingpanda.scoreboard.data.Group;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GroupActivity extends AppCompatActivity {

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        group = (Group) getIntent().getExtras().get("GROUP");

        TextView groupNameTextView = findViewById(R.id.banner_group_name);
        groupNameTextView.setText(group.getGroupName());

        ViewPager2 viewPager2 = findViewById(R.id.viewpager2);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        viewPager2.setAdapter(createGroupActivityListAdapter());

        //TODO set tab names to correct names (Groups, Members, Board Games, Game Categories)
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText("TAB " + (position + 1))
        ).attach();
    }

    private GroupActivityAdapter createGroupActivityListAdapter() {
        GroupActivityAdapter adapter = new GroupActivityAdapter(this, group);
        return adapter;
    }
}
