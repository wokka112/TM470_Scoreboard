/*
ScoreBoard

Copyright © 2020 Adam Poole

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.floatingpanda.scoreboard.views.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.utils.PictureFormatter;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * View for adding members to the database.
 */
public class MemberAddActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_PICK_IMAGE = 2;
    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private String currentImgFilePath;

    private MemberViewModel memberViewModel;

    private EditText nicknameEditText, notesEditText;
    private ImageButton browseButton, cameraButton;
    private ImageView memberImageView;
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

        memberImageView = findViewById(R.id.memberadd_img);

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browsePictures();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
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
                String imgFilePath = currentImgFilePath;

                Member member = null;

                if (imgFilePath == null || imgFilePath.isEmpty()) {
                    member = new Member(nickname, notes);
                } else {
                    member = new Member(nickname, notes, imgFilePath);
                }
                
                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, member);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }

    private void browsePictures() {
        Intent browsePictureIntent = new Intent(this, FilePickerActivity.class);
        browsePictureIntent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                                .setCheckPermission(true)
                                .setShowImages(true)
                                .setShowVideos(false)
                                .setSingleChoiceMode(true)
                                .build());
        startActivityForResult(browsePictureIntent, REQUEST_PICK_IMAGE);
    }


    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("MemberAddAct", "Exception thrown when creating image file: " + ex);
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.floatingpanda.scoreboard", photoFile);
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "ScoreBoard_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentImgFilePath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentImgFilePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setMemberImg() {
        if (currentImgFilePath == null || currentImgFilePath.isEmpty()) {
            memberImageView.setImageResource(R.drawable.default_member_icon_hd);
            return;
        }

        File file = new File(currentImgFilePath);
        Uri uri = Uri.fromFile(file);
        Bitmap bitmap;
        try {
            bitmap = PictureFormatter.handleSamplingAndRotationBitmap(getApplicationContext(), uri);
            memberImageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.e("MemberAddAct", "Image file not found: " + e);
        } catch (IOException e) {
            Log.e("MemberAddAct", "IO Exception when finding image file: " + e);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Add new picture to gallery
            galleryAddPic();
            setMemberImg();
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            ArrayList<MediaFile> files = new ArrayList<>();

            try {
                files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
            } catch (NullPointerException e) {
                Log.e("MemberEditActivity.java", "Getting media files returned null pointer.");
            }

            if (files == null || files.isEmpty()) {
                Log.w("MemberEditActivity.java", "Files were null or empty");
            } else {
                currentImgFilePath = files.get(0).getPath();
                setMemberImg();
            }
        }
    }
}
