package com.floatingpanda.scoreboard.views.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class BgCategoryListFragment extends Fragment implements ActivityAdapterInterface {

    private final int ADD_CATEGORY_REQUEST_CODE = 1;
    private final int EDIT_CATEGORY_REQUEST_CODE = 2;

    private BgCategoryViewModel bgCategoryViewModel;

    //USED FOR TESTING PURPOSES
    public BgCategoryViewModel getBgCategoryViewModel() {
        bgCategoryViewModel = new ViewModelProvider(this).get(BgCategoryViewModel.class);
        return bgCategoryViewModel;
    }

    public BgCategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_layout_with_fab, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        final BgCategoryListAdapter adapter = new BgCategoryListAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        bgCategoryViewModel = new ViewModelProvider(this).get(BgCategoryViewModel.class);

        bgCategoryViewModel.getAllBgCategories().observe(getViewLifecycleOwner(), new Observer<List<BgCategory>>() {
            @Override
            public void onChanged(@Nullable final List<BgCategory> bgCategories) {
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

    // Postconditions: - the BgCategory add activity is started.
    /**
     * Starts the BgCategoryAddActivity activity.
     */
    private void startAddActivity() {
        Intent addBgCategoryIntent = new Intent(getContext(), BgCategoryAddActivity.class);
        startActivityForResult(addBgCategoryIntent, ADD_CATEGORY_REQUEST_CODE);
    }

    // Preconditions: - object is a BgCategory
    // Postconditions: - BgCategory edit activity is started to edit object in the database.
    /**
     * Starts the BgCategoryEditActivity activity to edit object.
     *
     * object should be a BgCategory.
     *
     * Part of the ActivityAdapterInterface.
     * @param object an object of the BgCategory class that exists in the database
     */
    @Override
    public void startEditActivity(Object object) {
        //TODO get rid of defensive programming and put assertions in?
        BgCategory bgCategory;

        if (object.getClass() == BgCategory.class) {
            bgCategory = (BgCategory) object;

            Intent intent = new Intent(getContext(), BgCategoryEditActivity.class);
            intent.putExtra("BG_CATEGORY", bgCategory);
            startActivityForResult(intent, EDIT_CATEGORY_REQUEST_CODE);
        } else {
            Log.w("BgCatListFrag.java", "startEditActivity() did not receive BgCategory object.");
            return;
        }
    }

    // Preconditions: - object is a BgCategory.
    //                - the BgCategory object exists in the database.
    // Postconditions: - A popup is displayed warning the user of what deleting the bgcategory will result in
    //                    and offering the user the options to delete the bgcategory or cancel the deletion.
    //                 - if user hits delete on the delete popup, the BgCategory is removed from the database.
    //                 - if user hits cancel on the delete popup, popup is dismissed and nothing happens.
    /**
     * Displays a popup informing the user of what deleting a BgCategory results in and warning them
     * that it is irreversible. If the user presses the "Delete" button on the popup, then the
     * BgCategory passed as object will be deleted from the database. If the user presses the
     * "Cancel" button, then the popup will be dismissed and nothing will happen.
     *
     * object should be a BgCategory.
     *
     * object should exist in the database.
     *
     * Part of the ActivityAdapterInterface.
     * @param object an object of the BgCategory class that exists in the database
     */
    @Override
    public void startDeleteActivity(Object object) {
        BgCategory bgCategory = (BgCategory) object;

        //TODO refactor this popup window into a method and find somewhere better to put it.
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
