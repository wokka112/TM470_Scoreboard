package com.floatingpanda.scoreboard.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "assigned_categories", primaryKeys = {"bg_name", "category_id"})
public class AssignedCategories {
    @NonNull
    @ColumnInfo(name = "bg_name")
    public String bgName;

    @NonNull
    @ColumnInfo(name = "category_id")
    public int categoryId;

    public AssignedCategories(String bgName, int categoryId) {
        this.bgName = bgName;
        this.categoryId = categoryId;
    }

    public String getBgName() { return this.bgName; }
    public void setBgName(String bgName) { this.bgName = bgName; }
    public int getCategoryName() { return this.categoryId; }
    public void setCategoryName(int id) { this.categoryId = id; }
}
