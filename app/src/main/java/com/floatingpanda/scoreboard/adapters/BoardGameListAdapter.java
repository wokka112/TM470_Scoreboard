package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.BgAndBgCategoriesAndPlayModes;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.BoardGame;
import com.floatingpanda.scoreboard.data.BoardGamesAndBgCategories;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;

import java.util.List;

public class BoardGameListAdapter extends RecyclerView.Adapter<BoardGameListAdapter.BoardGameViewHolder> {

    private final LayoutInflater inflater;
    private List<BgAndBgCategoriesAndPlayModes> bgsAndBgCategoriesAndPlayModes;
    private DetailAdapterInterface listener;

    public BoardGameListAdapter(Context context, DetailAdapterInterface listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public BoardGameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_board_game, parent, false);
        return new BoardGameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BoardGameViewHolder holder, int position) {
        if (bgsAndBgCategoriesAndPlayModes != null) {
            BgAndBgCategoriesAndPlayModes current = bgsAndBgCategoriesAndPlayModes.get(position);

            BoardGame boardGame = current.getBoardGame();
            holder.bgNameItemView.setText(boardGame.getBgName());
            holder.difficultyItemView.setText(Integer.toString(boardGame.getDifficulty()));
            holder.playersItemView.setText(boardGame.getMinPlayers() + " - " + boardGame.getMaxPlayers());

            List<BgCategory> categories = boardGame.getBgCategories();
            StringBuilder sb = new StringBuilder();
            sb.append("");
            for(BgCategory bgCategory : categories) {
                sb.append(bgCategory.getCategoryName());
                //TODO sort out so the extra , doesn't appear at the end of the list of categories.
                sb.append(", ");
            }

            if (sb.toString().equals("")) {
                sb.append("None");
            }

            holder.categoriesItemView.setText(sb.toString());
        } else {
            holder.bgNameItemView.setText("No board game");
        }
    }

    public void setBgsAndBgCategoriesAndPlayModes(List<BgAndBgCategoriesAndPlayModes> bgsAndBgCategoriesAndPlayModes) {
        this.bgsAndBgCategoriesAndPlayModes = bgsAndBgCategoriesAndPlayModes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (bgsAndBgCategoriesAndPlayModes != null)
            return bgsAndBgCategoriesAndPlayModes.size();
        else return 0;
    }

    class BoardGameViewHolder extends RecyclerView.ViewHolder {
        private final TextView bgNameItemView, difficultyItemView, playersItemView, categoriesItemView;

        private BoardGameViewHolder(View itemView) {
            super(itemView);
            bgNameItemView = itemView.findViewById(R.id.bg_name);
            difficultyItemView = itemView.findViewById(R.id.bg_difficulty_output);
            playersItemView = itemView.findViewById(R.id.bg_players_output);
            categoriesItemView = itemView.findViewById(R.id.bg_categories_output);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    BoardGame boardGame = bgsAndBgCategoriesAndPlayModes.get(position).getBoardGame();
                    listener.viewDetails(boardGame);
                }
            });
        }
    }
}
