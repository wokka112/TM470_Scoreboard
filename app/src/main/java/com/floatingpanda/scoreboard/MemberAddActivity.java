package com.floatingpanda.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.data.MemberRepository;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;

//TODO put images into the browse and picture buttons

public class MemberAddActivity extends AppCompatActivity {

    //TODO maybe remove this EXTRA_REPLY thing and simply change to a string??
    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private MemberViewModel memberViewModel;

    //TODO remove cancelButton and replace with an up arrow?
    //TODO implement image taking/picking and filepath saving functionality
    private EditText nicknameEditText, notesEditText;
    private Button browseButton, cameraButton, cancelButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_member);

        memberViewModel = new ViewModelProvider(this).get(MemberViewModel.class);

        nicknameEditText = findViewById(R.id.memberadd_nickname_edittext);
        notesEditText = findViewById(R.id.memberadd_notes_edittext);

        browseButton = findViewById(R.id.memberadd_button_browse);
        cameraButton = findViewById(R.id.memberadd_button_camera);
        cancelButton = findViewById(R.id.memberadd_button_cancel);
        saveButton = findViewById(R.id.memberadd_button_save);

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MemberAddActivity.this, "Browse pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MemberAddActivity.this, "Camera pressed",
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
                String nickname = nicknameEditText.getText().toString();

                if (!memberViewModel.addActivityInputsValid(MemberAddActivity.this, nickname, false)) {
                    return;
                }

                String notes = notesEditText.getText().toString();
                //TODO implement image taking/picking and filepath saving functionality
                String imgFilePath = "TBA";

                Member member = new Member(nickname, notes, imgFilePath);

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, member);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }
}
