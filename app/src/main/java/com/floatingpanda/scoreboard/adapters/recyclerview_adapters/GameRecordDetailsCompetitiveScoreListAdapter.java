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

/**
 * Recyclerview adapter that takes a list of player teams with players and displays the teams of
 * players in a list format for competitive games, including what finishing place the team placed in
 * (e.g. 1st Place).
 */
public class GameRecordDetailsCompetitiveScoreListAdapter extends RecyclerView.Adapter<GameRecordDetailsCompetitiveScoreListAdapter.GameRecordDetailsCompetitiveScoreViewHolder> {

    private Context context;
    private final LayoutInflater inflater;
    private List<PlayerTeamWithPlayers> playerTeamsWithPlayers;
    private boolean teams;

    public GameRecordDetailsCompetitiveScoreListAdapter(Context context, boolean teams) {
        this.context = context;
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

            String placeString = createPlaceString(current.getPlayerTeam().getPosition());
            holder.placeTextView.setText(placeString);

            if (teams) {
                holder.teamTextView.setVisibility(View.VISIBLE);
                holder.teamOutputTextView.setText(current.getPlayerTeam().getTeamNumber());
            } else {
                holder.teamTextView.setVisibility(View.INVISIBLE);
            }

            String playersString = createPlayersString(current.getPlayers());
            holder.playersTextView.setText(playersString);
            holder.scoreOutputTextView.setText(Integer.toString(current.getPlayerTeam().getScore()));
        } else {

        }
    }

    /**
     * Sets the list of player teams with players that will be displayed by the adapter.
     *
     * Must be called before adapter will display anything.
     * @param playerTeamsWithPlayers list of player teams and their players to display
     */
    public void setPlayerTeamsWithPlayers(List<PlayerTeamWithPlayers> playerTeamsWithPlayers) {
        this.playerTeamsWithPlayers = playerTeamsWithPlayers;
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

    /**
     * Takes a list of players and creates a player string in the format 'name, name, name'.
     * @param players
     * @return
     */
    private String createPlayersString(List<Player> players) {
        StringBuilder sb = new StringBuilder();
        for (Player player : players) {
            if (sb.length() > 0) {
                sb.append(", ");
            }

            sb.append(player.getMemberNickname());
        }
        return sb.toString();
    }

    @Override
    public int getItemCount() {
        if (playerTeamsWithPlayers != null)
            return playerTeamsWithPlayers.size();
        else return 0;
    }

    class GameRecordDetailsCompetitiveScoreViewHolder extends RecyclerView.ViewHolder {
        private final TextView placeTextView, teamTextView, teamOutputTextView, playersTextView, scoreOutputTextView;

        private GameRecordDetailsCompetitiveScoreViewHolder(View itemView) {
            super(itemView);

            placeTextView = itemView.findViewById(R.id.place);
            teamTextView = itemView.findViewById(R.id.team_textview);
            teamOutputTextView = itemView.findViewById(R.id.team_output);
            playersTextView = itemView.findViewById(R.id.players);
            scoreOutputTextView = itemView.findViewById(R.id.score_output);
        }
    }
}
