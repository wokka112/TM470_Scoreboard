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

package com.floatingpanda.scoreboard.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.views.activities.BoardGameActivity;
import com.floatingpanda.scoreboard.views.activities.BoardGameAddActivity;
import com.floatingpanda.scoreboard.views.activities.MemberAddActivity;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.BoardGameListAdapter;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;
import com.floatingpanda.scoreboard.viewmodels.BoardGameViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * The view fragment showing the list of board games in the database, and providing the means to add,
 * edit or delete board games in the database.
 */
public class BoardGameListFragment extends Fragment implements DetailAdapterInterface {

    private final int ADD_BOARD_GAME_REQUEST_CODE = 1;

    private BoardGameViewModel boardGameViewModel;
    private TextView noBoardGamesTextView;

    public BoardGameListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_with_fab, container, false);

        noBoardGamesTextView = rootView.findViewById(R.id.no_element_exists_textview);
        noBoardGamesTextView.setText(R.string.no_board_games);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        final BoardGameListAdapter adapter = new BoardGameListAdapter(getActivity(), BoardGameListFragment.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        boardGameViewModel = new ViewModelProvider(this).get(BoardGameViewModel.class);

        boardGameViewModel.getAllBoardGamesWithBgCategories().observe(getViewLifecycleOwner(), new Observer<List<BoardGameWithBgCategories>>() {
            @Override
            public void onChanged(@Nullable final List<BoardGameWithBgCategories> boardGameWithBgCategories) {

                if (boardGameWithBgCategories == null
                        || boardGameWithBgCategories.isEmpty()) {
                    noBoardGamesTextView.setVisibility(View.VISIBLE);
                } else {
                    noBoardGamesTextView.setVisibility(View.GONE);
                }

                adapter.setBoardGamesWithBgCategories(boardGameWithBgCategories);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddActivity();
            }
        });

        return rootView;
    }

    /**
     * Starts the board game add activity.
     */
    public void startAddActivity() {
        Intent addBoardGameIntent = new Intent(getContext(), BoardGameAddActivity.class);
        startActivityForResult(addBoardGameIntent, ADD_BOARD_GAME_REQUEST_CODE);
    }

    /**
     * Starts the BoardGameActivity to view the board game passed as object in more details.
     *
     * object should be an object of the BoardGame class, and the board game should already exist in
     * the database.
     *
     * Part of the DetailAdapterInterface.
     * @param object a BoardGame object
     */
    @Override
    public void viewDetails(Object object) {
        BoardGame boardGame = (BoardGame) object;

        Intent detailsIntent = new Intent(getContext(), BoardGameActivity.class);
        detailsIntent.putExtra("BOARDGAME", boardGame);
        startActivity(detailsIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_BOARD_GAME_REQUEST_CODE && resultCode == RESULT_OK) {
            BoardGameWithBgCategoriesAndPlayModes bgWithBgCategoriesAndPlayModes =
                    (BoardGameWithBgCategoriesAndPlayModes) data.getExtras().get(MemberAddActivity.EXTRA_REPLY);
            boardGameViewModel.addBoardGameWithBgCategoriesAndPlayModes(bgWithBgCategoriesAndPlayModes);
        }
    }
}
