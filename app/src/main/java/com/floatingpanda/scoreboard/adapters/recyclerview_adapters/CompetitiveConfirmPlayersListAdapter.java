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
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.data.entities.Member;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Takes a list of TeamOfPlayer objects and from this displays a list of teams/players with their
 * finishing places for competitive games. This is used to get confirmation of players, teams and
 * their respective positions when creating game records.
 */
public class CompetitiveConfirmPlayersListAdapter extends RecyclerView.Adapter<CompetitiveConfirmPlayersListAdapter.GameRecordPlayerViewHolder> {

    private Context context;
    private final LayoutInflater inflater;
    private List<TeamOfPlayers> playerTeams;

    public CompetitiveConfirmPlayersListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public CompetitiveConfirmPlayersListAdapter.GameRecordPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_linear_vertical_wrapper, parent, false);
        return new CompetitiveConfirmPlayersListAdapter.GameRecordPlayerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CompetitiveConfirmPlayersListAdapter.GameRecordPlayerViewHolder holder, int position) {
        if (playerTeams != null) {
            TeamOfPlayers current = playerTeams.get(position);

            holder.layout.removeAllViews();

            int lastFinishingPlace;
            if (position == 0) {
                lastFinishingPlace = 0;
            } else {
                lastFinishingPlace = playerTeams.get(position - 1).getPosition();
            }

            int currentFinishingPlace = current.getPosition();

            //TODO change to skip empty places?
            //While lastFinishingPlace is less than the current place, increment the last finishing place and then pop in a new place header.
            // This way if 2 people place in 1st, and 1 in 3rd, you get the place header for 1st place, 2nd place and 3rd place, but 2nd
            // place has no people in it, while 1st has 2 and 3rd has 1.
            while (lastFinishingPlace < currentFinishingPlace) {
                lastFinishingPlace++;

                String placeString = createPlaceString(lastFinishingPlace);
                TextView placeHeaderTextView = createPlaceHeaderTextView(placeString);
                holder.layout.addView(placeHeaderTextView);
            }

            String teamString = context.getString(R.string.team) + " " + current.getTeamNo();
            TextView teamTextView = createTeamTextView(teamString);
            holder.layout.addView(teamTextView);
            
            List<Member> members = current.getMembers();

            for (Member member : members) {
                TextView teamPlayerTextView = createPlayerTextView(member);
                holder.layout.addView(teamPlayerTextView);
            }
        } else {

        }
    }

    /**
     * Sets the teams of players from which data is drawn to make a list of players/teams and their places.
     *
     * Must be called before adapter will display anything.
     * @param playerTeams
     */
    public void setPlayerTeams(List<TeamOfPlayers> playerTeams) {
        this.playerTeams = playerTeams;
        notifyDataSetChanged();
    }

    /**
     * Takes a finishing place integer (1, 2, 3, 4, etc.) and returns its String representation
     * (1st Place, 2nd Place, 3rd Place, 4th Place, etc.).
     * @param finishingPlace the finishing place of a player or team
     * @return
     */
    private String createPlaceString(int finishingPlace) {
        String placeString;
        switch (finishingPlace) {
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
                placeString = Integer.toString(finishingPlace) + context.getString(R.string.generic_place_ending);
        }

        return placeString;
    }

    /**
     * Creates a TextView for the place header text with the appropriate settings - 16 SP textsize,
     * 8dp padding all around, and centered gravity.
     * @param placeString the place text to be put in the TextView (e.g. 1st Place)
     * @return
     */
    private TextView createPlaceHeaderTextView(String placeString) {
        TextView placeTextView = new TextView(context);
        placeTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        placeTextView.setText(placeString);
        placeTextView.setTextAppearance(R.style.TextAppearance_MdcTypographyStyles_Headline6);
        placeTextView.setGravity(Gravity.CENTER);
        placeTextView.setPadding(8, 8, 8, 8);

        return placeTextView;
    }

    private TextView createTeamTextView(String teamString) {
        TextView teamTextView = (TextView) inflater.inflate(R.layout.recyclerview_team_header_item, null);
        teamTextView.setText(teamString);
        teamTextView.setTextAppearance(R.style.TextAppearance_MdcTypographyStyles_Body1);

        return teamTextView;
    }

    /**
     * Creates a textview with the members nickname ready for insertion into a linear layout.
     * @param member
     * @return
     */
    private TextView createPlayerTextView(Member member) {
        TextView playerTextView = new TextView(context);
        playerTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        playerTextView.setText(member.getNickname());
        playerTextView.setPadding(8, 4, 8, 4);
        playerTextView.setTextAppearance(R.style.TextAppearance_MdcTypographyStyles_Body1);

        return playerTextView;
    }

    @Override
    public int getItemCount() {
        if (playerTeams != null)
            return playerTeams.size();
        else return 0;
    }

    class GameRecordPlayerViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layout;

        private GameRecordPlayerViewHolder(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.recyclerview_linear_vertical_wrapper);
        }
    }
}
