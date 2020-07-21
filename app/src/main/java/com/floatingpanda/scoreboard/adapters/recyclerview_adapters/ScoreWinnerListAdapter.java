package com.floatingpanda.scoreboard.adapters.recyclerview_adapters;

import android.content.Context;
import android.util.Log;
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
        View itemView = inflater.inflate(R.layout.recyclerview_item_winner_list, parent, false);
        return new ScoreWinnerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ScoreWinnerViewHolder holder, int position) {
        //TODO make it skip months with no scores. Maybe a condition in the if like (!groupMonthlyScoresAndMemberDetails.get(position).getScoresWithMemberDetails().isEmpty())
        if (groupMonthlyScoresWithScoresAndMemberDetails != null) {
            GroupMonthlyScoreWithScoresAndMemberDetails current = groupMonthlyScoresWithScoresAndMemberDetails.get(position);
            GroupMonthlyScore currentGroupMonthlyScore = current.getGroupMonthlyScore();

            String dateString = DateStringCreator.convertMonthNumberToEnglishMonthNameString(currentGroupMonthlyScore.getMonth()) + " " + currentGroupMonthlyScore.getYear();
            holder.dateTextView.setText(dateString);

            //TODO move this logic into its own method and refactor it so it's simplified.
            //TODO make all text begin as GONE so that if there's only 2 members with scores, there's not a "third place" with nothing in it.
            List<ScoreWithMemberDetails> currentScoresWithMemberDetails = current.getScoresWithMemberDetails();
            int previousScore = 0;
            for (int i = 0; i < 3 && i < currentScoresWithMemberDetails.size(); i++) {
                ScoreWithMemberDetails scoreWithMemberDetails = currentScoresWithMemberDetails.get(i);
                int currentScore = scoreWithMemberDetails.getScore().getScore();

                Log.w("ScoreWinnerListAdapter.java", "Current Group Month: " + currentGroupMonthlyScore.getMonth() + ", i: " + i + ", previous score: " +
                        previousScore + ", current score: " + currentScore);

                if (i == 0) {
                    holder.firstPlaceNameTextView.setText(scoreWithMemberDetails.getMember().getNickname());
                    holder.firstPlaceScoreTextView.setText(Integer.toString(currentScore));
                    previousScore = currentScore;
                } else if (i == 1) {
                    if (currentScore == previousScore) {
                        holder.secondPlaceHeaderTextView.setText(holder.firstPlaceHeaderTextView.getText());
                    }

                    holder.secondPlaceNameTextView.setText(scoreWithMemberDetails.getMember().getNickname());
                    holder.secondPlaceScoreTextView.setText(Integer.toString(currentScore));
                    previousScore = currentScore;
                } else if (i == 2) {
                    if (currentScore == previousScore) {
                        holder.thirdPlaceHeaderTextView.setText(holder.secondPlaceHeaderTextView.getText());
                    }

                    holder.thirdPlaceNameTextView.setText(scoreWithMemberDetails.getMember().getNickname());
                    holder.thirdPlaceScoreTextView.setText(Integer.toString(currentScore));
                    previousScore = currentScore;
                }
            }
        } else {

        }
    }

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
