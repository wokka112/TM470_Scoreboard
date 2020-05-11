package com.floatingpanda.scoreboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.floatingpanda.scoreboard.interfaces.ActivityAdapterInterface;
import com.floatingpanda.scoreboard.R;
import com.floatingpanda.scoreboard.data.BgCategory;

import java.util.List;

public class BgCategoryListAdapter extends RecyclerView.Adapter<BgCategoryListAdapter.BgCategoryViewHolder> {

    private final LayoutInflater inflater;
    private List<BgCategory> bgCategories;
    private ActivityAdapterInterface listener;

    public BgCategoryListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public BgCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_bg_category, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            //TODO change from buttons to a long press popping up the options of delete or edit?
            // would need to give visual feedback that you are pressing on the text
            @Override
            public void onClick(View v) {
                Button editButton = v.findViewById(R.id.category_edit_button);
                Button deleteButton = v.findViewById(R.id.category_delete_button);
                if (editButton.getVisibility() == View.GONE) {
                    editButton.setVisibility(View.VISIBLE);
                    deleteButton.setVisibility(View.VISIBLE);
                }
                else {
                    editButton.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.GONE);
                }
            }
        });
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

    class BgCategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView bgCategoryItemView;
        private final Button bgCategoryEditButton;

        private BgCategoryViewHolder(View itemView) {
            super(itemView);
            bgCategoryItemView = itemView.findViewById(R.id.category_text);
            bgCategoryEditButton = itemView.findViewById(R.id.category_edit_button);

            bgCategoryEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    BgCategory bgCategory = bgCategories.get(position);
                    listener.editEntity(bgCategory);
                }
            });
        }
    }
}
