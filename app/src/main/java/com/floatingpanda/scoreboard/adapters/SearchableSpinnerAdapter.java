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

This license does not apply to Mitesh Pithadiya's searchable spinner that is used with this adapter.
 */

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

/**
 * Searchable spinner adapter that uses Mitesh Pithadiya's (2016) searchable spinner to display a
 * searchable list of board games with bg categories for the user to choose from.
 *
 * Pithadiya, 2016 - https://github.com/miteshpithadiya/SearchableSpinner
 */
public class SearchableSpinnerAdapter extends ArrayAdapter<BoardGameWithBgCategoriesAndPlayModes> {

    public SearchableSpinnerAdapter(Context context, List<BoardGameWithBgCategoriesAndPlayModes> boardGamesWithBgCategoriesAndPlayModes) {
        super(context, android.R.layout.simple_spinner_item, boardGamesWithBgCategoriesAndPlayModes);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes = getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        TextView text = view.findViewById(android.R.id.text1);
        text.setText(boardGameWithBgCategoriesAndPlayModes.getBoardGame().getBgName());

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes = getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        TextView text = view.findViewById(android.R.id.text1);
        text.setText(boardGameWithBgCategoriesAndPlayModes.getBoardGame().getBgName());

        return view;
    }

    @Override
    public int getCount() {
        return super.getCount(); // Adjust for initial selection item
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = convertView != null && !(convertView instanceof TextView)
                ? convertView :
                LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);

        BoardGameWithBgCategoriesAndPlayModes boardGameWithBgCategoriesAndPlayModes = getItem(position);

        TextView text = convertView.findViewById(android.R.id.text1);
        text.setText(boardGameWithBgCategoriesAndPlayModes.getBoardGame().getBgName());

        return row;
    }

}
