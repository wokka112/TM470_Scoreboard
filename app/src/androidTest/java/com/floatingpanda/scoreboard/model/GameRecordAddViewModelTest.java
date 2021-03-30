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

package com.floatingpanda.scoreboard.model;

import android.content.Context;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.daos.AssignedCategoryDao;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.daos.PlayModeDao;
import com.floatingpanda.scoreboard.data.daos.PlayerDao;
import com.floatingpanda.scoreboard.data.daos.PlayerSkillRatingChangeDao;
import com.floatingpanda.scoreboard.data.daos.PlayerTeamDao;
import com.floatingpanda.scoreboard.data.entities.AssignedCategory;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.repositories.BoardGameRepository;
import com.floatingpanda.scoreboard.repositories.GameRecordRepository;
import com.floatingpanda.scoreboard.viewmodels.GameRecordAddViewModel;
import com.floatingpanda.scoreboard.viewmodels.GameRecordViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class GameRecordAddViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private BoardGameDao boardGameDao;
    private BgCategoryDao bgCategoryDao;
    private AssignedCategoryDao assignedCategoryDao;
    private PlayModeDao playModeDao;

    private BoardGameRepository boardGameRepository;
    private GameRecordAddViewModel gameRecordAddViewModel;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        boardGameDao = db.boardGameDao();
        bgCategoryDao = db.bgCategoryDao();
        assignedCategoryDao = db.assignedCategoryDao();
        playModeDao = db.playModeDao();

        boardGameRepository = new BoardGameRepository(db);
        gameRecordAddViewModel = new GameRecordAddViewModel(ApplicationProvider.getApplicationContext(), db);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllBoardGamesWithBgCategoriesAndPlayModes() throws InterruptedException {
        List<BoardGameWithBgCategoriesAndPlayModes> boardGamesWithBgCategoriesAndPlayModes = LiveDataTestUtil.getValue(gameRecordAddViewModel.getAllBoardGamesWithBgCategoriesAndPlayModes());
        assertTrue(boardGamesWithBgCategoriesAndPlayModes.isEmpty());

        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        assignedCategoryDao.insertAll(TestData.ASSIGNED_CATEGORIES.toArray(new AssignedCategory[TestData.ASSIGNED_CATEGORIES.size()]));
        playModeDao.insertAll(TestData.PLAY_MODES.toArray(new PlayMode[TestData.PLAY_MODES.size()]));

        boardGamesWithBgCategoriesAndPlayModes = LiveDataTestUtil.getValue(gameRecordAddViewModel.getAllBoardGamesWithBgCategoriesAndPlayModes());
        assertThat(boardGamesWithBgCategoriesAndPlayModes.size(), is(TestData.BOARD_GAMES.size()));
    }

    @Test
    public void testInputsValid() {
        Context context = ApplicationProvider.getApplicationContext();

        String playerCountString = " ";
        EditText playerCountEditText = new EditText(context);
        playerCountEditText.setText(playerCountString);

        PlayMode.PlayModeEnum playModePlayed = PlayMode.PlayModeEnum.COMPETITIVE;
        boolean winLoseRadioGroupChecked = true;
        RadioButton rightMostRadioButton = new RadioButton(context);

        //Test case 1 - Invalid, player count is empty
        boolean isValid = gameRecordAddViewModel.inputsValid(context, playerCountEditText, playModePlayed, winLoseRadioGroupChecked, true);
        assertFalse(isValid);

        //Test case 2 - Invalid, competitive playmode is selected but playercount is less than 2.
        playerCountString = "1";
        playerCountEditText.setText(playerCountString);

        isValid = gameRecordAddViewModel.inputsValid(context, playerCountEditText, playModePlayed, winLoseRadioGroupChecked, true);
        assertFalse(isValid);

        //Test case 3 - Invalid, cooperative playmode is selected but playercount is less than 1.
        playModePlayed = PlayMode.PlayModeEnum.COOPERATIVE;
        playerCountString = "0";
        playerCountEditText.setText(playerCountString);

        isValid = gameRecordAddViewModel.inputsValid(context, playerCountEditText, playModePlayed, winLoseRadioGroupChecked, true);
        assertFalse(isValid);

        //Test case 4 - Invalid, cooperative playmode is selected but playercount is greater than 1.
        playerCountString = "2";
        playerCountEditText.setText(playerCountString);

        isValid = gameRecordAddViewModel.inputsValid(context, playerCountEditText, playModePlayed, winLoseRadioGroupChecked, true);
        assertFalse(isValid);

        //Test case 5 - Invalid, solitaire playmode is selected but playercount is less than 1.
        playModePlayed = PlayMode.PlayModeEnum.SOLITAIRE;
        playerCountString = "0";
        playerCountEditText.setText(playerCountString);

        isValid = gameRecordAddViewModel.inputsValid(context, playerCountEditText, playModePlayed, winLoseRadioGroupChecked, true);
        assertFalse(isValid);

        //Test case 6 - Invalid, solitaire playmode is selected but playercount is greater than 1.
        playerCountString = "2";
        playerCountEditText.setText(playerCountString);

        isValid = gameRecordAddViewModel.inputsValid(context, playerCountEditText, playModePlayed, winLoseRadioGroupChecked, true);
        assertFalse(isValid);

        //Test case 7 - Invalid, cooperative playmode is selected but neither win nor lose is selected.
        playModePlayed = PlayMode.PlayModeEnum.COOPERATIVE;
        playerCountString = "1";
        playerCountEditText.setText(playerCountString);
        winLoseRadioGroupChecked = false;

        isValid = gameRecordAddViewModel.inputsValid(context, playerCountEditText, playModePlayed, winLoseRadioGroupChecked, true);
        assertFalse(isValid);

        //Test case 8 - Invalid, solitaire playmode is selected but neither win nor lose is selected.
        playModePlayed = PlayMode.PlayModeEnum.SOLITAIRE;

        isValid = gameRecordAddViewModel.inputsValid(context, playerCountEditText, playModePlayed, winLoseRadioGroupChecked, true);
        assertFalse(isValid);

        //Test case 9 - Valid, competitive playmode is selected and playercount is 2.
        playModePlayed = PlayMode.PlayModeEnum.COMPETITIVE;
        playerCountString = "2";
        playerCountEditText.setText(playerCountString);

        isValid = gameRecordAddViewModel.inputsValid(context, playerCountEditText, playModePlayed, winLoseRadioGroupChecked, true);
        assertTrue(isValid);

        //Test case 10 - Valid, competitive playmode is selected and playercount is greater than 2.
        playerCountString = "3";
        playerCountEditText.setText(playerCountString);

        isValid = gameRecordAddViewModel.inputsValid(context, playerCountEditText, playModePlayed, winLoseRadioGroupChecked, true);
        assertTrue(isValid);

        //Test case 11 - Valid, competitive playmode is selected, playercount is greater than 2, and won or lost is checked.
        winLoseRadioGroupChecked = true;

        isValid = gameRecordAddViewModel.inputsValid(context, playerCountEditText, playModePlayed, winLoseRadioGroupChecked, true);
        assertTrue(isValid);

        //Test case 12 - Valid, cooperative playmode is selected, playercount is 1 and won or lost is selected.
        playModePlayed = PlayMode.PlayModeEnum.COOPERATIVE;
        playerCountString = "1";
        playerCountEditText.setText(playerCountString);

        isValid = gameRecordAddViewModel.inputsValid(context, playerCountEditText, playModePlayed, winLoseRadioGroupChecked, true);
        assertTrue(isValid);

        //Test case 13 - Valid, solitaire playmode is selected, playercount is 1 and won or lost is selected.
        playModePlayed = PlayMode.PlayModeEnum.SOLITAIRE;

        isValid = gameRecordAddViewModel.inputsValid(context, playerCountEditText, playModePlayed, winLoseRadioGroupChecked, true);
        assertTrue(isValid);
    }
}
