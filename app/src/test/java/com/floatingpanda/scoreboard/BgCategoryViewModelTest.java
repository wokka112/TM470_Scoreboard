package com.floatingpanda.scoreboard;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.viewmodels.BgCategoryViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class BgCategoryViewModelTest {

    @Mock
    private Activity activity;
    @Mock
    private Application application;

    private AppDatabase appDatabase;
    private BgCategoryViewModel bgCategoryViewModel;

    @Before
    public void initViewModel() {
        Context context = application.getApplicationContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        bgCategoryViewModel = Mockito.spy(new BgCategoryViewModel(application));
    }

    // public boolean addActivityInputsValid(Activity activity, String categoryName) tests //

    //Test cases:
    //1. Valid string given.
    @Test
    public void addActivityInputsValid_CorrectString_ReturnsTrue() {
        assertTrue(bgCategoryViewModel.addActivityInputsValid(activity, "Name"));
    }

    //2. Empty string given.
    //3. Non-unique string given.
    //4. Null activity given.



}
