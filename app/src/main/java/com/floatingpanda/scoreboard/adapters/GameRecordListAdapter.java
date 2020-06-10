package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
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
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;

import java.util.Calendar;
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

    @Override
    public void onBindViewHolder(GameRecordViewHolder holder, int position) {
        if (gameRecordsWithPlayerTeamsAndPlayers != null) {
            holder.firstPlaceWrapper.removeAllViews();
            holder.secondPlaceWrapper.removeAllViews();
            holder.thirdPlaceWrapper.removeAllViews();

            GameRecordWithPlayerTeamsAndPlayers current = gameRecordsWithPlayerTeamsAndPlayers.get(position);
            GameRecord currentGameRecord = current.getGameRecord();
            holder.gameNameTextView.setText(currentGameRecord.getBoardGameName());
            holder.dateTextView.setText(currentGameRecord.getDate().toString());
            holder.playModeTextView.setText(currentGameRecord.getPlayModePlayed().toString());
            holder.difficultyTextView.setText(Integer.toString(currentGameRecord.getDifficulty()));

            if(currentGameRecord.getTeamsAllowed()) {
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

            //TODO move this stuff into its own method.
            for (PlayerTeamWithPlayers playerTeamWithPlayers : currentPlayerTeamsWithPlayers) {
                PlayerTeam playerTeam = playerTeamWithPlayers.getPlayerTeam();
                List<Player> players = playerTeamWithPlayers.getPlayers();

                if (players.isEmpty()) {
                    continue;
                }

                if (playerTeam.getPosition() == 1) {
                    View view = inflater.inflate(R.layout.game_record_team, null);
                    TextView teamTextView = view.findViewById(R.id.team);
                    TextView playersTextView = view.findViewById(R.id.players);

                    teamTextView.setText("Team " + playerTeam.getTeamNumber());

                    if (!currentGameRecord.getTeamsAllowed()) {
                       teamTextView.setVisibility(View.INVISIBLE);
                    }

                    StringBuilder sb = new StringBuilder();
                    for (Player player : players) {
                        if (sb.length() > 0) {
                            sb.append(", ");
                        }

                        sb.append(player.getMemberNickname());
                    }

                    playersTextView.setText(sb.toString());

                    holder.firstPlaceWrapper.addView(view);
                } else if (playerTeam.getPosition() == 2) {
                    View view = inflater.inflate(R.layout.game_record_team, null);
                    TextView teamTextView = view.findViewById(R.id.team);
                    TextView playersTextView = view.findViewById(R.id.players);

                    teamTextView.setText("Team " + playerTeam.getTeamNumber());

                    if (!currentGameRecord.getTeamsAllowed()) {
                        teamTextView.setVisibility(View.INVISIBLE);
                    }

                    StringBuilder sb = new StringBuilder();
                    for (Player player : players) {
                        if (sb.length() > 0) {
                            sb.append(", ");
                        }

                        sb.append(player.getMemberNickname());
                    }

                    playersTextView.setText(sb.toString());

                    holder.secondPlaceWrapper.addView(view);
                } else if (playerTeam.getPosition() == 3) {
                    View view = inflater.inflate(R.layout.game_record_team, null);
                    TextView teamTextView = view.findViewById(R.id.team);
                    TextView playersTextView = view.findViewById(R.id.players);

                    teamTextView.setText("Team " + playerTeam.getTeamNumber());

                    if (!currentGameRecord.getTeamsAllowed()) {
                        teamTextView.setVisibility(View.INVISIBLE);
                    }

                    StringBuilder sb = new StringBuilder();
                    for (Player player : players) {
                        if (sb.length() > 0) {
                            sb.append(", ");
                        }

                        sb.append(player.getMemberNickname());
                    }

                    playersTextView.setText(sb.toString());

                    holder.thirdPlaceWrapper.addView(view);
                }
            }

            //Go through teams and populate first wrapper with teams.
            //Get teams in second place.
            //Go through teams and populate second wrapper with teams.
            //Get teams in third place.
            //Go through teams and populate third wrapper with teams.
        } else {

        }
    }

    public void setGameRecordsWithPlayerTeamsAndPlayers(List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers) {
        this.gameRecordsWithPlayerTeamsAndPlayers = gameRecordsWithPlayerTeamsAndPlayers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (gameRecordsWithPlayerTeamsAndPlayers != null)
            return gameRecordsWithPlayerTeamsAndPlayers.size();
        else return 0;
    }

    class GameRecordViewHolder extends RecyclerView.ViewHolder {
        private final TextView gameNameTextView, dateTextView, timeTextView, difficultyTextView, teamCountHeaderTextView, teamCountOutputTextView,
                playModeTextView;
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

            firstPlaceWrapper = itemView.findViewById(R.id.records_first_wrapper);
            secondPlaceWrapper = itemView.findViewById(R.id.records_second_wrapper);
            thirdPlaceWrapper = itemView.findViewById(R.id.records_third_wrapper);
        }
    }
}
