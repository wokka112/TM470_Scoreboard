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

package com.floatingpanda.scoreboard.db;

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
public class MemberDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private MemberDao memberDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        memberDao = db.memberDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getLiveMembersWhenNoMembersInserted() throws InterruptedException {
        List<Member> members = LiveDataTestUtil.getValue(memberDao.getAllLive());

        assertTrue(members.isEmpty());
    }

    @Test
    public void getNonLiveMembersWhenNoMembersInserted() {
        List<Member> members = memberDao.getAllNonLive();

        assertTrue(members.isEmpty());
    }

    @Test
    public void getLiveMembersWhenMembersInserted() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        List<Member> members = LiveDataTestUtil.getValue(memberDao.getAllLive());

        assertFalse(members.isEmpty());
        assertThat(members.size(), is(TestData.MEMBERS.size()));
    }

    @Test
    public void getNonLiveMembersWhenMembersInserted() {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        List<Member> members = memberDao.getAllNonLive();

        assertFalse(members.isEmpty());
        assertThat(members.size(), is(TestData.MEMBERS.size()));
    }

    @Test
    public void getLiveMembersWhenSpecificMemberInserted() throws InterruptedException {
        memberDao.insert(TestData.MEMBER_1);

        List<Member> members = LiveDataTestUtil.getValue(memberDao.getAllLive());

        assertFalse(members.isEmpty());
        assertThat(members.size(), is(1));
        assertThat(members.get(0), is(TestData.MEMBER_1));
    }

    @Test
    public void getNonLiveMembersWhenSpecificMemberInserted() {
        memberDao.insert(TestData.MEMBER_1);

        List<Member> members = memberDao.getAllNonLive();

        assertFalse(members.isEmpty());
        assertThat(members.size(), is(1));
        assertThat(members.get(0), is(TestData.MEMBER_1));
    }

    @Test
    public void getLiveMembersWhenSameMemberInsertedTwice() throws InterruptedException {
        memberDao.insert(TestData.MEMBER_1);

        List<Member> members = LiveDataTestUtil.getValue(memberDao.getAllLive());

        assertFalse(members.isEmpty());
        assertThat(members.size(), is(1));
        assertThat(members.get(0), is(TestData.MEMBER_1));

        memberDao.insert(TestData.MEMBER_1);
        TimeUnit.MILLISECONDS.sleep(100);

        members = LiveDataTestUtil.getValue(memberDao.getAllLive());

        assertFalse(members.isEmpty());
        assertThat(members.size(), is(not(2)));
        assertThat(members.size(), is (1));
        assertThat(members.get(0), is(TestData.MEMBER_1));
    }

    @Test
    public void getLiveMembersWhenMemberIsInsertedThenEditedVersionWithSamePrimaryKeyIsInserted() throws InterruptedException {
        memberDao.insert(TestData.MEMBER_1);

        List<Member> members = LiveDataTestUtil.getValue(memberDao.getAllLive());

        assertFalse(members.isEmpty());
        assertThat(members.size(), is(1));
        assertThat(members.get(0), is(TestData.MEMBER_1));

        Member editedMember = new Member(members.get(0));

        editedMember.setNickname("Changed");
        assertThat(editedMember, is(not(members.get(0))));

        memberDao.insert(editedMember);
        TimeUnit.MILLISECONDS.sleep(100);

        members = LiveDataTestUtil.getValue(memberDao.getAllLive());

        assertFalse(members.isEmpty());
        assertThat(members.size(), is(not(2)));
        assertThat(members.size(), is(1));
        assertThat(members.get(0), is(not(editedMember)));
        assertThat(members.get(0), is(TestData.MEMBER_1));
    }

    @Test
    public void getLiveMemberById() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        Member member = LiveDataTestUtil.getValue(memberDao.findLiveDataById(TestData.MEMBER_2.getId()));

        assertThat(member, is(TestData.MEMBER_2));
    }

    @Test
    public void getLiveMemberByNickname() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        Member member = LiveDataTestUtil.getValue(memberDao.findLiveDataByNickname(TestData.MEMBER_2.getNickname()));

        assertThat(member, is(TestData.MEMBER_2));
    }

    @Test
    public void getNonLiveMemberByNickname() {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        Member member = memberDao.findNonLiveDataByNickname(TestData.MEMBER_2.getNickname());

        assertThat(member, is(TestData.MEMBER_2));
    }

    @Test
    public void insertAndDeleteAllMembers() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        List<Member> members = LiveDataTestUtil.getValue(memberDao.getAllLive());
        assertFalse(members.isEmpty());

        memberDao.deleteAll();

        members = LiveDataTestUtil.getValue((memberDao.getAllLive()));
        assertTrue(members.isEmpty());
    }

    @Test
    public void insertAllMembersAndDeleteSpecificMember() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        List<Member> members = LiveDataTestUtil.getValue(memberDao.getAllLive());
        assertFalse(members.isEmpty());
        assertThat(members.size(), is(TestData.MEMBERS.size()));

        Member member = LiveDataTestUtil.getValue(memberDao.findLiveDataById(TestData.MEMBER_2.getId()));
        assertTrue(member != null);

        memberDao.delete(TestData.MEMBER_2);

        members = LiveDataTestUtil.getValue(memberDao.getAllLive());
        assertThat(members.size(), is(TestData.MEMBERS.size() - 1));

        member = LiveDataTestUtil.getValue(memberDao.findLiveDataById(TestData.MEMBER_2.getId()));
        assertNull(member);
    }

    @Test
    public void insertAllMembersAndUpdateSpecificMember() throws InterruptedException {
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));

        Member member = LiveDataTestUtil.getValue(memberDao.findLiveDataById(TestData.MEMBER_2.getId()));
        assertThat(member, is(TestData.MEMBER_2));

        String newNickname = "Changed nickname";
        String newNotes = "Changed notes";
        String newImgFilePath = "Changed img file path";

        member.setNickname(newNickname);
        member.setNotes(newNotes);
        member.setImgFilePath(newImgFilePath);
        member.setDateCreated(new Date(100));
        memberDao.update(member);

        //Should no longer exist in database, hence should return null.
        Member oldMember = LiveDataTestUtil.getValue(memberDao.findLiveDataByNickname(TestData.MEMBER_2.getNickname()));
        assertNull(oldMember);

        Member updatedMember = LiveDataTestUtil.getValue(memberDao.findLiveDataByNickname(member.getNickname()));
        assertThat(updatedMember, is(member));
        assertThat(updatedMember, is(not(TestData.MEMBER_2)));

        assertThat(updatedMember.getId(), is(member.getId()));
        assertThat(updatedMember.getNickname(), is(member.getNickname()));
        assertThat(updatedMember.getNotes(), is(member.getNotes()));
        assertThat(updatedMember.getImgFilePath(), is(member.getImgFilePath()));
        assertThat(updatedMember.getDateCreated(), is(member.getDateCreated()));

        assertThat(updatedMember.getId(), is(TestData.MEMBER_2.getId()));
        assertThat(updatedMember.getNickname(), is(not(TestData.MEMBER_2.getNickname())));
        assertThat(updatedMember.getNotes(), is(not(TestData.MEMBER_2.getNotes())));
        assertThat(updatedMember.getImgFilePath(), is(not(TestData.MEMBER_2.getImgFilePath())));

        assertThat(updatedMember.getNickname(), is(newNickname));
        assertThat(updatedMember.getNotes(), is(newNotes));
        assertThat(updatedMember.getImgFilePath(), is(newImgFilePath));
    }

    @Test
    public void testContains() throws InterruptedException {
        boolean contains = memberDao.containsMember(TestData.MEMBER_1.getNickname());
        assertFalse(contains);

        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        contains = memberDao.containsMember(TestData.MEMBER_1.getNickname());
        assertTrue(contains);
    }
}
