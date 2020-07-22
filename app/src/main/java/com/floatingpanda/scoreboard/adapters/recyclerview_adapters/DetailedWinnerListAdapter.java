package com.floatingpanda.scoreboard.adapters.recyclerview_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.relations.ScoreWithMemberDetails;

import java.util.List;

public class DetailedWinnerListAdapter extends RecyclerView.Adapter<DetailedWinnerListAdapter.DetailedWinnerViewHolder> {
    private final LayoutInflater inflater;
    private List<ScoreWithMemberDetails> scoresWithMemberDetails;
    private int place;

    public DetailedWinnerListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        place = 0;
    }

    @Override
    public DetailedWinnerListAdapter.DetailedWinnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_winner_score, parent, false);
        return new DetailedWinnerListAdapter.DetailedWinnerViewHolder(itemView);
    }

    //TODO Think this system will break if any views are recycled. Unless a group has 100s of members
    // this shouldn't be a problem...
    @Override
    public void onBindViewHolder(DetailedWinnerListAdapter.DetailedWinnerViewHolder holder, int position) {
        if (scoresWithMemberDetails != null) {
            ScoreWithMemberDetails current = scoresWithMemberDetails.get(position);

            if (position > 0 && current.getScore().getScore() == scoresWithMemberDetails.get(position - 1).getScore().getScore()) {
                place = place;
            } else {
                place = position + 1;
            }

            String placeString;
            switch (place) {
                case 1:
                    placeString = place + "st Place";
                    break;
                case 2:
                    placeString = place + "nd Place";
                    break;
                case 3:
                    placeString = place + "rd Place";
                    break;
                default:
                    placeString = place + "th Place";
                    break;
            }

            holder.placeTextView.setText(placeString);
            holder.nameTextView.setText(current.getMember().getNickname());
            holder.scoreTextView.setText(Integer.toString(current.getScore().getScore()));
        } else {

        }
    }

    public void setScoresWithMemberDetails(List<ScoreWithMemberDetails> scoresWithMemberDetails) {
        this.scoresWithMemberDetails = scoresWithMemberDetails;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (scoresWithMemberDetails != null)
            return scoresWithMemberDetails.size();
        else return 0;
    }

    class DetailedWinnerViewHolder extends RecyclerView.ViewHolder {
        private final TextView placeTextView, nameTextView, scoreTextView;

        private DetailedWinnerViewHolder(View itemView) {
            super(itemView);

            placeTextView = itemView.findViewById(R.id.winner_score_place);
            nameTextView = itemView.findViewById(R.id.winner_score_name);
            scoreTextView = itemView.findViewById(R.id.winner_score_score);
        }
    }
}
