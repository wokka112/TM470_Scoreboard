package com.floatingpanda.scoreboard.old.mainactivityfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.floatingpanda.scoreboard.views.activities.MemberActivity;
import com.floatingpanda.scoreboard.old.mainactivityadapters.MemberAdapter;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.old.system.Member;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MemberListFragment extends Fragment {

    public MemberListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_and_fab, container, false);

        final ArrayList<Member> members = new ArrayList<>();

        members.add(new Member("Toaster"));
        members.add(new Member("Eggs"));
        members.add(new Member("Bill"));
        members.add(new Member("Jenkins"));
        members.add(new Member("Fat Tony"));
        members.add(new Member("Twit"));
        members.add(new Member("Twigs"));
        members.add(new Member("Fen"));
        members.add(new Member("Ragnar"));
        members.add(new Member("Bjorn"));
        members.add(new Member("Aethelwold"));
        members.add(new Member("Atlas"));

        MemberAdapter memberAdapter = new MemberAdapter(getActivity(), members);

        ListView listView = rootView.findViewById(R.id.list);

        listView.setAdapter(memberAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent memberIntent = new Intent(getContext(), MemberActivity.class);
                //TODO change this to be primary key for member database?
                memberIntent.putExtra("MEMBER", members.get(position));
                startActivity(memberIntent);
            }
        });

        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return rootView;
    }
}
