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

package com.floatingpanda.scoreboard.adapters.recyclerview_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.relations.GroupMonthlyScoreWithScoresAndMemberDetails;
import com.floatingpanda.scoreboard.data.relations.ScoreWithMemberDetails;
import com.floatingpanda.scoreboard.data.entities.GroupMonthlyScore;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;
import com.floatingpanda.scoreboard.utils.DateStringCreator;

import java.util.List;

/**
 * Adapter that takes a list of group monthly scores with scores and member details (i.e. name of
 * the member who has the score) and then displays each group monthly score along with the 3 top
 * scores and the details of the member that got the score.
 *
 * Preconditions: Group monthly scores must be sorted into descending order based on year followed
 * by month.
 * Scores with member details must be sorted into descending order based on score values.
 */
public class ScoreWinnerListAdapter extends RecyclerView.Adapter<ScoreWinnerListAdapter.ScoreWinnerViewHolder> {

    private final LayoutInflater inflater;
    private Context context;
    private List<GroupMonthlyScoreWithScoresAndMemberDetails> groupMonthlyScoresWithScoresAndMemberDetails;
    private DetailAdapterInterface listener;

    public ScoreWinnerListAdapter(Context context, DetailAdapterInterface listener) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public ScoreWinnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_monthly_score_list, parent, false);
        return new ScoreWinnerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ScoreWinnerViewHolder holder, int position) {
        if (groupMonthlyScoresWithScoresAndMemberDetails != null) {
            GroupMonthlyScoreWithScoresAndMemberDetails current = groupMonthlyScoresWithScoresAndMemberDetails.get(position);
            GroupMonthlyScore currentGroupMonthlyScore = current.getGroupMonthlyScore();

            String dateString = DateStringCreator.convertMonthNumberToEnglishMonthNameString(currentGroupMonthlyScore.getMonth()) + " " + currentGroupMonthlyScore.getYear();
            holder.dateTextView.setText(dateString);

            //Hide all places with names and scores. Places with names and scores can be made visible and have text supplied to them in later steps.
            // This way only places with players will show information. For instance, months with only 2 players with scores will show them in
            // places 1 and 2 with no place 3 on display, rather than an empty place 3.
            hideScoreViews(holder);

            List<ScoreWithMemberDetails> currentScoresWithMemberDetails = current.getScoresWithMemberDetails();
            if (!currentScoresWithMemberDetails.isEmpty()) {
                populateScores(holder, currentScoresWithMemberDetails);
            } else {
                displayNoScoresText(holder);
            }
        } else {

        }
    }

    /**
     * Sets the list of group monthly scores with scores and member details that the adapter uses to
     * populate the list.
     *
     * Must be called before the adapter will display anything.
     * @param groupMonthlyScoresWithScoresAndMemberDetails
     */
    public void setGroupMonthlyScoresWithScoresAndMemberDetails(List<GroupMonthlyScoreWithScoresAndMemberDetails> groupMonthlyScoresWithScoresAndMemberDetails) {
        this.groupMonthlyScoresWithScoresAndMemberDetails = groupMonthlyScoresWithScoresAndMemberDetails;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (groupMonthlyScoresWithScoresAndMemberDetails != null)
            return groupMonthlyScoresWithScoresAndMemberDetails.size();
        else return 0;
    }

    /**
     * Sets the views for the first three positions for the month, including who attained them and
     * what scores they got. If there are not 3 players with scores for the month then it shows
     * details only for as many players as have scores.
     * @param holder
     * @param currentScoresWithMemberDetails
     */
    private void populateScores(ScoreWinnerViewHolder holder, List<ScoreWithMemberDetails> currentScoresWithMemberDetails) {
        int previousScore = 0;
        for (int i = 0; i < 3 && i < currentScoresWithMemberDetails.size(); i++) {
            ScoreWithMemberDetails scoreWithMemberDetails = currentScoresWithMemberDetails.get(i);
            int currentScore = scoreWithMemberDetails.getScore().getScore();

            if (i == 0) {
                displayFirstPlaceViews(holder);

                holder.firstPlaceNameTextView.setText(scoreWithMemberDetails.getMember().getNickname());
                holder.firstPlaceScoreTextView.setText(Integer.toString(currentScore));
                previousScore = currentScore;
            } else if (i == 1) {
                displaySecondPlaceViews(holder);

                if (currentScore == previousScore) {
                    holder.secondPlaceHeaderTextView.setText(holder.firstPlaceHeaderTextView.getText());
                }

                holder.secondPlaceNameTextView.setText(scoreWithMemberDetails.getMember().getNickname());
                holder.secondPlaceScoreTextView.setText(Integer.toString(currentScore));
                previousScore = currentScore;
            } else if (i == 2) {
                displayThirdPlaceViews(holder);

                if (currentScore == previousScore) {
                    holder.thirdPlaceHeaderTextView.setText(holder.secondPlaceHeaderTextView.getText());
                }

                holder.thirdPlaceNameTextView.setText(scoreWithMemberDetails.getMember().getNickname());
                holder.thirdPlaceScoreTextView.setText(Integer.toString(currentScore));
            }
        }
    }

    /**
     * Hides the first place header and displays a message informing the user that there are no
     * scores for this month.
     * @param holder
     */
    private void displayNoScoresText(ScoreWinnerViewHolder holder) {
        displayFirstPlaceViews(holder);
        holder.firstPlaceHeaderTextView.setVisibility(View.INVISIBLE);
        holder.firstPlaceNameTextView.setText(context.getString(R.string.no_scores_this_month));
    }

    private void displayFirstPlaceViews(ScoreWinnerViewHolder holder) {
        holder.firstPlaceHeaderTextView.setVisibility(View.VISIBLE);
        holder.firstPlaceNameTextView.setVisibility(View.VISIBLE);
        holder.firstPlaceScoreTextView.setVisibility(View.VISIBLE);
    }

    private void displaySecondPlaceViews(ScoreWinnerViewHolder holder) {
        holder.secondPlaceHeaderTextView.setVisibility(View.VISIBLE);
        holder.secondPlaceNameTextView.setVisibility(View.VISIBLE);
        holder.secondPlaceScoreTextView.setVisibility(View.VISIBLE);
    }

    private void displayThirdPlaceViews(ScoreWinnerViewHolder holder) {
        holder.thirdPlaceHeaderTextView.setVisibility(View.VISIBLE);
        holder.thirdPlaceNameTextView.setVisibility(View.VISIBLE);
        holder.thirdPlaceScoreTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the first, second and third place header, nickname and score textviews.
     * @param holder
     */
    private void hideScoreViews(ScoreWinnerViewHolder holder) {
        holder.firstPlaceHeaderTextView.setVisibility(View.GONE);
        holder.firstPlaceNameTextView.setVisibility(View.GONE);
        holder.firstPlaceScoreTextView.setVisibility(View.GONE);
        holder.secondPlaceHeaderTextView.setVisibility(View.GONE);
        holder.secondPlaceNameTextView.setVisibility(View.GONE);
        holder.secondPlaceScoreTextView.setVisibility(View.GONE);
        holder.thirdPlaceHeaderTextView.setVisibility(View.GONE);
        holder.thirdPlaceNameTextView.setVisibility(View.GONE);
        holder.thirdPlaceScoreTextView.setVisibility(View.GONE);
    }

    class ScoreWinnerViewHolder extends RecyclerView.ViewHolder {
        private final TextView firstPlaceNameTextView, firstPlaceScoreTextView, firstPlaceHeaderTextView, secondPlaceNameTextView, secondPlaceScoreTextView,
            secondPlaceHeaderTextView, thirdPlaceNameTextView, thirdPlaceScoreTextView, thirdPlaceHeaderTextView, dateTextView;
        private final ConstraintLayout layout;

        private ScoreWinnerViewHolder(View itemView) {
            super(itemView);

            firstPlaceNameTextView = itemView.findViewById(R.id.winners_first_player_name);
            firstPlaceScoreTextView = itemView.findViewById(R.id.winners_first_score);
            firstPlaceHeaderTextView = itemView.findViewById(R.id.winners_first_header);
            secondPlaceNameTextView = itemView.findViewById(R.id.winners_second_player_name);
            secondPlaceScoreTextView = itemView.findViewById(R.id.winners_second_score);
            secondPlaceHeaderTextView = itemView.findViewById(R.id.winners_second_header);
            thirdPlaceNameTextView = itemView.findViewById(R.id.winners_third_player_name);
            thirdPlaceScoreTextView = itemView.findViewById(R.id.winners_third_score);
            thirdPlaceHeaderTextView = itemView.findViewById(R.id.winners_third_header);
            dateTextView = itemView.findViewById(R.id.winners_date);

            layout = itemView.findViewById(R.id.winners_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    GroupMonthlyScoreWithScoresAndMemberDetails groupMonthlyScoreWithScoresAndMemberDetails = groupMonthlyScoresWithScoresAndMemberDetails.get(position);
                    listener.viewDetails(groupMonthlyScoreWithScoresAndMemberDetails);
                }
            });
        }
    }
}
