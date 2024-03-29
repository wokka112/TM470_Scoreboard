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

package com.floatingpanda.scoreboard.views.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;
import com.floatingpanda.scoreboard.views.activities.GroupMembersAddActivity;
import com.floatingpanda.scoreboard.views.activities.MemberActivity;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.GroupMemberListAdapter;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;
import com.floatingpanda.scoreboard.interfaces.RemoveGroupMemberInterface;
import com.floatingpanda.scoreboard.viewmodels.GroupMemberViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A view fragment showing a list of all the members in a specific group, and also providing the
 * means to remove members from the group and add members to the group.
 */
public class GroupMemberListFragment extends Fragment implements DetailAdapterInterface, RemoveGroupMemberInterface {

    private final int ADD_GROUP_MEMBERS_REQUEST_CODE = 1;

    //The group view model is used to get the groupId, which is supplied by the calling activity -
    // GroupActivity - to this and other fragments.
    private GroupViewModel groupViewModel;
    private GroupMemberViewModel groupMemberViewModel;
    private int groupId;
    private List<Member> groupMembers;
    private TextView noGroupMembersTextView;

    public GroupMemberListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_with_fab, container, false);

        noGroupMembersTextView = rootView.findViewById(R.id.no_element_exists_textview);
        noGroupMembersTextView.setText(R.string.no_group_members);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        final GroupMemberListAdapter adapter = new GroupMemberListAdapter(getActivity(), this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupViewModel = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);
        groupId = groupViewModel.getSharedGroupId();

        groupMemberViewModel = new ViewModelProvider(this).get(GroupMemberViewModel.class);
        groupMemberViewModel.getGroupMembersByGroupId(groupId).observe(getViewLifecycleOwner(), new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> members) {
                setGroupMembers(members);

                if (members == null
                        || members.isEmpty()) {
                    noGroupMembersTextView.setVisibility(View.VISIBLE);
                } else {
                    noGroupMembersTextView.setVisibility(View.GONE);
                }

                adapter.setGroupMembers(members);
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

    /**
     * Pops up a warning asking the user if they are sure they want to remove the member from the
     * group. If they are, then they can click "remove" and remove the member.
     * @param member
     */
    @Override
    public void removeGroupMember(Member member) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Remove Group Member?")
                .setMessage("Are you sure you want to remove " + member.getNickname() + " from the " +
                        "group?")
                .setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        groupMemberViewModel.removeGroupMember(groupId, member);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .create()
                .show();
    }

    /**
     * Starts the MemberActivity to view a member in more detail.
     *
     * object should be an object of the Member class, and the member should already exist in the
     * database.
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

    /**
     * Starts the activity to select and add members from the database that don't yet belong to the
     * group, to the group.
     */
    private void startAddGroupMember() {
        Intent intent = new Intent(getContext(), GroupMembersAddActivity.class);
        intent.putParcelableArrayListExtra("GROUP_MEMBERS", (ArrayList<Member>) groupMembers);
        startActivityForResult(intent, ADD_GROUP_MEMBERS_REQUEST_CODE);
    }

    private void setGroupMembers(List<Member> groupMembers) {
        this.groupMembers = groupMembers;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_GROUP_MEMBERS_REQUEST_CODE && resultCode == RESULT_OK) {
            List<Member> members = (ArrayList<Member>) data.getExtras().get(GroupMembersAddActivity.EXTRA_REPLY);
            groupMemberViewModel.addGroupMembers(groupId, members);
        }
    }
}
