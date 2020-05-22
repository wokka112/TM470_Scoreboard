package com.floatingpanda.scoreboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.adapters.GroupListAdapter;
import com.floatingpanda.scoreboard.adapters.MemberListAdapter;
import com.floatingpanda.scoreboard.data.Group;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;
import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;

import java.util.List;

//TODO add in add functionality

public class GroupListFragment extends Fragment implements DetailAdapterInterface {

    private GroupViewModel groupViewModel;

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_test, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        final GroupListAdapter adapter = new GroupListAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);

        groupViewModel.getAllGroups().observe(getViewLifecycleOwner(), new Observer<List<Group>>() {
            @Override
            public void onChanged(@Nullable final List<Group> groups) {
                adapter.setGroups(groups);
            }
        });

        return rootView;
    }

    @Override
    public void viewDetails(Object object) {
        Group group = (Group) object;

        Intent detailsIntent = new Intent(getContext(), GroupActivity.class);
        detailsIntent.putExtra("GROUP", group);
        startActivity(detailsIntent);
    }
}
