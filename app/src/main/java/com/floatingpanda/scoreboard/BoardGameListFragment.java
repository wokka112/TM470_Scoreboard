package com.floatingpanda.scoreboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.adapters.BoardGameListAdapter;
import com.floatingpanda.scoreboard.data.BoardGamesAndBgCategories;
import com.floatingpanda.scoreboard.viewmodels.BoardGameViewModel;

import java.util.List;

public class BoardGameListFragment extends Fragment {
    private BoardGameViewModel boardGameViewModel;

    public BoardGameListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_test, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        final BoardGameListAdapter adapter = new BoardGameListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        boardGameViewModel = new ViewModelProvider(this).get(BoardGameViewModel.class);

        boardGameViewModel.getAllBgsAndCategories().observe(getViewLifecycleOwner(), new Observer<List<BoardGamesAndBgCategories>>() {
            @Override
            public void onChanged(@Nullable final List<BoardGamesAndBgCategories> boardGamesAndBgCategories) {
                adapter.setBgsAndBgCategories(boardGamesAndBgCategories);
            }
        });

        return rootView;
    }
}
