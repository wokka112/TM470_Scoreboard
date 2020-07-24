package com.floatingpanda.scoreboard.adapters.recyclerview_adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerSkillRatingChange;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayers;
import com.floatingpanda.scoreboard.data.relations.PlayerWithRatingChanges;

import java.util.List;

/**
 * Adapter that takes a list of players with their skill rating changes and displays the information
 * in a list format. Skill rating changes are colour coded to show where gains and losses have
 * occurred.
 *
 * The player list is assumed to be sorted so that all of a player's skill ratings
 * are grouped together. That is, Bill's skill ratings should not have any of Phil's skill ratings
 * between them, and vice versa.
 *
 * Precondition: playersWithRatingChanges must be sorted so a player and all of their skill
 * ratings are together (i.e. all of Bill's ratings follow one another, there is no one else's
 * rating between any of Bill's).
 */
public class GameRecordDetailsSkillRatingListAdapter extends RecyclerView.Adapter<GameRecordDetailsSkillRatingListAdapter.GameRecordDetailsSkillRatingViewHolder> {

    private Context context;
    private final LayoutInflater inflater;
    private List<PlayerWithRatingChanges> playersWithRatingChanges;

    public GameRecordDetailsSkillRatingListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public GameRecordDetailsSkillRatingListAdapter.GameRecordDetailsSkillRatingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_game_record_skill_rating, parent, false);
        return new GameRecordDetailsSkillRatingListAdapter.GameRecordDetailsSkillRatingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GameRecordDetailsSkillRatingListAdapter.GameRecordDetailsSkillRatingViewHolder holder, int position) {
        if (playersWithRatingChanges != null) {
            PlayerWithRatingChanges current = playersWithRatingChanges.get(position);
            current.sortSkillRatingChanges();
            holder.nicknameTextView.setText(current.getPlayer().getMemberNickname());

            holder.ratingWrapper.removeAllViews();
            for (PlayerSkillRatingChange playerSkillRatingChange : current.getPlayerSkillRatingChanges()) {
                View view = createRatingView(playerSkillRatingChange);
                holder.ratingWrapper.addView(view);
            }
        } else {

        }
    }

    /**
     * Sets the list of players with rating changes that will be displayed by the adapter.
     *
     * Must be called before adapter will display anything.
     *
     * Precondition: playersWithRatingChanges must be sorted so a player and all of their skill
     * ratings are together (i.e. all of Bill's ratings follow one another, there is no one else's
     * rating between any of Bill's).
     * @param playersWithRatingChanges
     */
    public void setPlayersWithRatingChanges(List<PlayerWithRatingChanges> playersWithRatingChanges) {
        this.playersWithRatingChanges = playersWithRatingChanges;
        notifyDataSetChanged();
    }

    /**
     * Creates and returns a view inflated from recyclerview_item_game_record_skill_rating_wrapper_entry.
     * This view is filled with textviews to represent a skill rating's category, original rating,
     * rating change, and the resulting, changed rating.
     * @param playerSkillRatingChange
     * @return
     */
    private View createRatingView(PlayerSkillRatingChange playerSkillRatingChange) {
        View view = inflater.inflate(R.layout.recyclerview_item_game_record_skill_rating_wrapper_entry, null);
        TextView categoryNameTextView = view.findViewById(R.id.skill_rating_wrapper_entry_category);
        TextView oldRatingTextView = view.findViewById(R.id.skill_rating_wrapper_entry_old_rating);
        TextView ratingChangeTextView = view.findViewById(R.id.skill_rating_wrapper_entry_rating_change);
        TextView newRatingTextView = view.findViewById(R.id.skill_rating_wrapper_entry_new_rating);

        categoryNameTextView.setText(playerSkillRatingChange.getCategoryName());
        oldRatingTextView.setText(Double.toString(playerSkillRatingChange.getOldRating()));

        String ratingChangeString = createRatingChangeString(playerSkillRatingChange.getRatingChange());

        ratingChangeTextView.setText(ratingChangeString);
        newRatingTextView.setText(Double.toString(playerSkillRatingChange.get2DpRoundedNewRating()));

        int colour = getColour(playerSkillRatingChange.getRatingChange());

        ratingChangeTextView.setTextColor(colour);
        newRatingTextView.setTextColor(colour);

        return view;
    }

    /**
     * Creates and returns a string representation of the rating change doubled passed to it. If
     * the double is positive, the double string is preceded by +.
     * @param ratingChange
     * @return
     */
    private String createRatingChangeString(double ratingChange) {
        String ratingChangeString = "";
        if (ratingChange > 0.0) {
            ratingChangeString += "+";
        }
        ratingChangeString += Double.toString(ratingChange);

        return ratingChangeString;
    }

    /**
     * Takes a rating change double and returns a colour id based on the rating change.
     *
     * For positive rating changes (i.e. > 0.0), a green colour is returned.
     * For neutral rating changes (i.e. 0.0), a grey/blue colour is returned.
     * For negative rating changes (i.e. < 0.0), a red colour is returned.
     * @param ratingChange
     * @return
     */
    private int getColour(double ratingChange) {
        int colour = ContextCompat.getColor(context, R.color.noRatingChange);
        if (ratingChange > 0.0) {
            colour = ContextCompat.getColor(context, R.color.positiveRatingChange);
        } else if (ratingChange < 0.0) {
            colour = ContextCompat.getColor(context, R.color.negativeRatingChange);
        }

        return colour;
    }

    @Override
    public int getItemCount() {
        if (playersWithRatingChanges != null)
            return playersWithRatingChanges.size();
        else return 0;
    }

    class GameRecordDetailsSkillRatingViewHolder extends RecyclerView.ViewHolder {
        private final TextView nicknameTextView;
        private final LinearLayout ratingWrapper;

        private GameRecordDetailsSkillRatingViewHolder(View itemView) {
            super(itemView);

            nicknameTextView = itemView.findViewById(R.id.game_record_skill_rating_member_nickname);
            ratingWrapper = itemView.findViewById(R.id.game_record_skill_rating_wrapper);
        }
    }
}
