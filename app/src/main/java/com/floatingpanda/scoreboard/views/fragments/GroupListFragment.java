package com.floatingpanda.scoreboard.views.fragments;

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

import com.floatingpanda.scoreboard.views.activities.GroupActivity;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.GroupListAdapter;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;
import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;
import com.floatingpanda.scoreboard.views.activities.GroupAddActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.app.Activity.RESULT_OK;

//TODO add in add functionality

public class GroupListFragment extends Fragment implements DetailAdapterInterface {

    private final int ADD_GROUP_REQUEST_CODE = 1;

    private GroupViewModel groupViewModel;

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_main, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddActivity();
            }
        });

        return rootView;
    }

    private void startAddActivity() {
        Intent addMemberIntent = new Intent(getContext(), GroupAddActivity.class);
        startActivityForResult(addMemberIntent, ADD_GROUP_REQUEST_CODE);
    }

    @Override
    public void viewDetails(Object object) {
        Group group = (Group) object;

        Intent detailsIntent = new Intent(getContext(), GroupActivity.class);
        detailsIntent.putExtra("GROUP", group);
        startActivity(detailsIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_GROUP_REQUEST_CODE && resultCode == RESULT_OK) {
            Group group = (Group) data.getExtras().get(GroupAddActivity.EXTRA_REPLY);
            groupViewModel.addGroup(group);
        }
    }
}
