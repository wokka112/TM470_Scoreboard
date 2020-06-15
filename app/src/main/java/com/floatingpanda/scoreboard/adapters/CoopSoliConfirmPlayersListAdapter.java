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

//TODO maybe change to take a list of members and then just use a simple layout with a textview in it for populating the recyclerview?
// Might be simpler than this current method.
public class CoopSoliConfirmPlayersListAdapter extends RecyclerView.Adapter<CoopSoliConfirmPlayersListAdapter.GameRecordPlayerViewHolder> {

    private Context context;
    private final LayoutInflater inflater;
    private List<TeamOfPlayers> playerTeams;
    private boolean won;

    public CoopSoliConfirmPlayersListAdapter(Context context, boolean won) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.won = won;
    }

    @Override
    public CoopSoliConfirmPlayersListAdapter.GameRecordPlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_linear_vertical_wrapper, parent, false);
        return new CoopSoliConfirmPlayersListAdapter.GameRecordPlayerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CoopSoliConfirmPlayersListAdapter.GameRecordPlayerViewHolder holder, int position) {
        if (playerTeams != null) {
            Log.w("CoopSoliPlayerListAdapt.java", "Adapter position: " + position);
            holder.layout.removeAllViews();

            //If first position, add in won or lost text.
            if (position == 0) {
                TextView placeHeaderTextView = new TextView(context);
                placeHeaderTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                if (won) {
                    placeHeaderTextView.setText("WON");
                } else {
                    placeHeaderTextView.setText("LOST");
                }

                placeHeaderTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                placeHeaderTextView.setGravity(Gravity.CENTER);
                placeHeaderTextView.setPadding(8, 8, 8, 8);
                holder.layout.addView(placeHeaderTextView);
            }

            TeamOfPlayers current = playerTeams.get(position);
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
