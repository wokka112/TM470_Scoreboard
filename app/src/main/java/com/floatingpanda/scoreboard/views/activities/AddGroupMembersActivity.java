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

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.recyclerview_adapters.AddGroupMembersListAdapter;
import com.floatingpanda.scoreboard.data.relations.GroupWithMembers;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.interfaces.SelectedMemberInterface;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddGroupMembersActivity extends AppCompatActivity implements SelectedMemberInterface {

    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";
    public final int ADD_MEMBER_REQUEST_CODE = 1;

    private MemberViewModel memberViewModel;
    private List<Member> groupMembers;
    private List<Member> nonGroupMembers;
    private List<Member> selectedMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_members);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button cancelButton, saveButton;
        TextView buttonTextView = findViewById(R.id.dialog_button_textview);
        ImageButton addMemberButton = findViewById(R.id.dialog_add_button);
        cancelButton = findViewById(R.id.add_group_members_button_cancel);
        saveButton = findViewById(R.id.add_group_members_button_save);

        RecyclerView recyclerView = findViewById(R.id.dialog_recycler_view);

        final AddGroupMembersListAdapter adapter = new AddGroupMembersListAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonTextView.setText("Create new member");

        groupMembers = (ArrayList<Member>) getIntent().getExtras().get("GROUP_MEMBERS");
        selectedMembers = new ArrayList<>();

        memberViewModel = new ViewModelProvider(this).get(MemberViewModel.class);

        memberViewModel.getAllMembers().observe(this, new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> memberList) {
                setNonGroupMembers(memberList);
                adapter.setMembers(nonGroupMembers);
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
                Intent replyIntent = new Intent();
                replyIntent.putParcelableArrayListExtra(EXTRA_REPLY, new ArrayList<Member>(selectedMembers));
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }

    private void setNonGroupMembers(List<Member> nonGroupMembers) {
        this.nonGroupMembers = new ArrayList<>(nonGroupMembers);
        this.nonGroupMembers.removeAll(groupMembers);
    }

    public void addSelectedMember(Member member) {
        if (!selectedMembers.contains(member)) {
            this.selectedMembers.add(member);
        }
    }

    public void removeSelectedMember(Member member) {
        selectedMembers.remove(member);
    }

    public void startAddMemberActivity() {
        Intent addMemberIntent = new Intent(this, MemberAddActivity.class);
        startActivityForResult(addMemberIntent, ADD_MEMBER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_MEMBER_REQUEST_CODE && resultCode == RESULT_OK) {
            Member member = (Member) data.getExtras().get(MemberAddActivity.EXTRA_REPLY);
            memberViewModel.addMember(member);
        }
    }

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
