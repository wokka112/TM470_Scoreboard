package com.floatingpanda.scoreboard.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.BgCategory;

import java.util.List;

public class BgCategoryListAdapter extends RecyclerView.Adapter<BgCategoryListAdapter.BgCategoryViewHolder> {

    class BgCategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView bgCategoryItemView;

        private BgCategoryViewHolder(View itemView) {
            super(itemView);
            bgCategoryItemView = itemView.findViewById(R.id.category_text);
        }
    }

    private final LayoutInflater inflater;
    private List<BgCategory> bgCategories;

    public BgCategoryListAdapter(Context context) { inflater = LayoutInflater.from(context); }

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
}
