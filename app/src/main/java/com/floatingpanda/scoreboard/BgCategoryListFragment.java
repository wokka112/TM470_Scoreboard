package com.floatingpanda.scoreboard;

import android.app.AlertDialog;
import android.app.Dialog;
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

import com.floatingpanda.scoreboard.adapters.BgCategoryListAdapter;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.interfaces.ActivityAdapterInterface;
import com.floatingpanda.scoreboard.viewmodels.BgCategoryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class BgCategoryListFragment extends Fragment implements ActivityAdapterInterface {

    private final int ADD_CATEGORY_REQUEST_CODE = 1;
    private final int EDIT_CATEGORY_REQUEST_CODE = 2;

    private BgCategoryViewModel bgCategoryViewModel;

    public BgCategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recyclerview_test, container, false);

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

    public void startAddActivity() {
        Intent addBgCategoryIntent = new Intent(getContext(), BgCategoryAddActivity.class);
        startActivityForResult(addBgCategoryIntent, ADD_CATEGORY_REQUEST_CODE);
    }

    // Preconditions: - bgCategory does not exist in the database.
    // Postconditions: - bgCategory is added to the database.
    public void addBgCategory(BgCategory bgCategory) {
        bgCategoryViewModel.addBgCategory(bgCategory);
    }

    // Preconditions: - object is a BgCategory
    // Postconditions: - BgCategory edit view is started to edit object in the database.
    @Override
    public void startEditActivity(Object object) {
        //TODO look into whether this should be application context rather than just context
        BgCategory bgCategory;

        if (object.getClass() == BgCategory.class) {
            bgCategory = (BgCategory) object;

            Intent intent = new Intent(getContext(), BgCategoryEditActivity.class);
            intent.putExtra("BG_CATEGORY", bgCategory);
            startActivityForResult(intent, EDIT_CATEGORY_REQUEST_CODE);
        } else {
            Log.w("BgCatListFrag.java", "Did not receive category.");
            return;
        }
    }

    // Preconditions: - BgCategory with editedBgCategory's id (primary key) exists in the database.
    // Postconditions: - BgCategory with editedBgCategory's id (primary key) is updated in database
    //      to have editedBgCategory's details.
    public void editBgCategory(BgCategory editedBgCategory) {
        bgCategoryViewModel.editBgCategory(editedBgCategory);
    }

    // Preconditions: - object is a BgCategory.
    //                - the BgCategory object exists in the database.
    // Postconditions: - if user hits delete on the delete popup, the BgCategory is removed from the database.
    //                 - if user hits cancel on the delete popup, popup is dismissed and nothing happens.
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
                        deleteBgCategory(bgCategory);
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

    // Preconditions: - bgCategory exists in the database.
    // Postconditions: - bgCategory is removed from the database.
    //                 - assigned_categories with bgCategory in them are removed from the database.
    public void deleteBgCategory(BgCategory bgCategory) {
        bgCategoryViewModel.deleteBgCategory(bgCategory);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_CATEGORY_REQUEST_CODE && resultCode == RESULT_OK) {
            BgCategory newBgCategory = (BgCategory) data.getExtras().get(BgCategoryAddActivity.EXTRA_REPLY);
            addBgCategory(newBgCategory);
        } else if (requestCode == EDIT_CATEGORY_REQUEST_CODE && resultCode == RESULT_OK) {
            //TODO replace the String here with a set value, maybe make a static value somewhere for EXTRA_REPLY.
            BgCategory editedBgCategory = (BgCategory) data.getExtras().get("EDITED_BG_CATEGORY");
            editBgCategory(editedBgCategory);
        }
    }
}
