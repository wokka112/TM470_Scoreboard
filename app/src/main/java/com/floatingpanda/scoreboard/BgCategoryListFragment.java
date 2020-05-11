package com.floatingpanda.scoreboard;

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addBgCategoryIntent = new Intent(getContext(), BgCategoryAddActivity.class);
                startActivityForResult(addBgCategoryIntent, ADD_CATEGORY_REQUEST_CODE);
            }
        });

        return rootView;
    }

    //TODO add an add category fab or other button
    public void addBgCategory(BgCategory bgCategory) {
        bgCategoryViewModel.addBgCategory(bgCategory);
    }

    //TODO add pre-conditions and post-conditions? Pre-condition: object will be a BgCategory.
    // postcondition: update() will be called in viewmodel?
    @Override
    public void startEditActivity(Object object) {
        //TODO look into whether this should be application context rather than just context
        //TODO make category parcelable so I can pass the bgcategory rather than the name???
        BgCategory bgCategory;

        if (object.getClass() == BgCategory.class) {
            bgCategory = (BgCategory) object;

            Intent intent = new Intent(getContext(), BgCategoryEditActivity.class);
            intent.putExtra("ORIGINAL_BG_CATEGORY", bgCategory);
            startActivityForResult(intent, EDIT_CATEGORY_REQUEST_CODE);
        } else {
            Log.w("BgCatListFrag.java", "Did not receive category.");
            return;
        }
    }

    public void editBgCategory(BgCategory originalBgCategory, BgCategory editedBgCategory) {
        bgCategoryViewModel.editBgCategory(originalBgCategory, editedBgCategory);
    }

    @Override
    public void startDeleteActivity(Object object) {
        //TODO make a popup window and get a yes or no before deletion
    }

    public void deleteBgCategory(BgCategory bgCategory) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_CATEGORY_REQUEST_CODE && resultCode == RESULT_OK) {
            BgCategory newBgCategory = (BgCategory) data.getExtras().get(BgCategoryAddActivity.EXTRA_REPLY);
            addBgCategory(newBgCategory);
        } else if (requestCode == EDIT_CATEGORY_REQUEST_CODE && resultCode == RESULT_OK) {
            BgCategory originalBgCategory = (BgCategory) data.getExtras().get("ORIGINAL_BG_CATEGORY");
            BgCategory editedBgCategory = (BgCategory) data.getExtras().get("EDITED_BG_CATEGORY");
        }
    }

    //TODO implement on activity result properly.
    //TODO for add, simply call insert in viewmodel.
    //TODO for edit, call edit in viewmodel.
    //TODO for delete, call delete in viewmodel.
    /*
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EDIT_CATEGORY_REQUEST_CODE && resultCode == RESULT_OK) {
            String nickname = data.getStringExtra(NewMemberActivity.EXTRA_REPLY);
            String realName = data.getStringExtra(NewMemberActivity.EXTRA_REPLY);
            String notes = data.getStringExtra(NewMemberActivity.EXTRA_REPLY);
            Member member = new Member(nickname, realName, notes);

            mMemberViewModel.insert(member);
        } else {
            Toast.makeText( getApplicationContext(), "Not saved", Toast.LENGTH_SHORT).show();
        }
    }

     */
}
