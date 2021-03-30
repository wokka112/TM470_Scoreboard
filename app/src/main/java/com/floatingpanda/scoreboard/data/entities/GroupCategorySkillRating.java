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

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Represents a group member's skill rating in that group for a particular board game category. The
 * category the rating is for is stored in the bg_categories table (BgCategory class) and linked via
 * the categoryId attribute, which is a foreign key to the bg_categories table. The group the rating
 * is for is stored in the groups table (Group class) and linked via the groupId attribute, which is
 * a foreign key to the groups table.
 *
 * Skill ratings are represented by doubles and calculated using a variant of Elo's rating system.
 */
@Entity(tableName = "group_category_skill_ratings",
        foreignKeys = {@ForeignKey(entity = Group.class,
                parentColumns = "group_id",
                childColumns = "group_id",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Member.class,
                parentColumns = "member_id",
                childColumns = "member_id",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = BgCategory.class,
                        parentColumns = "category_id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.CASCADE)})
public class GroupCategorySkillRating {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "group_category_skill_rating_id")
    private int id;

    @ColumnInfo(name = "group_id")
    private int groupId;

    @ColumnInfo(name = "member_id")
    private int memberId;

    @ColumnInfo(name = "category_id")
    private int categoryId;

    @ColumnInfo(name = "skill_rating")
    private double skillRating;

    @ColumnInfo(name = "games_rated")
    private int gamesRated;

    public GroupCategorySkillRating(int id, int groupId, int memberId, int categoryId, double skillRating, int gamesRated) {
        this.id = id;
        this.groupId = groupId;
        this.memberId = memberId;
        this.categoryId = categoryId;
        this.skillRating = skillRating;
        this.gamesRated = gamesRated;
    }

    @Ignore
    public GroupCategorySkillRating(int groupId, int memberId, int categoryId, double skillRating, int gamesRated) {
        this(0, groupId, memberId, categoryId, skillRating, gamesRated);
    }

    @Ignore
    public GroupCategorySkillRating(int groupId, int memberId, int categoryId, double skillRating) {
        this(0, groupId, memberId, categoryId, skillRating, 0);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public double getSkillRating() { return skillRating; }
    public void setSkillRating(double skillRating) { this.skillRating = skillRating; }
    public int getGamesRated() { return gamesRated; }
    public void setGamesRated(int gamesRated) { this.gamesRated = gamesRated; }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        GroupCategorySkillRating groupCategorySkillRating = (GroupCategorySkillRating) obj;

        return groupCategorySkillRating.getMemberId() == this.getMemberId()
                && groupCategorySkillRating.getGroupId() == this.getGroupId()
                && groupCategorySkillRating.getCategoryId() == this.getCategoryId();
    }
}
