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
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;

import java.util.List;

/**
 * Recyclerview adapter that takes a list of player teams with players and displays the teams of
 * players in a list format for cooperative and solitaire games, including whether the players won
 * or lost the game.
 *
 * The following precondition exists because it is simpler where this adapter is used to keep
 * playerTeams as a list rather than extract a single playerTeam and plug it into the adapter.
 *
 * Precondition: list of PlayerTeamWithPlayer objects should only hold 1 TeamOfPlayer objects, no
 * more or less.
 */
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

    class GameRecordDetailsCooperativeScoreViewHolder extends RecyclerView.ViewHolder {
        private final TextView playersTextView, scoreOutputTextView;

        private GameRecordDetailsCooperativeScoreViewHolder(View itemView) {
            super(itemView);

            playersTextView = itemView.findViewById(R.id.players);
            scoreOutputTextView = itemView.findViewById(R.id.score_output);
        }
    }
}
