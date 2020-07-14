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

public class GameRecordDetailsCooperativeScoreListAdapter extends RecyclerView.Adapter<GameRecordDetailsCooperativeScoreListAdapter.GameRecordDetailsCooperativeScoreViewHolder> {

    private final LayoutInflater inflater;
    private List<PlayerTeamWithPlayers> playerTeamsWithPlayers;

    public GameRecordDetailsCooperativeScoreListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public GameRecordDetailsCooperativeScoreListAdapter.GameRecordDetailsCooperativeScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_game_record_cooperative_solitaire_team, parent, false);
        return new GameRecordDetailsCooperativeScoreListAdapter.GameRecordDetailsCooperativeScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GameRecordDetailsCooperativeScoreListAdapter.GameRecordDetailsCooperativeScoreViewHolder holder, int position) {
        if (playerTeamsWithPlayers != null) {
            PlayerTeamWithPlayers current = playerTeamsWithPlayers.get(position);
            StringBuilder sb = new StringBuilder();

            for (Player player : current.getPlayers()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(player.getMemberNickname());
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

    class GameRecordDetailsCooperativeScoreViewHolder extends RecyclerView.ViewHolder {
        private final TextView playersTextView, scoreTextView;

        private GameRecordDetailsCooperativeScoreViewHolder(View itemView) {
            super(itemView);

            playersTextView = itemView.findViewById(R.id.players);
            scoreTextView = itemView.findViewById(R.id.score);
        }
    }
}
