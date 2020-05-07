package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGamesAndBgCategories;

import java.util.List;

public class BoardGameListAdapter extends RecyclerView.Adapter<BoardGameListAdapter.BoardGameViewHolder> {

    class BoardGameViewHolder extends RecyclerView.ViewHolder {
        private final TextView bgNameItemView, difficultyItemView, playersItemView, categoriesItemView;

        private BoardGameViewHolder(View itemView) {
            super(itemView);
            bgNameItemView = itemView.findViewById(R.id.bg_name);
            difficultyItemView = itemView.findViewById(R.id.bg_difficulty_output);
            playersItemView = itemView.findViewById(R.id.bg_players_output);
            categoriesItemView = itemView.findViewById(R.id.bg_categories_output);
        }
    }

    private final LayoutInflater inflater;
    private List<BoardGamesAndBgCategories> bgsAndBgCategories;

    public BoardGameListAdapter(Context context) { inflater = LayoutInflater.from(context); }

    @Override
    public BoardGameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_board_game, parent, false);
        return new BoardGameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BoardGameViewHolder holder, int position) {
        if (bgsAndBgCategories != null) {
            Log.w("BoardGameListAdapt.java", "Position: " + position);
            BoardGamesAndBgCategories current = bgsAndBgCategories.get(position);

            BoardGame boardGame = current.getBoardGame();
            holder.bgNameItemView.setText(boardGame.getBgName());
            holder.difficultyItemView.setText(Integer.toString(boardGame.getDifficulty()));
            holder.playersItemView.setText(boardGame.getMinPlayers() + " - " + boardGame.getMaxPlayers());

            List<BgCategory> categories = current.getBgCategories();
            StringBuilder sb = new StringBuilder();
            sb.append("");
            for (int i = 0; i < categories.size(); i++) {
                sb.append(categories.get(i).getCategoryName());
                sb.append(", ");
            }
            holder.categoriesItemView.setText(sb.toString());
        } else {
            holder.bgNameItemView.setText("No board game");
        }
    }

    public void setBgsAndBgCategories(List<BoardGamesAndBgCategories> bgsAndBgCategories) {
        this.bgsAndBgCategories = bgsAndBgCategories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (bgsAndBgCategories != null)
            return bgsAndBgCategories.size();
        else return 0;
    }
}
