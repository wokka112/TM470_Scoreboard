package com.floatingpanda.scoreboard;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.floatingpanda.scoreboard.system.BoardGame;

import java.util.ArrayList;
import java.util.List;

public class BoardGameAdapter extends ArrayAdapter<BoardGame> {

    public BoardGameAdapter(Activity context, ArrayList<BoardGame> boardGames) {
        super(context, 0, boardGames);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.board_game_list_item, parent, false);
        }

        BoardGame currentBoardGame = getItem(position);

        TextView bgNameTextView = listItemView.findViewById(R.id.bg_name);
        bgNameTextView.setText(currentBoardGame.getName());

        TextView bgDifficultyOutput = listItemView.findViewById(R.id.bg_difficulty_output);
        bgDifficultyOutput.setText(Integer.toString(currentBoardGame.getDifficulty()));

        TextView bgPlayersOutput = listItemView.findViewById(R.id.bg_players_output);
        String players = currentBoardGame.getMinPlayers() + " - " + currentBoardGame.getMaxPlayers();
        bgPlayersOutput.setText(players);

        TextView bgCategoriesOutput = listItemView.findViewById(R.id.bg_categories_output);
        String categories = createCategoriesString(currentBoardGame.getCategories());
        bgCategoriesOutput.setText(categories);

        ImageView imageView = listItemView.findViewById(R.id.bg_image);
        int imageResourceId = currentBoardGame.getImageResourceId();
        imageView.setImageResource(imageResourceId);

        return listItemView;
    }

    private String createCategoriesString(List<String> categoriesList) {
        if (categoriesList == null || categoriesList.isEmpty()) {
            return "None yet...";
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;

        while (i < 2 && i < categoriesList.size()) {
            sb.append(categoriesList.get(i));
            sb.append(", ");
            i++;
        }

        if (categoriesList.size() > 2) {
            int categoriesLeft = categoriesList.size() - 2;
            sb.append(" and " + categoriesLeft + " more");
        }

        return sb.toString();
    }
}
