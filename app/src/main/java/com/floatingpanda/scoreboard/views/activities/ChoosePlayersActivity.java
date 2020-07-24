package com.floatingpanda.scoreboard.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.adapters.ChoosePlayersPagerAdapter;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.utils.AlertDialogHelper;
import com.floatingpanda.scoreboard.viewmodels.ChoosePlayerSharedViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * View for selecting players for teams in a game record.
 */
public class ChoosePlayersActivity extends AppCompatActivity {

    private final int CONFIRM_GAME_RECORD_REQUEST_CODE = 1;
    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private Button backButton, nextButton;

    private ChoosePlayerSharedViewModel choosePlayerSharedViewModel;

    private Group group;
    private GameRecord gameRecord;
    private int noOfTeams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_players);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        backButton = findViewById(R.id.back_button);
        nextButton = findViewById(R.id.next_button);

        group = (Group) getIntent().getExtras().get("GROUP");
        gameRecord = (GameRecord) getIntent().getExtras().get("GAME_RECORD");
        noOfTeams = gameRecord.getNoOfTeams();

        choosePlayerSharedViewModel = new ViewModelProvider(this).get(ChoosePlayerSharedViewModel.class);
        choosePlayerSharedViewModel.initialisePotentialPlayers(group.getId());

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new ChoosePlayersPagerAdapter(this, noOfTeams));
        //Disables swiping so it can be controlled with buttons.
        viewPager.setUserInputEnabled(false);

        if (noOfTeams == 1) {
            nextButton.setText("Finish");
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0) {
                    finish();
                }

                choosePlayerSharedViewModel.updateObservablePotentialPlayers();

                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

                if (viewPager.getCurrentItem() < (noOfTeams - 1)) {
                    nextButton.setText("Next Team");
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!choosePlayerSharedViewModel.isValidTeam(getApplicationContext(), viewPager.getCurrentItem() + 1, gameRecord.getTeams(), false)) {
                    return;
                }

                choosePlayerSharedViewModel.updateObservablePotentialPlayers();

                if (viewPager.getCurrentItem() == (noOfTeams - 1)) {
                    if (choosePlayerSharedViewModel.areValidTeams(getApplicationContext(), gameRecord.getPlayModePlayed(), false)) {
                        startConfirmGameRecordActivity();
                    }
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }

                if (viewPager.getCurrentItem() == (noOfTeams - 1)) {
                    nextButton.setText("Finish");
                }
            }
        });
    }

    private void startConfirmGameRecordActivity() {
        Intent intent = new Intent(this, ConfirmGameRecordActivity.class);
        intent.putExtra("GAME_RECORD", gameRecord);
        intent.putParcelableArrayListExtra("TEAMS_OF_PLAYERS", (ArrayList) choosePlayerSharedViewModel.getTeamsOfPlayers());
        startActivityForResult(intent, CONFIRM_GAME_RECORD_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CONFIRM_GAME_RECORD_REQUEST_CODE) {
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY, (ArrayList) choosePlayerSharedViewModel.getTeamsOfPlayers());
            setResult(RESULT_OK, replyIntent);
            finish();
        }
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
