package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.comparators.PlayerTeamWithPlayersComparator;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.GameRecordWithPlayerTeamsAndPlayers;
import com.floatingpanda.scoreboard.data.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;

import java.util.List;

public class GameRecordListAdapter extends RecyclerView.Adapter<GameRecordListAdapter.GameRecordViewHolder> {

    private final LayoutInflater inflater;
    private List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers;
    //private DetailAdapterInterface listener;

    public GameRecordListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public GameRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_game_record, parent, false);
        return new GameRecordViewHolder(itemView);
    }

    //TODO fix bug where competitive games showing LOST and WON and not behaving like competitive games

    @Override
    public void onBindViewHolder(GameRecordViewHolder holder, int position) {
        if (gameRecordsWithPlayerTeamsAndPlayers != null) {
            GameRecordWithPlayerTeamsAndPlayers current = gameRecordsWithPlayerTeamsAndPlayers.get(position);
            GameRecord currentGameRecord = current.getGameRecord();
            holder.gameNameTextView.setText(currentGameRecord.getBoardGameName());
            holder.dateTextView.setText(currentGameRecord.getDateTime().toString());
            holder.playModeTextView.setText(currentGameRecord.getPlayModePlayed().toString());
            holder.difficultyTextView.setText(Integer.toString(currentGameRecord.getDifficulty()));

            if(currentGameRecord.getTeams()) {
                holder.teamCountHeaderTextView.setText("Teams: ");
            } else {
                holder.teamCountHeaderTextView.setText("Players: ");
            }

            holder.teamCountOutputTextView.setText(Integer.toString(currentGameRecord.getNoOfTeams()));

            List<PlayerTeamWithPlayers> currentPlayerTeamsWithPlayers = current.getPlayerTeamsWithPlayers();
            //TODO this sort requires minimum API level 24 which targets 74% of devices. Look into doing this some other way so I can
            // reduce to level 18?
            currentPlayerTeamsWithPlayers.sort(new PlayerTeamWithPlayersComparator());

            //TODO add padding or margin to wrappers so the text wraps around before it hits the right end of phone.

            if (currentGameRecord.getPlayModePlayed() == PlayMode.PlayModeEnum.COMPETITIVE) {
                populateCompetitive(holder, currentGameRecord.getTeams(), currentPlayerTeamsWithPlayers);
            } else if (currentGameRecord.getPlayModePlayed() == PlayMode.PlayModeEnum.COOPERATIVE
                    || currentGameRecord.getPlayModePlayed() == PlayMode.PlayModeEnum.SOLITAIRE){
                populateCooperative(holder, currentGameRecord.getWon(), currentPlayerTeamsWithPlayers);
            } else {
                Log.w("GameRecordListAdapt.java", "Current game record is neither competitive, coop or solitaire.");
            }

        } else {

        }
    }

    public void setGameRecordsWithPlayerTeamsAndPlayers(List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers) {
        this.gameRecordsWithPlayerTeamsAndPlayers = gameRecordsWithPlayerTeamsAndPlayers;
        notifyDataSetChanged();
    }

    private void populateCompetitive(GameRecordViewHolder holder, boolean teams, List<PlayerTeamWithPlayers> playerTeamsWithPlayers) {
        holder.firstPlaceTextView.setText("1st Place");
        holder.firstPlaceWrapper.removeAllViews();
        holder.secondPlaceWrapper.removeAllViews();
        holder.thirdPlaceWrapper.removeAllViews();

        for (PlayerTeamWithPlayers playerTeamWithPlayers : playerTeamsWithPlayers) {
            PlayerTeam playerTeam = playerTeamWithPlayers.getPlayerTeam();
            List<Player> players = playerTeamWithPlayers.getPlayers();

            if (players.isEmpty()) {
                continue;
            }

            if (playerTeam.getPosition() == 1) {
                View view = createTeamView(playerTeam, players, teams);
                holder.firstPlaceWrapper.addView(view);
            } else if (playerTeam.getPosition() == 2) {
                View view = createTeamView(playerTeam, players, teams);
                holder.secondPlaceWrapper.addView(view);
            } else if (playerTeam.getPosition() == 3) {
                View view = createTeamView(playerTeam, players, teams);
                holder.thirdPlaceWrapper.addView(view);
            }
        }
    }

    private void populateCooperative(GameRecordViewHolder holder, boolean won, List<PlayerTeamWithPlayers> playerTeamsWithPlayers) {
        holder.firstPlaceWrapper.removeAllViews();
        holder.secondPlaceWrapper.removeAllViews();
        holder.thirdPlaceWrapper.removeAllViews();
        holder.secondPlaceTextView.setVisibility(View.GONE);
        holder.thirdPlaceTextView.setVisibility(View.GONE);

        //TODO hide first place text view and move the WON/LOST thin to replace the teamCountHeader/Output textviews?
        if (won) {
            holder.firstPlaceTextView.setText("WON");
        } else {
            holder.firstPlaceTextView.setText("LOST");
        }

        for (PlayerTeamWithPlayers playerTeamWithPlayers : playerTeamsWithPlayers) {
            PlayerTeam playerTeam = playerTeamWithPlayers.getPlayerTeam();
            List<Player> players = playerTeamWithPlayers.getPlayers();

            if (players.isEmpty()) {
                continue;
            }

            View view = createTeamView(playerTeam, players, false);
            holder.firstPlaceWrapper.addView(view);
        }
    }

    private View createTeamView(PlayerTeam playerTeam, List<Player> players, boolean teams) {
        View view = inflater.inflate(R.layout.game_record_team, null);
        TextView teamTextView = view.findViewById(R.id.team);
        TextView playersTextView = view.findViewById(R.id.players);
        teamTextView.setText("Team " + playerTeam.getTeamNumber());

        if (!teams) {
            teamTextView.setVisibility(View.INVISIBLE);
        } else {
            teamTextView.setVisibility(View.VISIBLE);
        }

        StringBuilder sb = new StringBuilder();
        for (Player player : players) {
            if (sb.length() > 0) {
                sb.append(", ");
            }

            sb.append(player.getMemberNickname());
        }

        playersTextView.setText(sb.toString());

        return view;
    }

    @Override
    public int getItemCount() {
        if (gameRecordsWithPlayerTeamsAndPlayers != null)
            return gameRecordsWithPlayerTeamsAndPlayers.size();
        else return 0;
    }

    class GameRecordViewHolder extends RecyclerView.ViewHolder {
        private final TextView gameNameTextView, dateTextView, timeTextView, difficultyTextView, teamCountHeaderTextView, teamCountOutputTextView,
                playModeTextView, firstPlaceTextView, secondPlaceTextView, thirdPlaceTextView;
        private final LinearLayout firstPlaceWrapper, secondPlaceWrapper, thirdPlaceWrapper;

        private GameRecordViewHolder(View itemView) {
            super(itemView);

            gameNameTextView = itemView.findViewById(R.id.records_game_name);
            dateTextView = itemView.findViewById(R.id.records_date);
            timeTextView = itemView.findViewById(R.id.records_time);
            difficultyTextView = itemView.findViewById(R.id.records_difficulty_output);
            teamCountHeaderTextView = itemView.findViewById(R.id.records_player_count_header);
            teamCountOutputTextView = itemView.findViewById(R.id.records_player_count_output);
            playModeTextView = itemView.findViewById(R.id.records_play_mode);
            firstPlaceTextView = itemView.findViewById(R.id.records_first_place);
            secondPlaceTextView = itemView.findViewById(R.id.records_second_place);
            thirdPlaceTextView = itemView.findViewById(R.id.records_third_place);

            firstPlaceWrapper = itemView.findViewById(R.id.records_first_wrapper);
            secondPlaceWrapper = itemView.findViewById(R.id.records_second_wrapper);
            thirdPlaceWrapper = itemView.findViewById(R.id.records_third_wrapper);
        }
    }
}
