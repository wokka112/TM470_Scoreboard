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
 */

package com.floatingpanda.scoreboard.adapters.recyclerview_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.relations.BoardGameWithBgCategories;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.interfaces.DetailAdapterInterface;

import java.util.List;

/**
 * Recyclerview adapter for displaying a list of board games with their board game categories.
 */
public class BoardGameListAdapter extends RecyclerView.Adapter<BoardGameListAdapter.BoardGameViewHolder> {

    private final LayoutInflater inflater;
    private List<BoardGameWithBgCategories> boardGamesWithBgCategories;
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
        if (boardGamesWithBgCategories != null) {
            BoardGameWithBgCategories current = boardGamesWithBgCategories.get(position);

            BoardGame boardGame = current.getBoardGame();
            holder.bgNameItemView.setText(boardGame.getBgName());
            holder.difficultyItemView.setText(Integer.toString(boardGame.getDifficulty()));
            holder.playersItemView.setText(boardGame.getMinPlayers() + " - " + boardGame.getMaxPlayers());
            holder.categoriesItemView.setText(current.getLimitedBgCategoriesString());
        } else {
            holder.bgNameItemView.setText("No board game");
        }
    }

    /**
     * Sets the list of board games with board game categories that will be displayed by the adapter.
     *
     * Must be called before adapter will display anything.
     * @param boardGamesWithBgCategories a list of BoardGameWithBgCategory objects
     */
    public void setBoardGamesWithBgCategories(List<BoardGameWithBgCategories> boardGamesWithBgCategories) {
        this.boardGamesWithBgCategories = boardGamesWithBgCategories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (boardGamesWithBgCategories != null)
            return boardGamesWithBgCategories.size();
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
                    BoardGame boardGame = boardGamesWithBgCategories.get(position).getBoardGame();
                    listener.viewDetails(boardGame);
                }
            });
        }
    }
}
