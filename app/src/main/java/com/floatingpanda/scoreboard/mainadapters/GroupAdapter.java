package com.floatingpanda.scoreboard.mainadapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.system.Group;

import java.util.ArrayList;

public class GroupAdapter extends ArrayAdapter<Group> {

    public GroupAdapter(Activity context, ArrayList<Group> groups) {
        super(context, 0, groups);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.group_list_item, parent, false);
        }

        Group currentGroup = getItem(position);

        TextView groupNameOutput = listItemView.findViewById(R.id.group_name_output);
        groupNameOutput.setText(currentGroup.getName());

        TextView groupMembersOutput = listItemView.findViewById(R.id.group_members_output);
        groupMembersOutput.setText(Integer.toString(currentGroup.getMemberCount()));

        TextView groupRecordsOutput = listItemView.findViewById(R.id.group_records_output);
        groupRecordsOutput.setText(Integer.toString(currentGroup.getRecordsCount()));

        ImageView imageView = listItemView.findViewById(R.id.group_image);
        int imageResourceId = currentGroup.getImageResourceId();

        imageView.setImageResource(imageResourceId);

        return listItemView;
    }
}
