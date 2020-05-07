package com.floatingpanda.scoreboard.old.mainactivityadapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.floatingpanda.scoreboard.R;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<String> {

    public CategoryAdapter(Activity context, ArrayList<String> categories) {
        super(context, 0, categories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.recyclerview_item_bg_category, parent, false);
        }

        String text = getItem(position);

        TextView categoryText = listItemView.findViewById(R.id.category_text);
        categoryText.setText(text);

        return listItemView;
    }
}
