package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategoriesAndPlayModes;

import java.util.List;

public class SearchableSpinnerAdapter extends ArrayAdapter<BoardGameWithBgCategoriesAndPlayModes> {

    public SearchableSpinnerAdapter(Context context, List<BoardGameWithBgCategoriesAndPlayModes> boardGamesWithBgCategoriesAndPlayModes) {
        super(context, android.R.layout.simple_spinner_item, boardGamesWithBgCategoriesAndPlayModes);
    }

    //TODO try and get it working with the custom views rather than using boardGamesWithBgCategoriesAndPlayModes toString() method.

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes = getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        TextView text = view.findViewById(android.R.id.text1);
        Log.w("SearchableSpinnerAdapt.java", "Boardgame id: " + boardGameWithBgCategoriesAndPlayModes);
        Log.w("SearchableSpinnerAdapt.java", "Boardgame name: " + boardGameWithBgCategoriesAndPlayModes.getBoardGame().getBgName());
        text.setText(boardGameWithBgCategoriesAndPlayModes.getBoardGame().getBgName());

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes = getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        TextView text = view.findViewById(android.R.id.text1);
        Log.w("SearchableSpinnerAdapt.java", "Boardgame id: " + boardGameWithBgCategoriesAndPlayModes);
        Log.w("SearchableSpinnerAdapt.java", "Boardgame name: " + boardGameWithBgCategoriesAndPlayModes.getBoardGame().getBgName());
        text.setText(boardGameWithBgCategoriesAndPlayModes.getBoardGame().getBgName());

        return view;
    }

    @Override
    public int getCount() {
        return super.getCount(); // Adjust for initial selection item
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        // Distinguish "real" spinner items (that can be reused) from initial selection item
        View row = convertView != null && !(convertView instanceof TextView)
                ? convertView :
                LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);

        BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes = getItem(position);

        TextView text = convertView.findViewById(android.R.id.text1);
        text.setText(boardGameWithBgCategoriesAndPlayModes.getBoardGame().getBgName());

        return row;
    }

}
