package com.floatingpanda.scoreboard.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.GroupCategorySkillRatingListAdapter;
import com.floatingpanda.scoreboard.adapters.GroupListAdapter;
import com.floatingpanda.scoreboard.data.GroupCategorySkillRatingWithMemberDetailsView;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.viewmodels.GroupCategorySkillRatingViewModel;
import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class GroupSkillRatingListFragment extends Fragment {

    private Group group;
    private GroupCategorySkillRatingViewModel groupCategorySkillRatingViewModel;

    public GroupSkillRatingListFragment(Group group) {
        this.group = group;
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

        groupCategorySkillRatingViewModel = new ViewModelProvider(this).get(GroupCategorySkillRatingViewModel.class);

        groupCategorySkillRatingViewModel.getAllBgCategories().observe(getViewLifecycleOwner(), new Observer<List<BgCategory>>() {
            @Override
            public void onChanged(List<BgCategory> bgCategories) {
                ArrayAdapter<BgCategory> spinnerAdapter = new ArrayAdapter<BgCategory>(getContext(), android.R.layout.simple_spinner_item, bgCategories);
                spinner.setAdapter(spinnerAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        BgCategory selectedBgCategory = (BgCategory) parent.getItemAtPosition(position);
                        groupCategorySkillRatingViewModel.setGroupAndSkillRatingCategory(group.getId(), selectedBgCategory.getId());
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
                recyclerViewAdapter.setGroupCategorySkillRatingsWithMemberDetailsViews(groupCategorySkillRatingWithMemberDetailsViews);
            }
        });

        return rootView;
    }
}
