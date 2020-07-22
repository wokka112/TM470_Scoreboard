package com.floatingpanda.scoreboard.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.viewmodels.BgCategoryViewModel;

public class BgCategoryAddActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.floatingpanda.scoreboard.REPLY";

    private BgCategoryViewModel bgCategoryViewModel;
    private EditText categoryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_bg_category);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bgCategoryViewModel = new ViewModelProvider(this).get(BgCategoryViewModel.class);

        categoryEditText = findViewById(R.id.add_category_edittext);

        final Button saveButton = findViewById(R.id.add_category_save_button);
        final Button cancelButton = findViewById(R.id.add_category_cancel_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String categoryName = categoryEditText.getText().toString();
                if (!bgCategoryViewModel.addActivityInputsValid(BgCategoryAddActivity.this, categoryName, false)) {
                    return;
                }

                BgCategory bgCategory = new BgCategory(categoryName);

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
