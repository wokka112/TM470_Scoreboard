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

package com.floatingpanda.scoreboard.data.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.floatingpanda.scoreboard.data.database_views.GroupCategorySkillRatingWithMemberDetailsView;
import com.floatingpanda.scoreboard.data.entities.GroupCategorySkillRating;

import java.util.List;

@Dao
public interface GroupCategorySkillRatingDao {
    @Query("SELECT * FROM group_category_skill_ratings")
    LiveData<List<GroupCategorySkillRating>> getAll();

    //Need to
    //-Get a group's skill ratings
    //-Get a group's skill ratings in a particular category
    @Query("SELECT * FROM group_category_skill_ratings WHERE group_id LIKE :groupId")
    LiveData<List<GroupCategorySkillRating>> getGroupsSkillRatingsByGroupId(int groupId);

    @Query("SELECT * FROM group_category_skill_ratings WHERE group_id LIKE :groupId AND member_id LIKE :memberId")
    LiveData<List<GroupCategorySkillRating>> getGroupMembersSkillRatingsByGroupIdAndMemberId(int groupId, int memberId);

    @Query("SELECT * FROM group_category_skill_ratings WHERE group_id LIKE :groupId AND category_id LIKE :categoryId")
    LiveData<List<GroupCategorySkillRating>> getGroupsCategorysSkillRatingsByGroupIdAndCategoryId(int groupId, int categoryId);

    @Query("SELECT * FROM group_category_skill_ratings WHERE group_category_skill_rating_id LIKE :groupCategorySkillRatingId")
    LiveData<GroupCategorySkillRating> getGroupCategorySkillRatingById(int groupCategorySkillRatingId);

    @Query("SELECT * FROM group_category_skill_ratings WHERE group_id LIKE :groupId AND category_id LIKE :categoryId AND member_id LIKE :memberId")
    LiveData<GroupCategorySkillRating> getGroupCategorySkillRatingByGroupIdAndCategoryIdAndMemberId(int groupId, int categoryId, int memberId);

    @Query("SELECT EXISTS(SELECT * FROM group_category_skill_ratings WHERE group_id LIKE :groupId AND category_id LIKE :categoryId AND member_id LIKE :memberId)")
    boolean containsGroupCategorySkillRating(int groupId, int categoryId, int memberId);

    @Query("SELECT skill_rating FROM group_category_skill_ratings where group_id LIKE :groupId AND category_id LIKE :categoryId AND member_id LIKE :memberId")
    double getNonLiveSkillRatingValueViaGroupIdAndCategoryIdAndMemberId(int groupId, int categoryId, int memberId);

    @Query("UPDATE group_category_skill_ratings SET skill_rating = ROUND(skill_rating + :addSkillRating, 2), games_rated = games_rated + 1 " +
            "WHERE group_category_skill_rating_id LIKE :groupCategorySkillRatingId")
    void addSkillRatingFromSingleGame(int groupCategorySkillRatingId, double addSkillRating);

    @Query("UPDATE group_category_skill_ratings SET skill_rating = ROUND(skill_rating + :addSkillRating, 2), games_rated = games_rated + 1 " +
            "WHERE group_id LIKE :groupId AND category_id LIKE :categoryId AND member_id LIKE :memberId")
    void addSkillRatingFromSingleGame(int groupId, int categoryId, int memberId, double addSkillRating);

    @Query("UPDATE group_category_skill_ratings SET skill_rating = ROUND(skill_rating - :removeSkillRating, 2), games_rated = games_rated - 1 " +
            "WHERE group_id LIKE :groupId AND category_id LIKE :categoryId AND member_id LIKE :memberId")
    void removeSkillRatingFromSingleGame(int groupId, int categoryId, int memberId, double removeSkillRating);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(GroupCategorySkillRating... groupCategorySkillRatings);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(GroupCategorySkillRating groupCategorySkillRating);

    @Query ("DELETE FROM group_category_skill_ratings")
    void deleteAll();

    @Delete
    void delete(GroupCategorySkillRating groupCategorySkillRating);

    @Query("SELECT * FROM GroupCategorySkillRatingWithMemberDetailsView WHERE category_id LIKE :categoryId AND group_id LIKE :groupId ORDER BY skill_rating DESC, nickname ASC")
    LiveData<List<GroupCategorySkillRatingWithMemberDetailsView>> getGroupSkillRatingsWithMemberDetailsByCategoryIdAndGroupId(int categoryId, int groupId);
}
