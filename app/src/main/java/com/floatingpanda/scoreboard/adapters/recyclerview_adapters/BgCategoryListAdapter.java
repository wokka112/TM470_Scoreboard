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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.interfaces.ActivityAdapterInterface;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.entities.BgCategory;

import java.util.List;

/**
 * Recyclerview adapter for displaying a list of board game categories (bgCategories).
 */
public class BgCategoryListAdapter extends RecyclerView.Adapter<BgCategoryListAdapter.BgCategoryViewHolder> {

    private final LayoutInflater inflater;
    private List<BgCategory> bgCategories;
    private ActivityAdapterInterface listener;

    public BgCategoryListAdapter(Context context, ActivityAdapterInterface listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public BgCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_bg_category, parent, false);
        return new BgCategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BgCategoryViewHolder holder, int position) {
        if (bgCategories != null) {
            BgCategory current = bgCategories.get(position);
            holder.bgCategoryItemView.setText(current.getCategoryName());
        } else {
            holder.bgCategoryItemView.setText("No nickname");
        }
    }

    /**
     * Sets the list of board game categories that will be displayed by the adapter.
     *
     * Must be called before adapter will display anything.
     * @param bgCategories a list of BgCategory objects
     */
    public void setBgCategories(List<BgCategory> bgCategories) {
        this.bgCategories = bgCategories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (bgCategories != null)
            return bgCategories.size();
        else return 0;
    }

    class BgCategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView bgCategoryItemView;
        private final ImageButton bgCategoryEditButton, bgCategoryDeleteButton;

        private BgCategoryViewHolder(View itemView) {
            super(itemView);
            bgCategoryItemView = itemView.findViewById(R.id.category_text);
            bgCategoryEditButton = itemView.findViewById(R.id.category_edit_img_button);
            bgCategoryDeleteButton = itemView.findViewById(R.id.category_delete_img_button);

            bgCategoryEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    BgCategory bgCategory = bgCategories.get(position);
                    listener.startEditActivity(bgCategory);
                }
            });

            bgCategoryDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    BgCategory bgCategory = bgCategories.get(position);
                    listener.startDeleteActivity(bgCategory);
                }
            });
        }
    }
}
