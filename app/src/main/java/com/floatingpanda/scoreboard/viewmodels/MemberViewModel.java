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

package com.floatingpanda.scoreboard.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.widget.EditText;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.utils.AlertDialogHelper;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.repositories.MemberRepository;

import java.util.List;

/**
 * Used as view model for all member related views - MemberListFragment and MemberActivity.
 */
public class MemberViewModel extends AndroidViewModel {

    private MemberRepository memberRepository;
    private LiveData<List<Member>> allMembers;

    public MemberViewModel(Application application) {
        super(application);
        memberRepository = new MemberRepository(application);
        allMembers = memberRepository.getAll();
    }

    // Used for testing
    public MemberViewModel(Application application, AppDatabase db) {
        super(application);
        memberRepository = new MemberRepository(db);
        allMembers = memberRepository.getAll();
    }

    /**
     * @return live data list of all members from the database
     */
    public LiveData<List<Member>> getAllMembers() { return allMembers; }

    // Postconditions: - if member exists in the database, returns a LiveData object of member.
    //                 - if member doesn't exist in the database, returns null.
    /**
     * Returns a live data version of a member from the database if member exists in the database.
     * If member does not exist in the database, then null is returned.
     * @return live data member from the database
     */
    public LiveData<Member> getLiveDataMember(Member member) {
        return memberRepository.getLiveMemberById(member.getId());
    }

    // Preconditions: - member does not exist in the database.
    // Postconditions: - member is added to the database.
    /**
     * Inserts a new Member into the database. If the Member already exists in the database, no new
     * member is inserted.
     *
     * member should have an id of 0 so Room can autogenerate an id for it.
     *
     * member should have a unique nickname, i.e. no MEmber should already exist in the database
     * with the same nickname as member.
     * @param member a Member with a unique nickname and an id of 0
     */
    public void addMember(Member member) { memberRepository.insert(member); }

    // Precondition: - a Member with member's id (primary key) exists in database.
    // Postcondition: - the Member in the database will be updated to have the details of member.
    //                - edits will cascade, so foreign keys of member (such as in group_members) will be updated as well.
    /**
     * Updates a Member in the database to match the data of member. A Member with member's id
     * should already exist in the database for this to work.
     *
     * Note that a Member's id should not change, so update will not update Member ids in the
     * database, only their other details.
     *
     * member should have a unique nickname, i.e. no Member should already exist in the
     * database with the same nickname as member.
     * @param member a member with a unique nickname
     */
    public void editMember(Member member) { memberRepository.update(member); }

    // Precondition: - member should exist in the database.
    // Postconditions: - member will no longer exist in the database.
    //                 - group_members tables with bgCategory in will have been deleted.
    //                 - category_skill_rating, group_category_skill_rating and board_game_skill_rating
    //                    tables for this member will have been deleted.
    //                 - monthly_scores, quarterly_scores and yearly_scores tables for this member will
    //                    have been deleted.
    //                 - game_records which this member is registered on will have the member turned into an
    //                    anonymous member (i.e. the record will no longer have a foreign key linking to the
    //                    member's table in the members tables).
    /**
     * Deletes member from the database
     * @param member a Member that exists in the database
     */
    public void deleteMember(Member member) { memberRepository.delete(member); }

    public int getNumberOfGroupsMemberIsPartOf(int memberId) {
        return memberRepository.getNumberOfGroupsMemberIsPartOf(memberId);
    }

    public boolean addActivityInputsValid(EditText nicknameEditText, boolean testing) {
        return editActivityInputsValid("", nicknameEditText, testing);
    }

    public boolean editActivityInputsValid(String originalNickname, EditText nicknameEditText, boolean testing) {
        String nickname = nicknameEditText.getText().toString();
        if (nickname.isEmpty()) {
            if(!testing) {
                nicknameEditText.setError("You must enter a nickname.");
                nicknameEditText.requestFocus();
            }
            return false;
        }

        if (!nickname.equals(originalNickname)
                && memberRepository.containsMemberNickname(nickname)) {
            if(!testing) {
                nicknameEditText.setError("A member with this nickname already exists in the app. You must enter a unique nickname.");
                nicknameEditText.requestFocus();
            }
            return false;
        }

        return true;
    }
}
