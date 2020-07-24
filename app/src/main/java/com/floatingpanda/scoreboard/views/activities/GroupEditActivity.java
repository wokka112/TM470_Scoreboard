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

/**
 * View for editing groups in the database.
 */
public class GroupEditActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private GroupViewModel groupViewModel;
    private Group group;

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

        group = (Group) getIntent().getExtras().get("GROUP");
        setViews(group);

        imgBrowseButton = findViewById(R.id.groupadd_button_img_browse);
        imgCameraButton = findViewById(R.id.groupadd_button_img_camera);
        bannerBrowseButton = findViewById(R.id.groupadd_button_banner_browse);
        bannerCameraButton = findViewById(R.id.groupadd_button_banner_camera);
        cancelButton = findViewById(R.id.groupadd_button_cancel);
        saveButton = findViewById(R.id.groupadd_button_save);

        imgBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupEditActivity.this, "Image browse pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        imgCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupEditActivity.this, "Image camera pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        bannerBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupEditActivity.this, "Banner browse pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        bannerCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupEditActivity.this, "Banner camera pressed",
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
                if(!groupViewModel.editActivityInputsValid(group.getGroupName(), groupNameEditText, false)) {
                    return;
                }

                String groupName = groupNameEditText.getText().toString();
                group.setGroupName(groupName);
                group.setDescription(descriptionEditText.getText().toString());
                group.setNotes(notesEditText.getText().toString());
                //TODO implement image taking/picking and banner taking/picking and filepath saving functionality
                group.setImgFilePath("TBA");
                group.setBannerFilePath("TBA");

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, group);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }

    private void setViews(Group group) {
        groupNameEditText.setText(group.getGroupName());
        descriptionEditText.setText(group.getDescription());
        notesEditText.setText(group.getNotes());
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
