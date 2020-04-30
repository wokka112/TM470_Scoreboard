package com.floatingpanda.scoreboard.groupactivityfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.groupactivityadapters.WinnerListAdapter;

import java.util.ArrayList;

public class GroupDetailsFragment extends Fragment {

    public GroupDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_group_details, container, false);

        TextView name = rootView.findViewById(R.id.details_name_output);
        name.setText("Test Group");

        TextView creationDate = rootView.findViewById(R.id.details_date_output);
        creationDate.setText("February 24 1995");

        TextView gamesPlayed = rootView.findViewById(R.id.details_games_played_output);
        gamesPlayed.setText("216");

        TextView membersCount = rootView.findViewById(R.id.details_members_output);
        membersCount.setText("10");

        TextView description = rootView.findViewById(R.id.details_description_output);
        description.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam et risus lorem. " +
                "Maecenas faucibus, sem a lacinia suscipit, lacus velit consectetur nunc, vel " +
                "iaculis tortor nisl ut elit. Morbi ac dictum libero, vulputate congue nisi.");

        TextView notes = rootView.findViewById(R.id.details_notes_output);
        notes.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam et risus lorem. " +
                "Maecenas faucibus, sem a lacinia suscipit, lacus velit consectetur nunc, vel " +
                "iaculis tortor nisl ut elit. Morbi ac dictum libero, vulputate congue nisi.");

        return rootView;
    }
}
