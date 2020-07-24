package com.floatingpanda.scoreboard.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.daos.GroupMemberDao;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.entities.Member;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * A repository primarily for accessing the members table in the database, but also providing very
 * minor access to the group members table.
 */
public class MemberRepository {

    private MemberDao memberDao;
    private GroupMemberDao groupMemberDao;
    private LiveData<List<Member>> allMembers;

    public MemberRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        memberDao = db.memberDao();
        groupMemberDao = db.groupMemberDao();
        allMembers = memberDao.getAllLive();
    }

    //Used for testing
    public MemberRepository(AppDatabase db) {
        memberDao = db.memberDao();
        groupMemberDao = db.groupMemberDao();
        allMembers = memberDao.getAllLive();
    }

    /**
     * Returns a livedata list of all the members in the database.
     */
    public LiveData<List<Member>> getAll() {
        return allMembers;
    }

    /**
     * Returns a livedata version of a member from the database if a member with the parameter id
     * exists in the database. If no member has the parameter id, then null is returned.
     * @param id the int id of a member in the database
     * @return live data member from the database with a member_id of id
     */
    public LiveData<Member> getLiveMemberById(int id) {
        return memberDao.findLiveDataById(id);
    }

    /**
     * Returns a livedata list of members belonging to a specific group in the database, i.e.
     * members who are associated with the group via the group members table. The group is
     * determined by groupId.
     * @param groupId an int id identifying a group in the table.
     * @return
     */
    public LiveData<List<Member>> getLiveMembersOfAGroupByGroupId(int groupId) { return groupMemberDao.findMembersOfASpecificGroupByGroupId(groupId); }

    /**
     * Inserts a new Member into the database. If the Member already exists in the database, no new
     * member is inserted.
     *
     * member should have an id of 0 so Room can autogenerate an id for it.
     *
     * member should have a unique nickname, i.e. no Member should already exist in the database
     * with the same nickname as member.
     * @param member a Member with a unique nickname and an id of 0
     */
    public void insert(Member member) {
        AppDatabase.getExecutorService().execute(() -> {
            memberDao.insert(member);
        });
    }

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
    public void update(Member member) {
        AppDatabase.getExecutorService().execute(() -> {
            memberDao.update(member);
        });
    }

    /**
     * Deletes member from the database. This also deletes entries in the group members,
     * skill rating, scores, and player tables which relate to this member. Game record entries will
     * be turned null so the scores remain and can be displayed as "anonymous player" in game
     * records.
     * @param member a member that exists in the database
     */
    public void delete(Member member) {
        AppDatabase.getExecutorService().execute(() -> {
            memberDao.delete(member);
        });
    }

    /**
     * Returns the number of groups the member is a part of, i.e. the number of entries in the group
     * members table that include their memberId. The member is specified by memberId.
     * @param memberId int id that identifies a member in the database
     * @return
     */
    public int getNumberOfGroupsMemberIsPartOf(int memberId) {
        Future future = AppDatabase.getExecutorService().submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int groups = groupMemberDao.getNoOfGroupsMemberIsPartOfByMemberId(memberId);
                return groups;
            };
        });

        try {
            return (Integer) future.get();
        } catch (Exception e) {
            Log.e("MemberRepos.java", "Could not get number of groups member is part of. Exception: " + e);
            return -1;
        }
    }

    /**
     * Checks whether the database contains a Member with the nickname, nickname. If it does, returns
     * true. Otherwise, returns false.
     * @param nickname
     * @return
     */
    public boolean containsMemberNickname(String nickname) throws IllegalArgumentException {
        Future future = AppDatabase.getExecutorService().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return memberDao.containsMember(nickname);
            }
        });

        try {
            return (Boolean) future.get();
        } catch (Exception e) {
            Log.e("MemberRepos.java", "Could not get future for contains. Exception: " + e);
            return true;
        }
    }
}
