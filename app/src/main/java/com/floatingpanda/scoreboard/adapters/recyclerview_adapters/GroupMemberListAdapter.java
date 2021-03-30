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
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;
import com.floatingpanda.scoreboard.interfaces.RemoveGroupMemberInterface;

import java.util.List;

/**
 * Adapter that displays a list of group members and provides buttons to remove said group members
 * from the group.
 */
public class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListAdapter.GroupMemberViewHolder> {

    private final LayoutInflater inflater;
    private List<Member> groupMembers;
    private RemoveGroupMemberInterface removeGroupMemberListener;
    private DetailAdapterInterface detailsListener;

    public GroupMemberListAdapter(Context context, DetailAdapterInterface detailsListener, RemoveGroupMemberInterface removeGroupMemberListener) {
        inflater = LayoutInflater.from(context);
        this.detailsListener = detailsListener;
        this.removeGroupMemberListener = removeGroupMemberListener;
    }

    @Override
    public GroupMemberListAdapter.GroupMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_member, parent, false);
        return new GroupMemberListAdapter.GroupMemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupMemberListAdapter.GroupMemberViewHolder holder, int position) {
        if (groupMembers != null) {
            Member current = groupMembers.get(position);
            holder.nicknameItemView.setText(current.getNickname());
            holder.removeButton.setVisibility(View.VISIBLE);

            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    Member member = groupMembers.get(position);
                    removeGroupMemberListener.removeGroupMember(member);
                }
            });
        } else {
            holder.nicknameItemView.setText("No nickname");
        }
    }

    /**
     * Sets the list of group members that the adapter will display.
     *
     * Must be called before the adapter will display anything.
     * @param groupMembers
     */
    public void setGroupMembers(List<Member> groupMembers) {
        this.groupMembers = groupMembers;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (groupMembers != null)
            return groupMembers.size();
        else return 0;
    }

    class GroupMemberViewHolder extends RecyclerView.ViewHolder {
        private final TextView nicknameItemView;
        private final ImageButton removeButton;

        private GroupMemberViewHolder(View itemView) {
            super(itemView);
            nicknameItemView = itemView.findViewById(R.id.rmember_name_output);
            removeButton = itemView.findViewById(R.id.rmember_group_member_remove_button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Member member = groupMembers.get(position);
                    detailsListener.viewDetails(member);
                }
            });
        }
    }
}
