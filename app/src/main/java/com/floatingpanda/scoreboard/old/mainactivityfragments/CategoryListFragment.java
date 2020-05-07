package com.floatingpanda.scoreboard.old.mainactivityfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.old.mainactivityadapters.CategoryAdapter;

import java.util.ArrayList;

public class CategoryListFragment extends Fragment {

    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_list, container, false);

        final ArrayList<String> categories = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            if ((i % 3) == 0) {
                categories.add("Strategy");
            }
            else if ((i % 3) == 1) {
                categories.add("Gambling");
            }
            else {
                categories.add("Party");
            }
        }

        CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(), categories);

        ListView listView = rootView.findViewById(R.id.list);

        listView.setAdapter(categoryAdapter);

        return rootView;
    }
}
