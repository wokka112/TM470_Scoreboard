package com.floatingpanda.scoreboard.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

//TODO potentially refactor to be "skill-based category" rather than just "category" so people don't
// get confused.
/**
 * Represents a skill-based board game category such as Strategy, Bluffing, Random Luck, Ameritrash,
 * Eurotrash, etc. This is in contrast to thematic categories, such as Zombies, Post-apocalyptic, etc.
 *
 * A fabricated integer id primary key is used instead of the category name because the category name
 * can be readily edited and is thus not very stable.
 */
@Entity(tableName = "bg_categories", indices = {@Index(value = "category_name",
        unique = true)})
public class BgCategory implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    @NonNull
    private int id;

    @ColumnInfo(name = "category_name")
    @NonNull
    public String categoryName;

    /**
     * Simple constructor for bg categories that takes a name for the category.
     *
     * The category's id is set to 0 so Room will autogenerate a value for it when adding it to the
     * database.
     * @param categoryName the name of the category
     */
    public BgCategory(String categoryName) {
        this.id = 0;
        this.categoryName = categoryName;
    }

    @Ignore
    public BgCategory(Parcel source) {
        this.id = source.readInt();
        this.categoryName = source.readString();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(categoryName);
    }

    public static final Creator<BgCategory> CREATOR = new Creator<BgCategory>() {
        @Override
        public BgCategory[] newArray(int size) {
            return new BgCategory[size];
        }

        @Override
        public BgCategory createFromParcel(Parcel source) {
            return new BgCategory(source);
        }
    };

    /**
     * A BgCategory is equal to another BgCategory if they have the same category name.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        BgCategory other = (BgCategory) obj;

        return other.getCategoryName().equals(this.getCategoryName());
    }

    /**
     * Returns the bg category's name.
     * @return returns bg category's name string.
     */
    @NonNull
    @Override
    public String toString() {
        return this.getCategoryName();
    }
}
