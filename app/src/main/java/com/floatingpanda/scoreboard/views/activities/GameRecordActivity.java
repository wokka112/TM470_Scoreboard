package com.floatingpanda.scoreboard.views.activities;

import android.os.Bundle;
import android.view.MenuItem;
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

/**
 * View for viewing details about a game record. Includes a ViewPager that goes through fragments
 * to display the general details (teams, positions, scores) for a game record, and to display the
 * skill rating changes for the players in the game record.
 */
public class GameRecordActivity extends AppCompatActivity {

    private GameRecordViewModel gameRecordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager2 viewPager2 = findViewById(R.id.viewpager2);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        GameRecord gameRecord = (GameRecord) getIntent().getExtras().get("GAME_RECORD");

        gameRecordViewModel = new ViewModelProvider(this).get(GameRecordViewModel.class);
        gameRecordViewModel.setSharedGameRecord(gameRecord);

        GameRecordActivityAdapter adapter = new GameRecordActivityAdapter(this, gameRecord);
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
