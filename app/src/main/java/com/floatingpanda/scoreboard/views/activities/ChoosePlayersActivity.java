package com.floatingpanda.scoreboard.views.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.floatingpanda.scoreboard.viewmodels.ChoosePlayerSharedViewModel;

import java.util.ArrayList;
import java.util.List;

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
                choosePlayerSharedViewModel.updateObservablePotentialPlayers();
                if (viewPager.getCurrentItem() == (noOfTeams - 1)) {
                    if (checkValidTeams()) {
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
        intent.putParcelableArrayListExtra("TEAMS_OF_PLAYERS", (ArrayList) choosePlayerSharedViewModel.getTeamsOfMembers());
        startActivityForResult(intent, CONFIRM_GAME_RECORD_REQUEST_CODE);
    }

    private boolean checkValidTeams() {
        List<TeamOfPlayers> teamsOfMembers = choosePlayerSharedViewModel.getTeamsOfMembers();

        if (teamsOfMembers.isEmpty()) {
            Toast.makeText(getApplicationContext(), "You need to have at least one team of members.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (gameRecord.getPlayModePlayed() == PlayMode.PlayModeEnum.SOLITAIRE) {
            if (teamsOfMembers.size() > 1) {
                Toast.makeText(getApplicationContext(), "A solitaire game cannot have more than one team.", Toast.LENGTH_SHORT).show();
                return false;
            }

            if(teamsOfMembers.get(0).getMembers().size() != 1) {
                Toast.makeText(getApplicationContext(), "A solitaire game can only have one player.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        for (TeamOfPlayers teamOfPlayers : teamsOfMembers) {
            if (teamOfPlayers.getMembers().size() < 1) {
                Toast.makeText(getApplicationContext(), "Each team needs to have at least one player.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CONFIRM_GAME_RECORD_REQUEST_CODE) {
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY, (ArrayList) choosePlayerSharedViewModel.getTeamsOfMembers());
            setResult(RESULT_OK, replyIntent);
            finish();
        }
    }
}
