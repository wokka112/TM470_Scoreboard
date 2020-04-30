package com.floatingpanda.scoreboard.groupactivityfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.groupactivityadapters.RecordListAdapter;
import com.floatingpanda.scoreboard.system.GameRecord;
import com.floatingpanda.scoreboard.system.Member;

import java.util.ArrayList;

public class RecordListFragment extends Fragment {

    public RecordListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_list, container, false);

        final ArrayList<GameRecord> gameRecords = new ArrayList<>();
        ArrayList<Member> players = new ArrayList<>();

        players.add(new Member("Bill"));
        players.add(new Member("Bailey"));
        players.add(new Member("Emily"));
        players.add(new Member("Josephine"));
        players.add(new Member("Theo"));
        players.add(new Member("Becky"));

        GameRecord testGameRecord = new GameRecord(1, 240220, 2045, "Medieval", 4, 6, players);

        for (int i = 0; i < 10; i++) {
            gameRecords.add(testGameRecord);
        }

        RecordListAdapter recordListAdapter = new RecordListAdapter(getActivity(), gameRecords);

        ListView listView = rootView.findViewById(R.id.list);

        listView.setAdapter(recordListAdapter);

        /*
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

         */

        return rootView;
    }

}
