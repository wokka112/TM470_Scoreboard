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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.TeamOfPlayers;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.GameRecordListAdapter;
import com.floatingpanda.scoreboard.data.relations.GameRecordWithPlayerTeamsAndPlayers;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;
import com.floatingpanda.scoreboard.viewmodels.GameRecordViewModel;
import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;
import com.floatingpanda.scoreboard.views.activities.GameRecordAddActivity;
import com.floatingpanda.scoreboard.views.activities.GameRecordActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * The view fragment showing the list of game records in the database, including the top 3 teams for
 * the record and the scores they attained. This also provides to means to add new game records to
 * the database.
 */
public class GameRecordListFragment extends Fragment implements DetailAdapterInterface {

    private final int ADD_GAME_RECORD_REQUEST_CODE = 1;

    private Group group;
    private GameRecordViewModel gameRecordViewModel;
    private GroupViewModel groupViewModel;
    private TextView noGameRecordsTextView;

    public GameRecordListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_with_fab, container, false);

        noGameRecordsTextView = rootView.findViewById(R.id.no_element_exists_textview);
        noGameRecordsTextView.setText(R.string.no_game_records);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        final GameRecordListAdapter adapter = new GameRecordListAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupViewModel = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);
        int groupId = groupViewModel.getSharedGroupId();
        groupViewModel.getLiveDataGroupById(groupId).observe(getViewLifecycleOwner(), new Observer<Group>() {
            @Override
            public void onChanged(Group group) {
                setGroup(group);
            }
        });

        gameRecordViewModel = new ViewModelProvider(this).get(GameRecordViewModel.class);
        gameRecordViewModel.initGameRecordWithPlayerTeamsAndPlayers(groupId);

        gameRecordViewModel.getGroupsGameRecordsWithTeamsAndPlayers().observe(getViewLifecycleOwner(), new Observer<List<GameRecordWithPlayerTeamsAndPlayers>>() {
            @Override
            public void onChanged(List<GameRecordWithPlayerTeamsAndPlayers> gameRecordsWithPlayerTeamsAndPlayers) {

                if (gameRecordsWithPlayerTeamsAndPlayers == null
                        || gameRecordsWithPlayerTeamsAndPlayers.isEmpty()) {
                    noGameRecordsTextView.setVisibility(View.VISIBLE);
                } else {
                    noGameRecordsTextView.setVisibility(View.GONE);
                }

                adapter.setGameRecordsWithPlayerTeamsAndPlayers(gameRecordsWithPlayerTeamsAndPlayers);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddGameRecordActivity();
            }
        });

        return rootView;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * Starts the add game record activity for creating and adding new game records to the database.
     *
     * If this method is called before every element can initialise (namely, before the current
     * group has been pulled from the database), then instead of starting the activity, a toast pops
     * up informing the user that data is still loading and to try again in a moment.
     */
    private void startAddGameRecordActivity() {
        if (group == null) {
            Toast.makeText(getActivity(), "Still loading data, try again in a moment.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getContext(), GameRecordAddActivity.class);
        intent.putExtra("GROUP", group);
        startActivityForResult(intent, ADD_GAME_RECORD_REQUEST_CODE);
    }

    /**
     * Starts the GameRecord activity to view the details of a particular game record, passed as
     * object.
     *
     * object should be a GameRecord, and the game record it represents should exist in the
     * database.
     *
     * Part of the DetailAdapterInterface.
     * @param object
     */
    @Override
    public void viewDetails(Object object) {
        GameRecordWithPlayerTeamsAndPlayers gameRecordWithPlayerTeamsAndPlayers = (GameRecordWithPlayerTeamsAndPlayers) object;

        Intent detailsIntent = new Intent(getContext(), GameRecordActivity.class);
        detailsIntent.putExtra("GAME_RECORD", gameRecordWithPlayerTeamsAndPlayers.getGameRecord());
        startActivity(detailsIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_GAME_RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            GameRecord gameRecord = (GameRecord) data.getExtras().get(GameRecordAddActivity.EXTRA_REPLY_GAME_RECORD);
            List<TeamOfPlayers> teamsOfPlayers = (ArrayList) data.getExtras().get(GameRecordAddActivity.EXTRA_REPLY_PLAYERS);

            gameRecordViewModel.addGameRecordAndPlayers(gameRecord, teamsOfPlayers);
        }
    }
}
