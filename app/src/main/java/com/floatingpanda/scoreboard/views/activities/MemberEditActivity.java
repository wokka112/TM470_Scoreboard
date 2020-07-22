package com.floatingpanda.scoreboard.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;

public class MemberEditActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private MemberViewModel memberViewModel;
    private Member member;

    private EditText nicknameEditText, notesEditText;
    private ImageButton browseButton, cameraButton;
    private Button cancelButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_member);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        memberViewModel = new ViewModelProvider(this).get(MemberViewModel.class);

        nicknameEditText = findViewById(R.id.memberadd_nickname_edittext);
        notesEditText = findViewById(R.id.memberadd_notes_edittext);

        member = (Member) getIntent().getExtras().get("MEMBER");
        setViews(member);

        browseButton = findViewById(R.id.memberadd_button_browse);
        cameraButton = findViewById(R.id.memberadd_button_camera);
        cancelButton = findViewById(R.id.memberadd_button_cancel);
        saveButton = findViewById(R.id.memberadd_button_save);

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MemberEditActivity.this, "Browse pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MemberEditActivity.this, "Camera pressed",
                        Toast.LENGTH_SHORT).show();
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
                if(!memberViewModel.editActivityInputsValid(member.getNickname(), nicknameEditText, false)) {
                    return;
                }

                member.setNickname(nicknameEditText.getText().toString());
                member.setNotes(notesEditText.getText().toString());
                //TODO implement image taking/picking and filepath saving functionality
                member.setImgFilePath("TBA");

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, member);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }

    private void setViews(Member member) {
        nicknameEditText.setText(member.getNickname());
        notesEditText.setText(member.getNotes());
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
