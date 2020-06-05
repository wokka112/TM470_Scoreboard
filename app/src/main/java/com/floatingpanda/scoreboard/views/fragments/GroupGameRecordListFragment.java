package com.floatingpanda.scoreboard.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.Group;
import com.floatingpanda.scoreboard.views.activities.AddGameRecordActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GroupGameRecordListFragment extends Fragment {

    private final int ADD_GAME_RECORD_REQUEST_CODE = 1;

    private Group group;

    public GroupGameRecordListFragment(Group group) {
        this.group = group;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_main, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        //TODO implement basic functionality using preset test game records.
        //TODO once basic functioanlity is in, get in basic game record creation functionality
        // - adding a game record with players, teams and positions, but no scores or skill ratings yet.
        //TODO once that's done, start on the mediator
        // - first of all sort out the score calculations as that's the simplest thing.
        // - then move on to skill rating as that's much more complex to deal with.

        /*
        final GroupMemberListAdapter adapter = new GroupMemberListAdapter(getActivity(), this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupMemberViewModel = new ViewModelProvider(this).get(GroupMemberViewModel.class);
        groupMemberViewModel.initGroupWithMembers(group.getId());

        groupMemberViewModel.getGroupWithMembers().observe(getViewLifecycleOwner(), new Observer<GroupWithMembers>() {
            @Override
            public void onChanged(GroupWithMembers observedGroupWithMembers) {
                Log.w("GroupMemberListFrg.java", "Got groupWithMembers: " + groupWithMembers);
                setGroupWithMembers(observedGroupWithMembers);
                adapter.setGroupMembers(groupWithMembers.getMembers());
            }
        });
        */

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddGameRecord();
            }
        });

        return rootView;
    }

    private void startAddGameRecord() {
        Intent intent = new Intent(getContext(), AddGameRecordActivity.class);
        startActivityForResult(intent, ADD_GAME_RECORD_REQUEST_CODE);
    }
}
