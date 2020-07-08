package com.floatingpanda.scoreboard.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.DetailedWinnerListAdapter;
import com.floatingpanda.scoreboard.adapters.ScoreWinnerListAdapter;
import com.floatingpanda.scoreboard.data.GroupMonthlyScoreWithScoresAndMemberDetails;
import com.floatingpanda.scoreboard.data.ScoreWithMemberDetails;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.old.groupactivityadapters.WinnerListAdapter;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;

import java.util.ArrayList;
import java.util.List;

public class GroupMonthlyScoreActivity extends AppCompatActivity {

    private TextView monthYearTextView;
    private RecyclerView recyclerView;

    private List<ScoreWithMemberDetails> scoresWithMemberDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner_list);

        monthYearTextView = findViewById(R.id.activity_winner_list_month_year);
        recyclerView = findViewById(R.id.activity_winner_list_recyclerview);

        int month = (int) getIntent().getExtras().get("MONTH");
        int year = (int) getIntent().getExtras().get("YEAR");
        scoresWithMemberDetails = (ArrayList<ScoreWithMemberDetails>) getIntent().getExtras().get("SCORES");

        monthYearTextView.setText(month + " " + year);

        final DetailedWinnerListAdapter adapter = new DetailedWinnerListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setScoresWithMemberDetails(scoresWithMemberDetails);
    }
}
