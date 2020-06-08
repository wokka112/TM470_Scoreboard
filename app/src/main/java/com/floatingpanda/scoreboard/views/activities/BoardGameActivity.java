package com.floatingpanda.scoreboard.views.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.viewmodels.BoardGameViewModel;

public class BoardGameActivity extends AppCompatActivity {
    private final int EDIT_BOARDGAME_REQUEST_CODE = 1;

    private BoardGameViewModel boardGameViewModel;

    private BoardGameWithBgCategoriesAndPlayModes bgWithBgCategoriesAndPlayModes;
    private BoardGame boardGame;

    private TextView nameTextView, difficultyTextView, playerCountTextView, categoriesTextView, playModesTextView, teamOptionsTextView,
            descriptionTextView, houseRulesTextView, notesTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_game);

        nameTextView = findViewById(R.id.bgact_name);
        difficultyTextView = findViewById(R.id.bgact_difficulty_output);
        playerCountTextView = findViewById(R.id.bgact_players_output);
        categoriesTextView = findViewById(R.id.bgact_categories_output);
        playModesTextView = findViewById(R.id.bgact_play_modes_output);
        teamOptionsTextView = findViewById(R.id.bgact_team_options_output);
        descriptionTextView = findViewById(R.id.bgact_description_output);
        houseRulesTextView = findViewById(R.id.bgact_house_rules_output);
        notesTextView = findViewById(R.id.bgact_notes_output);
        imageView = findViewById(R.id.bgact_image);

        Button editButton, deleteButton;
        //TODO replace this with the bgId and then simply find the boardgamewithcategoriesandplaymode via that??
        // Or maybe use the bg name?
        boardGame = (BoardGame) getIntent().getExtras().get("BOARDGAME");

        boardGameViewModel = new ViewModelProvider(this).get(BoardGameViewModel.class);

        boardGameViewModel.getBoardGameWithBgCategoriesAndPlayModes(boardGame).observe(BoardGameActivity.this, new Observer<BoardGameWithBgCategoriesAndPlayModes>() {
            @Override
            public void onChanged(@Nullable final BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes) {
                bgWithBgCategoriesAndPlayModes = boardGameWithBgCategoriesAndPlayModes;
                setViews(bgWithBgCategoriesAndPlayModes);
                //TODO remove observer at this point so it doesn't change again?
                //Get the live data list, then attach this observer to that list, then call remove observer at this point.
            }
        });

        editButton = findViewById(R.id.bgact_edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity(bgWithBgCategoriesAndPlayModes);
            }
        });

        deleteButton = findViewById(R.id.bgact_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDeleteActivity(bgWithBgCategoriesAndPlayModes);
            }
        });
    }

    /**
     * Helper method that sets up the views on the page - sets the text, checkboxes, radio buttons,
     * and chips up to represent the board game passed to the activity.
     * @param boardGameWithBgCategoriesAndPlayModes a board game, its categories and its playmodes
     */
    private void setViews(BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes) {
        // Catches if the object has been deleted to stop app from crashing before finish() is
        // called.
        if (boardGameWithBgCategoriesAndPlayModes == null) {
            return;
        }

        //TODO see if I can make this simpler. Maybe put these methods (getboardgame and getbgcategoriesstring(). in boardgamewithcategoriesandplaymodes.
        BoardGame boardGame = bgWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().getBoardGame();

        nameTextView.setText(boardGame.getBgName());
        difficultyTextView.setText(Integer.toString(boardGame.getDifficulty()));

        String players = boardGame.getMinPlayers() + " - " + boardGame.getMaxPlayers();
        playerCountTextView.setText(players);

        categoriesTextView.setText(bgWithBgCategoriesAndPlayModes.getBoardGameWithBgCategories().getBgCategoriesString());
        playModesTextView.setText(bgWithBgCategoriesAndPlayModes.getPlayModesString());
        teamOptionsTextView.setText(boardGame.getTeamOptionsString());
        descriptionTextView.setText(boardGame.getDescription());
        houseRulesTextView.setText(boardGame.getHouseRules());
        notesTextView.setText(boardGame.getNotes());
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
     * @param boardGameWithBgCategoriesAndPlayModes a BoardGame with bg categories and play modes that exists in the database
     */
    private void startDeleteActivity(BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes) {
        //TODO refactor this popup window into a method and find somewhere better to put it.
        AlertDialog.Builder builder = new AlertDialog.Builder(BoardGameActivity.this);
        builder.setTitle("Delete Board Game?")
                .setMessage("Are you sure you want to delete " + boardGameWithBgCategoriesAndPlayModes.getBoardGame().getBgName() +
                        "?\nThe board game will be removed from game records and will no longer appear as a best or worst " +
                        "board game for members.\nThis operation is irreversible.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBoardGameWithBgCategoriesAndPlayModes(boardGameWithBgCategoriesAndPlayModes);
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
     * @param boardGameWithBgCategoriesAndPlayModes a BoardGame with categories and play modes which exists in the db.
     */
    private void deleteBoardGameWithBgCategoriesAndPlayModes(BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes) {
        this.boardGameViewModel.deleteBoardGameWithBgCategoriesAndPlayModes(boardGameWithBgCategoriesAndPlayModes);
        finish();
    }

    /**
     * Starts the BoardGameEditActivity for boardGame.
     * @param boardGameWithBgCategoriesAndPlayModes a boardGame with bg categories and play modes
     */
    private void startEditActivity(BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes) {
        Intent editIntent = new Intent(BoardGameActivity.this, BoardGameEditActivity.class);
        editIntent.putExtra("BOARD_GAME_WITH_CATEGORIES_AND_PLAY_MODES", boardGameWithBgCategoriesAndPlayModes);
        startActivityForResult(editIntent, EDIT_BOARDGAME_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_BOARDGAME_REQUEST_CODE && resultCode == RESULT_OK) {
            BoardGameWithBgCategoriesAndPlayModes originalBoardGameWithBgCategoriesAndPlayModes =
                    (BoardGameWithBgCategoriesAndPlayModes) data.getExtras().get(BoardGameEditActivity.EXTRA_REPLY_ORIGINAL);

            BoardGameWithBgCategoriesAndPlayModes editedBoardGameWithBgCategoriesAndPlayModes =
                    (BoardGameWithBgCategoriesAndPlayModes) data.getExtras().get(BoardGameEditActivity.EXTRA_REPLY_EDITED);

            boardGameViewModel.editBoardGameWithBgCategoriesAndPlayModes(originalBoardGameWithBgCategoriesAndPlayModes, editedBoardGameWithBgCategoriesAndPlayModes);
        }
    }
}
