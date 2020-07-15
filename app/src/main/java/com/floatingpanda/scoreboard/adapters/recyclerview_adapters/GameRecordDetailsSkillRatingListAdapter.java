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

    private View createRatingView(PlayerSkillRatingChange playerSkillRatingChange) {
        View view = inflater.inflate(R.layout.recyclerview_item_game_record_skill_rating_wrapper_entry, null);
        TextView categoryNameTextView = view.findViewById(R.id.skill_rating_wrapper_entry_category);
        TextView oldRatingTextView = view.findViewById(R.id.skill_rating_wrapper_entry_old_rating);
        TextView ratingChangeTextView = view.findViewById(R.id.skill_rating_wrapper_entry_rating_change);
        TextView newRatingTextView = view.findViewById(R.id.skill_rating_wrapper_entry_new_rating);

        categoryNameTextView.setText(playerSkillRatingChange.getCategoryName());
        oldRatingTextView.setText(Double.toString(playerSkillRatingChange.getOldRating()));

        String ratingChangeString = "";
        if (playerSkillRatingChange.getRatingChange() > 0.0) {
            ratingChangeString += "+";
        }
        ratingChangeString += Double.toString(playerSkillRatingChange.getRatingChange());

        ratingChangeTextView.setText(ratingChangeString);
        newRatingTextView.setText(Double.toString(playerSkillRatingChange.get2DpRoundedNewRating()));

        int color = ContextCompat.getColor(context, R.color.noRatingChange);
        if (playerSkillRatingChange.getRatingChange() > 0.0) {
            color = ContextCompat.getColor(context, R.color.positiveRatingChange);
        } else if (playerSkillRatingChange.getRatingChange() < 0.0) {
            color = ContextCompat.getColor(context, R.color.negativeRatingChange);
        }

        ratingChangeTextView.setTextColor(color);
        newRatingTextView.setTextColor(color);

        return view;
    }

    public void setPlayersWithRatingChanges(List<PlayerWithRatingChanges> playersWithRatingChanges) {
        this.playersWithRatingChanges = playersWithRatingChanges;
        notifyDataSetChanged();
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
