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

import com.floatingpanda.scoreboard.views.activities.MemberActivity;
import com.floatingpanda.scoreboard.views.activities.MemberAddActivity;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.MemberListAdapter;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A view fragment showing a list of all the members in the database. This also provides the means
 * to add, edit or delete members in the database.
 */
public class MemberListFragment extends Fragment implements DetailAdapterInterface {

    private final int ADD_MEMBER_REQUEST_CODE = 1;

    private MemberViewModel memberViewModel;
    private TextView noMembersTextView;

    public MemberListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_with_fab, container, false);

        noMembersTextView = rootView.findViewById(R.id.no_element_exists_textview);
        noMembersTextView.setText(R.string.no_members);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        final MemberListAdapter adapter = new MemberListAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        memberViewModel = new ViewModelProvider(this).get(MemberViewModel.class);

        memberViewModel.getAllMembers().observe(getViewLifecycleOwner(), new Observer<List<Member>>() {
            @Override
            public void onChanged(@Nullable final List<Member> members) {

                if(members == null
                        || members.isEmpty()) {
                    noMembersTextView.setVisibility(View.VISIBLE);
                } else {
                    noMembersTextView.setVisibility(View.GONE);
                }

                adapter.setMembers(members);
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
     * Starts the MemberActivity to view a member, passed as object, in more detail.
     *
     * object should be an object of the Member class, and the member should exist in the database.
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
     * Starts the MemberAddActivity activity to add a new member to the database.
     */
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
}

