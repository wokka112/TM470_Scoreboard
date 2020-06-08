package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;

import java.util.List;

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
            holder.groupNameItemView.setText(current.getGroupName());
            holder.membersItemView.setText("7");
            holder.recordsItemView.setText("7");
        } else {
            holder.groupNameItemView.setText("No name");
        }
    }

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
        private final TextView groupNameItemView, membersItemView, recordsItemView;

        private GroupViewHolder(View itemView) {
            super(itemView);
            groupNameItemView = itemView.findViewById(R.id.group_name_output);
            membersItemView = itemView.findViewById(R.id.group_members_output);
            recordsItemView = itemView.findViewById(R.id.group_records_output);

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
