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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.database_views.GroupCategorySkillRatingWithMemberDetailsView;

import java.util.List;

/**
 * Adapter that takes a list of category skill ratings with their member details (i.e. who's ratings
 * they are) and displays the information.
 *
 * Precondition: the list of group category skill ratings should be sorted into descending order
 * based on skill rating values.
 */
public class GroupCategorySkillRatingListAdapter extends RecyclerView.Adapter<GroupCategorySkillRatingListAdapter.GroupCategorySkillRatingViewHolder> {

    private Context context;
    private final LayoutInflater inflater;
    private List<GroupCategorySkillRatingWithMemberDetailsView> groupCategorySkillRatingsWithMemberDetailsViews;

    public GroupCategorySkillRatingListAdapter(Context context) {
        this.context = context;
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

            Double skillRating = current.getSkillRating();
            String skillRatingString = String.format("%.2f", skillRating);
            holder.skillRatingTextView.setText(skillRatingString);

            holder.gamesPlayedTextView.setText(Integer.toString(current.getGamesRated()));
        } else {

        }
    }

    /**
     * Sets the list of group category skill ratings with member detail database views.
     *
     * Must be called before adapter will display anything.
     * @param groupCategorySkillRatingsWithMemberDetailsViews
     */
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
