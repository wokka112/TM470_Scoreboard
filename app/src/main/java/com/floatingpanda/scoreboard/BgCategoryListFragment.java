package com.floatingpanda.scoreboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.adapters.BgCategoryListAdapter;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.viewmodels.BgCategoryViewModel;

import java.util.List;

public class BgCategoryListFragment extends Fragment {

    private BgCategoryViewModel bgCategoryViewModel;

    public BgCategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_test, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        final BgCategoryListAdapter adapter = new BgCategoryListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        bgCategoryViewModel = new ViewModelProvider(this).get(BgCategoryViewModel.class);

        bgCategoryViewModel.getAllBgCategories().observe(getViewLifecycleOwner(), new Observer<List<BgCategory>>() {
            @Override
            public void onChanged(@Nullable final List<BgCategory> bgCategories) {
                adapter.setBgCategories(bgCategories);
            }
        });

        return rootView;
    }
}
