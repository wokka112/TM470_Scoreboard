package com.floatingpanda.scoreboard.views.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Dao;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.utils.DateStringCreator;
import com.floatingpanda.scoreboard.views.activities.GroupEditActivity;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static android.app.Activity.RESULT_OK;

/**
 * A view fragment showing the details about a group in the database.
 */
public class GroupDetailsFragment extends Fragment {

    private final int EDIT_GROUP_REQUEST_CODE = 1;

    private Group group;
    private GroupViewModel groupViewModel;

    TextView nameTextView, dateCreatedTextView, gamesPlayedTextView, membersCountTextView, descriptionTextView,
            notesTextView;

    public GroupDetailsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_details, container, false);

        nameTextView = rootView.findViewById(R.id.group_fragment_name_output);
        dateCreatedTextView = rootView.findViewById(R.id.group_fragment_date_created_output);
        gamesPlayedTextView = rootView.findViewById(R.id.group_fragment_games_played_output);
        membersCountTextView = rootView.findViewById(R.id.group_fragment_member_count_output);
        descriptionTextView = rootView.findViewById(R.id.group_fragment_description_output);
        notesTextView = rootView.findViewById(R.id.group_fragment_notes_output);

        groupViewModel = new ViewModelProvider(requireActivity()).get(GroupViewModel.class);
        int groupId = groupViewModel.getSharedGroupId();

        groupViewModel.getLiveDataGroupById(groupId).observe(getViewLifecycleOwner(), new Observer<Group>() {
            @Override
            public void onChanged(Group group) {
                setGroup(group);
                setViews(group);
            }
        });

        final Button editButton, deleteButton, resetButton;

        editButton = rootView.findViewById(R.id.group_fragment_edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity(group);
            }
        });

        deleteButton = rootView.findViewById(R.id.group_fragment_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDeleteActivity(group);
            }
        });

        return rootView;
    }

    private void setGroup(Group group) {
        if (group == null) {
            return;
        }

        this.group = group;
    }

    private void setViews(Group group) {
        if (group == null) {
            return;
        }

        nameTextView.setText(group.getGroupName());

        DateStringCreator dateStringCreator = new DateStringCreator(group.getDateCreated());
        String dateString = dateStringCreator.getEnglishMonth3LetterString() + " " + dateStringCreator.getDayOfMonthString() + " " + dateStringCreator.getYearString();

        dateCreatedTextView.setText(dateString);

        int gamesPlayed = groupViewModel.getGamesPlayedByGroup(group.getId());
        gamesPlayedTextView.setText(Integer.toString(gamesPlayed));

        int noOfMembers = groupViewModel.getNoOfMembersInGroup(group.getId());
        membersCountTextView.setText(Integer.toString(noOfMembers));

        String description = group.getDescription();
        if (description.trim().isEmpty()) {
            description = getContext().getString(R.string.no_description);
        }
        descriptionTextView.setText(description);

        String notes = group.getNotes();
        if (notes.trim().isEmpty()) {
            notes = getContext().getString(R.string.no_notes);
        }
        notesTextView.setText(notes);
    }

    /**
     * Starts the edit activity to edit the Group, group.
     *
     * group should already exist in the database.
     * @param group
     */
    private void startEditActivity(Group group) {
        Intent intent = new Intent(getActivity(), GroupEditActivity.class);
        intent.putExtra("GROUP", group);
        startActivityForResult(intent, EDIT_GROUP_REQUEST_CODE);
    }

    /**
     * Pops up a warning informing the user of the consequences of deleting a group and providing
     * them with the option to proceed. If the user clicks to proceed (i.e. clicks delete) then the
     * group will be deleted from the database.
     *
     * group should already exist in the database.
     * @param group
     */
    private void startDeleteActivity(Group group) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Group?")
                .setMessage("Are you sure you want to delete " + group.getGroupName() + "?\n" +
                        "The group and all of its associated skill ratings, game records, and scores" +
                        "will be deleted permanently.\n" +
                        "This operation is irreversible.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGroup(group);
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

    /**
     * Deletes the Group, group, from the database, along with its associated skill rating, game
     * record and score tables.
     *
     * group should already exist in the database.
     * @param group
     */
    private void deleteGroup(Group group) {
        groupViewModel.deleteGroup(group);
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_GROUP_REQUEST_CODE && resultCode == RESULT_OK) {
            Group editedGroup = (Group) data.getExtras().get(GroupEditActivity.EXTRA_REPLY);
            groupViewModel.editGroup(editedGroup);
        }
    }
}
