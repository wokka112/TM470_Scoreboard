/*
ScoreBoard

Copyright © 2020 Adam Poole

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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

    //TODO maybe split into different validity tests. Then in view I can do a full validity test and call each individual test. If one is wrong I can create
    // an error to show the user. This will greatly reduce coupling.
    // e.g. isPlayerCountEmpty, isCompetitivePlayerCountCorrect, etc.
    // Although at that point is it better to simply include the validity test in the view?
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
