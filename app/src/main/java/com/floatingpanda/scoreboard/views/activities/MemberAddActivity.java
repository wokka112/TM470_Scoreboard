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

/**
 * View for adding members to the database.
 */
public class MemberAddActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private MemberViewModel memberViewModel;

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
                if (!memberViewModel.addActivityInputsValid(nicknameEditText, false)) {
                    return;
                }

                String nickname = nicknameEditText.getText().toString();
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
