package com.floatingpanda.scoreboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;

import java.util.List;

//TODO remove testing log warnings.

//TODO update layout for member so it reflects the edit/add activity, with large image at top followed by nickname and stuff below.
// or maybe have nickname at very top centre with image below and everything below that?

public class MemberActivity extends AppCompatActivity {

    private final int EDIT_MEMBER_REQUEST_CODE = 1;

    private MemberViewModel memberViewModel;
    private Member member;

    private TextView nicknameTextView, realNameTextView, notesTextView, groupsTextView, viewGroupsLink,
            bestGameTextView, worstGameTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        memberViewModel = new ViewModelProvider(this).get(MemberViewModel.class);

        nicknameTextView = findViewById(R.id.memberact_nickname_output);
        realNameTextView = findViewById(R.id.memberact_othername_output);
        notesTextView = findViewById(R.id.memberact_notes_output);
        //TODO implement groupcount in member and functionality to work it out.
        groupsTextView = findViewById(R.id.memberact_groups_output);
        //TODO add listener to link to a list of groups member is part of.
        // also change colour to be blue like a hyperlink
        viewGroupsLink = findViewById(R.id.memberact_view_groups);
        //TODO implement best game in member and functionality to work it out.
        bestGameTextView = findViewById(R.id.memberact_best_game_output);
        //TODO implement worst game in member and functionality to work it out.
        worstGameTextView = findViewById(R.id.memberact_worst_game_output);
        //TODO implement imgfilepath and functionality for it all
        imageView = findViewById(R.id.memberact_image);

        member = (Member) getIntent().getExtras().get("MEMBER");

        Log.w("MemberActivity.java", "Using member: " + member.getNickname());
        memberViewModel.getLiveDataMember(member).observe(MemberActivity.this, new Observer<Member>() {
            @Override
            public void onChanged(@Nullable final Member liveMember) {
                Log.w("MemberActivity.java", "Got liveMember: " + liveMember);
                setDetails(liveMember);
            }
        });

        Button editButton, deleteButton;

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
                startDeleteActivity(member);
            }
        });
    }

    private void setDetails(Member member) {
        if (member == null) {
            //TODO remove setting nickname to deleted before exiting?
            nicknameTextView.setText("Deleted");
            return;
        }

        Log.w("MemberActivity.java", "Setting details: " + member);
        nicknameTextView.setText(member.getNickname());
        realNameTextView.setText(member.getRealName());
        notesTextView.setText(member.getNotes());

        //TODO implement groupcount in member and functionality to work it out.
        groupsTextView.setText("TBA");

        //TODO implement best game in member and functionality to work it out.
        bestGameTextView.setText("TBA");

        //TODO implement worst game in member and functionality to work it out.
        worstGameTextView.setText("TBA");

        //TODO implement imgfilepath and functionality for it all
        imageView.setImageResource(R.drawable.ic_launcher_foreground);

        this.member = member;
    }

    //TODO change to Object and make an interface?
    private void startEditActivity(Member member) {
        Intent intent = new Intent(MemberActivity.this, MemberEditActivity.class);
        intent.putExtra("MEMBER", member);
        startActivityForResult(intent, EDIT_MEMBER_REQUEST_CODE);
    }

    private void editMember(Member member) {
        memberViewModel.editMember(member);
    }

    //TODO change to Object and make an interface??
    private void startDeleteActivity(Member member) {
        //TODO refactor this popup window into a method and find somewhere better to put it.
        AlertDialog.Builder builder = new AlertDialog.Builder(MemberActivity.this);
        builder.setTitle("Delete Member?")
                .setMessage("Are you sure you want to delete " + member.getNickname() + "?\n" +
                        "The member will be removed from winner lists, groups and game records, and " +
                        "their skill ratings will be deleted.\n" +
                        "This operation is irreversible.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMember(member);
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

    private void deleteMember(Member member) {
        memberViewModel.deleteMember(member);
        finish();
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
