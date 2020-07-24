package com.floatingpanda.scoreboard.views.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.GroupActivityAdapter;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * View for viewing the details of a group in the database. This includes a viewpager with pages to
 * display a group's game records, monthly scores, group members, category skill ratings, and the
 * general group details.
 */
public class GroupActivity extends AppCompatActivity {

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO switch to using a viewmodel to get the group as LiveData. As it stands, if group is
        // edited then the name doesn't change on the banner.

        group = (Group) getIntent().getExtras().get("GROUP");

        TextView groupNameTextView = findViewById(R.id.banner_group_name);
        groupNameTextView.setText(group.getGroupName());

        ViewPager2 viewPager2 = findViewById(R.id.viewpager2);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        GroupActivityAdapter adapter = new GroupActivityAdapter(this, group);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(adapter.getTabTitle(position))).attach();
    }

    /**
     * Sets the back arrow in the taskbar to go back to the previous activity.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
