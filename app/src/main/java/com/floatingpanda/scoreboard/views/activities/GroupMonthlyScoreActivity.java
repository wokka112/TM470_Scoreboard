package com.floatingpanda.scoreboard.views.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.DetailedWinnerListAdapter;
import com.floatingpanda.scoreboard.data.relations.ScoreWithMemberDetails;
import com.floatingpanda.scoreboard.utils.DateStringCreator;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        monthYearTextView = findViewById(R.id.activity_winner_list_month_year);
        recyclerView = findViewById(R.id.activity_winner_list_recyclerview);

        int month = (int) getIntent().getExtras().get("MONTH");
        int year = (int) getIntent().getExtras().get("YEAR");
        scoresWithMemberDetails = (ArrayList<ScoreWithMemberDetails>) getIntent().getExtras().get("SCORES");

        String monthString = DateStringCreator.convertMonthNumberToEnglishMonthNameString(month);
        monthYearTextView.setText(monthString + " " + year);

        final DetailedWinnerListAdapter adapter = new DetailedWinnerListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setScoresWithMemberDetails(scoresWithMemberDetails);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
