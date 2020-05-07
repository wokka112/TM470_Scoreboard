package com.floatingpanda.scoreboard.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "assigned_categories", primaryKeys = {"bg_name", "category_name"})
public class AssignedCategories {
    @NonNull
    @ColumnInfo(name = "bg_name")
    public String bgName;

    @NonNull
    @ColumnInfo(name = "category_name")
    public String categoryName;

    public AssignedCategories(String bgName, String categoryName) {
        this.bgName = bgName;
        this.categoryName = categoryName;
    }

    public String getBgName() { return this.bgName; }
    public void setBgName(String bgName) { this.bgName = bgName; }
    public String getCategoryName() { return this.categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
