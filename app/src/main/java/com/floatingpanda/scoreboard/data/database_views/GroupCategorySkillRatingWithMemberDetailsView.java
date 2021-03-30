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

package com.floatingpanda.scoreboard.data.database_views;

import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;

/**
 * A database view that combines information from the group_category_skill_ratings table
 * (GroupCategorySkillRatingClass) with information about the member the skill rating is for taken
 * from the members (Member class) table via the group_members (GroupMember class) table.
 *
 * The information taken includes the category id, the group id, the member's nickname, the category
 * skill rating, and the number of games rated (i.e. games with that category played competitively)
 * in this group for the member in question.
 */
@DatabaseView(
        "SELECT group_category_skill_ratings.category_id AS category_id, group_members.group_id AS group_id, " +
                "members.nickname AS nickname, group_category_skill_ratings.skill_rating AS skill_rating, group_category_skill_ratings.games_rated AS games_rated " +
                "FROM group_members INNER JOIN members ON group_members.member_id = members.member_id " +
                "INNER JOIN group_category_skill_ratings ON group_category_skill_ratings.member_id = group_members.member_id AND group_category_skill_ratings.group_id = group_members.group_id "
)
public class GroupCategorySkillRatingWithMemberDetailsView {

    @ColumnInfo(name = "category_id")
    private int categoryId;

    @ColumnInfo(name = "group_id")
    private int groupId;

    private String nickname;

    @ColumnInfo(name = "skill_rating")
    private double skillRating;

    @ColumnInfo(name = "games_rated")
    private int gamesRated;

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public double getSkillRating() { return skillRating; }
    public void setSkillRating(double skillRating) { this.skillRating = skillRating; }
    public int getGamesRated() { return gamesRated; }
    public void setGamesRated(int gamesRated) { this.gamesRated = gamesRated; }
}
