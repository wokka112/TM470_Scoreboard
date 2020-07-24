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

import java.util.List;

/**
 * Takes a list of teams of players (TeamOfPlayer objects) and from this displays a list of players
 * for cooperative and solitaire games. This list also shows whether the players won or lost the
 * game. This is used to get confirmation of players and whether the game was won or lost when
 * creating game records.
 *
 * The following precondition exists because it is simpler where this adapter is used to keep
 * playerTeams as a list rather than extract a single playerTeam and plug it into the adapter.
 *
 * Precondition: list of TeamOfPlayer objects should only hold 1 TeamOfPlayer objects, no more or
 * less.
 */
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
            holder.layout.removeAllViews();

            //If first position, add in won or lost text.
            if (position == 0) {
                TextView wonLostTextView = createWonLostTextView(won);
                holder.layout.addView(wonLostTextView);
            }

            TeamOfPlayers current = playerTeams.get(position);
            List<Member> members = current.getMembers();

            for (Member member : members) {
                TextView teamPlayerTextView = createPlayerTextView(member);
                holder.layout.addView(teamPlayerTextView);
            }
        } else {

        }
    }

    /**
     * Sets the teams of players from which data is drawn to make a list of players/teams and whether
     * they won or lost.
     *
     * Must be called before adapter will display anything.
     *
     * Precondition: playerTeams should only have 1 TeamOfPlayers in it.
     * @param playerTeams
     */
    public void setPlayerTeams(List<TeamOfPlayers> playerTeams) {
        this.playerTeams = playerTeams;
        notifyDataSetChanged();
    }

    /**
     * Creates the won or lost textview to display at the top of the player list. This shows whether
     * the team won or lost. The textview is given 16 SP text size, 8dp padding all round, and a
     * gravity of GRAVITY_CENTER.
     * @param won boolean showing whether the players won the game or not.
     * @return
     */
    private TextView createWonLostTextView(boolean won) {
        TextView wonLostTextView = new TextView(context);
        wonLostTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (won) {
            wonLostTextView.setText(context.getString(R.string.won));
        } else {
            wonLostTextView.setText(context.getString(R.string.lost));
        }

        wonLostTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        wonLostTextView.setGravity(Gravity.CENTER);
        wonLostTextView.setPadding(8, 8, 8, 8);

        return wonLostTextView;
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
