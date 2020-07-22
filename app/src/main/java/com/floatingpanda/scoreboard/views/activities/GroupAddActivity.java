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
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.viewmodels.GroupViewModel;

public class GroupAddActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private GroupViewModel groupViewModel;

    private EditText groupNameEditText, descriptionEditText, notesEditText;
    private ImageButton imgBrowseButton, imgCameraButton, bannerBrowseButton, bannerCameraButton;
    private Button cancelButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);

        groupNameEditText = findViewById(R.id.groupadd_name_edittext);
        descriptionEditText = findViewById(R.id.groupadd_description_edittext);
        notesEditText = findViewById(R.id.groupadd_notes_edittext);

        imgBrowseButton = findViewById(R.id.groupadd_button_img_browse);
        imgCameraButton = findViewById(R.id.groupadd_button_img_camera);
        bannerBrowseButton = findViewById(R.id.groupadd_button_banner_browse);
        bannerCameraButton = findViewById(R.id.groupadd_button_banner_camera);
        cancelButton = findViewById(R.id.groupadd_button_cancel);
        saveButton = findViewById(R.id.groupadd_button_save);

        imgBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupAddActivity.this, "Image browse pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        imgCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupAddActivity.this, "Image camera pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        bannerBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupAddActivity.this, "Banner browse pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        bannerCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupAddActivity.this, "Banner camera pressed",
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
                if(!groupViewModel.addActivityInputsValid(groupNameEditText, false)) {
                    return;
                }

                String groupName = groupNameEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String notes = notesEditText.getText().toString();
                //TODO implement image taking/picking and banner taking/picking and filepath saving functionality
                String imgFilePath = "TBA";
                String bannerFilePath = "TBA";

                Group group = new Group(groupName, description, notes, imgFilePath, bannerFilePath);

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, group);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
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
