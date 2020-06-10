package com.floatingpanda.scoreboard.views.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.views.activities.AddGroupMembersActivity;
import com.floatingpanda.scoreboard.views.activities.MemberActivity;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.GroupMemberListAdapter;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.GroupWithMembers;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;
import com.floatingpanda.scoreboard.interfaces.RemoveGroupMemberInterface;
import com.floatingpanda.scoreboard.viewmodels.GroupMemberViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class GroupMemberListFragment extends Fragment implements DetailAdapterInterface, RemoveGroupMemberInterface {

    private final int ADD_GROUP_MEMBERS_REQUEST_CODE = 1;

    private GroupMemberViewModel groupMemberViewModel;
    private Group group;
    private GroupWithMembers groupWithMembers;
    private List<Member> allMembers;

    public GroupMemberListFragment(Group group) {
        this.group = group;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_main, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddGroupMember();
            }
        });

        return rootView;
    }

    @Override
    public void removeGroupMember(Member member) {
        //TODO move this elsewhere.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Remove Group Member?")
                .setMessage("Are you sure you want to remove " + member.getNickname() + " from the " +
                        "group?")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        groupMemberViewModel.removeGroupMember(group, member);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .create()
                .show();
    }

    // Preconditions: - object is an object of the Member class.
    //                - the Member object exists in the database.
    // Postconditions: - The MemberActivity is started to view the details of object.
    /**
     * Starts the MemberActivity to view object in more detail.
     *
     * object should be an object of the Member class.
     *
     * Part of the DetailAdapterInterface.
     * @param object a Member object
     */
    @Override
    public void viewDetails(Object object) {
        Member member = (Member) object;

        Intent detailsIntent = new Intent(getContext(), MemberActivity.class);
        detailsIntent.putExtra("MEMBER", member);
        startActivity(detailsIntent);
    }

    public void startAddGroupMember() {
        Intent intent = new Intent(getContext(), AddGroupMembersActivity.class);
        intent.putExtra("GROUP_WITH_MEMBERS", groupWithMembers);
        startActivityForResult(intent, ADD_GROUP_MEMBERS_REQUEST_CODE);
    }

    private void setGroupWithMembers(GroupWithMembers groupWithMembers) {
        if(groupWithMembers == null) {
            return;
        }

        Log.w("GroupMemberListFrg.java", "Set groupWithMembers: " + groupWithMembers);
        this.groupWithMembers = groupWithMembers;
        Log.w("GroupMemberListFrg.java", "groupWithMembers set: " + this.groupWithMembers);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_GROUP_MEMBERS_REQUEST_CODE && resultCode == RESULT_OK) {
            List<Member> members = (ArrayList<Member>) data.getExtras().get(AddGroupMembersActivity.EXTRA_REPLY);
            groupMemberViewModel.addGroupMembers(group, members);
        }
    }

    // Postconditions: - The Member add activity is started.
    /**
     * Starts the MemberAddActivity activity.
     */
    /*
    public void startAddActivity() {
        Intent addMemberIntent = new Intent(getContext(), MemberAddActivity.class);
        startActivityForResult(addMemberIntent, ADD_MEMBER_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_MEMBER_REQUEST_CODE && resultCode == RESULT_OK) {
            Member member = (Member) data.getExtras().get(MemberAddActivity.EXTRA_REPLY);
            memberViewModel.addMember(member);
        }
    }

     */

}
