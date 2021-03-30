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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.GameRecordDetailsSkillRatingListAdapter;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.relations.PlayerTeamWithPlayersAndRatingChanges;
import com.floatingpanda.scoreboard.data.relations.PlayerWithRatingChanges;
import com.floatingpanda.scoreboard.viewmodels.GameRecordViewModel;

import java.util.List;

/**
 * A view fragment which shows a list of category skill ratings and their changes resulting from a
 * specific game record. The skill ratings are provided along with member details, showing whose
 * ratings each are. The ratings are grouped for each member and the members are sorted according to
 * their finishing positions in the game record. The skill ratings themselves are sorted in the
 * grouping alphabetically based on category name.
 */
public class GameRecordSkillRatingsListFragment extends Fragment {

    private GameRecord gameRecord;
    //Holds the shared game record that is used by the fragment. This record is set in the calling
    // activity - GameRecordActivity.
    private GameRecordViewModel gameRecordViewModel;

    public GameRecordSkillRatingsListFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);

        final GameRecordDetailsSkillRatingListAdapter adapter = new GameRecordDetailsSkillRatingListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        gameRecordViewModel = new ViewModelProvider(requireActivity()).get(GameRecordViewModel.class);
        gameRecord = gameRecordViewModel.getSharedGameRecord();

        gameRecordViewModel.getPlayerTeamsWithPlayersAndRatingChangesByRecordId(gameRecord.getId()).observe(getViewLifecycleOwner(), new Observer<List<PlayerTeamWithPlayersAndRatingChanges>>() {
            @Override
            public void onChanged(List<PlayerTeamWithPlayersAndRatingChanges> playerTeamsWithPlayersAndRatingChanges) {
                List<PlayerWithRatingChanges> playersWithRatingChanges =
                        gameRecordViewModel.extractPlayersWithRatingChanges(playerTeamsWithPlayersAndRatingChanges);

                adapter.setPlayersWithRatingChanges(playersWithRatingChanges);
            }
        });

        return rootView;
    }
}
