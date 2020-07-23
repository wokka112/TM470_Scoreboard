package com.floatingpanda.scoreboard.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.repositories.GroupRepository;
import com.floatingpanda.scoreboard.repositories.MemberRepository;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberAddViewModel extends AndroidViewModel {

    private MemberRepository memberRepository;
    private LiveData<List<Member>> allMembersLiveData;
    private List<Member> allMembers;
    private List<Member> groupMembers;
    private List<Member> selectedMembers;

    public GroupMemberAddViewModel(Application application) {
        super(application);
        memberRepository = new MemberRepository(application);
        allMembersLiveData = memberRepository.getAll();
        selectedMembers = new ArrayList<>();
    }

    // Used for testing
    public GroupMemberAddViewModel(Application application, AppDatabase db) {
        super(application);
        memberRepository = new MemberRepository(db);
        allMembersLiveData = memberRepository.getAll();
        selectedMembers = new ArrayList<>();
    }

    public LiveData<List<Member>> getAllMembersLiveData() {
        return allMembersLiveData;
    }

    public void setAllMembers(List<Member> allMembers) {
        this.allMembers = allMembers;
    }

    /**
     * May return null if all members has not been set via setAllMembers.
     * @return
     */
    public List<Member> getAllMembers() {
        return allMembers;
    }

    public void setGroupMembers(List<Member> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public List<Member> getGroupMembers() {
        return groupMembers;
    }

    public List<Member> getNonGroupMembers() {
        if (allMembers == null || groupMembers == null) {
            return null;
        }

        List<Member> nonGroupMembers = new ArrayList<>(allMembers);
        nonGroupMembers.removeAll(groupMembers);
        return nonGroupMembers;
    }

    public List<Member> getSelectedMembers() {
        return selectedMembers;
    }

    public void addSelectedMember(Member member) {
        if (!selectedMembers.contains(member)) {
            selectedMembers.add(member);
        }
    }

    public void removeSelectedMember(Member member) {
        selectedMembers.remove(member);
    }

    public void clearSelectedMembers() {
        selectedMembers.clear();
    }

    public void addMemberToDatabase(Member member) {
        memberRepository.insert(member);
    }
}
