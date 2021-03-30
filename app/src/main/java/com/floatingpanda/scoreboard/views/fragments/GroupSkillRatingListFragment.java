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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.GroupCategorySkillRatingListAdapter;
import com.floatingpanda.scoreboard.data.database_views.GroupCategorySkillRatingWithMemberDetailsView;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.viewmodels.GroupCategorySkillRatingViewModel;
import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;

import java.util.List;

/**
 * A view fragment showing a list of all the skill ratings for a specific category in a specific
 * group. The category can be changed using a spinner in the view to browse different ratings for
 * different categories in the group.
 */
public class GroupSkillRatingListFragment extends Fragment {

    //The group view model is used to get the groupId, which is supplied by the calling activity -
    // GroupActivity - to this and other fragments.
    private GroupViewModel groupViewModel;
    private GroupCategorySkillRatingViewModel groupCategorySkillRatingViewModel;

    public GroupSkillRatingListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_skill_ratings_list, container, false);

        Spinner spinner = rootView.findViewById(R.id.fragment_skill_rating_spinner);
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_skill_rating_recyclerview);

        final GroupCategorySkillRatingListAdapter recyclerViewAdapter = new GroupCategorySkillRatingListAdapter(getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupViewModel = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);
        int groupId = groupViewModel.getSharedGroupId();

        groupCategorySkillRatingViewModel = new ViewModelProvider(this).get(GroupCategorySkillRatingViewModel.class);

        TextView noRatingsTextView = rootView.findViewById(R.id.fragment_skill_rating_no_ratings_textview);

        groupCategorySkillRatingViewModel.getAllBgCategories().observe(getViewLifecycleOwner(), new Observer<List<BgCategory>>() {
            @Override
            public void onChanged(List<BgCategory> bgCategories) {
                if (bgCategories == null
                        || bgCategories.isEmpty()) {
                    noRatingsTextView.setText(R.string.no_skill_ratings_reason_no_categories);
                    noRatingsTextView.setVisibility(View.VISIBLE);
                } else {
                    noRatingsTextView.setVisibility(View.GONE);
                }

                //TODO make work with R.layout.textview_spinner_item to have a custom style similar to rest of text on the page
                ArrayAdapter<BgCategory> spinnerAdapter = new ArrayAdapter<BgCategory>(getContext(), android.R.layout.simple_spinner_item, bgCategories);
                spinner.setAdapter(spinnerAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        BgCategory selectedBgCategory = (BgCategory) parent.getItemAtPosition(position);
                        groupCategorySkillRatingViewModel.setGroupAndSkillRatingCategory(groupId, selectedBgCategory.getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        groupCategorySkillRatingViewModel.getGroupCategorySkillRatingsWithMemberDetails().observe(getViewLifecycleOwner(), new Observer<List<GroupCategorySkillRatingWithMemberDetailsView>>() {
            @Override
            public void onChanged(List<GroupCategorySkillRatingWithMemberDetailsView> groupCategorySkillRatingWithMemberDetailsViews) {
                if (groupCategorySkillRatingWithMemberDetailsViews == null
                        || groupCategorySkillRatingWithMemberDetailsViews.isEmpty()) {
                    noRatingsTextView.setVisibility(View.VISIBLE);
                } else {
                    noRatingsTextView.setVisibility(View.GONE);
                }

                recyclerViewAdapter.setGroupCategorySkillRatingsWithMemberDetailsViews(groupCategorySkillRatingWithMemberDetailsViews);
            }
        });

        return rootView;
    }
}
