package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.data.MemberRepository;

import java.util.List;

public class MemberViewModel extends AndroidViewModel {

    private MemberRepository memberRepository;
    private LiveData<List<Member>> allMembers;

    public MemberViewModel(Application application) {
        super(application);
        memberRepository = new MemberRepository(application);
        allMembers = memberRepository.getAllMembers();
    }

    public LiveData<List<Member>> getAllMembers() { return allMembers; }

    //TODO implement method to get a livedata member that I can then use in the member activity.
    public LiveData<Member> getLiveDataMember(Member member) {
        return memberRepository.getLiveMember(member.getNickname());
    }

    public LiveData<Member> getLiveDataMember(String nickname) {
        return memberRepository.getLiveMember(nickname);
    }

    public void addMember(Member member) { memberRepository.insert(member); }

    public void editMember(Member member) { memberRepository.update(member); }
}
