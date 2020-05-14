package com.floatingpanda.scoreboard.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "assigned_categories", primaryKeys = {"bg_id", "category_id"})
public class AssignedCategories {
    @NonNull
    @ColumnInfo(name = "bg_id")
    public int bgId;

    @NonNull
    @ColumnInfo(name = "category_id")
    public int categoryId;

    public AssignedCategories(int bgId, int categoryId) {
        this.bgId = bgId;
        this.categoryId = categoryId;
    }

    public int getBgId() { return this.bgId; }
    public void setBgId(int id) { this.bgId = id; }
    public int getCategoryName() { return this.categoryId; }
    public void setCategoryName(int id) { this.categoryId = id; }
}
