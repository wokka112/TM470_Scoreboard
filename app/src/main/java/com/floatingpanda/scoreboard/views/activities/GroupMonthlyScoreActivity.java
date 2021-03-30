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

/**
 * View for viewing a group's monthly scores. This view gives all the scores for the month, for
 * every member that played a game in that month and got a score.
 */
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

    /**
     * Sets the back arrow in the taskbar to go back to the previous activity.
     * @param item
     * @return
     */
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
