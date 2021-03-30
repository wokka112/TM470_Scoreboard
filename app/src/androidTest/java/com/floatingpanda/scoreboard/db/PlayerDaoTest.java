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
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.daos.PlayerSkillRatingChangeDao;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.daos.PlayerDao;
import com.floatingpanda.scoreboard.data.entities.PlayerSkillRatingChange;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.data.daos.PlayerTeamDao;
import com.floatingpanda.scoreboard.data.relations.PlayerWithRatingChanges;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PlayerDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;

    private GameRecordDao gameRecordDao;
    private GroupDao groupDao;
    private BoardGameDao boardGameDao;
    private PlayerTeamDao playerTeamDao;
    private PlayerDao playerDao;
    private MemberDao memberDao;
    private BgCategoryDao bgCategoryDao;
    private PlayerSkillRatingChangeDao playerSkillRatingChangeDao;

    @Before
    public void createDb() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        gameRecordDao = db.gameRecordDao();
        boardGameDao = db.boardGameDao();
        groupDao = db.groupDao();
        playerTeamDao = db.playerTeamDao();
        playerDao = db.playerDao();
        memberDao = db.memberDao();
        bgCategoryDao = db.bgCategoryDao();
        playerSkillRatingChangeDao = db.playerSkillRatingChangeDao();

        boardGameDao.insertAll(TestData.BOARD_GAMES.toArray(new BoardGame[TestData.BOARD_GAMES.size()]));
        groupDao.insertAll(TestData.GROUPS.toArray(new Group[TestData.GROUPS.size()]));
        memberDao.insertAll(TestData.MEMBERS.toArray(new Member[TestData.MEMBERS.size()]));
        gameRecordDao.insertAll(TestData.GAME_RECORDS.toArray(new GameRecord[TestData.GAME_RECORDS.size()]));
        playerTeamDao.insertAll(TestData.PLAYER_TEAMS.toArray(new PlayerTeam[TestData.PLAYER_TEAMS.size()]));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getAllPlayersWhenNoneInserted() throws InterruptedException {
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertTrue(players.isEmpty());
    }

    @Test
    public void getAllPlayersWhenAllInserted() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size()));
    }

    @Test
    public void getPlayerByPlayerIdWhenNoneInserted() throws InterruptedException {
        Player player = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayerByPlayerId(TestData.PLAYER_2.getId()));

        assertNull(player);
    }

    @Test
    public void getPlayerByPlayerIdWhenAllInserted() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        Player player = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayerByPlayerId(TestData.PLAYER_2.getId()));

        assertNotNull(player);
        assertThat(player, is(TestData.PLAYER_2));
    }

    @Test
    public void getPlayersByPlayerTeamIdWhenNoneInserted() throws InterruptedException {
        List<Player> players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByPlayerTeamId(TestData.PLAYER_TEAM_2.getId()));

        assertTrue(players.isEmpty());
    }

    @Test
    public void getPlayersByPlayerTeamIdWhenAllInserted() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        //Player team 2 has 2 players in it.
        List<Player> players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByPlayerTeamId(TestData.PLAYER_TEAM_2.getId()));

        assertThat(players.size(), is(2));

        //Player team 4 has 0 players in it.
        players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByPlayerTeamId(TestData.PLAYER_TEAM_4.getId()));

        assertTrue(players.isEmpty());
    }

    @Test
    public void getPlayersByMemberNicknameWhenNoneInserted() throws InterruptedException {
        List<Player> players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByMemberNickname(TestData.PLAYER_1.getMemberNickname()));

        assertTrue(players.isEmpty());
    }

    @Test
    public void getPlayersByMemberNicknameWhenAllInserted() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));

        // The member nickname associated with player 1 is shared by 1 other player - player 5.
        List<Player> players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByMemberNickname(TestData.PLAYER_1.getMemberNickname()));

        assertThat(players.size(), is(2));
        assertTrue(players.contains(TestData.PLAYER_1));
        assertTrue(players.contains(TestData.PLAYER_5));
    }

    @Test
    public void getPlayersWhenOnlyOneInserted() throws InterruptedException {
        playerDao.insert(TestData.PLAYER_1);

        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is (1));
        assertThat(players.get(0), is(TestData.PLAYER_1));
    }

    @Test
    public void getAllPlayersAfterInsertingAndDeletingAll() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size()));

        playerDao.deleteAll();
        TimeUnit.MILLISECONDS.sleep(100);

        players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertTrue(players.isEmpty());
    }

    @Test
    public void getAllPlayersAfterInsertingAllAndDeletingOne() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size()));
        assertTrue(players.contains(TestData.PLAYER_2));

        playerDao.delete(TestData.PLAYER_2);
        TimeUnit.MILLISECONDS.sleep(100);

        players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size() - 1));
        assertFalse(players.contains(TestData.PLAYER_2));
    }

    @Test
    public void getAllPlayersAfterInsertingAllAndDeletingAnAssociatedMember() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size()));

        Player player = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayerByPlayerId(TestData.PLAYER_3.getId()));

        assertNotNull(player);
        assertThat(player, is(TestData.PLAYER_3));
        assertThat(player.getMemberNickname(), is(TestData.PLAYER_3.getMemberNickname()));
        assertThat(player.getMemberNickname(), is(TestData.MEMBER_3.getNickname()));

        memberDao.delete(TestData.MEMBER_3);
        TimeUnit.MILLISECONDS.sleep(100);

        List<Member> members = LiveDataTestUtil.getValue(memberDao.getAllLive());

        assertThat(members.size(), is(TestData.MEMBERS.size() - 1));
        assertFalse(members.contains(TestData.MEMBER_3));

        players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size()));

        player = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayerByPlayerId(TestData.PLAYER_3.getId()));

        assertNotNull(player);
        assertThat(player, is(TestData.PLAYER_3));
        assertThat(player.getId(), is(TestData.PLAYER_3.getId()));
        assertThat(player.getPlayerTeamId(), is(TestData.PLAYER_3.getPlayerTeamId()));
        assertThat(player.getMemberNickname(), is(not(TestData.PLAYER_3.getMemberNickname())));
        assertNull(player.getMemberNickname());
    }

    @Test
    public void getAllPlayersAfterInsertingAllAndDeletingAnAssociatedPlayerTeam() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size()));
        assertTrue(players.contains(TestData.PLAYER_1));

        List<Player> playerTeam1Players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByPlayerTeamId(TestData.PLAYER_TEAM_1.getId()));
        assertThat(playerTeam1Players.size(), is(1));
        assertThat(playerTeam1Players.get(0), is(TestData.PLAYER_1));

        playerTeamDao.delete(TestData.PLAYER_TEAM_1);
        TimeUnit.MILLISECONDS.sleep(100);

        List<PlayerTeam> playerTeams = LiveDataTestUtil.getValue(playerTeamDao.getAll());
        assertThat(playerTeams.size(), is(TestData.PLAYER_TEAMS.size() - 1));
        assertFalse(playerTeams.contains(TestData.PLAYER_TEAM_1));

        players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size() - 1));
        assertFalse(players.contains(TestData.PLAYER_1));

        playerTeam1Players = LiveDataTestUtil.getValue(playerDao.findLiveDataPlayersByPlayerTeamId(TestData.PLAYER_TEAM_1.getId()));
        assertTrue(playerTeam1Players.isEmpty());
    }

    @Test
    public void getAllPlayersAfterInsertingAllAndDeletingAnAssociatedGameRecord() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        List<Player> players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size()));
        assertTrue(players.contains(TestData.PLAYER_1));

        gameRecordDao.delete(TestData.GAME_RECORD_1);
        TimeUnit.MILLISECONDS.sleep(100);

        players = LiveDataTestUtil.getValue(playerDao.getAll());

        assertThat(players.size(), is(TestData.PLAYERS.size() - 3));
        assertFalse(players.contains(TestData.PLAYER_1));
    }

    @Test
    public void getAllPlayersWithRatingChangesWhenAllInserted() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));

        //3 Players have skill rating changes - 1 has 2, and the other 2 have 1 each. So should be 3 in the list.
        List<PlayerWithRatingChanges> playersWithRatingChanges = LiveDataTestUtil.getValue(playerDao.getAllPlayersWithRatingChanges());
        assertThat(playersWithRatingChanges.size(), is(TestData.PLAYERS.size()));
    }

    @Test
    public void getPlayerWithRatingChangesByPlayerIdWhenAllInserted() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        bgCategoryDao.insertAll(TestData.BG_CATEGORIES.toArray(new BgCategory[TestData.BG_CATEGORIES.size()]));
        playerSkillRatingChangeDao.insertAll(TestData.PLAYER_SKILL_RATING_CHANGES.toArray(new PlayerSkillRatingChange[TestData.PLAYER_SKILL_RATING_CHANGES.size()]));

        //Player 1 has 2 skill rating changes
        PlayerWithRatingChanges playerWithRatingChanges = LiveDataTestUtil.getValue(playerDao.getPlayerWithRatingChangesByPlayerId(TestData.PLAYER_1.getId()));
        assertThat(playerWithRatingChanges.getPlayerSkillRatingChanges().size(), is(2));

        //Player 5 has 0 skill rating changes
        playerWithRatingChanges = LiveDataTestUtil.getValue(playerDao.getPlayerWithRatingChangesByPlayerId(TestData.PLAYER_5.getId()));
        assertTrue(playerWithRatingChanges.getPlayerSkillRatingChanges().isEmpty());
    }

    @Test
    public void getPlayerIdByPlayerTeamIdAndMemberNicknameWhenAllInserted() throws InterruptedException {
        playerDao.insertAll(TestData.PLAYERS.toArray(new Player[TestData.PLAYERS.size()]));
        int playerId = playerDao.getPlayerIdByPlayerTeamIdAndMemberNickname(TestData.PLAYER_1.getPlayerTeamId(), TestData.PLAYER_1.getMemberNickname());

        assertThat(playerId, is(TestData.PLAYER_1.getId()));
    }
}
