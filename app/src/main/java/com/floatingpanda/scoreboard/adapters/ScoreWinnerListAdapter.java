package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.comparators.PlayerTeamWithPlayersComparator;
import com.floatingpanda.scoreboard.data.GameRecordWithPlayerTeamsAndPlayers;
import com.floatingpanda.scoreboard.data.GroupMonthlyScoreWithScoresAndMemberDetails;
import com.floatingpanda.scoreboard.data.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.data.ScoreWithMemberDetails;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.GroupMonthlyScore;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.entities.Score;

import java.util.List;

public class ScoreWinnerListAdapter extends RecyclerView.Adapter<ScoreWinnerListAdapter.ScoreWinnerViewHolder> {

    private final LayoutInflater inflater;
    private Context context;
    private List<GroupMonthlyScoreWithScoresAndMemberDetails> groupMonthlyScoresWithScoresAndMemberDetails;
    //private DetailAdapterInterface listener;

    public ScoreWinnerListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ScoreWinnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_winner_list, parent, false);
        return new ScoreWinnerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ScoreWinnerViewHolder holder, int position) {
        //TODO make it skip months with no scores. Maybe a condition in the if like (!groupMonthlyScoresAndMemberDetails.get(position).getScoresWithMemberDetails().isEmpty())
        //TODO make it so in a month with only 2 people with scores, the 3rd position header is turned invisible. Likewise, a 1 person scoring month make 2nd and 3rd position headers invisible.
        if (groupMonthlyScoresWithScoresAndMemberDetails != null) {
            GroupMonthlyScoreWithScoresAndMemberDetails current = groupMonthlyScoresWithScoresAndMemberDetails.get(position);

            //TODO add logic to turn month integer into a month string - e.g. 1 becomes January
            GroupMonthlyScore currentGroupMonthlyScore = current.getGroupMonthlyScore();
            holder.dateTextView.setText(currentGroupMonthlyScore.getMonth() + " " + currentGroupMonthlyScore.getYear());

            List<ScoreWithMemberDetails> currentScoresWithMemberDetails = current.getScoresWithMemberDetails();
            int previousScore = 0;
            for (int i = 0; i < currentScoresWithMemberDetails.size(); i++) {
                ScoreWithMemberDetails scoreWithMemberDetails = currentScoresWithMemberDetails.get(i);
                int currentScore = scoreWithMemberDetails.getScore().getScore();

                Log.w("ScoreWinnerListAdapter.java", "Current Group Month: " + currentGroupMonthlyScore.getMonth() + ", i: " + i + ", previous score: " +
                        previousScore + ", current score: " + currentScore);

                if (i == 0) {
                    holder.firstPlaceNameTextView.setText(scoreWithMemberDetails.getMember().getNickname());
                    holder.firstPlaceScoreTextView.setText(Integer.toString(currentScore));
                    previousScore = currentScore;
                } else if (i == 1) {
                    // The else fixes a bug where sometimes headers would be made invisible when they shouldn't be.
                    if (currentScore == previousScore) {
                        Log.w("ScoreWinnerListAdapter.java", "Set second invisible. Current Group Month: " + currentGroupMonthlyScore.getMonth() + ", i: " + i + ", previous score: "
                                + previousScore + ", current score: " + currentScore);
                        holder.secondPlaceHeaderTextView.setVisibility(View.INVISIBLE);
                    } else {
                        holder.secondPlaceHeaderTextView.setVisibility(View.VISIBLE);
                    }

                    holder.secondPlaceNameTextView.setText(scoreWithMemberDetails.getMember().getNickname());
                    holder.secondPlaceScoreTextView.setText(Integer.toString(currentScore));
                    previousScore = currentScore;
                } else if (i == 2) {
                    // The else fixes a bug where sometimes headers would be made invisible when they shouldn't be.
                    if (currentScore == previousScore) {
                        Log.w("ScoreWinnerListAdapter.java", "Set third invisible. Current Group Month: " + currentGroupMonthlyScore.getMonth() + ", i: " + i + ", previous score: "
                                + previousScore + ", current score: " + currentScore);
                        holder.thirdPlaceHeaderTextView.setVisibility(View.INVISIBLE);
                    } else {
                        holder.thirdPlaceHeaderTextView.setVisibility(View.VISIBLE);
                    }

                    holder.thirdPlaceNameTextView.setText(scoreWithMemberDetails.getMember().getNickname());
                    holder.thirdPlaceScoreTextView.setText(Integer.toString(currentScore));
                    previousScore = currentScore;
                } else {
                    if (i > 7 || currentScore < previousScore) {
                        break;
                    }

                    //TODO add in logic to add more players to list if needed. (Is this necessary???)
                    // Instantiate ConstraintSet before the for loop, then add to it via a helper method that does the ConstraintSet stuff for us - getConstraints(ConstraintSet set, int nameId, int scoreId)
                    // Then apply constraint set after the for loop is finished.
                    /*
                    TextView playerNameTextView = new TextView(context);
                    playerNameTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    playerNameTextView.setText(scoreWithMemberDetails.getMember().getNickname());
                    playerNameTextView.setId(i);

                    TextView playerScoreTextView = new TextView(context);
                    playerScoreTextView.setText(Integer.toString(scoreWithMemberDetails.getScore().getScore()));
                    playerScoreTextView.setId(i + 20);

                    holder.layout.addView(playerNameTextView);
                    holder.layout.addView(playerScoreTextView);

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(holder.layout);
                    constraintSet.connect(playerNameTextView.getId(), ConstraintSet.LEFT, R.id.winners_name_header, ConstraintSet.LEFT, 0);
                    constraintSet.connect(playerNameTextView.getId(), ConstraintSet.RIGHT, R.id.winners_name_header, ConstraintSet.RIGHT, 0);
                    constraintSet.connect(playerScoreTextView.getId(), ConstraintSet.LEFT, R.id.winners_score_header, ConstraintSet.LEFT, 0);
                    constraintSet.connect(playerScoreTextView.getId(), ConstraintSet.RIGHT, R.id.winners_score_header, ConstraintSet.RIGHT, 0);
                    constraintSet.applyTo(holder.layout);

                     */
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
        private final TextView firstPlaceNameTextView, firstPlaceScoreTextView, secondPlaceNameTextView, secondPlaceScoreTextView,
            secondPlaceHeaderTextView, thirdPlaceNameTextView, thirdPlaceScoreTextView, thirdPlaceHeaderTextView, dateTextView;
        private final ConstraintLayout layout;

        private ScoreWinnerViewHolder(View itemView) {
            super(itemView);

            firstPlaceNameTextView = itemView.findViewById(R.id.winners_first_player_name);
            firstPlaceScoreTextView = itemView.findViewById(R.id.winners_first_score);
            secondPlaceNameTextView = itemView.findViewById(R.id.winners_second_player_name);
            secondPlaceScoreTextView = itemView.findViewById(R.id.winners_second_score);
            secondPlaceHeaderTextView = itemView.findViewById(R.id.winners_second_header);
            thirdPlaceNameTextView = itemView.findViewById(R.id.winners_third_player_name);
            thirdPlaceScoreTextView = itemView.findViewById(R.id.winners_third_score);
            thirdPlaceHeaderTextView = itemView.findViewById(R.id.winners_third_header);
            dateTextView = itemView.findViewById(R.id.winners_date);

            layout = itemView.findViewById(R.id.winners_layout);
        }
    }
}
