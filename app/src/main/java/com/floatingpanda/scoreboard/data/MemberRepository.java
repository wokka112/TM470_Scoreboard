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
        allMembers = memberDao.getAll();
    }

    public LiveData<List<Member>> getAllMembers() {
        return allMembers;
    }

    //TODO get rid of using nickname to getlivemember? Observing this is problematic because nicknames can change,
    // in which case you can no longer find the member in the database using this.
    // Fails if nickname changes while this is being observed.
    // Could change this so that you get a non live member instead. That is a useful thing.
    public LiveData<Member> getLiveMember(String nickname) {
        return memberDao.findLiveDataByNickname(nickname);
    }

    // Hinges on id not changing. Primary keys should be immutable, hence the id.
    // Postcondition: - if a member with a member_id of id exists in the database, returns a LiveData version
    //                   of the member.
    //                - if a member with a member_id of id does not exist in the database, returns null.
    public LiveData<Member> getLiveMember(int id) {
        return memberDao.findLiveDataById(id);
    }

    // Precondition: member should not exist in database.
    // Postcondition: new member exists in the database.
    public void insert(Member member) {
        AppDatabase.getExecutorService().execute(() -> {
            memberDao.insert(member);
        });
    }

    // Precondition: - a Member with member's id (primary key) exists in database.
    // Postcondition: - the Member in the database will be updated to have the details of member.
    //                - edits will cascade, so foreign keys of member (such as in group_members) will be updated as well.
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
    public void delete(Member member) {
        AppDatabase.getExecutorService().execute(() -> {
            memberDao.delete(member);
        });
    }

    // Postconditions: - if a member with nickname exists in the database, returns true.
    //                 - if no member with nickname exists in the database, returns false.
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
            Log.e("BgCatRepos.java", "Exception: " + e);
            return false;
        }
    }
}
