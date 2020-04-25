package com.floatingpanda.scoreboard.mainfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.floatingpanda.scoreboard.GroupAdapter;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.system.Group;

import java.util.ArrayList;

public class GroupListFragment extends Fragment {

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_list, container, false);

        final ArrayList<Group> groups = new ArrayList<>();

        groups.add(new Group("The Monday Knights"));
        groups.add(new Group("Test"));
        groups.add(new Group("Theo's Group"));
        groups.add(new Group("Ragnarok"));
        groups.add(new Group("Asbjorn"));
        groups.add(new Group("Kjalten"));
        groups.add(new Group("God Is Great"));
        groups.add(new Group("Jehovah-Shamah"));
        groups.add(new Group("Woop woop"));

        GroupAdapter groupAdapter = new GroupAdapter(getActivity(), groups);

        ListView listView = rootView.findViewById(R.id.list);

        listView.setAdapter(groupAdapter);

        return rootView;
    }

}
