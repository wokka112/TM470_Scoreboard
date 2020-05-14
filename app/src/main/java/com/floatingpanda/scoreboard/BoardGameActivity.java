package com.floatingpanda.scoreboard;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGamesAndBgCategories;
import com.floatingpanda.scoreboard.viewmodels.BoardGameViewModel;

import java.util.List;

public class BoardGameActivity extends AppCompatActivity {

    private BoardGameViewModel boardGameViewModel;

    private BoardGame boardGame;

    private TextView name, difficultyOutput, playersOutput, categoriesOutput, playModesOutput, teamsOutput,
            descriptionOutput, houseRulesOutput, notesOutput;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_game);

        name = findViewById(R.id.bgact_name);
        difficultyOutput = findViewById(R.id.bgact_difficulty_output);
        playersOutput = findViewById(R.id.bgact_players_output);
        categoriesOutput = findViewById(R.id.bgact_categories_output);
        playModesOutput = findViewById(R.id.bgact_play_modes_output);
        teamsOutput = findViewById(R.id.bgact_team_options_output);
        descriptionOutput = findViewById(R.id.bgact_description_output);
        houseRulesOutput = findViewById(R.id.bgact_house_rules_output);
        notesOutput = findViewById(R.id.bgact_notes_output);
        imageView = findViewById(R.id.bgact_image);

        Button editButton, deleteButton;

        boardGame = (BoardGame) getIntent().getExtras().get("BOARDGAME");

        boardGameViewModel = new ViewModelProvider(this).get(BoardGameViewModel.class);

        Log.w("BoardGameActivity.java", "Using bg: " + boardGame.getBgName());
        boardGameViewModel.getLiveDataBoardGameAndCategories(boardGame).observe(BoardGameActivity.this, new Observer<BoardGamesAndBgCategories>() {
            @Override
            public void onChanged(@Nullable final BoardGamesAndBgCategories bgAndBgCategories) {
                Log.w("MemberActivity.java", "Got liveMember: " + bgAndBgCategories);
                setViews(bgAndBgCategories);
            }
        });

        /*
        Log.w("BoardGameActivity.java", "Using bg: " + boardGame.getBgName());
        boardGameViewModel.getLiveDataBoardGameAndCategories(boardGame).observe(BoardGameActivity.this, new Observer<BoardGamesAndBgCategories>() {
            @Override
            public void onChanged(@Nullable final BoardGamesAndBgCategories bgAndBgCategories) {
                Log.w("MemberActivity.java", "Got liveMember: " + bgAndBgCategories);
                setViews(bgAndBgCategories);
            }
        });

         */

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

    private void setViews(BoardGamesAndBgCategories bgAndBgCategories) {
        boardGame = bgAndBgCategories.getBoardGame();

        name.setText(boardGame.getBgName());
        difficultyOutput.setText(Integer.toString(boardGame.getDifficulty()));

        String players = boardGame.getMinPlayers() + " - " + boardGame.getMaxPlayers();
        playersOutput.setText(players);

        String categories = createCategoriesString(boardGame.getBgCategories());
        categoriesOutput.setText(categories);

        playModesOutput.setText(boardGame.getPlayModesString());
        teamsOutput.setText(boardGame.getTeamOptionsString());
        descriptionOutput.setText(boardGame.getDescription());
        houseRulesOutput.setText(boardGame.getHouseRules());
        notesOutput.setText(boardGame.getNotes());
    }

    //TODO replace this with a better method for formatting categories.
    // Maybe put a "categoriesListToString" method in Boardgame or something that would return a
    // formatted string.
    // Also work out what I'm going to do with categories. Do I show them all? Do I show only 2 of
    // them and then put an "expand" text at the end that you can tap to show the rest?
    private String createCategoriesString(List<BgCategory> categoriesList) {
        if (categoriesList == null || categoriesList.isEmpty()) {
            return "None yet";
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;

        while (i < 2 && i < categoriesList.size()) {
            sb.append(categoriesList.get(i).getCategoryName());
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
