package com.floatingpanda.scoreboard;

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

import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.Group;
import com.floatingpanda.scoreboard.viewmodels.BgCategoryViewModel;
import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;

import java.util.List;

//TODO will need to get a group with game records and members to be able to get member count and
// game record count. Alternatively, could simply call the DAOs to get the count of how many members
// are associated with the group...

//TODO add in group edit and delete functionality
//TODO maybe remove the game record and member counts from here? Could just put them at the top of
// the member and game record lists, respectively.

public class GroupDetailsFragment extends Fragment {

    private Group group;
    private GroupViewModel groupViewModel;

    TextView nameTextView, dateCreatedTextView, gamesPlayedTextView, membersCountTextView, descriptionTextView,
            notesTextView;

    public GroupDetailsFragment(Group group) {
        this.group = group;
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

        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);

        groupViewModel.getGroupById(group.getId()).observe(getViewLifecycleOwner(), new Observer<Group>() {
            @Override
            public void onChanged(Group group) {
                setGroup(group);
                setViews(group);
            }
        });

        final Button editButton, deleteButton, resetButton;

        return rootView;
    }

    private void setGroup(Group group) {
        this.group = group;
    }

    private void setViews(Group group) {
        nameTextView.setText(group.getGroupName());
        dateCreatedTextView.setText(Integer.toString(group.getDateCreated()));
        gamesPlayedTextView.setText(Integer.toString(group.getGamesPlayed()));
        membersCountTextView.setText(Integer.toString(group.getMembersCount()));
        descriptionTextView.setText(group.getDescription());
        notesTextView.setText(group.getNotes());
    }
}
