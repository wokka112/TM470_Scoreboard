package com.floatingpanda.scoreboard.views.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.AddGameRecordPlayersAdapter;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;

public class AddGameRecordActivity extends AppCompatActivity {

    private Button setUpButton;
    private RadioGroup teamsRadioGroup;
    private EditText playerCountEditText;
    private SearchableSpinner boardGameSpinner;
    private RecyclerView playerWrapperRecyclerView;
    int playerCount;
    List<Member> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game_record);

        setUpButton = findViewById(R.id.add_game_record_set_up_teams_button);
        teamsRadioGroup = findViewById(R.id.add_game_record_teams_setting_radio_group);
        playerCountEditText = findViewById(R.id.add_game_record_player_count_edittext);
        boardGameSpinner = findViewById(R.id.add_game_record_bg_spinner);
        playerWrapperRecyclerView = findViewById(R.id.add_game_record_players_recyclerview_wrapper);

        players = new ArrayList<>();

        LayoutInflater inflater = getLayoutInflater();

        final AddGameRecordPlayersAdapter adapter = new AddGameRecordPlayersAdapter(this);
        playerWrapperRecyclerView.setAdapter(adapter);
        playerWrapperRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldPlayerCount = players.size();
                int newPlayerCount = Integer.parseInt(playerCountEditText.getText().toString());

                if (oldPlayerCount > newPlayerCount) {
                    for (int i = oldPlayerCount - 1; i >= newPlayerCount; i--) {
                        players.remove(i);
                    }
                }

                for (int i = oldPlayerCount; i < newPlayerCount; i++) {
                    players.add(new Member("Test", "Test", "Test"));
                }

                adapter.setPlayers(players);

                /*
                int newPlayerCount = Integer.parseInt(playerCountEditText.getText().toString());

                List<Integer> positions = new ArrayList<>();
                for (int i = 1; i <= newPlayerCount; i++) {
                    positions.add(i);
                }

                ArrayAdapter<Integer> positionsAdapter = new ArrayAdapter<Integer>(getApplicationContext(), android.R.layout.simple_spinner_item, positions);

                if (newPlayerCount > playerCount) {
                    for (int i = newPlayerCount; newPlayerCount > playerCount; newPlayerCount--) {
                        playerWrapper.removeViewAt(i);
                    }
                } else {
                    for (int i = playerCount; playerCount < newPlayerCount; playerCount++) {
                        View newView = inflater.inflate(R.layout.activity_add_game_record_player_item, null);

                        Spinner positionSpinner = newView.findViewById(R.id.player_item_position_spinner);
                        positionSpinner.setAdapter(positionsAdapter);

                        playerWrapper.addView(newView);
                    }
                }

                playerCount = newPlayerCount;
                */
            }
        });
    }
}
