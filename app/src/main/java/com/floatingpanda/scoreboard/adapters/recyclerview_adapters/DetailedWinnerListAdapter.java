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

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.relations.ScoreWithMemberDetails;

import java.util.List;

/**
 * This adapter takes a list of scores with member details (ScoreWithMemberDetail) objects and
 * displays the members, their scores, and their places relative to one another based on said scores.
 *
 * So 3 players with 100, 75, and 50 points, respectively, will be displayed as
 * 1st Place name 100pts
 * 2nd Place name 75pts
 * 3rd Place name 50pts
 *
 * Precondition: the list of scores with member details must be ordered into descending order based
 * on score.
 */
public class DetailedWinnerListAdapter extends RecyclerView.Adapter<DetailedWinnerListAdapter.DetailedWinnerViewHolder> {
    private Context context;
    private final LayoutInflater inflater;
    private List<ScoreWithMemberDetails> scoresWithMemberDetails;
    private int place;

    public DetailedWinnerListAdapter(Context context) {
        this.context = context;
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
                //Nothing to do here.
            } else {
                place = position + 1;
            }

            String placeString = createPlaceString(place);
            holder.placeTextView.setText(placeString);
            holder.nameTextView.setText(current.getMember().getNickname());
            holder.scoreTextView.setText(Integer.toString(current.getScore().getScore()));
        } else {

        }
    }

    /**
     * Sets the scores and their member details (i.e. the details about the member who attained the
     * score) from which data is drawn to make a list of scores, members and the relative places
     * they have placed based on their scores.
     *
     * Must be called before adapter will display anything.
     * @param scoresWithMemberDetails
     */
    public void setScoresWithMemberDetails(List<ScoreWithMemberDetails> scoresWithMemberDetails) {
        this.scoresWithMemberDetails = scoresWithMemberDetails;
        notifyDataSetChanged();
    }

    /**
     * Takes a finishing place integer (1, 2, 3, 4, etc.) and returns its String representation
     * (1st Place, 2nd Place, 3rd Place, 4th Place, etc.).
     * @param place the finishing place of a player or team
     * @return
     */
    private String createPlaceString(int place) {
        String placeString;
        switch (place) {
            case 1:
                placeString = context.getString(R.string.first_place_header);
                break;
            case 2:
                placeString = context.getString(R.string.second_place_header);
                break;
            case 3:
                placeString = context.getString(R.string.third_place_header);
                break;
            default:
                placeString = Integer.toString(place) + context.getString(R.string.generic_place_ending);
                break;
        }

        return placeString;
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
