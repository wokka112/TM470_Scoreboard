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

public class BgCategoryEditActivity extends AppCompatActivity {

    private BgCategoryRepository bgCategoryRepository;
    private BgCategory originalBgCategory;
    private EditText categoryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bg_category);

        originalBgCategory = (BgCategory) getIntent().getExtras().get("BG_CATEGORY");
        bgCategoryRepository = new BgCategoryRepository(getApplication());

        categoryEditText = findViewById(R.id.add_category_edittext);
        categoryEditText.setText(originalBgCategory.getCategoryName());

        final Button button = findViewById(R.id.add_category_save_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(categoryEditText.getText())) {
                    //TODO popup a warning on the edittext saying you need to put in a name.
                    Log.w("BgCatEditAct.java", "POPUP: You must enter a name for the category.");
                    return;
                }

                String bgCategoryName = categoryEditText.getText().toString();
                BgCategory bgCategory = new BgCategory(bgCategoryName);

                Log.w("BgCatEditAct.java", "Includes category: " + bgCategoryRepository.contains(bgCategory));
                if ((bgCategoryName.equals(originalBgCategory.getCategoryName()))
                        || bgCategoryRepository.contains(bgCategory)) {
                    //TODO popup a warning on the edittext saying this name already exists.
                    Log.w("BgCatEditAct.java", "POPUP: You must enter a new, unique name for the category.");
                    return;
                }

                Intent replyIntent = new Intent();
                replyIntent.putExtra("ORIGINAL_BG_CATEGORY", originalBgCategory);
                replyIntent.putExtra("EDITED_BG_CATEGORY", bgCategory);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }
}
