package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.SelectedMemberInterface;

import java.util.List;

public class AddGroupMembersListAdapter extends RecyclerView.Adapter<AddGroupMembersListAdapter.AddGroupMemberDialogViewHolder> {
    private final LayoutInflater inflater;
    private SelectedMemberInterface listener;
    private List<Member> members;

    public AddGroupMembersListAdapter(Context context, SelectedMemberInterface listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public AddGroupMembersListAdapter.AddGroupMemberDialogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_dialog_item, parent, false);
        return new AddGroupMembersListAdapter.AddGroupMemberDialogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddGroupMembersListAdapter.AddGroupMemberDialogViewHolder holder, int position) {
        if (members != null) {
            Member current = members.get(position);
            holder.nicknameItemView.setText(current.getNickname());
            holder.groupsItemView.setText("7");

            holder.checkBoxItemView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        listener.addSelectedMember(current);
                    } else {
                        listener.removeSelectedMember(current);
                    }
                }
            });
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

    class AddGroupMemberDialogViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxItemView;
        TextView nicknameItemView, groupsItemView;
        ImageView imageItemView;

        private AddGroupMemberDialogViewHolder(View itemView) {
            super(itemView);

            checkBoxItemView = itemView.findViewById(R.id.checkbox);
            nicknameItemView = itemView.findViewById(R.id.rmember_name_output);
            groupsItemView = itemView.findViewById(R.id.rmember_groups_output);
            imageItemView = itemView.findViewById(R.id.rmember_image);
        }
    }
}
