package com.floatingpanda.scoreboard.adapters.recyclerview_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;

import java.util.List;

/**
 * Adapter that displays a list of members.
 */
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
        } else {
            holder.nicknameItemView.setText("No nickname");
        }
    }

    /**
     * Sets the list of members the adapter will display.
     *
     * Must be called before the adapter will display anything.
     * @param members
     */
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
        private final TextView nicknameItemView;

        private MemberViewHolder(View itemView) {
            super(itemView);
            nicknameItemView = itemView.findViewById(R.id.rmember_name_output);

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
