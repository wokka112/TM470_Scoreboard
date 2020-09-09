package com.floatingpanda.scoreboard.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.floatingpanda.scoreboard.data.daos.AssignedCategoryDao;
import com.floatingpanda.scoreboard.data.daos.BgCategoryDao;
import com.floatingpanda.scoreboard.data.daos.BoardGameDao;
import com.floatingpanda.scoreboard.data.daos.GameRecordDao;
import com.floatingpanda.scoreboard.data.daos.GroupCategorySkillRatingDao;
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.daos.GroupMemberDao;
import com.floatingpanda.scoreboard.data.daos.GroupMonthlyScoreDao;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.daos.PlayModeDao;
import com.floatingpanda.scoreboard.data.daos.PlayerDao;
import com.floatingpanda.scoreboard.data.daos.PlayerSkillRatingChangeDao;
import com.floatingpanda.scoreboard.data.daos.PlayerTeamDao;
import com.floatingpanda.scoreboard.data.daos.ScoreDao;
import com.floatingpanda.scoreboard.data.entities.AssignedCategory;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.GroupCategorySkillRating;
import com.floatingpanda.scoreboard.data.entities.GroupMember;
import com.floatingpanda.scoreboard.data.entities.GroupMonthlyScore;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerSkillRatingChange;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
import com.floatingpanda.scoreboard.data.entities.Score;
import com.floatingpanda.scoreboard.data.database_views.GroupCategorySkillRatingWithMemberDetailsView;
import com.floatingpanda.scoreboard.typeconverters.DateTypeConverter;
import com.floatingpanda.scoreboard.typeconverters.PlayModeTypeConverter;
import com.floatingpanda.scoreboard.typeconverters.TeamOptionTypeConverter;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {AssignedCategory.class, BgCategory.class, BoardGame.class, Group.class,
        GroupMember.class, Member.class, PlayMode.class, GameRecord.class, Player.class,
        PlayerTeam.class, GroupMonthlyScore.class, Score.class, GroupCategorySkillRating.class,
        PlayerSkillRatingChange.class}, version = 40, exportSchema = false,
        views = {GroupCategorySkillRatingWithMemberDetailsView.class})
