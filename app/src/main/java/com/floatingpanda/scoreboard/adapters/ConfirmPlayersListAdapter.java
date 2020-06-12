package com.floatingpanda.scoreboard.adapters;

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

import java.util.List;

public class ConfirmPlayersListAdapter extends RecyclerView.Adapter<ConfirmPlayersListAdapter.GameRecordPlayerViewHolder> {

    private Context context;
    private final LayoutInflater inflater;
    //Needs to be sorted according to finishing place.
    private List<TeamOfPlayers> playerTeams;
    //private DetailAdapterInterface listener;

    public ConfirmPlayersListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ConfirmPlayersListAdapter.GameRecordPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_linear_vertical_wrapper, parent, false);
        return new ConfirmPlayersListAdapter.GameRecordPlayerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ConfirmPlayersListAdapter.GameRecordPlayerViewHolder holder, int position) {
        if (playerTeams != null) {
            Log.w("GameRecordPlayListAdapt.java", "Adapter position: " + position);
            holder.layout.removeAllViews();

            TeamOfPlayers current = playerTeams.get(position);
            int lastFinishingPlace;
            if (position == 0) {
                lastFinishingPlace = 0;
            } else {
                lastFinishingPlace = playerTeams.get(position - 1).getPosition();
            }
            Log.w("GameRecordPlayListAdapt.java", "Initial Last Finishing Position: " + lastFinishingPlace);
            Log.w("GameRecordPlayListAdapt.java", "Current team: " + current.getTeamNo() + ", Position: " + current.getPosition());
            //Get the current finishing place
            int currentFinishingPlace = current.getPosition();

            //While lastFinishingPlace is less than the current place, increment the last finishing place and then pop in a new place header.
            while (lastFinishingPlace < currentFinishingPlace) {
                lastFinishingPlace++;
                Log.w("GameRecordPlayListAdapt.java", "Incremented Last Finishing Place: " + lastFinishingPlace);

                TextView placeHeaderTextView = new TextView(context);
                placeHeaderTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                String placeString = Integer.toString(lastFinishingPlace);

                switch (lastFinishingPlace) {
                    case 1:
                        Log.w("GameRecordPlayListAdapt.java", "Creating 1st place.");
                        placeString += "st Place";
                        break;
                    case 2:
                        Log.w("GameRecordPlayListAdapt.java", "Creating 2nd place.");
                        placeString += "nd Place";
                        break;
                    case 3:
                        Log.w("GameRecordPlayListAdapt.java", "Creating 3rd place.");
                        placeString += "rd Place";
                        break;
                    default:
                        Log.w("GameRecordPlayListAdapt.java", "Creating " + lastFinishingPlace + "th place.");
                        placeString += "th Place";
                }

                placeHeaderTextView.setText(placeString);
                placeHeaderTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                placeHeaderTextView.setGravity(Gravity.CENTER);
                placeHeaderTextView.setPadding(8, 8, 8, 8);
                holder.layout.addView(placeHeaderTextView);
            }

            //TODO make this work for cooperative and solitaire games
            // - coop games should show a single team (Do i include win/loss?)
            // - solitaire games show a single player (Do I include win/loss?)
            // Do I do this by using booleans
            // - competitiveTeams for the places and teamtextview, coop for coop, soli for soli??
            // or do I create different adapters for them?

            TextView teamTextView = (TextView) inflater.inflate(R.layout.recyclerview_team_header_item, null);
            teamTextView.setText("Team " + current.getTeamNo());
            holder.layout.addView(teamTextView);
            
            List<Member> members = current.getMembers();

            for (Member member : members) {
                TextView teamPlayerTextView = new TextView(context);
                teamPlayerTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                teamPlayerTextView.setText(member.getNickname());
                teamPlayerTextView.setPadding(8, 4, 8, 4);
                holder.layout.addView(teamPlayerTextView);
            }
        } else {

        }
    }

    public void setPlayerTeams(List<TeamOfPlayers> playerTeams) {
        this.playerTeams = playerTeams;
        notifyDataSetChanged();
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
