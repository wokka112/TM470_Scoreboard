package com.floatingpanda.scoreboard.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Database;

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

    public LiveData<Member> getLiveMember(String nickname) {
        return memberDao.findLivedataByNickname(nickname);
    }

    public void insert(Member member) {
        AppDatabase.getExecutorService().execute(() -> {
            memberDao.insert(member);
        });
    }

    public void update(Member member) {
        AppDatabase.getExecutorService().execute(() -> {
            memberDao.update(member);
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
