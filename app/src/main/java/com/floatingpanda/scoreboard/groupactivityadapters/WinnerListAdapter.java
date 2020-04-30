package com.floatingpanda.scoreboard.groupactivityadapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.floatingpanda.scoreboard.R;

import java.util.ArrayList;

public class WinnerListAdapter extends ArrayAdapter<Integer> {

    public WinnerListAdapter(Activity context, ArrayList<Integer> lists) {
        super(context, 0, lists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.winner_list_item, parent, false);
        }

        int currentList = getItem(position);

        TextView date = listItemView.findViewById(R.id.winners_date);
        date.setText("2019");

        TextView firstName = listItemView.findViewById(R.id.winners_first_player_name);
        firstName.setText("Bill");

        TextView firstScore = listItemView.findViewById(R.id.winners_first_score);
        firstScore.setText("220");

        TextView secondName = listItemView.findViewById(R.id.winners_second_player_name);
        secondName.setText("Josephine");

        TextView secondScore = listItemView.findViewById(R.id.winners_second_score);
        secondScore.setText("115");

        TextView thirdName = listItemView.findViewById(R.id.winners_third_player_name);
        thirdName.setText("Frank");

        TextView thirdScore = listItemView.findViewById(R.id.winners_third_score);
        thirdScore.setText("70");

        return listItemView;
    }

}
