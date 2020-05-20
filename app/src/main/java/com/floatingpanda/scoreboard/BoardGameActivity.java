package com.floatingpanda.scoreboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.data.BgAndBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGamesAndBgCategories;
import com.floatingpanda.scoreboard.data.PlayMode;
import com.floatingpanda.scoreboard.viewmodels.BoardGameViewModel;

import java.util.List;

public class BoardGameActivity extends AppCompatActivity {
    private final int EDIT_BOARDGAME_REQUEST_CODE = 1;

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
        boardGameViewModel.getLiveDataBoardGameAndCategoriesAndPlayModes(boardGame).observe(BoardGameActivity.this, new Observer<BgAndBgCategoriesAndPlayModes>() {
            @Override
            public void onChanged(@Nullable final BgAndBgCategoriesAndPlayModes bgAndBgCategoriesAndPlayModes) {
                setViews(bgAndBgCategoriesAndPlayModes);
            }
        });

        editButton = findViewById(R.id.bgact_edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity(boardGame);
            }
        });

        deleteButton = findViewById(R.id.bgact_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDeleteActivity(boardGame);
            }
        });
    }

    /**
     * Helper method that sets up the views on the page - sets the text, checkboxes, radio buttons,
     * and chips up to represent the board game passed to the activity.
     * @param bgAndBgCategoriesAndPlayModes a board game, its categories and its playmodes
     */
    private void setViews(BgAndBgCategoriesAndPlayModes bgAndBgCategoriesAndPlayModes) {
        // Catches if the object has been deleted to stop app from crashing before finish() is
        // called.
        if (bgAndBgCategoriesAndPlayModes == null) {
            return;
        }

        boardGame = bgAndBgCategoriesAndPlayModes.getBoardGame();

        name.setText(boardGame.getBgName());
        difficultyOutput.setText(Integer.toString(boardGame.getDifficulty()));

        String players = boardGame.getMinPlayers() + " - " + boardGame.getMaxPlayers();
        playersOutput.setText(players);

        categoriesOutput.setText(boardGame.getBgCategoriesString());
        playModesOutput.setText(boardGame.getPlayModesString());
        teamsOutput.setText(boardGame.getTeamOptionsString());
        descriptionOutput.setText(boardGame.getDescription());
        houseRulesOutput.setText(boardGame.getHouseRules());
        notesOutput.setText(boardGame.getNotes());
    }

    // Preconditions: boardGame exists in database.
    // Postconditions: boardGame is removed from database.
    /**
     * Displays a popup informing the user of what deleting a Board Game results in and warning them
     * that it is irreversible. If the user presses the "Delete" button on the popup, then boardGame
     * will be deleted from the database. If the user presses the "Cancel" button, then the popup
     * will be dismissed and nothing will happen.
     *
     * boardGame should exist in the database.
     * @param boardGame a BoardGame that exists in the database
     */
    private void startDeleteActivity(BoardGame boardGame) {
        //TODO refactor this popup window into a method and find somewhere better to put it.
        AlertDialog.Builder builder = new AlertDialog.Builder(BoardGameActivity.this);
        builder.setTitle("Delete Board Game?")
                .setMessage("Are you sure you want to delete " + boardGame.getBgName() + "?\n" +
                        "The member will be removed from winner lists, groups and game records, and " +
                        "their skill ratings will be deleted.\n" +
                        "This operation is irreversible.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBoardGame(boardGame);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .create()
                .show();
    }

    // Preconditions: boardGame should exist in database.
    // Postconditions: boardGame will not longer exist in database.
    /**
     * Deletes boardGame from database and finishes activity.
     * @param boardGame a BoardGame that exists in the database
     */
    private void deleteBoardGame(BoardGame boardGame) {
        this.boardGameViewModel.deleteBoardGame(boardGame);
        finish();
    }

    /**
     * Starts the BoardGameEditActivity for boardGame.
     * @param boardGame a boardGame
     */
    private void startEditActivity(BoardGame boardGame) {
        Intent editIntent = new Intent(BoardGameActivity.this, BoardGameEditActivity.class);
        editIntent.putExtra("BOARDGAME", boardGame);
        startActivityForResult(editIntent, EDIT_BOARDGAME_REQUEST_CODE);
    }

    //TODO create editBoardGame method and onactivityresult method.

    private void editBoardGame(BoardGame originalBoardGame, BoardGame editedBoardGame) {
        boardGameViewModel.editBoardGame(originalBoardGame, editedBoardGame);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_BOARDGAME_REQUEST_CODE && resultCode == RESULT_OK) {
            BoardGame originalBoardGame = (BoardGame) data.getExtras().get(BoardGameEditActivity.EXTRA_REPLY_ORIGINAL_BG);
            BoardGame editedBoardGame = (BoardGame) data.getExtras().get(BoardGameEditActivity.EXTRA_REPLY_EDITED_BG);
            Log.w("BoardGameAct.java","Original Bg: " + originalBoardGame.getId() + ", " + originalBoardGame.getBgName());
            Log.w("BoardGameAct.java","Edited Bg: " + editedBoardGame.getId() + ", " + editedBoardGame.getBgName());
            editBoardGame(originalBoardGame, editedBoardGame);
        }
    }
}
