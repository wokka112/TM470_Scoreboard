package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.Member;

import java.util.List;

public class SearchableSpinnerAdapter extends ArrayAdapter<Member> {

    public SearchableSpinnerAdapter(Context context, List<Member> members) {
        super(context, android.R.layout.simple_spinner_item, members);
        this.insert(null, 0);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        if (position == 0) {
            return initialSelection(true);
        }
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            return initialSelection(false);
        }
        return getCustomView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return super.getCount(); // Adjust for initial selection item
    }

    private View initialSelection(boolean dropdown) {

        TextView view = new TextView(getContext());
        view.setText("Select player");

        /*
        if (dropdown == true) {
            view.setHeight(0);
            view.setVisibility(View.GONE);
        }
        */

        return view;
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        // Distinguish "real" spinner items (that can be reused) from initial selection item
        View row = convertView != null && !(convertView instanceof TextView)
                ? convertView :
                LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);

        position = position; // Adjust for initial selection item
        Member member = getItem(position);

        TextView text = row.findViewById(android.R.id.text1);
        text.setText(member.getNickname());

        return row;
    }

}
