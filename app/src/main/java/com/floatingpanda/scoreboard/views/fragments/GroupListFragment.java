package com.floatingpanda.scoreboard.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

/**
 * A view fragment showing a list of all the groups in the database. Also provides the means to add,
 * edit and delete groups in the database.
 */
public class GroupListFragment extends Fragment implements DetailAdapterInterface {

    private final int ADD_GROUP_REQUEST_CODE = 1;

    private GroupViewModel groupViewModel;
    private TextView noGroupsTextView;

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_with_fab, container, false);

        noGroupsTextView = rootView.findViewById(R.id.no_element_exists_textview);
        noGroupsTextView.setText(R.string.no_groups);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        final GroupListAdapter adapter = new GroupListAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);

        groupViewModel.getAllGroups().observe(getViewLifecycleOwner(), new Observer<List<Group>>() {
            @Override
            public void onChanged(@Nullable final List<Group> groups) {
                if (groups == null
                        || groups.isEmpty()) {
                    noGroupsTextView.setVisibility(View.VISIBLE);
                } else {
                    noGroupsTextView.setVisibility(View.GONE);
                }

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

    /**
     * Starts the add activity to add a new group to the database.
     */
    private void startAddActivity() {
        Intent addGroupIntent = new Intent(getContext(), GroupAddActivity.class);
        startActivityForResult(addGroupIntent, ADD_GROUP_REQUEST_CODE);
    }

    /**
     * Starts the Group activity to view the details of a specific group, passed as object.
     *
     * object should be a Group, and the Group should exist in the database.
     *
     * Part of the DetailAdapterInterface.
     * @param object
     */
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
