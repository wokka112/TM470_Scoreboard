package com.floatingpanda.scoreboard.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

/**
 * Represents the board game categories a board game belongs to. This is a table that displays
 * many-to-many relations between board games and board game categories. The bgId attribute is a
 * foreign key linking to the board_games table (BoardGame class), and the categoryId attribute is
 * a foreign key linking to the bg_categories table (BgCategory class).
 */
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
