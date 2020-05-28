package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;
import com.floatingpanda.scoreboard.interfaces.RemoveGroupMemberInterface;

import java.util.List;

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
            holder.groupsItemView.setText("7");
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
        private final TextView nicknameItemView, groupsItemView;
        private final ImageButton removeButton;

        private GroupMemberViewHolder(View itemView) {
            super(itemView);
            nicknameItemView = itemView.findViewById(R.id.rmember_name_output);
            groupsItemView = itemView.findViewById(R.id.rmember_groups_output);
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
