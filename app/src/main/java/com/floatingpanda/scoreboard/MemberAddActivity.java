package com.floatingpanda.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BgCategoryRepository;
import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.data.MemberRepository;

//TODO put images into the browse and picture buttons

public class MemberAddActivity extends AppCompatActivity {

    //TODO maybe remove this EXTRA_REPLY thing and simply change to a string??
    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private MemberRepository memberRepository;

    //TODO remove cancelButton and replace with an up arrow?
    private EditText nicknameEditText, realNameEditText, notesEditText;
    private Button browseButton, cameraButton, cancelButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        memberRepository = new MemberRepository(getApplication());

        nicknameEditText = findViewById(R.id.memberadd_nickname_edittext);
        realNameEditText = findViewById(R.id.memberadd_realname_edittext);
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
                Toast.makeText(MemberAddActivity.this, "Cancel pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nicknameEditText.getText())) {
                    AlertDialogHelper.popupWarning("You must enter a nickname for the member.", MemberAddActivity.this);
                    return;
                }

                String nickname = nicknameEditText.getText().toString();

                if (memberRepository.contains(nickname)) {
                    AlertDialogHelper.popupWarning("A member with that nickname already exists. " +
                            "You must enter a unique nickname.", MemberAddActivity.this);
                    return;
                }

                String realName = realNameEditText.getText().toString();
                String notes = notesEditText.getText().toString();
                //TODO implement image taking/picking and filepath saving functionality
                String imgFilePath = "TBA";

                Member member = new Member(nickname, realName, notes, imgFilePath);

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, member);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }
}
