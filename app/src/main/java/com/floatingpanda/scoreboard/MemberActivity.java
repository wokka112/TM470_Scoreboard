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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;

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
                setViews(liveMember);
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

    // Postconditions: - The text and imageviews in this member activity are updated to match the member
    //                    passed as a parameter.
    /**
     * Sets the views in the MemberActivity to the details of the Member passed in as the parameter member.
     *
     * If member is null, does nothing.
     * @param member a member drawn from the database
     */
    private void setViews(Member member) {
        if (member == null) {
            //TODO remove log?
            Log.w("MemberActivity.java", "In setViews() method. member is null. This is okay if a " +
                    "result of the delete method.");
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

    // Preconditions: - member exists in database.
    // Postconditions: - Member edit activity is started to edit object in the database.
    private void startEditActivity(Member member) {
        Intent intent = new Intent(MemberActivity.this, MemberEditActivity.class);
        intent.putExtra("MEMBER", member);
        startActivityForResult(intent, EDIT_MEMBER_REQUEST_CODE);
    }

    // Preconditions: - A Member with a primary key id matching the member parameter's id exists in the database.
    // Postconditions: - The matching Member in the database is updated to be the same as the member passed to
    //                    this method.
    private void editMember(Member member) {
        memberViewModel.editMember(member);
    }

    // Preconditions: - member exists in the database.
    // Postconditions: - A popup is displayed warning the user of what deleting the member will result in
    //                      and offering the user the options to delete the member or cancel the deletion.
    //                 - if user hits delete on the delete popup, the Member is removed from the database.
    //                 - if user hits cancel on the delete popup, popup is dismissed and nothing happens.
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

    // Precondition: - member should exist in the database.
    // Postconditions: - member will no longer exist in the database.
    //                 - group_members tables with bgCategory in will have been deleted.
    //                 - category_skill_rating, group_category_skill_rating and board_game_skill_rating
    //                    tables for this member will have been deleted.
    //                 - monthly_scores, quarterly_scores and yearly_scores tables for this member will
    //                    have been deleted.
    //                 - game_records which this member is registered on will have the member turned into an
    //                    anonymous member (i.e. the record will no longer have a foreign key linking to the
    //                    member's table in the members tables).
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
