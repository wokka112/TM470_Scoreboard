package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.BgCategoryRepository;
import com.floatingpanda.scoreboard.data.BoardGameRepository;
import com.floatingpanda.scoreboard.data.GroupRepository;
import com.floatingpanda.scoreboard.data.GroupWithMembers;
import com.floatingpanda.scoreboard.data.MemberRepository;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class GameRecordAddViewModel extends AndroidViewModel {

    private MemberRepository memberRepository;

    public GameRecordAddViewModel(Application application) {
        super(application);
        memberRepository = new MemberRepository(application);
    }

    // Used for testing.
    public GameRecordAddViewModel(Application application, AppDatabase db) {
        super(application);
        memberRepository = new MemberRepository(db);
    }

    public LiveData<List<Member>> getMembersFromAGroupByGroupId(int groupId) {
        return memberRepository.getLiveMembersOfAGroupByGroupId(groupId);
    }
}