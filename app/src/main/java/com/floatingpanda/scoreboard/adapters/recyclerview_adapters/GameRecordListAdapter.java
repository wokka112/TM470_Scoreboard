package com.floatingpanda.scoreboard.adapters.recyclerview_adapters;

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
import com.floatingpanda.scoreboard.data.relations.GameRecordWithPlayerTeamsAndPlayers;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;
import com.floatingpanda.scoreboard.utils.DateStringCreator;

import java.util.List;

/**
 * Adapter that populates a list with game records and their details, including what game was played,
 * when, and how many players were in it.
 *
 * For cooperative or solitaire games, this includes a list of the players and whether or not the
 * game was won.
 *
 * For competitive games, this includes the first three teams of players and the scores they earned
 * in the game.
 */
public class GameRecordListAdapter extends RecyclerView.Adapter<GameRecordListAdapter.GameRecordViewHolder> {

    private Context context;
    private final LayoutInflater inflater;
    private List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers;
    private DetailAdapterInterface listener;

    public GameRecordListAdapter(Context context, DetailAdapterInterface listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.context = context;
    }

    @Override
    public GameRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_game_record, parent, false);
        return new GameRecordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GameRecordViewHolder holder, int position) {
        if (gameRecordsWithPlayerTeamsAndPlayers != null) {
            GameRecordWithPlayerTeamsAndPlayers current = gameRecordsWithPlayerTeamsAndPlayers.get(position);
            GameRecord currentGameRecord = current.getGameRecord();

            setStaticViews(holder, currentGameRecord);

            List<PlayerTeamWithPlayers> currentPlayerTeamsWithPlayers = current.getPlayerTeamsWithPlayers();
            currentPlayerTeamsWithPlayers.sort(new PlayerTeamWithPlayersComparator());

            if (currentGameRecord.getPlayModePlayed() == PlayMode.PlayModeEnum.COMPETITIVE) {
                populateWrapperWithCompetitiveViews(holder, currentGameRecord.getTeams(), currentPlayerTeamsWithPlayers);
            } else if (currentGameRecord.getPlayModePlayed() == PlayMode.PlayModeEnum.COOPERATIVE
                    || currentGameRecord.getPlayModePlayed() == PlayMode.PlayModeEnum.SOLITAIRE){
                populateWrapperWithCooperativeViews(holder, currentGameRecord.getWon(), currentPlayerTeamsWithPlayers);
            } else {
                Log.w("GameRecordListAdapt.java", "Current game record is neither competitive, coop or solitaire.");
            }

        } else {

        }
    }

    /**
     * Sets the list of game records with player teams and their players from which the adapter
     * populates the view.
     *
     * Must be called before adapter will display anything.
     * @param gameRecordsWithPlayerTeamsAndPlayers
     */
    public void setGameRecordsWithPlayerTeamsAndPlayers(List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers) {
        this.gameRecordsWithPlayerTeamsAndPlayers = gameRecordsWithPlayerTeamsAndPlayers;
        notifyDataSetChanged();
    }

    /**
     * Sets the static views in the layout to information taken from the game record, as opposed to
     * populating the more dynamic linear layout which acts as a wrapper holding manually inflated
     * views.
     * @param holder
     * @param gameRecord
     */
    private void setStaticViews(GameRecordViewHolder holder, GameRecord gameRecord) {
        holder.gameNameTextView.setText(gameRecord.getBoardGameName());

        DateStringCreator dateStringCreator = new DateStringCreator(gameRecord.getDateTime());

        String dateString = dateStringCreator.getDayOfWeek3LetterString() + " " + dateStringCreator.getEnglishMonth3LetterString() +
                " " + dateStringCreator.getDayOfMonthString() + " " + dateStringCreator.getYearString();
        holder.dateTextView.setText(dateString);

        String timeString = dateStringCreator.getHourOfDayString() + ":" + dateStringCreator.getMinuteString();
        holder.timeTextView.setText(timeString);

        holder.playModeTextView.setText(getPlayModeString(gameRecord.getPlayModePlayed()));
        holder.difficultyTextView.setText(Integer.toString(gameRecord.getDifficulty()));

        if(gameRecord.getTeams()) {
            holder.teamCountHeaderTextView.setText(context.getString(R.string.teams_colon_header));
        } else {
            holder.teamCountHeaderTextView.setText(context.getString(R.string.players_colon_header));
        }

        holder.teamCountOutputTextView.setText(Integer.toString(gameRecord.getNoOfTeams()));
    }

    /**
     * Returns the correct string to represent the playmode the game record was played in - Competitive,
     * Cooperative or Solitaire.
     * @param playModePlayed playmode enum representing the playmode played
     * @return
     */
    private String getPlayModeString(PlayMode.PlayModeEnum playModePlayed) {
        switch (playModePlayed) {
            case COMPETITIVE:
                return context.getString(R.string.competitive);
            case COOPERATIVE:
                return context.getString(R.string.cooperative);
            case SOLITAIRE:
                return context.getString(R.string.solitaire);
            default:
                return context.getString(R.string.play_mode_error);
        }
    }

    /**
     * Takes a list of player teams with players and a boolean showing whether the game was played
     * in teams. Using this, the method populates the linear layout wrapper with the players and, if
     * played in teams, their teams, as well as their finishing positions and the scores they earned
     * from the game.
     *
     * Precondition: list of player teams with players must be sorted into ascending order based on
     * teams' finishing positions.
     * @param holder
     * @param teams
     * @param playerTeamsWithPlayers
     */
    private void populateWrapperWithCompetitiveViews(GameRecordViewHolder holder, boolean teams, List<PlayerTeamWithPlayers> playerTeamsWithPlayers) {
        holder.wonLostTextView.setVisibility(View.GONE);
        holder.firstPlaceWrapper.removeAllViews();

        for (PlayerTeamWithPlayers playerTeamWithPlayers : playerTeamsWithPlayers) {
            PlayerTeam playerTeam = playerTeamWithPlayers.getPlayerTeam();
            List<Player> players = playerTeamWithPlayers.getPlayers();

            if (players.isEmpty()) {
                continue;
            }

            if (playerTeam.getPosition() <= 3) {
                View view = createCompetitiveTeamView(playerTeam, players, teams);
                holder.firstPlaceWrapper.addView(view);
            } else {
                break;
            }
        }
    }

    /**
     * Takes a list of player teams with players and a boolean showing whether or not the game was
     * won. Using this, the method populates the linear layout wrapper with whether or not the game
     * was won, followed by a list of the players who played in the game and the score they earned
     * from it.
     * @param holder
     * @param won
     * @param playerTeamsWithPlayers
     */
    private void populateWrapperWithCooperativeViews(GameRecordViewHolder holder, boolean won, List<PlayerTeamWithPlayers> playerTeamsWithPlayers) {
        holder.firstPlaceWrapper.removeAllViews();
        holder.wonLostTextView.setVisibility(View.VISIBLE);

        if (won) {
            holder.wonLostTextView.setText("WON");
        } else {
            holder.wonLostTextView.setText("LOST");
        }

        for (PlayerTeamWithPlayers playerTeamWithPlayers : playerTeamsWithPlayers) {
            PlayerTeam playerTeam = playerTeamWithPlayers.getPlayerTeam();
            List<Player> players = playerTeamWithPlayers.getPlayers();

            if (players.isEmpty()) {
                continue;
            }

            View view = createCooperativeTeamOrSolitaireView(playerTeam, players);
            holder.firstPlaceWrapper.addView(view);
        }
    }

    /**
     * Takes a player team, list of players and boolean showing whether the game was played in teams
     * or alone, and creates a view showing the players in the game as well as the positions they
     * finished in and the scores they earned.
     * @param playerTeam
     * @param players
     * @return
     */
    private View createCompetitiveTeamView(PlayerTeam playerTeam, List<Player> players, boolean teams) {
        View view = inflater.inflate(R.layout.recyclerview_item_game_record_competitive_team, null);
        TextView placeTextView = view.findViewById(R.id.place);
        TextView teamTextView = view.findViewById(R.id.team_textview);
        TextView teamOutputTextView = view.findViewById(R.id.team_output);
        TextView playersTextView = view.findViewById(R.id.players);
        TextView scoreOutputTextView = view.findViewById(R.id.score_output);

        String placeString = createPlaceString(playerTeam.getPosition());
        placeTextView.setText(placeString);

        if (!teams) {
            teamTextView.setVisibility(View.INVISIBLE);
            teamOutputTextView.setVisibility(View.INVISIBLE);
        } else {
            teamTextView.setText(context.getString(R.string.team));
            teamTextView.setVisibility(View.VISIBLE);

            teamOutputTextView.setText(Integer.toString(playerTeam.getTeamNumber()));
            teamOutputTextView.setVisibility(View.VISIBLE);
        }

        String playersString = createPlayersString(players);
        playersTextView.setText(playersString);

        scoreOutputTextView.setText(Integer.toString(playerTeam.getScore()));

        return view;
    }

    /**
     * Takes a player team and list of players and creates a view showing the players in the team,
     * as well as the score they earned.
     * @param playerTeam
     * @param players
     * @return
     */
    private View createCooperativeTeamOrSolitaireView(PlayerTeam playerTeam, List<Player> players) {
        View view = inflater.inflate(R.layout.recyclerview_item_game_record_cooperative_solitaire_team, null);
        TextView playersTextView = view.findViewById(R.id.players);
        TextView scoreOutputTextView = view.findViewById(R.id.score_output);

        String playersString = createPlayersString(players);
        playersTextView.setText(playersString);

        scoreOutputTextView.setText(Integer.toString(playerTeam.getScore()));

        return view;
    }

    /**
     * Takes a finishing place integer (1, 2, 3, 4, etc.) and returns its String representation
     * (1st Place, 2nd Place, 3rd Place, 4th Place, etc.).
     * @param finishingPlace the finishing place of a player or team
     * @return
     */
    private String createPlaceString(int finishingPlace) {
        String placeString;
        switch (finishingPlace) {
            case 1:
                Log.w("GameRecordPlayListAdapt.java", "Creating 1st place.");
                placeString = context.getString(R.string.first_place_header);
                break;
            case 2:
                Log.w("GameRecordPlayListAdapt.java", "Creating 2nd place.");
                placeString = context.getString(R.string.second_place_header);
                break;
            case 3:
                Log.w("GameRecordPlayListAdapt.java", "Creating 3rd place.");
                placeString = context.getString(R.string.third_place_header);
                break;
            default:
                Log.w("GameRecordPlayListAdapt.java", "Creating " + finishingPlace + "th place.");
                placeString = Integer.toString(finishingPlace) + context.getString(R.string.generic_place_ending);
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
        if (gameRecordsWithPlayerTeamsAndPlayers != null)
            return gameRecordsWithPlayerTeamsAndPlayers.size();
        else return 0;
    }

    class GameRecordViewHolder extends RecyclerView.ViewHolder {
        private final TextView gameNameTextView, dateTextView, timeTextView, difficultyTextView, teamCountHeaderTextView, teamCountOutputTextView,
                playModeTextView, wonLostTextView;
        private final LinearLayout firstPlaceWrapper;

        private GameRecordViewHolder(View itemView) {
            super(itemView);

            gameNameTextView = itemView.findViewById(R.id.records_game_name);
            dateTextView = itemView.findViewById(R.id.records_date);
            timeTextView = itemView.findViewById(R.id.records_time);
            difficultyTextView = itemView.findViewById(R.id.records_difficulty_output);
            teamCountHeaderTextView = itemView.findViewById(R.id.records_player_count_header);
            teamCountOutputTextView = itemView.findViewById(R.id.records_player_count_output);
            playModeTextView = itemView.findViewById(R.id.records_play_mode);
            wonLostTextView = itemView.findViewById(R.id.records_won_lost);

            firstPlaceWrapper = itemView.findViewById(R.id.records_first_wrapper);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    GameRecordWithPlayerTeamsAndPlayers gameRecordWithPlayerTeamsAndPlayers = gameRecordsWithPlayerTeamsAndPlayers.get(position);
                    listener.viewDetails(gameRecordWithPlayerTeamsAndPlayers);
                }
            });
        }
    }
}
