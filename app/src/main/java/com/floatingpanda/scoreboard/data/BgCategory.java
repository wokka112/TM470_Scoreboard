package com.floatingpanda.scoreboard.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bg_categories")
public class BgCategory {
    @PrimaryKey
    @ColumnInfo(name = "category_name")
    @NonNull
    public String categoryName;

    public BgCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() { return this.categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
