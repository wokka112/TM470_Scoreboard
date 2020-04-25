package com.floatingpanda.scoreboard;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.floatingpanda.scoreboard.system.BoardGame;
import com.floatingpanda.scoreboard.system.Member;

import java.util.List;

public class MemberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        Member member = (Member) getIntent().getExtras().get("MEMBER");

        TextView nicknameOutput, othernamesOutput, notesOutput, groupsOutput, viewGroups, bestGameOutput,
                worstGameOutput;

        Button editButton, deleteButton;

        ImageView imageView;

        nicknameOutput = findViewById(R.id.memberact_nickname_output);
        nicknameOutput.setText(member.getNickname());

        othernamesOutput = findViewById(R.id.memberact_othername_output);
        othernamesOutput.setText(member.getOtherNames());

        notesOutput = findViewById(R.id.memberact_notes_output);
        notesOutput.setText(member.getNotes());

        groupsOutput = findViewById(R.id.memberact_groups_output);
        groupsOutput.setText(Integer.toString(member.getGroupsCount()));

        //TODO add listener to link to a list of groups member is part of.
        viewGroups = findViewById(R.id.memberact_view_groups);

        bestGameOutput = findViewById(R.id.memberact_best_game_output);
        bestGameOutput.setText(member.getBestBoardGame());

        worstGameOutput = findViewById(R.id.memberact_worst_game_output);
        worstGameOutput.setText(member.getWorstBoardGame());

        imageView = findViewById(R.id.memberact_image);
        imageView.setImageResource(member.getImageResourceId());

        editButton = findViewById(R.id.memberact_edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MemberActivity.this, "Edit pressed",
                        Toast.LENGTH_SHORT).show();
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
}
