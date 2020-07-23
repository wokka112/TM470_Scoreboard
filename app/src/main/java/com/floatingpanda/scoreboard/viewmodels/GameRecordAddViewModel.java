package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.repositories.BoardGameRepository;
import com.floatingpanda.scoreboard.repositories.MemberRepository;
import com.floatingpanda.scoreboard.data.entities.Member;

import java.util.List;

public class GameRecordAddViewModel extends AndroidViewModel {

    private BoardGameRepository boardGameRepository;

    public GameRecordAddViewModel(Application application) {
        super(application);
        boardGameRepository = new BoardGameRepository(application);
    }

    // Used for testing.
    public GameRecordAddViewModel(Application application, AppDatabase db) {
        super(application);
        boardGameRepository = new BoardGameRepository(db);
    }

    public LiveData<List<BoardGameWithBgCategoriesAndPlayModes>> getAllBoardGamesWithBgCategoriesAndPlayModes() {
        return boardGameRepository.getAllBoardGamesWithBgCategoriesAndPlayModes();
    }

    public boolean inputsValid(Context applicationContext, EditText playerCountEditText, PlayMode.PlayModeEnum playModePlayed, boolean winLoseRadioGroupChecked,
                               boolean testing) {
        if (playerCountEditText.getText().toString().trim().isEmpty()) {
            if (!testing) {
                playerCountEditText.setError("You must set the number of players or teams that played.");
                playerCountEditText.requestFocus();
            }
            return false;
        }

        int playerCount = Integer.parseInt(playerCountEditText.getText().toString());

        if (playModePlayed == PlayMode.PlayModeEnum.COMPETITIVE) {
            if (playerCount < 2) {
                if (!testing) {
                    playerCountEditText.setError("You must set the number of players or teams to 2 or more when playing competitively.");
                    playerCountEditText.requestFocus();
                }
                return false;
            }
        }

        if (playModePlayed == PlayMode.PlayModeEnum.COOPERATIVE
                || playModePlayed == PlayMode.PlayModeEnum.SOLITAIRE) {

            if (playerCount != 1) {
                if (!testing) {
                    playerCountEditText.setError("You must set the number of players or teams to 1. Cooperative and solitaire games can " +
                            "only have 1 team or player, respectively.");
                }
                return false;
            }

            if (!winLoseRadioGroupChecked) {
                if (!testing) {
                    Toast.makeText(applicationContext, "You must set whether you won or lost the game.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        }

        return true;
    }
}
