package com.floatingpanda.scoreboard.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.AddGameRecordPlayersAdapter;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.viewmodels.GameRecordAddViewModel;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

//TODO Add in competitive, cooperative or solitaire choice.
//TODO Add in functionality so teams or no teams choice affects things.
public class AddGameRecordActivity extends AppCompatActivity {

    private Button addPlayersButton;
    private RadioGroup teamsRadioGroup;
    private EditText playerCountEditText;
    private SearchableSpinner boardGameSpinner;
    private RecyclerView playerWrapperRecyclerView;

    private Group group;
    List<PlayerTeam> playerTeams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game_record);

        addPlayersButton = findViewById(R.id.add_game_record_add_players_button);
        teamsRadioGroup = findViewById(R.id.add_game_record_teams_setting_radio_group);
        playerCountEditText = findViewById(R.id.add_game_record_player_count_edittext);
        boardGameSpinner = findViewById(R.id.add_game_record_bg_spinner);

        //Populate board game spinner
        //Get all details
        //Hit next and then setup fragments, one for each team.

        group = (Group) getIntent().getExtras().get("GROUP");

        addPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inputsValid()) {
                    return;
                }

                startChoosePlayersActivity();
            }
        });
    }

    private void startChoosePlayersActivity() {
        int numOfTeams = Integer.parseInt(playerCountEditText.getText().toString());

        Intent intent = new Intent(this, ChoosePlayersActivity.class);
        intent.putExtra("GROUP", group);
        intent.putExtra("NUM_OF_TEAMS", numOfTeams);
        startActivity(intent);
    }

    public boolean inputsValid() {
        if (playerCountEditText.getText().toString().isEmpty()) {
            Log.w("AddGameRecordAct.java", "Player count is empty.");
            return false;
        }

        return true;
    }
}
