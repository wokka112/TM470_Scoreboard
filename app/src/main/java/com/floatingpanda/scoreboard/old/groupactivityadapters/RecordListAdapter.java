package com.floatingpanda.scoreboard.old.groupactivityadapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.old.system.GameRecord;
import com.floatingpanda.scoreboard.old.system.Member;

import java.util.ArrayList;
import java.util.List;

public class RecordListAdapter extends ArrayAdapter<GameRecord> {

    public RecordListAdapter(Activity context, ArrayList<GameRecord> records) {
        super(context, 0, records);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.game_record_list_item, parent, false);
        }

        GameRecord currentGameRecord = getItem(position);

        //boardgame, date, time, difficulty, player count, first, second, third

        TextView gameName = listItemView.findViewById(R.id.records_game_name);
        gameName.setText(currentGameRecord.getGamePlayed());

        TextView date = listItemView.findViewById(R.id.records_date);
        date.setText(Integer.toString(currentGameRecord.getDate()));

        TextView time = listItemView.findViewById(R.id.records_time);
        time.setText(Integer.toString(currentGameRecord.getTime()));

        TextView difficulty = listItemView.findViewById(R.id.records_difficulty_output);
        difficulty.setText(Integer.toString(currentGameRecord.getGameDifficulty()));

        TextView playerCount = listItemView.findViewById(R.id.records_player_count_output);
        playerCount.setText(Integer.toString(currentGameRecord.getPlayerCount()));

        List<Member> players = currentGameRecord.getPlayers();

        //TODO include checking code to check if less than 3 players and hide elements if there are less.

        //TODO add in method to get player's position for score calculation.

        Member first = players.get(0);
        Member second = players.get(1);
        Member third = players.get(2);

        int gameDifficulty = currentGameRecord.getGameDifficulty();
        int gamePlayerCount = currentGameRecord.getPlayerCount();

        TextView firstName = listItemView.findViewById(R.id.records_first_player_name);
        firstName.setText(first.getNickname());

        TextView firstScore = listItemView.findViewById(R.id.records_first_score);
        firstScore.setText(Integer.toString(getScore(gameDifficulty, 1, gamePlayerCount)));

        TextView secondName = listItemView.findViewById(R.id.records_second_player_name);
        secondName.setText(second.getNickname());

        TextView secondScore = listItemView.findViewById(R.id.records_second_score);
        secondScore.setText(Integer.toString(getScore(gameDifficulty, 2, gamePlayerCount)));

        TextView thirdName = listItemView.findViewById(R.id.records_third_player_name);
        thirdName.setText(third.getNickname());

        TextView thirdScore = listItemView.findViewById(R.id.records_third_score);
        thirdScore.setText(Integer.toString(getScore(gameDifficulty, 3, gamePlayerCount)));

        return listItemView;
    }

    //TODO put into a special class for score calculation
    private int getScore(int difficulty, int position, int playerCount) {
        return (playerCount + 1 - position) * difficulty;
    }
}
