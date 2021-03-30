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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.SelectedMemberInterface;

import java.util.List;

/**
 * Recyclerview adapter that displays a list of members to add to a group with checkboxes next to each.
 * The supplied object implementing the SelectedMemberInterface then has the relevant interface method
 * triggered in response to each box being checked or unchecked - that is, if a box is checked,
 * addSelectedMember() is called, and if a box is unchecked, removeSelectedMember() is called.
 * <p>
 * This adapter is very similar to the MemberListAdapter, except for the addition of checkboxes and
 * the SelectedMemberInterface to allow members to be selected from the list and used by the object
 * passed as the SelectedMemberInterface parameter.
 */
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
        View itemView = inflater.inflate(R.layout.recyclerview_checkbox_member_item, parent, false);
        return new AddGroupMembersListAdapter.AddGroupMemberDialogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AddGroupMembersListAdapter.AddGroupMemberDialogViewHolder holder, int position) {
        if (members != null) {
            Member current = members.get(position);
            holder.nicknameItemView.setText(current.getNickname());

            holder.checkBoxItemView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
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

    /**
     * Sets the list of members to display.
     * <p>
     * Must be called before adapter will display anything.
     *
     * @param members a list of members
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

    class AddGroupMemberDialogViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxItemView;
        TextView nicknameItemView;
        ImageView imageItemView;

        private AddGroupMemberDialogViewHolder(View itemView) {
            super(itemView);

            checkBoxItemView = itemView.findViewById(R.id.checkbox);
            nicknameItemView = itemView.findViewById(R.id.rmember_name_output);
            imageItemView = itemView.findViewById(R.id.rmember_image);
        }
    }
}
