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

import java.util.List;

//TODO add toolbar to activity layout (Maybe make a toolbar and simply <include> it in the activity layout for add)
// then edit up arrow to it.
//TODO add up arrow to toolbar and finish() activity from there. Then remove cancel button.
// alternatively, put finish() in onclicklistener for cancel button. Do same for edit activity.

public class BgCategoryAddActivity extends AppCompatActivity {

    //TODO maybe remove this EXTRA_REPLY thing and simply change to a string??
    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private BgCategoryRepository bgCategoryRepository;
    private EditText categoryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bg_category);
        categoryEditText = findViewById(R.id.add_category_edittext);
        bgCategoryRepository = new BgCategoryRepository(getApplication());

        final Button button = findViewById(R.id.add_category_save_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(categoryEditText.getText())) {
                    //TODO popup a warning on the edittext saying you need to put in a name.
                    Log.w("BgCatAddAct.java", "POPUP: You must enter a name for the new category.");
                    return;
                }

                String bgCategoryName = categoryEditText.getText().toString();
                BgCategory bgCategory = new BgCategory(bgCategoryName);


                Log.w("BgCatAddAct.java", "Includes category: " + bgCategoryRepository.contains(bgCategory));
                if (bgCategoryRepository.contains(bgCategory)) {
                    //TODO popup a warning on the edittext saying this name already exists.
                    Log.w("BgCatAddAct.java", "POPUP: This category already exists. You must enter a " +
                            "unique category name.");
                    return;
                }

                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, bgCategory);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }
}
