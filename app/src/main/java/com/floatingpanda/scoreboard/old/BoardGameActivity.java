package com.floatingpanda.scoreboard.old;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.old.system.BoardGame;

import java.util.List;

public class BoardGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_game);

        BoardGame boardGame = (BoardGame) getIntent().getExtras().get("BOARD_GAME");

        TextView name, difficultyOutput, playersOutput, categoriesOutput, gameTypesOutput, teamsOutput,
                descriptionOutput, houseRulesOutput, notesOutput;

        Button editButton, deleteButton;

        ImageView imageView;

        name = findViewById(R.id.bgact_name);
        name.setText(boardGame.getName());

        difficultyOutput = findViewById(R.id.bgact_difficulty_output);
        difficultyOutput.setText(Integer.toString(boardGame.getDifficulty()));

        playersOutput = findViewById(R.id.bgact_players_output);
        String players = boardGame.getMinPlayers() + " - " + boardGame.getMaxPlayers();
        playersOutput.setText(players);

        categoriesOutput = findViewById(R.id.bgact_categories_output);
        String categories = createCategoriesString(boardGame.getCategories());
        categoriesOutput.setText(categories);

        gameTypesOutput = findViewById(R.id.bgact_game_types_output);
        gameTypesOutput.setText("Competitive, Cooperative Losable, Solitaire Losable");

        teamsOutput = findViewById(R.id.bgact_teams_output);
        teamsOutput.setText("Solo and Teams");

        descriptionOutput = findViewById(R.id.bgact_description_output);
        descriptionOutput.setText(boardGame.getDescription());

        houseRulesOutput = findViewById(R.id.bgact_house_rules_output);
        houseRulesOutput.setText(boardGame.getHouseRules());

        notesOutput = findViewById(R.id.bgact_notes_output);
        notesOutput.setText(boardGame.getNotes());

        imageView = findViewById(R.id.bgact_image);
        imageView.setImageResource(boardGame.getImageResourceId());

        editButton = findViewById(R.id.bgact_edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BoardGameActivity.this, "Edit pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton = findViewById(R.id.bgact_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BoardGameActivity.this, "Delete pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //TODO replace this with a better method for formatting categories.
    private String createCategoriesString(List<String> categoriesList) {
        if (categoriesList == null || categoriesList.isEmpty()) {
            return "None yet...";
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;

        while (i < 2 && i < categoriesList.size()) {
            sb.append(categoriesList.get(i));
            sb.append(", ");
            i++;
        }

        if (categoriesList.size() > 2) {
            int categoriesLeft = categoriesList.size() - 2;
            sb.append(" and " + categoriesLeft + " more");
        }

        return sb.toString();
    }

    //TODO create methods for formatting game types and teams lists.
}
