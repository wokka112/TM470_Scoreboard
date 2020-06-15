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
