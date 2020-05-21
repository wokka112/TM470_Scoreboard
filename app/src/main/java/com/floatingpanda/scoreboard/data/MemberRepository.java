package com.floatingpanda.scoreboard.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class MemberRepository {

    private MemberDao memberDao;
    private LiveData<List<Member>> allMembers;

    public MemberRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        memberDao = db.memberDao();
        allMembers = memberDao.getAllLive();
    }

    /**
     * @return live data list of all members from the database
     */
    public LiveData<List<Member>> getAllMembers() {
        return allMembers;
    }

    // Postcondition: - if a member with a member_id of id exists in the database, returns a LiveData version
    //                   of the member.
    //                - if a member with a member_id of id does not exist in the database, returns null.
    /**
     * Returns a live data version of a member from the database if a member with the parameter id
     * exists in the database. If no member has the parameter id, then null is returned.
     * @param id the id of a member
     * @return live data member from the database with a member_id of id
     */
    public LiveData<Member> getLiveMember(int id) {
        return memberDao.findLiveDataById(id);
    }

    // Precondition: member should not exist in database.
    // Postcondition: new member exists in the database.
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
    public void insert(Member member) {
        AppDatabase.getExecutorService().execute(() -> {
            memberDao.insert(member);
        });
    }

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
    public void update(Member member) {
        AppDatabase.getExecutorService().execute(() -> {
            memberDao.update(member);
        });
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
     * @param member a member that exists in the database
     */
    public void delete(Member member) {
        AppDatabase.getExecutorService().execute(() -> {
            memberDao.delete(member);
        });
    }

    // Postconditions: - if a member with nickname exists in the database, returns true.
    //                 - if no member with nickname exists in the database, returns false.
    /**
     * Checks whether the database contains a Member with the nickname nickname. If it does, returns
     * true. Otherwise, returns false.
     * @param nickname
     * @return
     */
    //TODO look into whether this is basically just running on the main thread. I think it may be.
    public boolean contains(String nickname) {
        Future future = AppDatabase.getExecutorService().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Member databaseMember = memberDao.findNonLiveDataByNickname(nickname);
                Log.w("MemberRepos.java", "databaseMember: " + databaseMember);
                return databaseMember != null;
            };
        });

        try {
            return (Boolean) future.get();
        } catch (Exception e) {
            Log.e("MemberRepos.java", "Exception: " + e);
            return false;
        }
    }
}
