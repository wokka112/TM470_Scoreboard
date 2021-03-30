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
