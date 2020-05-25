package com.floatingpanda.scoreboard.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

//No need to cascade updates as ids should not change???
@Entity(tableName = "assigned_categories", primaryKeys = {"bg_id", "category_id"},
    foreignKeys = {@ForeignKey(entity = BoardGame.class,
            parentColumns = "bg_id",
            childColumns = "bg_id",
            onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = BgCategory.class,
            parentColumns = "category_id",
            childColumns = "category_id",
            onDelete = ForeignKey.CASCADE)})
public class AssignedCategory {
    @NonNull
    @ColumnInfo(name = "bg_id")
    public int bgId;

    @NonNull
    @ColumnInfo(name = "category_id")
    public int categoryId;

    public AssignedCategory(int bgId, int categoryId) {
        this.bgId = bgId;
        this.categoryId = categoryId;
    }

    public int getBgId() { return this.bgId; }
    public void setBgId(int id) { this.bgId = id; }
    public int getCategoryId() { return this.categoryId; }
    public void setCategoryId(int id) { this.categoryId = id; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        AssignedCategory assignedCategory = (AssignedCategory) obj;

        return (assignedCategory.getBgId() == this.getBgId()
                && assignedCategory.getCategoryId() == this.getCategoryId());
    }
}
