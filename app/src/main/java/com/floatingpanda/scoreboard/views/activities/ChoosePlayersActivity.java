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
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.viewmodels.ChoosePlayerSharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChoosePlayersActivity extends AppCompatActivity {

    private final int CONFIRM_PLAYERS_REQUEST_CODE = 1;
    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private Button backButton, nextButton;

    private ChoosePlayerSharedViewModel choosePlayerSharedViewModel;

    private Group group;
    private int numOfTeams;
    private boolean solitaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_players);

        backButton = findViewById(R.id.back_button);
        nextButton = findViewById(R.id.next_button);

        group = (Group) getIntent().getExtras().get("GROUP");
        numOfTeams = (int) getIntent().getExtras().get("NUM_OF_TEAMS");
        solitaire = (boolean) getIntent().getExtras().get("SOLITAIRE_BOOL");
        //TODO pass the map of selected players so if a person cancels at the last minute they can easily carry on with the teams as they were?
        // But then if they change lots, like from competitive 8-team to cooperative, then it may cause issues.

        choosePlayerSharedViewModel = new ViewModelProvider(this).get(ChoosePlayerSharedViewModel.class);
        choosePlayerSharedViewModel.initialisePotentialPlayers(group.getId());

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new ChoosePlayersPagerAdapter(this, numOfTeams));
        //Disables swiping so it can be controlled with buttons.
        viewPager.setUserInputEnabled(false);

        if (numOfTeams == 1) {
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

                if (viewPager.getCurrentItem() < (numOfTeams - 1)) {
                    nextButton.setText("Next Team");
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePlayerSharedViewModel.updateObservablePotentialPlayers();
                if (viewPager.getCurrentItem() == (numOfTeams - 1)) {
                    //TODO add in code to start new activity for result asking if the settings are okay.
                    // If they are, then return to original activity and create new record.
                    // If they are not, then return to this activity and allow changes to be made.

                    List<TeamOfPlayers> teamsOfMembers = choosePlayerSharedViewModel.getTeamsOfMembers();

                    if (teamsOfMembers.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "You need to have at least one team of members.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (solitaire) {
                        if (teamsOfMembers.size() > 1) {
                            Toast.makeText(getApplicationContext(), "A solitaire game cannot have more than one team.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(teamsOfMembers.get(0).getMembers().size() != 1) {
                            Toast.makeText(getApplicationContext(), "A solitaire game can only have one player.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    for (TeamOfPlayers teamOfPlayers : teamsOfMembers) {
                        if (teamOfPlayers.getMembers().size() < 1) {
                            Toast.makeText(getApplicationContext(), "Each team needs to have at least one player.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    startConfirmPlayersActivity();
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }

                if (viewPager.getCurrentItem() == (numOfTeams - 1)) {
                    nextButton.setText("Finish");
                }
            }
        });
    }

    public void startConfirmPlayersActivity() {
        Intent intent = new Intent(this, ConfirmPlayersActivity.class);
        intent.putParcelableArrayListExtra("TEAMS_OF_MEMBERS", (ArrayList) choosePlayerSharedViewModel.getTeamsOfMembers());
        startActivityForResult(intent, CONFIRM_PLAYERS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CONFIRM_PLAYERS_REQUEST_CODE) {
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY, (ArrayList) choosePlayerSharedViewModel.getTeamsOfMembers());
            setResult(RESULT_OK, replyIntent);
            finish();
        }
    }
}
