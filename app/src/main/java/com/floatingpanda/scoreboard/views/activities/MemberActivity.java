package com.floatingpanda.scoreboard.views.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.utils.DateStringCreator;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;

//TODO update layout for member so it reflects the edit/add activity, with large image at top followed by nickname and stuff below.
// or maybe have nickname at very top centre with image below and everything below that?

/**
 * View for viewing a member from the database.
 */
public class MemberActivity extends AppCompatActivity {

    private final int EDIT_MEMBER_REQUEST_CODE = 1;

    private MemberViewModel memberViewModel;
    private Member member;

    private TextView nicknameTextView, dateCreatedTextView, notesTextView, groupsTextView, viewGroupsLink;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        memberViewModel = new ViewModelProvider(this).get(MemberViewModel.class);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nicknameTextView = findViewById(R.id.memberact_nickname_output);
        dateCreatedTextView = findViewById(R.id.memberact_date_created_output);
        notesTextView = findViewById(R.id.memberact_notes_output);
        groupsTextView = findViewById(R.id.memberact_groups_output);
        //TODO add button with listener to link to a list of groups member is part of
        viewGroupsLink = findViewById(R.id.memberact_view_groups);
        //TODO implement imgfilepath and functionality for it all
        imageView = findViewById(R.id.memberact_image);

        // LiveData member is gotten instead so that details are automatically updated following member editing.
        member = (Member) getIntent().getExtras().get("MEMBER");
        memberViewModel.getLiveDataMember(member).observe(this, new Observer<Member>() {
            @Override
            public void onChanged(Member member) {
                setMember(member);
                setViews(member);
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

    private void setMember(Member member) {
        if (member == null) {
            return;
        }

        this.member = member;
    }
    /**
     * Sets the views in the MemberActivity to the details of member.
     *
     * If member is null, does nothing.
     * @param member a member
     */
    private void setViews(Member member) {
        if (member == null) {
            Log.w("MemberActivity.java", "In setViews() method. member is null. This is okay if a " +
                    "result of the delete method.");
            return;
        }

        nicknameTextView.setText(member.getNickname());

        DateStringCreator dateStringCreator = new DateStringCreator(member.getDateCreated());
        String dateString = dateStringCreator.getEnglishMonth3LetterString() + " " + dateStringCreator.getDayOfMonthString() + " " + dateStringCreator.getYearString();
        dateCreatedTextView.setText(dateString);

        notesTextView.setText(member.getNotes());

        int noOfGroupsMemberIsPartOf = memberViewModel.getNumberOfGroupsMemberIsPartOf(member.getId());
        groupsTextView.setText(Integer.toString(noOfGroupsMemberIsPartOf));

        imageView.setImageResource(R.drawable.ic_launcher_foreground);
    }

    /**
     * Starts the MemberEditActivity activity to edit member.
     * @param member a member
     */
    private void startEditActivity(Member member) {
        Intent intent = new Intent(MemberActivity.this, MemberEditActivity.class);
        intent.putExtra("MEMBER", member);
        startActivityForResult(intent, EDIT_MEMBER_REQUEST_CODE);
    }

    /**
     * Displays a popup informing the user of what deleting a Member results in and warning them
     * that it is irreversible. If the user presses the "Delete" button on the popup, then member
     * will be deleted from the database. If the user presses the "Cancel" button, then the popup
     * will be dismissed and nothing will happen.
     *
     * member should exist in the database.
     * @param member a member that exists in the database
     */
    private void startDeleteActivity(Member member) {
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

    /**
     * Deletes member from the database. This results in entries related to member being deleted
     * from the group members, skill ratings and group monthly scores tables. It also results in the
     * member's nickname in the game records table being turned to null.
     * @param member a member that exists in the database
     */
    private void deleteMember(Member member) {
        memberViewModel.deleteMember(member);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_MEMBER_REQUEST_CODE && resultCode == RESULT_OK) {
            Member editedMember = (Member) data.getExtras().get(MemberEditActivity.EXTRA_REPLY);
            memberViewModel.editMember(editedMember);
        }
    }

    /**
     * Sets the back arrow in the taskbar to go back to the previous activity.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
