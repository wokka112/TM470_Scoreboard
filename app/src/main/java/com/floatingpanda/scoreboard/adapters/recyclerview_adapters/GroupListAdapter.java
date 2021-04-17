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
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;

import java.util.List;

/**
 * Adapter that takes a list of groups and displays them.
 */
public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> {

    private final LayoutInflater inflater;
    private List<Group> groups;
    private DetailAdapterInterface listener;

    public GroupListAdapter(Context context, DetailAdapterInterface listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_group, parent, false);
        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        if (groups != null) {
            Group current = groups.get(position);

            //TODO remove when done implementing img picking/taking functionality
            Log.w("GroupListAdapt1", "Current group: " + current.getGroupName() + " File Path: " +
                    current.getImgFilePath());
            // Set group name
            holder.groupNameItemView.setText(current.getGroupName());

            // Try to create drawable from stored image file path
            Drawable drawable = Drawable.createFromPath(current.getImgFilePath());

            //TODO remove when done implementing img picking/taking functionality
            Log.w("GroupListAdapt2", "Current group: " + current.getGroupName() + " Drawable is null: " +
                    (drawable == null));
            // If drawable cannot be created (because image does not exist at path or is not set)
            if (drawable == null) {
                // Display default group image
                holder.groupImageView.setImageResource(R.drawable.default_group_icon_hd);
            } //Otherwise
            else {
                // Display the drawable image stored at file path
                holder.groupImageView.setImageDrawable(drawable);
            }
        } else {
            holder.groupNameItemView.setText("No name");
        }
    }

    /**
     * Sets the list of groups which the adapter will display.
     *
     * Must be called before the adapter will display anything.
     * @param groups
     */
    public void setGroups(List<Group> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (groups != null)
            return groups.size();
        else return 0;
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        private final ImageView groupImageView;
        private final TextView groupNameItemView;

        private GroupViewHolder(View itemView) {
            super(itemView);
            groupImageView = itemView.findViewById(R.id.group_image);
            groupNameItemView = itemView.findViewById(R.id.group_name_output);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Group group = groups.get(position);
                    listener.viewDetails(group);
                }
            });
        }
    }
}
