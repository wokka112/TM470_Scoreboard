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

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.GroupMember;
import com.floatingpanda.scoreboard.repositories.GroupRepository;
import com.floatingpanda.scoreboard.data.relations.GroupWithMembers;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.repositories.MemberRepository;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberViewModel extends AndroidViewModel {

    private MemberRepository memberRepository;
    private GroupRepository groupRepository;
    private LiveData<GroupWithMembers> groupWithMembers;
    private LiveData<List<Member>> allMembers;
    private List<Member> nonGroupMembers;

    public GroupMemberViewModel(Application application) {
        super(application);
        groupRepository = new GroupRepository(application);
        memberRepository = new MemberRepository(application);
        allMembers = memberRepository.getAll();
    }

    // Used for testing
    public GroupMemberViewModel(Application application, AppDatabase db) {
        super(application);
        groupRepository = new GroupRepository(db);
        memberRepository = new MemberRepository(db);
        allMembers = memberRepository.getAll();
    }

    public void initGroupWithMembers(int groupId) {
        groupWithMembers = groupRepository.getGroupWithMembersByGroupId(groupId);
    }

    public LiveData<GroupWithMembers> getGroupWithMembers() { return groupWithMembers; }

    public LiveData<List<Member>> getGroupMembersByGroupId(int groupId) {
        return groupRepository.getGroupMembersByGroupId(groupId);
    }

    public LiveData<List<Member>> getAllMembers() { return allMembers; }

    //TODO write tests for the groupId variants below.

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
    public void addGroupMember(Group group, Member member) {
        GroupMember groupMember = new GroupMember(group.getId(), member.getId());
        groupRepository.insertGroupMember(groupMember);
    }

    public void addGroupMember(int groupId, Member member) {
        GroupMember groupMember = new GroupMember(groupId, member.getId());
        groupRepository.insertGroupMember(groupMember);
    }

    public void addGroupMembers(Group group, List<Member> members) {
        List<GroupMember> groupMembers = new ArrayList<>();
        for(Member member : members) {
            groupMembers.add(new GroupMember(group.getId(), member.getId()));
        }

        groupRepository.insertGroupMembers(groupMembers);
    }

    public void addGroupMembers(int groupId, List<Member> members) {
        List<GroupMember> groupMembers = new ArrayList<>();
        for(Member member : members) {
            groupMembers.add(new GroupMember(groupId, member.getId()));
        }

        groupRepository.insertGroupMembers(groupMembers);
    }

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
    public void removeGroupMember(Group group, Member member) {
        GroupMember groupMember = new GroupMember(group.getId(), member.getId());
        groupRepository.removeGroupMember(groupMember);
    }

    public void removeGroupMember(int groupId, Member member) {
        GroupMember groupMember = new GroupMember(groupId, member.getId());
        groupRepository.removeGroupMember(groupMember);
    }
}
