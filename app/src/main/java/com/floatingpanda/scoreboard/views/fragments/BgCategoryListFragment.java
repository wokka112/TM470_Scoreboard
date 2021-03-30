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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.floatingpanda.scoreboard.views.activities.BgCategoryAddActivity;
import com.floatingpanda.scoreboard.views.activities.BgCategoryEditActivity;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.BgCategoryListAdapter;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.interfaces.ActivityAdapterInterface;
import com.floatingpanda.scoreboard.viewmodels.BgCategoryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * The view fragment showing the list of board game categories in the database, and providing the
 * means to add, edit or delete categories in the database.
 */
public class BgCategoryListFragment extends Fragment implements ActivityAdapterInterface {

    private final int ADD_CATEGORY_REQUEST_CODE = 1;
    private final int EDIT_CATEGORY_REQUEST_CODE = 2;

    private BgCategoryViewModel bgCategoryViewModel;
    TextView noBgCategoriesTextView;

    public BgCategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_with_fab, container, false);

        noBgCategoriesTextView = rootView.findViewById(R.id.no_element_exists_textview);
        noBgCategoriesTextView.setText(R.string.no_bg_categories);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        final BgCategoryListAdapter adapter = new BgCategoryListAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        bgCategoryViewModel = new ViewModelProvider(this).get(BgCategoryViewModel.class);

        bgCategoryViewModel.getAllBgCategories().observe(getViewLifecycleOwner(), new Observer<List<BgCategory>>() {
            @Override
            public void onChanged(@Nullable final List<BgCategory> bgCategories) {
                if (bgCategories == null
                        || bgCategories.isEmpty()) {
                    noBgCategoriesTextView.setVisibility(View.VISIBLE);
                } else {
                    noBgCategoriesTextView.setVisibility(View.GONE);
                }

                adapter.setBgCategories(bgCategories);
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
     * Starts the BgCategoryAddActivity activity.
     */
    private void startAddActivity() {
        Intent addBgCategoryIntent = new Intent(getContext(), BgCategoryAddActivity.class);
        startActivityForResult(addBgCategoryIntent, ADD_CATEGORY_REQUEST_CODE);
    }

    /**
     * Starts the BgCategoryEditActivity activity to edit object.
     *
     * object should be a BgCategory, and this BgCategory should exist in the database.
     *
     * Part of the ActivityAdapterInterface.
     * @param object an object of the BgCategory class that exists in the database
     */
    @Override
    public void startEditActivity(Object object) {
        BgCategory bgCategory = (BgCategory) object;

        Intent intent = new Intent(getContext(), BgCategoryEditActivity.class);
        intent.putExtra("BG_CATEGORY", bgCategory);
        startActivityForResult(intent, EDIT_CATEGORY_REQUEST_CODE);
    }

    /**
     * Displays a popup informing the user of what deleting a BgCategory results in and warning them
     * that it is irreversible. If the user presses the "Delete" button on the popup, then the
     * BgCategory passed as object will be deleted from the database. If the user presses the
     * "Cancel" button, then the popup will be dismissed and nothing will happen.
     *
     * object should be a BgCategory, and that BgCategory should already exist in the database.
     *
     * Part of the ActivityAdapterInterface.
     * @param object an object of the BgCategory class that exists in the database
     */
    @Override
    public void startDeleteActivity(Object object) {
        BgCategory bgCategory = (BgCategory) object;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Category?")
                .setMessage("Are you sure you want to delete " + bgCategory.getCategoryName() + "?\n" +
                        "Skill ratings for this category will be deleted as well.\n" +
                        "This is irreversible.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bgCategoryViewModel.deleteBgCategory(bgCategory);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .create()
                .show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_CATEGORY_REQUEST_CODE && resultCode == RESULT_OK) {
            BgCategory bgCategory = (BgCategory) data.getExtras().get(BgCategoryAddActivity.EXTRA_REPLY);
            bgCategoryViewModel.addBgCategory(bgCategory);
        } else if (requestCode == EDIT_CATEGORY_REQUEST_CODE && resultCode == RESULT_OK) {
            BgCategory bgCategory = (BgCategory) data.getExtras().get(BgCategoryEditActivity.EXTRA_REPLY);
            bgCategoryViewModel.editBgCategory(bgCategory);
        }
    }
}
