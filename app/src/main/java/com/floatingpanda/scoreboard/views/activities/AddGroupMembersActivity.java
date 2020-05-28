package com.floatingpanda.scoreboard.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.adapters.AddGroupMembersListAdapter;
import com.floatingpanda.scoreboard.data.GroupWithMembers;
import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.interfaces.SelectedMemberInterface;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//TODO make the groups list sort itself based on date.
public class AddGroupMembersActivity extends AppCompatActivity implements SelectedMemberInterface {

    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private GroupWithMembers groupWithMembers;
    private MemberViewModel memberViewModel;
    //TODO could make a comparator for members and simply sort them alphabetically in this list using that.
    // Alternatively could make an SQL query that sorts out what order the members ascend in or something.
    private List<Member> nonGroupMembers;
    private List<Member> selectedMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_members);

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

        groupWithMembers = (GroupWithMembers) getIntent().getExtras().get("GROUP_WITH_MEMBERS");
        selectedMembers = new ArrayList<>();

        memberViewModel = new ViewModelProvider(this).get(MemberViewModel.class);

        memberViewModel.getAllMembers().observe(this, new Observer<List<Member>>() {
            @Override
            public void onChanged(List<Member> memberList) {
                setNonGroupMembers(memberList);
                adapter.setMembers(nonGroupMembers);
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
        this.nonGroupMembers.removeAll(groupWithMembers.getMembers());
    }

    //TODO move selected members into the viewmodel and then pass that to the adapter?
    public void addSelectedMember(Member member) {
        if (!selectedMembers.contains(member)) {
            this.selectedMembers.add(member);
        }
    }

    public void removeSelectedMember(Member member) {
        selectedMembers.remove(member);
    }
}
