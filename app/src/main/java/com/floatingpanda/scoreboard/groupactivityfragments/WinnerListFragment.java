package com.floatingpanda.scoreboard.groupactivityfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.floatingpanda.scoreboard.GroupActivity;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.groupactivityadapters.WinnerListAdapter;
import com.floatingpanda.scoreboard.mainactivityadapters.GroupAdapter;

import java.util.ArrayList;

public class WinnerListFragment extends Fragment {

    public WinnerListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_list, container, false);

        final ArrayList<Integer> winnerLists = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            winnerLists.add(i);
        }

        WinnerListAdapter winnerListAdapter = new WinnerListAdapter(getActivity(), winnerLists);

        ListView listView = rootView.findViewById(R.id.list);

        listView.setAdapter(winnerListAdapter);

        return rootView;
    }

}
