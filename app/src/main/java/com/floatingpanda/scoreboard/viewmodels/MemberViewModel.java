package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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
        allMembers = memberRepository.getAllMembers();
    }

    public LiveData<List<Member>> getAllMembers() { return allMembers; }

    // Hinges on primary key in database for the member being observed not changing while this
    // method is observed.
    // Postconditions: - if member exists in the database, returns a LiveData object of member.
    //                 - if member doesn't exist in the database, returns null.
    public LiveData<Member> getLiveDataMember(Member member) {
        return memberRepository.getLiveMember(member.getId());
    }

    //TODO remove?
    //Hinges on nickname not changing while this method is observed.
    public LiveData<Member> getLiveDataMember(String nickname) {
        return memberRepository.getLiveMember(nickname);
    }

    // Preconditions: - member does not exist in the database.
    // Postconditions: - member is added to the database.
    public void addMember(Member member) { memberRepository.insert(member); }

    // Precondition: - a Member with member's id (primary key) exists in database.
    // Postcondition: - the Member in the database will be updated to have the details of member.
    //                - edits will cascade, so foreign keys of member (such as in group_members) will be updated as well.
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
    public void deleteMember(Member member) { memberRepository.delete(member); }
}
