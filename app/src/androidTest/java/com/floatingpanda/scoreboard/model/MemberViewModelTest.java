package com.floatingpanda.scoreboard.model;

import android.app.Activity;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.viewmodels.MemberViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MemberViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private MemberDao memberDao;
    private MemberViewModel memberViewModel;

    @Mock
    private Activity activity;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        memberDao = db.memberDao();
        memberViewModel = new MemberViewModel(ApplicationProvider.getApplicationContext(), db);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getLiveMembersWhenNoMembersInserted() throws InterruptedException {
        List<Member> members = LiveDataTestUtil.getValue(memberViewModel.getAllMembers());

        assertTrue(members.isEmpty());
    }

    @Test
    public void getLiveMembersFromDatabaseWhenMembersInserted() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> members = LiveDataTestUtil.getValue(memberViewModel.getAllMembers());

        assertFalse(members.isEmpty());
        assertThat(members.size(), is(TestData.MEMBERS.size()));
    }

    @Test
    public void getLiveMemberFromDatabaseWhenNoMembersInserted() throws InterruptedException {
        List<Member> members = LiveDataTestUtil.getValue(memberViewModel.getAllMembers());
        Member member = LiveDataTestUtil.getValue(memberViewModel.getLiveDataMember(TestData.MEMBER_1));

        assertTrue(members.isEmpty());
        assertNull(member);
    }

    @Test
    public void getLiveMemberFromDatabaseWhenMembersInserted() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> members = LiveDataTestUtil.getValue(memberViewModel.getAllMembers());
        assertThat(members.size(), is(TestData.MEMBERS.size()));

        Member member = LiveDataTestUtil.getValue(memberViewModel.getLiveDataMember(TestData.MEMBER_1));
        assertThat(member, is(TestData.MEMBER_1));
    }

    @Test
    public void addMemberToDatabase() throws InterruptedException {
        memberViewModel.addMember(TestData.MEMBER_1);
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> members = LiveDataTestUtil.getValue(memberViewModel.getAllMembers());

        assertThat(members.size(), is(1));
        assertThat(members.get(0), is(TestData.MEMBER_1));
    }

    @Test
    public void editMemberInDatabase() throws  InterruptedException {
        memberViewModel.addMember(TestData.MEMBER_1);
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> members = LiveDataTestUtil.getValue(memberViewModel.getAllMembers());

        assertThat(members.size(), is(1));
        assertThat(members.get(0), is(TestData.MEMBER_1));

        Member member = new Member(TestData.MEMBER_1);
        assertThat(members.get(0), is(members.get(0)));

        String newNickname = "Changed nickname";
        String newNotes = "Changed notes";
        String newImgFilePath = "Changed img file path";
        Date newDateCreated = new Date(100);

        member.setNickname(newNickname);
        member.setNotes(newNotes);
        member.setImgFilePath(newImgFilePath);
        member.setDateCreated(newDateCreated);

        assertThat(member, is(not(members.get(0))));

        memberViewModel.editMember(member);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        members = LiveDataTestUtil.getValue(memberViewModel.getAllMembers());

        assertThat(members.size(), is(1));

        Member editedMember = members.get(0);

        assertThat(editedMember, is(not(TestData.BG_CATEGORY_1)));
        assertThat(editedMember, is(member));
        assertThat(editedMember.getNickname(), is(newNickname));
        assertThat(editedMember.getNotes(), is(newNotes));
        assertThat(editedMember.getImgFilePath(), is(newImgFilePath));
        assertThat(editedMember.getDateCreated(), is(newDateCreated));
    }

    @Test
    public void deleteMemberInDatabase() throws InterruptedException {
        memberViewModel.addMember(TestData.MEMBER_1);
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> members = LiveDataTestUtil.getValue(memberViewModel.getAllMembers());

        assertThat(members.size(), is(1));
        assertThat(members.get(0), is(TestData.MEMBER_1));

        memberViewModel.deleteMember(TestData.MEMBER_1);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        members = LiveDataTestUtil.getValue(memberViewModel.getAllMembers());

        assertTrue(members.isEmpty());
    }

    @Test
    public void testAddActivityInputsValid() throws InterruptedException {
        //Test Case 1: Valid input
        String nickname = "Nickname";
        boolean isValid = memberViewModel.addActivityInputsValid(activity, nickname, true);
        assertTrue(isValid);

        //Test Case 2: Invalid empty String input
        nickname = "";
        isValid = memberViewModel.addActivityInputsValid(activity, nickname, true);
        assertFalse(isValid);

        //Test Case 3: Invalid String input that already exists in category database
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        TimeUnit.MILLISECONDS.sleep(100);

        Member member = memberDao.findNonLiveDataByNickname(TestData.MEMBER_1.getNickname());
        assertTrue(member != null);

        nickname = TestData.MEMBER_1.getNickname();
        isValid = memberViewModel.addActivityInputsValid(activity, nickname, true);
        assertFalse(isValid);
    }

    @Test
    public void testEditActivityInputsValid() throws InterruptedException {
        //Test Case 1: Valid input - originalNickname and nickname are same.
        String originalNickname = "Original";
        String nickname = "Original";
        boolean isValid = memberViewModel.editActivityInputsValid(activity, originalNickname, nickname, true);
        assertTrue(isValid);

        //Test Case 2: Valid input - originalNickname and nickname are different, and nickname is valid.
        nickname = "Valid name";
        isValid = memberViewModel.editActivityInputsValid(activity, originalNickname, nickname, true);
        assertTrue(isValid);

        //Test Case 3: Invalid input - originalNickname and nickname are different, and nickname is an
        // empty String.
        nickname = "";
        isValid = memberViewModel.editActivityInputsValid(activity, originalNickname, nickname, true);
        assertFalse(isValid);

        //Test Case 4: Invalid input - originalNickname and nickname are different, and nickname is a
        // String that already exists in the database.
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        Member member = memberDao.findNonLiveDataByNickname(TestData.MEMBER_1.getNickname());
        assertTrue(member != null);

        nickname = TestData.MEMBER_1.getNickname();
        isValid = memberViewModel.editActivityInputsValid(activity, originalNickname, nickname, true);
        assertFalse(isValid);
    }
}
