package com.floatingpanda.scoreboard.data.database_views;

import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;

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
