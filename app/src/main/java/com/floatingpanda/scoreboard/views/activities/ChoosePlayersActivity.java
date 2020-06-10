package com.floatingpanda.scoreboard.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.ChoosePlayersPagerAdapter;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.viewmodels.ChoosePlayerSharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChoosePlayersActivity extends AppCompatActivity {

    private Button backButton, nextButton;
    private Spinner spinner;
    private TextView teamTextView;

    private ChoosePlayerSharedViewModel choosePlayerSharedViewModel;

    private Group group;
    private int numOfTeams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_layout);

        backButton = findViewById(R.id.back_button);
        nextButton = findViewById(R.id.next_button);
        spinner = findViewById(R.id.viewpager_layout_position_spinner);
        teamTextView = findViewById(R.id.viewpager_layout_team_textview);

        group = (Group) getIntent().getExtras().get("GROUP");
        numOfTeams = (int) getIntent().getExtras().get("NUM_OF_TEAMS");

        choosePlayerSharedViewModel = new ViewModelProvider(this).get(ChoosePlayerSharedViewModel.class);
        choosePlayerSharedViewModel.initialisePotentialPlayers(group.getId());

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new ChoosePlayersPagerAdapter(this, numOfTeams));
        //Disables swiping so it can be controlled with buttons.
        viewPager.setUserInputEnabled(false);

        //TODO style the area where the teamTextView is so it's a different colour to the viewpager of players.
        teamTextView.setText("Team " + (viewPager.getCurrentItem() + 1));

        spinner.setAdapter(createPositionSpinnerAdapter());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int teamPosition = (int) parent.getItemAtPosition(position);
                Log.w("ChoosePlayersAct.java", "Got position: " + teamPosition);
                //TODO set the position of the team with this.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //TODO add in a tab at the top with the current team number and a spinner to select the team's position.

        //TODO maybe make a special class for holding the members of a team.
        // Class can hold a list of the members, an int for the team number, and an int for the position.
        // Map can hold a list of these classes.

        //TODO add in ability to choose cooperative, competitive or solitaire.
        // If coop or solitaire, also choose whether win or loss.
        // Also need to change to a specialised version of fragments
        // - coop uses a single team.
        // - solitaire uses a single team and limits to selecting 1 player.

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0) {
                    finish();
                }

                choosePlayerSharedViewModel.updateObservablePotentialPlayers();

                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                teamTextView.setText("Team " + (viewPager.getCurrentItem() + 1));

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

                    //Go to new activity showing selected teams, players in them, and their positions.
                    //Provide 2 buttons - create, cancel.
                    //Create returns here with the players to create the game.
                    //Cancel returns to the fragment to go through and change team players.
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    teamTextView.setText("Team " + (viewPager.getCurrentItem() + 1));
                }

                if (viewPager.getCurrentItem() == (numOfTeams - 1)) {
                    nextButton.setText("Finish");
                }
            }
        });
    }

    private ArrayAdapter<Integer> createPositionSpinnerAdapter() {
        List<Integer> positionList = new ArrayList<Integer>();

        for (int i = 1; i <= numOfTeams; i++) {
            positionList.add(i);
        }

        ArrayAdapter<Integer> positionsAdapter = new ArrayAdapter<Integer>(getApplicationContext(), android.R.layout.simple_spinner_item, positionList);
        return positionsAdapter;
    }
}
