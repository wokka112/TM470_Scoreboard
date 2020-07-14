package com.floatingpanda.scoreboard.adapters.recyclerview_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;

import java.util.List;

public class GameRecordDetailsCompetitiveScoreListAdapter extends RecyclerView.Adapter<GameRecordDetailsCompetitiveScoreListAdapter.GameRecordDetailsCompetitiveScoreViewHolder> {

    private final LayoutInflater inflater;
    private List<PlayerTeamWithPlayers> playerTeamsWithPlayers;
    private boolean teams;

    public GameRecordDetailsCompetitiveScoreListAdapter(Context context, boolean teams) {
        inflater = LayoutInflater.from(context);
        this.teams = teams;
    }

    @Override
    public GameRecordDetailsCompetitiveScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_game_record_competitive_team, parent, false);
        return new GameRecordDetailsCompetitiveScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GameRecordDetailsCompetitiveScoreViewHolder holder, int position) {
        if (playerTeamsWithPlayers != null) {
            PlayerTeamWithPlayers current = playerTeamsWithPlayers.get(position);
            StringBuilder sb = new StringBuilder();

            for (Player player : current.getPlayers()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(player.getMemberNickname());
            }

            switch (current.getPlayerTeam().getPosition()) {
                case 1 :
                    holder.placeTextView.setText("1st Place");
                    break;
                case 2:
                    holder.placeTextView.setText("2nd Place");
                    break;
                case 3:
                    holder.placeTextView.setText("3rd Place");
                    break;
                default:
                    holder.placeTextView.setText(current.getPlayerTeam().getPosition() + "th Place");
            }

            if (teams) {
                holder.teamTextView.setVisibility(View.INVISIBLE);
            } else {
                holder.teamTextView.setVisibility(View.VISIBLE);
                holder.teamTextView.setText("Team " + current.getPlayerTeam().getTeamNumber());
            }

            holder.playersTextView.setText(sb.toString());
            holder.scoreTextView.setText(current.getPlayerTeam().getScore() + "pts");
        } else {

        }
    }

    public void setPlayerTeamsWithPlayers(List<PlayerTeamWithPlayers> playerTeamsWithPlayers) {
        this.playerTeamsWithPlayers = playerTeamsWithPlayers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (playerTeamsWithPlayers != null)
            return playerTeamsWithPlayers.size();
        else return 0;
    }

    class GameRecordDetailsCompetitiveScoreViewHolder extends RecyclerView.ViewHolder {
        private final TextView placeTextView, teamTextView, playersTextView, scoreTextView;

        private GameRecordDetailsCompetitiveScoreViewHolder(View itemView) {
            super(itemView);

            placeTextView = itemView.findViewById(R.id.place);
            teamTextView = itemView.findViewById(R.id.team);
            playersTextView = itemView.findViewById(R.id.players);
            scoreTextView = itemView.findViewById(R.id.score);
        }
    }
}
