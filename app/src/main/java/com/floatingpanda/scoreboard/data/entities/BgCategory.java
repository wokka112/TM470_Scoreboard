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

package com.floatingpanda.scoreboard.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Represents a skill-based board game category such as Strategy, Bluffing, Random Luck, Ameritrash,
 * Eurotrash, etc. This is in contrast to thematic categories, such as Zombies, Post-apocalyptic, etc.
 */
@Entity(tableName = "bg_categories", indices = {@Index(value = "category_name",
        unique = true)})
public class BgCategory implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    private int id;

    @ColumnInfo(name = "category_name")
    @NonNull
    public String categoryName;

    @Ignore
    public BgCategory(int id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public BgCategory(String categoryName) {
        this(0, categoryName);
    }

    @Ignore
    public BgCategory(BgCategory bgCategory) {
        this.id = bgCategory.getId();
        this.categoryName = bgCategory.getCategoryName();
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        BgCategory other = (BgCategory) obj;

        return other.getCategoryName().equals(this.getCategoryName());
    }

    @NonNull
    @Override
    public String toString() {
        return this.getCategoryName();
    }
}
