package com.floatingpanda.scoreboard.adapters;

import android.app.Activity;
import android.content.Context;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;

import java.util.List;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.MemberViewHolder> {

    private final LayoutInflater inflater;
    private List<Member> members;
    private DetailAdapterInterface listener;

    public MemberListAdapter(Context context, DetailAdapterInterface listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_member, parent, false);
        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {
        if (members != null) {
            Member current = members.get(position);
            holder.nicknameItemView.setText(current.getNickname());
            holder.groupsItemView.setText("7");
        } else {
            holder.nicknameItemView.setText("No nickname");
        }
    }

    public void setMembers(List<Member> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (members != null)
            return members.size();
        else return 0;
    }

    class MemberViewHolder extends RecyclerView.ViewHolder {
        private final TextView nicknameItemView, groupsItemView;

        private MemberViewHolder(View itemView) {
            super(itemView);
            nicknameItemView = itemView.findViewById(R.id.rmember_name_output);
            groupsItemView = itemView.findViewById(R.id.rmember_groups_output);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Member member = members.get(position);
                    listener.viewDetails(member);
                }
            });
        }
    }
}
