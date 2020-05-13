package com.floatingpanda.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;

import java.util.List;

//TODO update layout for member so it reflects the edit/add activity, with large image at top followed by nickname and stuff below.
// or maybe have nickname at very top centre with image below and everything below that?

public class MemberActivity extends AppCompatActivity {

    private final int EDIT_MEMBER_REQUEST_CODE = 1;

    private MemberViewModel memberViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        memberViewModel = new ViewModelProvider(this).get(MemberViewModel.class);

        Member member = (Member) getIntent().getExtras().get("MEMBER");

        TextView nicknameTextView, realNameTextView, notesTextView, groupsTextView, viewGroupsLink,
                bestGameTextView, worstGameTextView;

        Button editButton, deleteButton;

        ImageView imageView;

        nicknameTextView = findViewById(R.id.memberact_nickname_output);
        nicknameTextView.setText(member.getNickname());

        realNameTextView = findViewById(R.id.memberact_othername_output);
        realNameTextView.setText(member.getRealName());

        notesTextView = findViewById(R.id.memberact_notes_output);
        notesTextView.setText(member.getNotes());

        //TODO implement groupcount in member and functionality to work it out.
        groupsTextView = findViewById(R.id.memberact_groups_output);
        groupsTextView.setText("TBA");

        //TODO add listener to link to a list of groups member is part of.
        viewGroupsLink = findViewById(R.id.memberact_view_groups);

        //TODO implement best game in member and functionality to work it out.
        bestGameTextView = findViewById(R.id.memberact_best_game_output);
        bestGameTextView.setText("TBA");

        //TODO implement worst game in member and functionality to work it out.
        worstGameTextView = findViewById(R.id.memberact_worst_game_output);
        worstGameTextView.setText("TBA");

        //TODO implement imgfilepath and functionality for it all
        imageView = findViewById(R.id.memberact_image);
        imageView.setImageResource(R.drawable.ic_launcher_foreground);

        /*
        memberViewModel.getLiveDataMember(member).observe(getLifecycle(), new Observer<List<Member>>() {
            @Override
            public void onChanged(@Nullable final Member liveMember) {
                setDetails(liveMember);
            }
        });
        */
        /*
        Member member = (Member) getIntent().getExtras().get("MEMBER");

        TextView nicknameTextView, realNameTextView, notesTextView, groupsTextView, viewGroupsLink,
                bestGameTextView, worstGameTextView;

        Button editButton, deleteButton;

        ImageView imageView;

        nicknameTextView = findViewById(R.id.memberact_nickname_output);
        nicknameTextView.setText(member.getNickname());

        realNameTextView = findViewById(R.id.memberact_othername_output);
        realNameTextView.setText(member.getRealName());

        notesTextView = findViewById(R.id.memberact_notes_output);
        notesTextView.setText(member.getNotes());

        //TODO implement groupcount in member and functionality to work it out.
        groupsTextView = findViewById(R.id.memberact_groups_output);
        groupsTextView.setText("TBA");

        //TODO add listener to link to a list of groups member is part of.
        viewGroupsLink = findViewById(R.id.memberact_view_groups);

        //TODO implement best game in member and functionality to work it out.
        bestGameTextView = findViewById(R.id.memberact_best_game_output);
        bestGameTextView.setText("TBA");

        //TODO implement worst game in member and functionality to work it out.
        worstGameTextView = findViewById(R.id.memberact_worst_game_output);
        worstGameTextView.setText("TBA");

        //TODO implement imgfilepath and functionality for it all
        imageView = findViewById(R.id.memberact_image);
        imageView.setImageResource(R.drawable.ic_launcher_foreground);

         */

        editButton = findViewById(R.id.memberact_edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity(member);
            }
        });

        deleteButton = findViewById(R.id.memberact_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MemberActivity.this, "Delete pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startEditActivity(Member member) {
        Intent intent = new Intent(MemberActivity.this, MemberEditActivity.class);
        intent.putExtra("MEMBER", member);
        startActivityForResult(intent, EDIT_MEMBER_REQUEST_CODE);
    }

    private void editMember(Member member) {
        memberViewModel.editMember(member);
    }

    private void startDeleteActivity(Member member) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_MEMBER_REQUEST_CODE && resultCode == RESULT_OK) {
            Member editedMember = (Member) data.getExtras().get(MemberEditActivity.EXTRA_REPLY);
            editMember(editedMember);
        }
    }
}
