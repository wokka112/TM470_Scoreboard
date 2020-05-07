package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.BoardGameRepository;
import com.floatingpanda.scoreboard.data.BoardGamesAndBgCategories;

import java.util.List;

public class BoardGameViewModel extends AndroidViewModel {

    private BoardGameRepository boardGameRepository;
    private LiveData<List<BoardGamesAndBgCategories>> allBgsAndCategories;

    public BoardGameViewModel(Application application) {
        super(application);
        boardGameRepository = new BoardGameRepository(application);
        allBgsAndCategories = boardGameRepository.getAllBgsWithCategories();
    }

    public LiveData<List<BoardGamesAndBgCategories>> getAllBgsAndCategories() { return allBgsAndCategories; }
}
