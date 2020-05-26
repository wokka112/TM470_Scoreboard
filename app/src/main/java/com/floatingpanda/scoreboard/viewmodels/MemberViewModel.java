package com.floatingpanda.scoreboard.viewmodels;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.AlertDialogHelper;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.data.MemberRepository;

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

    //TODO move this into a validator class??
    public boolean addActivityInputsValid(Activity activity, String nickname, boolean testing) {
        return editActivityInputsValid(activity, "", nickname, testing);
    }

    public boolean editActivityInputsValid(Activity activity, String originalNickname, String nickname, boolean testing) {
        //TODO sort out popup messages so they sound better.
        //TODO look into removing popup messages and replace with messages that appear next to highlighted edittext that is wrong
        if (nickname.isEmpty()) {
            if(!testing) {
                AlertDialogHelper.popupWarning("You must enter a nickname for the member.", activity);
            }
            return false;
        }

        if (!nickname.equals(originalNickname)
                && memberRepository.contains(nickname)) {
            if(!testing) {
                AlertDialogHelper.popupWarning("You must enter a unique nickname for the member.", activity);
            }
            return false;
        }

        return true;
    }
}
