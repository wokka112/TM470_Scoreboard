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

package com.floatingpanda.scoreboard.views.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.GroupActivityAdapter;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * View for viewing the details of a group in the database. This includes a viewpager with pages to
 * display a group's game records, monthly scores, group members, category skill ratings, and the
 * general group details.
 */
public class GroupActivity extends AppCompatActivity {

    private Group group;
    private GroupViewModel groupViewModel;
    private TextView groupNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groupNameTextView = findViewById(R.id.banner_group_name);

        group = (Group) getIntent().getExtras().get("GROUP");

        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);
        groupViewModel.setSharedGroupId(group.getId());
        groupViewModel.getLiveDataGroupById(group.getId()).observe(this, new Observer<Group>() {
            @Override
            public void onChanged(Group group) {
                setGroup(group);
                setViews(group);
            }
        });

        ViewPager2 viewPager2 = findViewById(R.id.viewpager2);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        GroupActivityAdapter adapter = new GroupActivityAdapter(this, group);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(adapter.getTabTitle(position))).attach();
    }

    private void setGroup(Group group) {
        this.group = group;
    }

    private void setViews(Group group) {
        groupNameTextView.setText(group.getGroupName());
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
