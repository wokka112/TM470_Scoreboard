package com.floatingpanda.scoreboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    private BgCategoryRepository bgCategoryRepository;
    private EditText categoryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bg_category);

        BgCategory bgCategory = (BgCategory) getIntent().getExtras().get("BG_CATEGORY");
        BgCategory originalBgCategory = new BgCategory(bgCategory.getCategoryName());

        bgCategoryRepository = new BgCategoryRepository(getApplication());

        categoryEditText = findViewById(R.id.add_category_edittext);
        categoryEditText.setText(bgCategory.getCategoryName());

        final Button button = findViewById(R.id.add_category_save_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(categoryEditText.getText())) {
                    AlertDialogHelper.popupWarning("You must enter a name for the category.", BgCategoryEditActivity.this);
                    return;
                }

                bgCategory.setCategoryName(categoryEditText.getText().toString());
                Log.w("BgCatEditAct.java", "Includes category: " + bgCategoryRepository.contains(bgCategory));
                if ((bgCategory.getCategoryName().equals(originalBgCategory.getCategoryName()))
                        || bgCategoryRepository.contains(bgCategory)) {
                    AlertDialogHelper.popupWarning("You must enter a new, unique name for the category.", BgCategoryEditActivity.this);
                    return;
                }

                Intent replyIntent = new Intent();
                replyIntent.putExtra("EDITED_BG_CATEGORY", bgCategory);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }
}
