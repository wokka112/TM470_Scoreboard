package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.GroupCategorySkillRatingWithMemberDetailsView;
import com.floatingpanda.scoreboard.data.entities.GroupCategorySkillRating;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;

import java.util.List;

public class GroupCategorySkillRatingListAdapter extends RecyclerView.Adapter<GroupCategorySkillRatingListAdapter.GroupCategorySkillRatingViewHolder> {

    private final LayoutInflater inflater;
    private List<GroupCategorySkillRatingWithMemberDetailsView> groupCategorySkillRatingsWithMemberDetailsViews;

    public GroupCategorySkillRatingListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public GroupCategorySkillRatingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_group_skill_rating, parent, false);
        return new GroupCategorySkillRatingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupCategorySkillRatingViewHolder holder, int position) {
        if (groupCategorySkillRatingsWithMemberDetailsViews != null) {
            GroupCategorySkillRatingWithMemberDetailsView current = groupCategorySkillRatingsWithMemberDetailsViews.get(position);

            holder.nicknameTextView.setText(current.getNickname());
            holder.skillRatingTextView.setText(Double.toString(current.getSkillRating()));
            holder.gamesPlayedTextView.setText(Integer.toString(current.getGamesRated()));
        } else {

        }
    }

    public void setGroupCategorySkillRatingsWithMemberDetailsViews(List<GroupCategorySkillRatingWithMemberDetailsView> groupCategorySkillRatingsWithMemberDetailsViews) {
        this.groupCategorySkillRatingsWithMemberDetailsViews = groupCategorySkillRatingsWithMemberDetailsViews;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (groupCategorySkillRatingsWithMemberDetailsViews != null)
            return groupCategorySkillRatingsWithMemberDetailsViews.size();
        else return 0;
    }

    class GroupCategorySkillRatingViewHolder extends RecyclerView.ViewHolder {
        private final TextView nicknameTextView, skillRatingTextView, gamesPlayedTextView;

        private GroupCategorySkillRatingViewHolder(View itemView) {
            super(itemView);
            nicknameTextView = itemView.findViewById(R.id.group_skill_rating_nickname);
            skillRatingTextView = itemView.findViewById(R.id.group_skill_rating_rating);
            gamesPlayedTextView = itemView.findViewById(R.id.group_skill_rating_games_played);
        }
    }
}
