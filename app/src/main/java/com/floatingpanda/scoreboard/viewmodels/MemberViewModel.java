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

    //Hinges on primary key not changing while this is observed.
    public LiveData<Member> getLiveDataMember(Member member) {
        return memberRepository.getLiveMember(member.getId());
    }

    //TODO remove?
    //Hinges on nickname not changing while this is observed.
    public LiveData<Member> getLiveDataMember(String nickname) {
        return memberRepository.getLiveMember(nickname);
    }

    public void addMember(Member member) { memberRepository.insert(member); }

    public void editMember(Member member) { memberRepository.update(member); }

    public void deleteMember(Member member) { memberRepository.delete(member); }
}
