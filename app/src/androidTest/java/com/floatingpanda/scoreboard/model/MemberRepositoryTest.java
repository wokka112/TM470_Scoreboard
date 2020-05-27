package com.floatingpanda.scoreboard.model;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.floatingpanda.scoreboard.LiveDataTestUtil;
import com.floatingpanda.scoreboard.TestData;
import com.floatingpanda.scoreboard.data.AppDatabase;
import com.floatingpanda.scoreboard.data.BgCategory;
import com.floatingpanda.scoreboard.data.Member;
import com.floatingpanda.scoreboard.data.MemberDao;
import com.floatingpanda.scoreboard.data.MemberRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
public class MemberRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private MemberDao memberDao;
    private MemberRepository memberRepository;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        memberDao = db.memberDao();
        memberRepository = new MemberRepository(db);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getLiveMembersWhenNoMembersInserted() throws InterruptedException {
        List<Member> members = LiveDataTestUtil.getValue(memberRepository.getAll());

        assertTrue(members.isEmpty());
    }

    @Test
    public void getLiveMembersFromDatabaseWhenMembersInserted() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> members = LiveDataTestUtil.getValue(memberRepository.getAll());

        assertFalse(members.isEmpty());
        assertThat(members.size(), is(TestData.MEMBERS.size()));
    }

    @Test
    public void getLiveMemberFromDatabaseWhenNoMembersInserted() throws InterruptedException {
        List<Member> members = LiveDataTestUtil.getValue((memberRepository.getAll()));
        Member member = LiveDataTestUtil.getValue(memberRepository.getLiveMemberById(TestData.MEMBER_1.getId()));

        assertTrue(members.isEmpty());
        assertNull(member);
    }

    @Test
    public void getLiveMemberFromDatabaseWhenMembersInserted() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> members = LiveDataTestUtil.getValue((memberRepository.getAll()));
        assertThat(members.size(), is(TestData.MEMBERS.size()));

        Member member = LiveDataTestUtil.getValue(memberRepository.getLiveMemberById(TestData.MEMBER_1.getId()));
        assertThat(member, is(TestData.MEMBER_1));
    }

    @Test
    public void addMemberToDatabase() throws InterruptedException {
        List<Member> members = LiveDataTestUtil.getValue(memberRepository.getAll());
        assertTrue(members.isEmpty());

        memberRepository.insert(TestData.MEMBER_1);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        members = LiveDataTestUtil.getValue(memberRepository.getAll());

        assertThat(members.size(), is(1));
        assertThat(members.get(0), is(TestData.MEMBER_1));
    }

    @Test
    public void editMemberInDatabase() throws  InterruptedException {
        memberRepository.insert(TestData.MEMBER_1);
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> members = LiveDataTestUtil.getValue(memberRepository.getAll());

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

        memberRepository.update(member);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        members = LiveDataTestUtil.getValue(memberRepository.getAll());

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
        memberRepository.insert(TestData.MEMBER_1);
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> members = LiveDataTestUtil.getValue(memberRepository.getAll());

        assertThat(members.size(), is(1));
        assertThat(members.get(0), is(TestData.MEMBER_1));

        memberRepository.delete(TestData.MEMBER_1);
        // Waiting for background thread to finish.
        TimeUnit.MILLISECONDS.sleep(100);

        members = LiveDataTestUtil.getValue(memberRepository.getAll());

        assertTrue(members.isEmpty());
    }

    @Test
    public void testContains() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        List<Member> members = LiveDataTestUtil.getValue(memberRepository.getAll());

        assertThat(members.size(), is(TestData.MEMBERS.size()));

        //Test case 1: Contains - nickname exists in database.
        String nickname = TestData.MEMBER_1.getNickname();
        boolean contains = memberRepository.contains(nickname);
        assertTrue(contains);

        //Test case 2: Does not contain - nickname does not exist in database.
        nickname = "Non-existent name";
        contains = memberRepository.contains(nickname);
        assertFalse(contains);

        //Test case 3: Does not contain - empty nickname does not exist in database.
        nickname = "";
        contains = memberRepository.contains(nickname);
        assertFalse(contains);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContainsWithNull() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        List<Member> members = LiveDataTestUtil.getValue(memberRepository.getAll());

        assertThat(members.size(), is(TestData.MEMBERS.size()));

        String nickname = null;
        boolean contains = memberRepository.contains(nickname);
    }
}
