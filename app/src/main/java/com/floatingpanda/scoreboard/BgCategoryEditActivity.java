package com.floatingpanda.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BgCategoryRepository;

//TODO add toolbar to activity layout (Maybe make a toolbar and simply <include> it in the activity layout for edit)
// then add up arrow to it.
//TODO add up arrow to toolbar and finish() activity from there. Then remove cancel button.
// alternatively, put finish() in onclicklistener for cancel button. Do same for add activity.

public class BgCategoryEditActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private BgCategoryRepository bgCategoryRepository;
    private BgCategory bgCategory;

    private EditText categoryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_bg_category);

        bgCategory = (BgCategory) getIntent().getExtras().get("BG_CATEGORY");

        bgCategoryRepository = new BgCategoryRepository(getApplication());

        categoryEditText = findViewById(R.id.add_category_edittext);

        setViews(bgCategory);

        final Button saveButton = findViewById(R.id.add_category_save_button);
        final Button cancelButton = findViewById(R.id.add_category_cancel_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!areInputsValid()) {
                    return;
                }

                String categoryName = categoryEditText.getText().toString();
                bgCategory.setCategoryName(categoryName);

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, bgCategory);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setViews(BgCategory bgCategory) {
        categoryEditText.setText(bgCategory.getCategoryName());
    }

    private boolean areInputsValid() {
        //TODO remove popup warnings and instead direct people to the edit text in error and
        // inform them what they need to do to fix it?
        if (TextUtils.isEmpty(categoryEditText.getText())) {
            AlertDialogHelper.popupWarning("You must enter a name for the category.", this);
            return false;
        }

        String categoryName = categoryEditText.getText().toString();

        if (categoryName.equals(bgCategory.getCategoryName())
                || bgCategoryRepository.containsCategoryName(categoryName)) {
            AlertDialogHelper.popupWarning("You must enter a new, unique name for the category.", BgCategoryEditActivity.this);
            return false;
        }

        return true;
    }
}
