package com.floatingpanda.scoreboard;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.floatingpanda.scoreboard.system.Group;
import com.floatingpanda.scoreboard.system.Member;

import java.util.ArrayList;
import java.util.Map;

public class MemberAdapter extends ArrayAdapter<Member> {

    public MemberAdapter(Activity context, ArrayList<Member> members) {
        super(context, 0, members);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.member_list_item, parent, false);
        }

        Member currentMember = getItem(position);

        TextView groupNameOutput = listItemView.findViewById(R.id.member_name_output);
        groupNameOutput.setText(currentMember.getNickname());

        TextView groupMembersOutput = listItemView.findViewById(R.id.member_groups_output);
        groupMembersOutput.setText(Integer.toString(currentMember.getGroupsCount()));

        ImageView imageView = listItemView.findViewById(R.id.member_image);
        int imageResourceId = currentMember.getImageResourceId();

        imageView.setImageResource(imageResourceId);

        return listItemView;
    }

}