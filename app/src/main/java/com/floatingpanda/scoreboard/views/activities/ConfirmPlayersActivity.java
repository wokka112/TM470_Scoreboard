package com.floatingpanda.scoreboard.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.adapters.ConfirmPlayersListAdapter;

import java.util.Collections;
import java.util.List;

public class ConfirmPlayersActivity extends AppCompatActivity {

    private Button yesButton, noButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_players);

        yesButton = findViewById(R.id.activity_confirm_players_yes_button);
        noButton = findViewById(R.id.activity_confirm_players_no_button);

        RecyclerView recyclerView = findViewById(R.id.activity_confirm_players_recyclerview);

        final ConfirmPlayersListAdapter adapter = new ConfirmPlayersListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<TeamOfPlayers> teamsOfMembers = getIntent().getExtras().getParcelableArrayList("TEAMS_OF_MEMBERS");
        adapter.setPlayerTeams(teamsOfMembers);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