@TypeConverters({DateTypeConverter.class, PlayModeTypeConverter.class, TeamOptionTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract MemberDao memberDao();
    public abstract BgCategoryDao bgCategoryDao();
    public abstract GroupDao groupDao();
    public abstract BoardGameDao boardGameDao();
    public abstract AssignedCategoryDao assignedCategoryDao();
    public abstract PlayModeDao playModeDao();
    public abstract GroupMemberDao groupMemberDao();
    public abstract GameRecordDao gameRecordDao();
    public abstract PlayerDao playerDao();
    public abstract PlayerTeamDao playerTeamDao();
    public abstract ScoreDao scoreDao();
    public abstract GroupMonthlyScoreDao groupMonthlyScoreDao();
    public abstract GroupCategorySkillRatingDao groupCategorySkillRatingDao();
    public abstract PlayerSkillRatingChangeDao playerSkillRatingChangeDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //TODO implement proper migrating method when releasing rather than using fallbackToDestructiveMigration()
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    final byte[] passphrase = SQLiteDatabase.getBytes("TestPassPhrase".toCharArray());
                    final SupportFactory factory = new SupportFactory(passphrase);
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "scoreboard_database")
                            .fallbackToDestructiveMigration()
                            //.allowMainThreadQueries() // Use for testing purposes
                            .addCallback(sRoomDatabaseCallback)
                            .openHelperFactory(factory)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static ExecutorService getExecutorService() {
        return databaseWriteExecutor;
    }

    //TODO comment out once done testing
    private static Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            databaseWriteExecutor.execute(() -> {

                // MEMBER ENTRIES //
                MemberDao memberDao = INSTANCE.memberDao();
                memberDao.deleteAll();

                Member member = new Member("Bill", "Bill", "TBA");
                memberDao.insert(member);
                member = new Member("Frank",  "Frank", "TBA");
                memberDao.insert(member);
                member = new Member("Bailey","Bailey", "TBA");
                memberDao.insert(member);
                member = new Member("Robert", "", "TBA");
                memberDao.insert(member);
                member = new Member("Ragnar", "", "TBA");
                memberDao.insert(member);
                member = new Member("Bjorn", "", "TBA");
                memberDao.insert(member);
                member = new Member("Rick", "", "TBA");
                memberDao.insert(member);
                member = new Member("Bob", "", "TBA");
                memberDao.insert(member);

                // GROUP ENTRIES //
                GroupDao groupDao = INSTANCE.groupDao();
                groupDao.deleteAll();

                Group group = new Group("Ragnarok", "", "", "TBA", "TBA");
                groupDao.insert(group);
                group = new Group("The Monday Knights", "", "", "TBA", "TBA");
                groupDao.insert(group);
                group = new Group("The Hospitallers", "", "", "TBA", "TBA");
                groupDao.insert(group);
                group = new Group("The Templars", "", "", "TBA", "TBA");
                groupDao.insert(group);
                group = new Group("The Crusaders", "", "", "TBA", "TBA");
                groupDao.insert(group);
                group = new Group("The Saracens", "", "", "TBA", "TBA");
                groupDao.insert(group);
                group = new Group("The Turks", "", "", "TBA", "TBA");
                groupDao.insert(group);

                // GROUP MEMBER ENTRIES //
                GroupMemberDao groupMemberDao = INSTANCE.groupMemberDao();

                Member member1 = memberDao.findNonLiveDataByNickname("Bill");
                Member member2 = memberDao.findNonLiveDataByNickname("Frank");
                Member member3 = memberDao.findNonLiveDataByNickname("Bailey");
                Member member4 = memberDao.findNonLiveDataByNickname("Robert");
                Member member5 = memberDao.findNonLiveDataByNickname("Ragnar");
                Member member6 = memberDao.findNonLiveDataByNickname("Bjorn");
                Member member7 = memberDao.findNonLiveDataByNickname("Rick");
                Member member8 = memberDao.findNonLiveDataByNickname("Bob");

                Group group1 = groupDao.findNonLiveDataByName("Ragnarok");
                Group group2 = groupDao.findNonLiveDataByName("The Monday Knights");
                Group group3 = groupDao.findNonLiveDataByName("The Hospitallers");

                GroupMember groupMember = new GroupMember(group1.getId(), member1.getId());
                groupMemberDao.insert(groupMember);
                groupMember = new GroupMember(group1.getId(), member2.getId());
                groupMemberDao.insert(groupMember);
                groupMember = new GroupMember(group1.getId(), member3.getId());
                groupMemberDao.insert(groupMember);
                groupMember = new GroupMember(group1.getId(), member4.getId());
                groupMemberDao.insert(groupMember);
                groupMember = new GroupMember(group1.getId(), member5.getId());
                groupMemberDao.insert(groupMember);
                groupMember = new GroupMember(group1.getId(), member6.getId());
                groupMemberDao.insert(groupMember);
                groupMember = new GroupMember(group1.getId(), member7.getId());
                groupMemberDao.insert(groupMember);
                groupMember = new GroupMember(group1.getId(), member8.getId());
                groupMemberDao.insert(groupMember);

                groupMember = new GroupMember(group2.getId(), member1.getId());
                groupMemberDao.insert(groupMember);
                groupMember = new GroupMember(group2.getId(), member3.getId());
                groupMemberDao.insert(groupMember);

                groupMember = new GroupMember(group3.getId(), member3.getId());
                groupMemberDao.insert(groupMember);

                // BG CATEGORY ENTRIES //
                BgCategoryDao boardGameCategoryDao = INSTANCE.bgCategoryDao();

                BgCategory strategy = new BgCategory("Strategy");
                boardGameCategoryDao.insert(strategy);
                strategy = boardGameCategoryDao.findNonLiveDataByName(strategy.getCategoryName());
                BgCategory skill = new BgCategory("Skill");
                boardGameCategoryDao.insert(skill);
                skill = boardGameCategoryDao.findNonLiveDataByName(skill.getCategoryName());
                BgCategory luck = new BgCategory("Luck");
                boardGameCategoryDao.insert(luck);
                luck = boardGameCategoryDao.findNonLiveDataByName(luck.getCategoryName());
                BgCategory gambling = new BgCategory("Gambling");
                boardGameCategoryDao.insert(gambling);
                gambling = boardGameCategoryDao.findNonLiveDataByName(gambling.getCategoryName());

                // BOARD GAME, PLAY MODES AND ASSIGNED CATEGORIES ENTRIES //
                // BOARD GAMES //
                BoardGameDao bgDao = INSTANCE.boardGameDao();

                BoardGame.TeamOption teamOption = BoardGame.TeamOption.TEAMS_ONLY;
                BoardGame bg = new BoardGame("Medieval", 4, 1, 8, teamOption,
                        "N/A", "N/A", "N/A", "N/A");

                teamOption = BoardGame.TeamOption.NO_TEAMS;
                BoardGame bg1 = new BoardGame("Monopoly", 2, 1, 8, teamOption,
                        "N/A", "N/A", "N/A", "N/A");

                teamOption = BoardGame.TeamOption.TEAMS_AND_SOLOS_ALLOWED;
                BoardGame bg2 = new BoardGame("Go", 5, 1, 8, teamOption,
                        "N/A", "N/A", "N/A", "N/A");

                teamOption = BoardGame.TeamOption.NO_TEAMS;
                BoardGame bg3 = new BoardGame("Game of Life", 1, 1, 8, teamOption,
                        "N/A", "N/A", "N/A", "N/A");

                teamOption = BoardGame.TeamOption.TEAMS_AND_SOLOS_ALLOWED;
                BoardGame bg4 = new BoardGame("Dawn of Madness", 4, 1, 8, teamOption,
                        "N/A", "N/A", "N/A", "N/A");

                teamOption = BoardGame.TeamOption.NO_TEAMS;
                BoardGame bg5 = new BoardGame("No Category Bg", 3, 1, 8, teamOption,
                         "N/A", "N/A", "N/A", "N/A");

                List<AssignedCategory> acs = new ArrayList<>();

                // PLAY MODES AND ASSIGNED CATEGORIES //
                PlayModeDao playModeDao = INSTANCE.playModeDao();
                AssignedCategoryDao acDao = INSTANCE.assignedCategoryDao();

                List<PlayMode.PlayModeEnum> playModes = new ArrayList<>();
                playModes.add(PlayMode.PlayModeEnum.COMPETITIVE);

                bgDao.insert(bg);
                bg = bgDao.findNonLiveDataByName(bg.getBgName());

                PlayMode playMode = new PlayMode(bg.getId(), playModes.get(0));
                playModeDao.insert(playMode);

                acs.add(new AssignedCategory(bg.getId(), strategy.getId()));
                acs.add(new AssignedCategory(bg.getId(), luck.getId()));
                acDao.insertAll(acs.toArray(new AssignedCategory[acs.size()]));

                Log.w("Database.java", "Inserted bg");

                playModes.add(PlayMode.PlayModeEnum.COOPERATIVE);

                bgDao.insert(bg1);
                bg1 = bgDao.findNonLiveDataByName(bg1.getBgName());

                playMode = new PlayMode(bg1.getId(), playModes.get(0));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg1.getId(), playModes.get(1));
                playModeDao.insert(playMode);

                acs.add(new AssignedCategory(bg1.getId(), gambling.getId()));
                acDao.insert(acs.get(0));

                Log.w("Database.java", "Inserted bg1");

                bgDao.insert(bg2);
                bg2 = bgDao.findNonLiveDataByName(bg2.getBgName());

                playMode = new PlayMode(bg2.getId(), playModes.get(0));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg2.getId(), playModes.get(1));
                playModeDao.insert(playMode);

                acs.add(new AssignedCategory(bg2.getId(), gambling.getId()));
                Log.w("Database.java", "Inserted bg2");

                playModes.add(PlayMode.PlayModeEnum.SOLITAIRE);

                bgDao.insert(bg3);
                bg3 = bgDao.findNonLiveDataByName(bg3.getBgName());

                playMode = new PlayMode(bg3.getId(), playModes.get(0));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg3.getId(), playModes.get(1));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg3.getId(), playModes.get(2));
                playModeDao.insert(playMode);

                acs.add(new AssignedCategory(bg3.getId(), strategy.getId()));
                Log.w("Database.java", "Inserted bg3");

                bgDao.insert(bg4);
                bg4 = bgDao.findNonLiveDataByName(bg4.getBgName());

                playMode = new PlayMode(bg4.getId(), playModes.get(0));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg4.getId(), playModes.get(1));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg4.getId(), playModes.get(2));
                playModeDao.insert(playMode);

                acs.add(new AssignedCategory(bg4.getId(), luck.getId()));
                Log.w("Database.java", "Inserted bg4");

                acDao.insertAll(acs.toArray(new AssignedCategory[acs.size()]));

                bgDao.insert(bg5);
                bg5 = bgDao.findNonLiveDataByName(bg5.getBgName());

                playMode = new PlayMode(bg5.getId(), playModes.get(0));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg5.getId(), playModes.get(1));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg5.getId(), playModes.get(2));
                playModeDao.insert(playMode);

                Log.w("Database.java", "Inserted bg5");

                // GAME RECORD ENTRIES //
                GameRecordDao gameRecordDao = INSTANCE.gameRecordDao();
                gameRecordDao.deleteAll();

                //group 1 - 3 game records
                // record 1 - Monopoly, no teams, 1 player per position, 8 players.
                GameRecord gameRecord1 = new GameRecord(group1.getId(), bg1.getBgName(), bg1.getDifficulty(), new Date(), false, PlayMode.PlayModeEnum.COMPETITIVE,
                        8);
                gameRecordDao.insert(gameRecord1);

                // record 2 - Medieval, 2 teams in 1st place, 1 in 3rd, 1 in 4th, 4 teams.
                // team 1 has 2 players, team 2 has 3, teams 3 and 4 each have 1.
                GameRecord gameRecord2 = new GameRecord(group1.getId(), bg.getBgName(), bg.getDifficulty(), new Date(), true, PlayMode.PlayModeEnum.COMPETITIVE,
                        4);
                gameRecordDao.insert(gameRecord2);

                // record 3 - Medieval, 3 teams in 1st place, 1 in 4th, 4 teams.
                // team 1 has 2 players, team 2 has 2 players, team 3 has 1 player, team 4 has 4 players.
                GameRecord gameRecord3 = new GameRecord(group1.getId(), bg.getBgName(), bg.getDifficulty(), new Date(), true, PlayMode.PlayModeEnum.COMPETITIVE,
                        4);
                gameRecordDao.insert(gameRecord3);

                //Can only have 1 team for cooperative and solitaire games.
                // record 4 - Dawn of Madness, cooperative, 1 team, 4 players, win.
                GameRecord gameRecord4 = new GameRecord(group1.getId(), bg4.getBgName(), bg4.getDifficulty(), new Date(), true, PlayMode.PlayModeEnum.COOPERATIVE,
                        1, true);
                gameRecordDao.insert(gameRecord4);

                //record 5 - Dawn of Madness, cooperative, 1 team, 6 players, lose.
                GameRecord gameRecord5 = new GameRecord(group1.getId(), bg4.getBgName(), bg4.getDifficulty(), new Date(), true, PlayMode.PlayModeEnum.COOPERATIVE,
                        1, false);
                gameRecordDao.insert(gameRecord5);

                // record 6 - Dawn of Madness, solitaire, 1 player, win.
                GameRecord gameRecord6 = new GameRecord(group1.getId(), bg4.getBgName(), bg4.getDifficulty(), new Date(), true, PlayMode.PlayModeEnum.SOLITAIRE,
                        1, true);
                gameRecordDao.insert(gameRecord6);

                // record 7 - Dawn of Madness, solitaire, 1 player, lose
                GameRecord gameRecord7 = new GameRecord(group1.getId(), bg4.getBgName(), bg4.getDifficulty(), new Date(), true, PlayMode.PlayModeEnum.SOLITAIRE,
                        1, false);
                gameRecordDao.insert(gameRecord7);

                gameRecord1 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord1.getGroupId(), gameRecord1.getBoardGameName(), gameRecord1.getDateTime().getTime());
                gameRecord2 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord2.getGroupId(), gameRecord2.getBoardGameName(), gameRecord2.getDateTime().getTime());
                gameRecord3 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord3.getGroupId(), gameRecord3.getBoardGameName(), gameRecord3.getDateTime().getTime());
                gameRecord4 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord4.getGroupId(), gameRecord4.getBoardGameName(), gameRecord4.getDateTime().getTime());
                gameRecord5 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord5.getGroupId(), gameRecord5.getBoardGameName(), gameRecord5.getDateTime().getTime());
                gameRecord6 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord6.getGroupId(), gameRecord6.getBoardGameName(), gameRecord6.getDateTime().getTime());
                gameRecord7 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord7.getGroupId(), gameRecord7.getBoardGameName(), gameRecord7.getDateTime().getTime());

                // PLAYER TEAM ENTRIES //
                PlayerTeamDao playerTeamDao = INSTANCE.playerTeamDao();
                playerTeamDao.deleteAll();

                // Game record 1's teams //
                // No teams, 8 players (i.e., 8 teams, each 1 player).
                PlayerTeam record1PlayerTeam1 = new PlayerTeam(1, gameRecord1.getId(), 1, 40);
                PlayerTeam record1PlayerTeam2 = new PlayerTeam(2, gameRecord1.getId(), 2, 35);
                PlayerTeam record1PlayerTeam3 = new PlayerTeam(3, gameRecord1.getId(), 3, 30);
                PlayerTeam record1PlayerTeam4 = new PlayerTeam(4, gameRecord1.getId(), 4, 25);
                PlayerTeam record1PlayerTeam5 = new PlayerTeam(5, gameRecord1.getId(), 5, 20);
                PlayerTeam record1PlayerTeam6 = new PlayerTeam(6, gameRecord1.getId(), 6, 15);
                PlayerTeam record1PlayerTeam7 = new PlayerTeam(7, gameRecord1.getId(), 7, 10);
                PlayerTeam record1PlayerTeam8 = new PlayerTeam(8, gameRecord1.getId(), 8, 5);

                List<PlayerTeam> record1PlayerTeams = new ArrayList<>();
                record1PlayerTeams.add(record1PlayerTeam1);
                record1PlayerTeams.add(record1PlayerTeam2);
                record1PlayerTeams.add(record1PlayerTeam3);
                record1PlayerTeams.add(record1PlayerTeam4);
                record1PlayerTeams.add(record1PlayerTeam5);
                record1PlayerTeams.add(record1PlayerTeam6);
                record1PlayerTeams.add(record1PlayerTeam7);
                record1PlayerTeams.add(record1PlayerTeam8);

                playerTeamDao.insertAll(record1PlayerTeams.toArray(new PlayerTeam[record1PlayerTeams.size()]));

                // Game record 2's teams //
                // team 1 has 2 players, team 2 has 3, teams 3 and 4 each have 1.
                // team 1 and 2 in 1st place, team 3 in 3rd and team 4 in 4th.
                PlayerTeam record2PlayerTeam1 = new PlayerTeam(1, gameRecord2.getId(), 1, 20);
                PlayerTeam record2PlayerTeam2 = new PlayerTeam(2, gameRecord2.getId(), 1, 20);
                PlayerTeam record2PlayerTeam3 = new PlayerTeam(3, gameRecord2.getId(), 3, 10);
                PlayerTeam record2PlayerTeam4 = new PlayerTeam(4, gameRecord2.getId(), 4, 5);

                List<PlayerTeam> record2PlayerTeams = new ArrayList<>();
                record2PlayerTeams.add(record2PlayerTeam1);
                record2PlayerTeams.add(record2PlayerTeam2);
                record2PlayerTeams.add(record2PlayerTeam3);
                record2PlayerTeams.add(record2PlayerTeam4);

                playerTeamDao.insertAll(record2PlayerTeams.toArray(new PlayerTeam[record2PlayerTeams.size()]));

                // Game record 3's teams //
                // team 1 has 2 players, team 2 has 2 players, team 3 has 1 player, team 4 has 4 players.
                // teams 1, 2 and 3 in 1st place, team 4 in 4th place.
                PlayerTeam record3PlayerTeam1 = new PlayerTeam(1, gameRecord3.getId(), 1, 20);
                PlayerTeam record3PlayerTeam2 = new PlayerTeam(2, gameRecord3.getId(), 1, 20);
                PlayerTeam record3PlayerTeam3 = new PlayerTeam(3, gameRecord3.getId(), 1, 20);
                PlayerTeam record3PlayerTeam4 = new PlayerTeam(4, gameRecord3.getId(), 4, 05);

                List<PlayerTeam> record3PlayerTeams = new ArrayList<>();
                record3PlayerTeams.add(record3PlayerTeam1);
                record3PlayerTeams.add(record3PlayerTeam2);
                record3PlayerTeams.add(record3PlayerTeam3);
                record3PlayerTeams.add(record3PlayerTeam4);

                playerTeamDao.insertAll(record3PlayerTeams.toArray(new PlayerTeam[record3PlayerTeams.size()]));

                // Game record 4's teams //
                //record 4 - Dawn of Madness, cooperative, 1 team, 4 players, win.
                PlayerTeam record4PlayerTeam = new PlayerTeam(1, gameRecord4.getId(), 1, 30);

                playerTeamDao.insert(record4PlayerTeam);

                // Game record 5's teams //
                ////record 5 - Dawn of Madness, cooperative, 1 team, 6 players, lose.
                PlayerTeam record5PlayerTeam = new PlayerTeam(1, gameRecord5.getId(), 1, 0);

                playerTeamDao.insert(record5PlayerTeam);

                // Game record 6's teams //
                // record 6 - Dawn of Madness, solitaire, 1 player, win.
                PlayerTeam record6PlayerTeam = new PlayerTeam(1, gameRecord6.getId(), 1, 40);

                playerTeamDao.insert(record6PlayerTeam);

                // Game record 7's teams //
                // record 7 - Dawn of Madness, solitaire, 1 player, lose
                PlayerTeam record7PlayerTeam = new PlayerTeam(1, gameRecord7.getId(), 1, 0);

                playerTeamDao.insert(record7PlayerTeam);

                // PLAYER ENTRIES //
                PlayerDao playerDao = INSTANCE.playerDao();
                playerDao.deleteAll();

                // GAME RECORD 1'S TEAMS //
                record1PlayerTeam1 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record1PlayerTeam1.getTeamNumber(), record1PlayerTeam1.getRecordId());
                record1PlayerTeam2 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record1PlayerTeam2.getTeamNumber(), record1PlayerTeam2.getRecordId());
                record1PlayerTeam3 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record1PlayerTeam3.getTeamNumber(), record1PlayerTeam3.getRecordId());
                record1PlayerTeam4 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record1PlayerTeam4.getTeamNumber(), record1PlayerTeam4.getRecordId());
                record1PlayerTeam5 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record1PlayerTeam5.getTeamNumber(), record1PlayerTeam5.getRecordId());
                record1PlayerTeam6 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record1PlayerTeam6.getTeamNumber(), record1PlayerTeam6.getRecordId());
                record1PlayerTeam7 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record1PlayerTeam7.getTeamNumber(), record1PlayerTeam7.getRecordId());
                record1PlayerTeam8 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record1PlayerTeam8.getTeamNumber(), record1PlayerTeam8.getRecordId());

                List<Player> record1Players = new ArrayList<>();
                record1Players.add(new Player(record1PlayerTeam1.getId(), member1.getNickname()));
                record1Players.add(new Player(record1PlayerTeam2.getId(), member2.getNickname()));
                record1Players.add(new Player(record1PlayerTeam3.getId(), member3.getNickname()));
                record1Players.add(new Player(record1PlayerTeam4.getId(), member4.getNickname()));
                record1Players.add(new Player(record1PlayerTeam5.getId(), member5.getNickname()));
                record1Players.add(new Player(record1PlayerTeam6.getId(), member6.getNickname()));
                record1Players.add(new Player(record1PlayerTeam7.getId(), member7.getNickname()));
                record1Players.add(new Player(record1PlayerTeam8.getId(), member8.getNickname()));

                playerDao.insertAll(record1Players.toArray(new Player[record1Players.size()]));

                // GAME RECORD 2's TEAMS //
                // team 1 has 2 players, team 2 has 3, teams 3 and 4 each have 1.
                record2PlayerTeam1 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record2PlayerTeam1.getTeamNumber(), record2PlayerTeam1.getRecordId());
                record2PlayerTeam2 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record2PlayerTeam2.getTeamNumber(), record2PlayerTeam2.getRecordId());
                record2PlayerTeam3 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record2PlayerTeam3.getTeamNumber(), record2PlayerTeam3.getRecordId());
                record2PlayerTeam4 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record2PlayerTeam4.getTeamNumber(), record2PlayerTeam4.getRecordId());

                List<Player> record2Players = new ArrayList<>();
                record2Players.add(new Player(record2PlayerTeam1.getId(), member1.getNickname()));
                record2Players.add(new Player(record2PlayerTeam1.getId(), member2.getNickname()));
                record2Players.add(new Player(record2PlayerTeam2.getId(), member3.getNickname()));
                record2Players.add(new Player(record2PlayerTeam2.getId(), member4.getNickname()));
                record2Players.add(new Player(record2PlayerTeam2.getId(), member5.getNickname()));
                record2Players.add(new Player(record2PlayerTeam3.getId(), member6.getNickname()));
                record2Players.add(new Player(record2PlayerTeam4.getId(), member7.getNickname()));

                playerDao.insertAll(record2Players.toArray(new Player[record2Players.size()]));

                // GAME RECORD 3's TEAMS//
                // team 1 has 2 players, team 2 has 1 players, team 3 has 1 player, team 4 has 4 players.
                record3PlayerTeam1 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record3PlayerTeam1.getTeamNumber(), record3PlayerTeam1.getRecordId());
                record3PlayerTeam2 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record3PlayerTeam2.getTeamNumber(), record3PlayerTeam2.getRecordId());
                record3PlayerTeam3 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record3PlayerTeam3.getTeamNumber(), record3PlayerTeam3.getRecordId());
                record3PlayerTeam4 = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record3PlayerTeam4.getTeamNumber(), record3PlayerTeam4.getRecordId());

                List<Player> record3Players = new ArrayList<>();
                record3Players.add(new Player(record3PlayerTeam1.getId(), member1.getNickname()));
                record3Players.add(new Player(record3PlayerTeam1.getId(), member2.getNickname()));
                record3Players.add(new Player(record3PlayerTeam2.getId(), member3.getNickname()));
                record3Players.add(new Player(record3PlayerTeam3.getId(), member4.getNickname()));
                record3Players.add(new Player(record3PlayerTeam4.getId(), member5.getNickname()));
                record3Players.add(new Player(record3PlayerTeam4.getId(), member6.getNickname()));
                record3Players.add(new Player(record3PlayerTeam4.getId(), member7.getNickname()));
                record3Players.add(new Player(record3PlayerTeam4.getId(), member8.getNickname()));

                playerDao.insertAll(record3Players.toArray(new Player[record3Players.size()]));

                // GAME RECORD 4's TEAMS //
                // record 4 - Dawn of Madness, cooperative, 1 team, 4 players, win.
                record4PlayerTeam = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record4PlayerTeam.getTeamNumber(), record4PlayerTeam.getRecordId());

                List<Player> record4Players = new ArrayList<>();
                record4Players.add(new Player(record4PlayerTeam.getId(), member1.getNickname()));
                record4Players.add(new Player(record4PlayerTeam.getId(), member2.getNickname()));
                record4Players.add(new Player(record4PlayerTeam.getId(), member3.getNickname()));
                record4Players.add(new Player(record4PlayerTeam.getId(), member4.getNickname()));

                playerDao.insertAll(record4Players.toArray(new Player[record4Players.size()]));

                // GAME RECORD 5's TEAMS //
                //record 5 - Dawn of Madness, cooperative, 1 team, 6 players, lose.
                record5PlayerTeam = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record5PlayerTeam.getTeamNumber(), record5PlayerTeam.getRecordId());

                List<Player> record5Players = new ArrayList<>();
                record5Players.add(new Player(record5PlayerTeam.getId(), member1.getNickname()));
                record5Players.add(new Player(record5PlayerTeam.getId(), member2.getNickname()));
                record5Players.add(new Player(record5PlayerTeam.getId(), member3.getNickname()));
                record5Players.add(new Player(record5PlayerTeam.getId(), member4.getNickname()));
                record5Players.add(new Player(record5PlayerTeam.getId(), member5.getNickname()));
                record5Players.add(new Player(record5PlayerTeam.getId(), member6.getNickname()));

                playerDao.insertAll(record5Players.toArray(new Player[record5Players.size()]));

                // GAME RECORD 6's TEAMS //
                // record 6 - Dawn of Madness, solitaire, 1 player, win.
                record6PlayerTeam = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record6PlayerTeam.getTeamNumber(), record6PlayerTeam.getRecordId());

                Player record6Player = new Player(record6PlayerTeam.getId(), member1.getNickname());

                playerDao.insert(record6Player);

                // GAME RECORD 7's TEAMS //
                // record 7 - Dawn of Madness, solitaire, 1 player, lose
                record7PlayerTeam = playerTeamDao.findNonLiveDataPlayerTeamByTeamNumberAndRecordId(record7PlayerTeam.getTeamNumber(), record7PlayerTeam.getRecordId());

                Player record7Player = new Player(record7PlayerTeam.getId(), member1.getNickname());

                playerDao.insert(record7Player);

                // GROUP 1 MONTHLY SCORES //
                GroupMonthlyScoreDao groupMonthlyScoreDao = INSTANCE.groupMonthlyScoreDao();
                ScoreDao scoreDao = INSTANCE.scoreDao();

                //Group 1 scores
                //Tests:
                // Group monthly score 1
                int groupId = group1.getId();
                int year = 2019;
                int quarter = 1;
                int month = 1;

                GroupMonthlyScore groupMonthlyScore1 = new GroupMonthlyScore(groupId, year, quarter, month);
                int groupMonthlyScore1Id = (int) groupMonthlyScoreDao.insert(groupMonthlyScore1);

                List<Score> scores1 = new ArrayList<Score>();
                // Jan Everyone scores, one person per position
                // - Player 1 100
                scores1.add(new Score(groupMonthlyScore1Id, member1.getId(), 100));
                // - Player 2 90
                scores1.add(new Score(groupMonthlyScore1Id, member2.getId(), 90));
                // - Player 3 80
                scores1.add(new Score(groupMonthlyScore1Id, member3.getId(), 80));
                // - Player 4 70
                scores1.add(new Score(groupMonthlyScore1Id, member4.getId(), 70));
                // - Player 5 55
                scores1.add(new Score(groupMonthlyScore1Id, member5.getId(), 55));
                // - Player 6 43
                scores1.add(new Score(groupMonthlyScore1Id, member6.getId(), 43));
                // - Player 7 18
                scores1.add(new Score(groupMonthlyScore1Id, member7.getId(), 18));
                // - Player 8 2
                scores1.add(new Score(groupMonthlyScore1Id, member8.getId(), 2));

                scoreDao.insertAll(scores1.toArray(new Score[scores1.size()]));

                // Group monthly score 2
                month = 2;

                GroupMonthlyScore groupMonthlyScore2 = new GroupMonthlyScore(groupId, year, quarter, month);
                int groupMonthlyScore2Id = (int) groupMonthlyScoreDao.insert(groupMonthlyScore2);

                List<Score> scores2 = new ArrayList<Score>();
                // Feb Everyone scores, 4 people for position 1.
                // - Player 1 77
                scores2.add(new Score(groupMonthlyScore2Id, member1.getId(), 77));
                // - Player 2 77
                scores2.add(new Score(groupMonthlyScore2Id, member2.getId(), 77));
                // - Player 3 77
                scores2.add(new Score(groupMonthlyScore2Id, member3.getId(), 77));
                // - Player 4 77
                scores2.add(new Score(groupMonthlyScore2Id, member4.getId(), 77));
                // - Player 5 34
                scores2.add(new Score(groupMonthlyScore2Id, member5.getId(), 34));
                // - Player 6 34
                scores2.add(new Score(groupMonthlyScore2Id, member6.getId(), 34));
                // - Player 7 17
                scores2.add(new Score(groupMonthlyScore2Id, member7.getId(), 17));
                // - Player 8 8
                scores2.add(new Score(groupMonthlyScore2Id, member8.getId(), 8));

                scoreDao.insertAll(scores2.toArray(new Score[scores2.size()]));

                // Group monthly score 3
                month = 3;

                GroupMonthlyScore groupMonthlyScore3 = new GroupMonthlyScore(groupId, year, quarter, month);
                int groupMonthlyScore3Id = (int) groupMonthlyScoreDao.insert(groupMonthlyScore3);

                List<Score> scores3 = new ArrayList<Score>();
                // Mar Everyone scores, 2 people for position 1, 4 for position 3.
                // - Player 1 100
                scores3.add(new Score(groupMonthlyScore3Id, member1.getId(), 100));
                // - Player 2 100
                scores3.add(new Score(groupMonthlyScore3Id, member2.getId(), 100));
                // - Player 3 62
                scores3.add(new Score(groupMonthlyScore3Id, member3.getId(), 62));
                // - Player 4 62
                scores3.add(new Score(groupMonthlyScore3Id, member4.getId(), 62));
                // - Player 5 62
                scores3.add(new Score(groupMonthlyScore3Id, member5.getId(), 62));
                // - Player 6 62
                scores3.add(new Score(groupMonthlyScore3Id, member6.getId(), 62));
                // - Player 7 62
                scores3.add(new Score(groupMonthlyScore3Id, member7.getId(), 62));
                // - Player 8 62
                scores3.add(new Score(groupMonthlyScore3Id, member8.getId(), 62));

                scoreDao.insertAll(scores3.toArray(new Score[scores3.size()]));

                // Group monthly score 4
                quarter = 2;
                month = 4;

                GroupMonthlyScore groupMonthlyScore4 = new GroupMonthlyScore(groupId, year, quarter, month);
                int groupMonthlyScore4Id = (int) groupMonthlyScoreDao.insert(groupMonthlyScore4);

                List<Score> scores4 = new ArrayList<>();
                // Apr Everyone scores, 1 for position 1, 2 for position 2
                // - Player 1 64
                scores4.add(new Score(groupMonthlyScore4Id, member1.getId(), 64));
                // - Player 2 40
                scores4.add(new Score(groupMonthlyScore4Id, member2.getId(), 40));
                // - Player 3 40
                scores4.add(new Score(groupMonthlyScore4Id, member3.getId(), 40));
                // - Player 4 35
                scores4.add(new Score(groupMonthlyScore4Id, member4.getId(), 35));
                // - Player 5 32
                scores4.add(new Score(groupMonthlyScore4Id, member5.getId(), 32));
                // - Player 6 28
                scores4.add(new Score(groupMonthlyScore4Id, member6.getId(), 28));
                // - Player 7 20
                scores4.add(new Score(groupMonthlyScore4Id, member7.getId(), 20));
                // - Player 8 15
                scores4.add(new Score(groupMonthlyScore4Id, member8.getId(), 15));

                scoreDao.insertAll(scores4.toArray(new Score[scores4.size()]));

                // Group monthly score 5
                month = 5;

                GroupMonthlyScore groupMonthlyScore5 = new GroupMonthlyScore(groupId, year, quarter, month);
                int groupMonthlyScore5Id = (int) groupMonthlyScoreDao.insert(groupMonthlyScore5);
                // May Nobody scores

                // Group monthly score 6
                month = 6;

                GroupMonthlyScore groupMonthlyScore6 = new GroupMonthlyScore(groupId, year, quarter, month);
                int groupMonthlyScore6Id = (int) groupMonthlyScoreDao.insert(groupMonthlyScore6);

                List<Score> scores6 = new ArrayList<Score>();
                // Jun Everyone scores, 1 person for position 1, 1 for position 2, 5 for position 3
                // - Player 1 75
                scores6.add(new Score(groupMonthlyScore6Id, member1.getId(), 75));
                // - Player 2 60
                scores6.add(new Score(groupMonthlyScore6Id, member2.getId(), 60));
                // - Player 3 55
                scores6.add(new Score(groupMonthlyScore6Id, member3.getId(), 55));
                // - Player 4 55
                scores6.add(new Score(groupMonthlyScore6Id, member4.getId(), 55));
                // - Player 5 55
                scores6.add(new Score(groupMonthlyScore6Id, member5.getId(), 55));
                // - Player 6 55
                scores6.add(new Score(groupMonthlyScore6Id, member6.getId(), 55));
                // - Player 7 55
                scores6.add(new Score(groupMonthlyScore6Id, member7.getId(), 55));
                // - Player 8 42
                scores6.add(new Score(groupMonthlyScore6Id, member8.getId(), 42));

                scoreDao.insertAll(scores6.toArray(new Score[scores6.size()]));

                // Group monthly score 7
                quarter = 3;
                month = 7;

                GroupMonthlyScore groupMonthlyScore7 = new GroupMonthlyScore(groupId, year, quarter, month);
                int groupMonthlyScore7Id = (int) groupMonthlyScoreDao.insert(groupMonthlyScore7);

                List<Score> scores7 = new ArrayList<>();
                // Jul Two people score, 1 for position 1, 1 for position 2.
                // - Player 1 4
                scores7.add(new Score(groupMonthlyScore7Id, member1.getId(), 4));
                // - Player 2 2
                scores7.add(new Score(groupMonthlyScore7Id, member2.getId(), 2));

                scoreDao.insertAll(scores7.toArray(new Score[scores7.size()]));

                // Group monthly score 8
                month = 8;

                GroupMonthlyScore groupMonthlyScore8 = new GroupMonthlyScore(groupId, year, quarter, month);
                int groupMonthlyScore8Id = (int) groupMonthlyScoreDao.insert(groupMonthlyScore8);

                List<Score> scores8 = new ArrayList<>();
                // Aug 8 people score for position 1.
                // - Player 1 90
                scores8.add(new Score(groupMonthlyScore8Id, member1.getId(), 90));
                // - Player 2 90
                scores8.add(new Score(groupMonthlyScore8Id, member2.getId(), 90));
                // - Player 3 90
                scores8.add(new Score(groupMonthlyScore8Id, member3.getId(), 90));
                // - Player 4 90
                scores8.add(new Score(groupMonthlyScore8Id, member4.getId(), 90));
                // - Player 5 90
                scores8.add(new Score(groupMonthlyScore8Id, member5.getId(), 90));
                // - Player 6 90
                scores8.add(new Score(groupMonthlyScore8Id, member6.getId(), 90));
                // - Player 7 90
                scores8.add(new Score(groupMonthlyScore8Id, member7.getId(), 90));
                // - Player 8 90
                scores8.add(new Score(groupMonthlyScore8Id, member8.getId(), 90));

                scoreDao.insertAll(scores8.toArray(new Score[scores8.size()]));

                // Group monthly score 9
                month = 9;

                GroupMonthlyScore groupMonthlyScore9 = new GroupMonthlyScore(groupId, year, quarter, month);
                int groupMonthlyScore9Id = (int) groupMonthlyScoreDao.insert(groupMonthlyScore9);

                List<Score> scores9 = new ArrayList<>();
                // Sep 1 for position 1, 1 for position 2, 6 for position 3.
                // - Player 1 75
                scores9.add(new Score(groupMonthlyScore9Id, member1.getId(), 75));
                // - Player 2 60
                scores9.add(new Score(groupMonthlyScore9Id, member2.getId(), 60));
                // - Player 3 55
                scores9.add(new Score(groupMonthlyScore9Id, member3.getId(), 55));
                // - Player 4 55
                scores9.add(new Score(groupMonthlyScore9Id, member4.getId(), 55));
                // - Player 5 55
                scores9.add(new Score(groupMonthlyScore9Id, member5.getId(), 55));
                // - Player 6 55
                scores9.add(new Score(groupMonthlyScore9Id, member6.getId(), 55));
                // - Player 7 55
                scores9.add(new Score(groupMonthlyScore9Id, member7.getId(), 55));
                // - Player 8 55
                scores9.add(new Score(groupMonthlyScore9Id, member8.getId(), 55));

                scoreDao.insertAll(scores9.toArray(new Score[scores9.size()]));

                //Group monthly score 10
                quarter = 4;
                month = 10;

                GroupMonthlyScore groupMonthlyScore10 = new GroupMonthlyScore(groupId, year, quarter, month);
                int groupMonthlyScore10Id = (int) groupMonthlyScoreDao.insert(groupMonthlyScore10);

                List<Score> scores10 = new ArrayList<>();
                // Oct 1 for each position, added in a jumbled order.
                // - Player 1 32
                scores10.add(new Score(groupMonthlyScore10Id, member1.getId(), 32));
                // - Player 2 5
                scores10.add(new Score(groupMonthlyScore10Id, member2.getId(), 5));
                // - Player 3 42
                scores10.add(new Score(groupMonthlyScore10Id, member3.getId(), 42));
                // - Player 4 80
                scores10.add(new Score(groupMonthlyScore10Id, member4.getId(), 80));
                // - Player 5 25
                scores10.add(new Score(groupMonthlyScore10Id, member5.getId(), 25));
                // - Player 6 63
                scores10.add(new Score(groupMonthlyScore10Id, member6.getId(), 63));
                // - Player 7 80
                scores10.add(new Score(groupMonthlyScore10Id, member7.getId(), 80));
                // - Player 8 70
                scores10.add(new Score(groupMonthlyScore10Id, member8.getId(), 70));

                scoreDao.insertAll(scores10.toArray(new Score[scores10.size()]));

                // GROUP 1 CATEGORY SKILL RATINGS //

                GroupCategorySkillRatingDao groupCategorySkillRatingDao = INSTANCE.groupCategorySkillRatingDao();

                // CATEGORY: STRATEGY //
                List<GroupCategorySkillRating> group1StrategySkillRatings = new ArrayList<>();

                //Member 1 - 1423, 6
                GroupCategorySkillRating groupCategorySkillRating = new GroupCategorySkillRating(group1.getId(), member1.getId(), strategy.getId(), 1423.40, 6);
                group1StrategySkillRatings.add(groupCategorySkillRating);

                //Member 2 - 1750, 42
                groupCategorySkillRating = new GroupCategorySkillRating(group1.getId(), member2.getId(), strategy.getId(), 1750.00, 42);
                group1StrategySkillRatings.add(groupCategorySkillRating);

                //Member 3 - 1575, 3
                groupCategorySkillRating = new GroupCategorySkillRating(group1.getId(), member3.getId(), strategy.getId(), 1575.00, 3);
                group1StrategySkillRatings.add(groupCategorySkillRating);

                //Member 4 - 900.91, 60
                groupCategorySkillRating = new GroupCategorySkillRating(group1.getId(), member4.getId(), strategy.getId(), 900.91, 60);
                group1StrategySkillRatings.add(groupCategorySkillRating);

                //Member 5 - 1423.40, 1
                groupCategorySkillRating = new GroupCategorySkillRating(group1.getId(), member5.getId(), strategy.getId(), 1423.40, 1);
                group1StrategySkillRatings.add(groupCategorySkillRating);

                //Member 6 - 1642.35, 20
                groupCategorySkillRating = new GroupCategorySkillRating(group1.getId(), member6.getId(), strategy.getId(), 1642.35, 20);
                group1StrategySkillRatings.add(groupCategorySkillRating);

                //Member 7 - 1200, 20
                groupCategorySkillRating = new GroupCategorySkillRating(group1.getId(), member7.getId(), strategy.getId(), 1200.73, 20);
                group1StrategySkillRatings.add(groupCategorySkillRating);

                //Member 8 - 1520, 1
                groupCategorySkillRating = new GroupCategorySkillRating(group1.getId(), member8.getId(), strategy.getId(), 1520.68, 1);
                group1StrategySkillRatings.add(groupCategorySkillRating);

                groupCategorySkillRatingDao.insertAll(group1StrategySkillRatings.toArray(new GroupCategorySkillRating[group1StrategySkillRatings.size()]));

                // CATEGORY: LUCK //
                List<GroupCategorySkillRating> group1LuckSkillRatings = new ArrayList<>();

                //Member 1 - 1464, 10
                groupCategorySkillRating = new GroupCategorySkillRating(group1.getId(), member1.getId(), luck.getId(), 1464.00, 10);
                group1LuckSkillRatings.add(groupCategorySkillRating);

                //Member 2 - 1800, 37
                groupCategorySkillRating = new GroupCategorySkillRating(group1.getId(), member2.getId(), luck.getId(), 1800.80, 37);
                group1LuckSkillRatings.add(groupCategorySkillRating);

                //Member 3 - 1515, 1
                groupCategorySkillRating = new GroupCategorySkillRating(group1.getId(), member3.getId(), luck.getId(), 1515.00, 1);
                group1LuckSkillRatings.add(groupCategorySkillRating);

                //Member 4 - 1356.08, 12
                groupCategorySkillRating = new GroupCategorySkillRating(group1.getId(), member4.getId(), luck.getId(), 1356.08, 12);
                group1LuckSkillRatings.add(groupCategorySkillRating);

                groupCategorySkillRatingDao.insertAll(group1LuckSkillRatings.toArray(new GroupCategorySkillRating[group1LuckSkillRatings.size()]));

                // SKILL RATING CHANGES

                PlayerSkillRatingChangeDao playerSkillRatingChangeDao = INSTANCE.playerSkillRatingChangeDao();

                int player1Id = playerDao.getPlayerIdByPlayerTeamIdAndMemberNickname(record2PlayerTeam1.getId(), member1.getNickname());
                int player2Id = playerDao.getPlayerIdByPlayerTeamIdAndMemberNickname(record2PlayerTeam1.getId(), member2.getNickname());
                int player3Id = playerDao.getPlayerIdByPlayerTeamIdAndMemberNickname(record2PlayerTeam2.getId(), member3.getNickname());
                int player4Id = playerDao.getPlayerIdByPlayerTeamIdAndMemberNickname(record2PlayerTeam2.getId(), member4.getNickname());
                int player5Id = playerDao.getPlayerIdByPlayerTeamIdAndMemberNickname(record2PlayerTeam2.getId(), member5.getNickname());
                int player6Id = playerDao.getPlayerIdByPlayerTeamIdAndMemberNickname(record2PlayerTeam3.getId(), member6.getNickname());
                int player7Id = playerDao.getPlayerIdByPlayerTeamIdAndMemberNickname(record2PlayerTeam4.getId(), member7.getNickname());

                String strategyCategoryName = strategy.getCategoryName();

                /*
                PlayerSkillRatingChange playerSkillRatingChange = new PlayerSkillRatingChange(player1Id, strategyCategoryName, 1550.00, 20.34);
                playerSkillRatingChangeDao.insert(playerSkillRatingChange);

                playerSkillRatingChange = new PlayerSkillRatingChange(player2Id, strategyCategoryName, 1242.35, 20.34);
                playerSkillRatingChangeDao.insert(playerSkillRatingChange);

                //playerSkillRatingChange = new PlayerSkillRatingChange(player3Id, strategyCategoryName, 1750.72, 13.37);
                //playerSkillRatingChangeDao.insert(playerSkillRatingChange);

                playerSkillRatingChange = new PlayerSkillRatingChange(player4Id, strategyCategoryName, 1683.91, 13.37);
                playerSkillRatingChangeDao.insert(playerSkillRatingChange);

                playerSkillRatingChange = new PlayerSkillRatingChange(player5Id, strategyCategoryName, 1712.12, 13.37);
                playerSkillRatingChangeDao.insert(playerSkillRatingChange);

                playerSkillRatingChange = new PlayerSkillRatingChange(player6Id, strategyCategoryName, 1173.41, -4.07);
                playerSkillRatingChangeDao.insert(playerSkillRatingChange);

                playerSkillRatingChange = new PlayerSkillRatingChange(player7Id, strategyCategoryName, 1472.64, -10.30);
                playerSkillRatingChangeDao.insert(playerSkillRatingChange);

                 */

                List<PlayerSkillRatingChange> strategySkillRatingChanges = new ArrayList<>();

                strategySkillRatingChanges.add(new PlayerSkillRatingChange(player1Id, strategyCategoryName, 1550.00, 20.34));
                strategySkillRatingChanges.add(new PlayerSkillRatingChange(player2Id, strategyCategoryName, 1242.35, 20.34));
                strategySkillRatingChanges.add(new PlayerSkillRatingChange(player3Id, strategyCategoryName, 1750.72, 13.37));
                strategySkillRatingChanges.add(new PlayerSkillRatingChange(player4Id, strategyCategoryName, 1683.91, 13.37));
                strategySkillRatingChanges.add(new PlayerSkillRatingChange(player5Id, strategyCategoryName, 1712.12, 13.37));
                strategySkillRatingChanges.add(new PlayerSkillRatingChange(player6Id, strategyCategoryName, 1173.41, -4.07));
                strategySkillRatingChanges.add(new PlayerSkillRatingChange(player7Id, strategyCategoryName, 1472.64, -10.30));

                playerSkillRatingChangeDao.insertAll(strategySkillRatingChanges.toArray(new PlayerSkillRatingChange[strategySkillRatingChanges.size()]));

                String luckCategoryName = luck.getCategoryName();

                List<PlayerSkillRatingChange> luckSkillRatingChanges = new ArrayList<>();

                luckSkillRatingChanges.add(new PlayerSkillRatingChange(player1Id, luckCategoryName, 1721.14, 7.41));
                luckSkillRatingChanges.add(new PlayerSkillRatingChange(player2Id, luckCategoryName, 1607.00, 7.41));
                luckSkillRatingChanges.add(new PlayerSkillRatingChange(player3Id, luckCategoryName, 942.17, 10.19));
                luckSkillRatingChanges.add(new PlayerSkillRatingChange(player4Id, luckCategoryName, 1234.92, 10.19));
                luckSkillRatingChanges.add(new PlayerSkillRatingChange(player5Id, luckCategoryName, 1500.00, 10.19));
                luckSkillRatingChanges.add(new PlayerSkillRatingChange(player6Id, luckCategoryName, 1343.40, -8.42));
                luckSkillRatingChanges.add(new PlayerSkillRatingChange(player7Id, luckCategoryName, 1400.72, -9.1));

                playerSkillRatingChangeDao.insertAll(luckSkillRatingChanges.toArray(new PlayerSkillRatingChange[luckSkillRatingChanges.size()]));
            });
        }
    };
}
