package com.floatingpanda.scoreboard.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.OnConflictStrategy;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.AddGroupMembersListAdapter;
import com.floatingpanda.scoreboard.data.relations.GroupWithMembers;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.SelectedMemberInterface;
import com.floatingpanda.scoreboard.viewmodels.GroupMemberAddViewModel;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * View for adding group members to a group. Only members that don't already belong to the group
 * are shown in this view.
 */
public class GroupMembersAddActivity extends AppCompatActivity implements SelectedMemberInterface {

    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";
    public final int ADD_MEMBER_REQUEST_CODE = 1;

    private GroupMemberAddViewModel groupMemberAddViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_members);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button cancelButton, saveButton;
        ImageButton addMemberButton = findViewById(R.id.dialog_add_button);
        cancelButton = findViewById(R.id.add_group_members_button_cancel);
        saveButton = findViewById(R.id.add_group_members_button_save);

        RecyclerView recyclerView = findViewById(R.id.dialog_recycler_view);

        final AddGroupMembersListAdapter adapter = new AddGroupMembersListAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groupMemberAddViewModel = new ViewModelProvider(this).get(GroupMemberAddViewModel.class);

        groupMemberAddViewModel.setGroupMembers((ArrayList<Member>) getIntent().getExtras().get("GROUP_MEMBERS"));
        groupMemberAddViewModel.getAllMembersLiveData().observe(this, new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> members) {
                groupMemberAddViewModel.setAllMembers(members);
                adapter.setMembers(groupMemberAddViewModel.getNonGroupMembers());
            }
        });

        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddMemberActivity();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Member> selectedMembers = groupMemberAddViewModel.getSelectedMembers();

                Intent replyIntent = new Intent();
                replyIntent.putParcelableArrayListExtra(EXTRA_REPLY, new ArrayList<Member>(selectedMembers));
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }

    /**
     * Adds the member to a list of selected members, which is later used to add members to the
     * group in a batch.
     * @param member
     */
    @Override
    public void addSelectedMember(Member member) {
        groupMemberAddViewModel.addSelectedMember(member);
    }

    /**
     * Removes the member from a list of selected members.
     * @param member
     */
    @Override
    public void removeSelectedMember(Member member) {
        groupMemberAddViewModel.removeSelectedMember(member);
    }

    /**
     * Starts the activity to add a member to the database.
     */
    public void startAddMemberActivity() {
        Intent addMemberIntent = new Intent(this, MemberAddActivity.class);
        startActivityForResult(addMemberIntent, ADD_MEMBER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_MEMBER_REQUEST_CODE && resultCode == RESULT_OK) {
            Member member = (Member) data.getExtras().get(MemberAddActivity.EXTRA_REPLY);
            groupMemberAddViewModel.addMemberToDatabase(member);
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
