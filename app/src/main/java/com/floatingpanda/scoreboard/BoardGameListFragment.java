package com.floatingpanda.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.adapters.BoardGameListAdapter;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGamesAndBgCategories;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;
import com.floatingpanda.scoreboard.viewmodels.BoardGameViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.app.Activity.RESULT_OK;

//TODO 1. Create a boardgame detail view
//TODO 2. Link boardgame detail view to this view.
//TODO 3. Create add boardgame functionality.
//TODO 4. Create edit boardgame functionality in boardgame detail view.
//TODO 5. Create delete boardgame functionality in boardgame detail view.

public class BoardGameListFragment extends Fragment implements DetailAdapterInterface {

    private final int ADD_BOARD_GAME_REQUEST_CODE = 1;

    private BoardGameViewModel boardGameViewModel;

    public BoardGameListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_test, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        final BoardGameListAdapter adapter = new BoardGameListAdapter(getActivity(), BoardGameListFragment.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        boardGameViewModel = new ViewModelProvider(this).get(BoardGameViewModel.class);

        boardGameViewModel.getAllBgsAndCategories().observe(getViewLifecycleOwner(), new Observer<List<BoardGamesAndBgCategories>>() {
            @Override
            public void onChanged(@Nullable final List<BoardGamesAndBgCategories> boardGamesAndBgCategories) {
                adapter.setBgsAndBgCategories(boardGamesAndBgCategories);
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

    // Postconditions: - The Board Game add activity is started.
    public void startAddActivity() {
        Intent addBoardGameIntent = new Intent(getContext(), BoardGameAddActivity.class);
        startActivityForResult(addBoardGameIntent, ADD_BOARD_GAME_REQUEST_CODE);
    }

    // Preconditions: - boardGame does not exist in the database.
    // Postconditions: - boardGame is added to the database.
    public void addBoardGame(BoardGame boardGame) {
        boardGameViewModel.addBoardGame(boardGame);
    }

    // Preconditions: - object is an object of the Member class.
    //                - the Member object exists in the database.
    // Postconditions: - The MemberActivity is started to view the details of object.
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
            BoardGame boardGame = (BoardGame) data.getExtras().get(MemberAddActivity.EXTRA_REPLY);
            addBoardGame(boardGame);
        }
    }
}
