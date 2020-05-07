package com.floatingpanda.scoreboard.old.mainactivityfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.floatingpanda.scoreboard.old.BoardGameActivity;
import com.floatingpanda.scoreboard.old.mainactivityadapters.BoardGameAdapter;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.old.system.BoardGame;

import java.util.ArrayList;
import java.util.List;

public class BoardGameListFragment extends Fragment {

    public BoardGameListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_list, container, false);

        final ArrayList<BoardGame> boardGames = new ArrayList<>();

        List<String> categoriesListSingle = new ArrayList<>();
        categoriesListSingle.add("Strategy");

        List<String> categoriesListDouble = new ArrayList<>();
        categoriesListDouble.add("Bluffing");
        categoriesListDouble.add("Party");

        List<String> categoriesListTriple = new ArrayList<>();
        categoriesListTriple.add("Strategy");
        categoriesListTriple.add("Bluffing");
        categoriesListTriple.add("Social Deduction");

        List<String> categoriesListFive = new ArrayList<>();
        categoriesListFive.add("Social Deduction");
        categoriesListFive.add("Party");
        categoriesListFive.add("Strategy");
        categoriesListFive.add("Bluffing");
        categoriesListFive.add("Gambling");

        BoardGame bg = new BoardGame("Medieval", 4, 1, 4);
        BoardGame bg1 = new BoardGame("DreamWars", 3, 1, 8);
        BoardGame bg2 = new BoardGame("Chaos in the Old World(1)", 3, 2, 5, categoriesListSingle);
        BoardGame bg3 = new BoardGame("Risk", 2, 2, 6);
        BoardGame bg4 = new BoardGame("Game Of life(2)", 1, 2, 6, categoriesListDouble);
        BoardGame bg5 = new BoardGame("Monopoly(5)", 1, 2, 8, categoriesListFive);
        BoardGame bg6 = new BoardGame("Cluedo(5)", 1, 2, 6, categoriesListFive);
        BoardGame bg7 = new BoardGame("Top Trumps(3)", 1, 2, 2, categoriesListTriple);
        BoardGame bg8 = new BoardGame("Poker(3)", 4, 2, 8, categoriesListTriple);
        BoardGame bg9 = new BoardGame("Saltlands", 3, 1, 6, categoriesListSingle);

        boardGames.add(bg);
        boardGames.add(bg1);
        boardGames.add(bg2);
        boardGames.add(bg3);
        boardGames.add(bg4);
        boardGames.add(bg5);
        boardGames.add(bg6);
        boardGames.add(bg7);
        boardGames.add(bg8);
        boardGames.add(bg9);

        BoardGameAdapter bgAdapter = new BoardGameAdapter(getActivity(), boardGames);

        ListView listView = rootView.findViewById(R.id.list);

        listView.setAdapter(bgAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent boardGameIntent = new Intent(getContext(), BoardGameActivity.class);
                //TODO change this to be primary key for member database?
                boardGameIntent.putExtra("BOARD_GAME", boardGames.get(position));
                startActivity(boardGameIntent);
            }
        });

        return rootView;
    }
}
