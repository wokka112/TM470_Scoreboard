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
import com.floatingpanda.scoreboard.data.daos.GroupDao;
import com.floatingpanda.scoreboard.data.daos.GroupMemberDao;
import com.floatingpanda.scoreboard.data.daos.MemberDao;
import com.floatingpanda.scoreboard.data.daos.PlayModeDao;
import com.floatingpanda.scoreboard.data.daos.PlayerDao;
import com.floatingpanda.scoreboard.data.daos.PlayerTeamDao;
import com.floatingpanda.scoreboard.data.entities.AssignedCategory;
import com.floatingpanda.scoreboard.data.entities.BgCategory;
import com.floatingpanda.scoreboard.data.entities.BoardGame;
import com.floatingpanda.scoreboard.data.entities.GameRecord;
import com.floatingpanda.scoreboard.data.entities.Group;
import com.floatingpanda.scoreboard.data.entities.GroupMember;
import com.floatingpanda.scoreboard.data.entities.Member;
import com.floatingpanda.scoreboard.data.entities.PlayMode;
import com.floatingpanda.scoreboard.data.entities.Player;
import com.floatingpanda.scoreboard.data.entities.PlayerTeam;
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

@Database(entities = {AssignedCategory.class, BgCategory.class, BoardGame.class, Group.class, GroupMember.class, Member.class, PlayMode.class,
            GameRecord.class, Player.class, PlayerTeam.class}, version = 26, exportSchema = false)
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

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //TODO implement proper migrating method, not fallbacktodestructivemigration()
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

    //TODO remove once done testing
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

                BgCategory test;

                for (int i = 1; i < 101; i++) {
                    test = new BgCategory("Test" + i);
                    boardGameCategoryDao.insert(test);
                }

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

                //TODO sort game records when pulled from database.

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

                //TODO add in cooperative win/lose to game record.
                //TODO add in layout for cooperative and solitaire that can be used for coop and solitaire.

                //Can only have 1 team for cooperative and solitaire games.
                // record 4 - Dawn of Madness, cooperative, 1 team, 4 players, win.
                GameRecord gameRecord4 = new GameRecord(group1.getId(), bg4.getBgName(), bg4.getDifficulty(), new Date(), true, PlayMode.PlayModeEnum.COOPERATIVE,
                        1);
                gameRecordDao.insert(gameRecord4);

                //record 5 - Dawn of Madness, cooperative, 1 team, 6 players, lose.
                GameRecord gameRecord5 = new GameRecord(group1.getId(), bg4.getBgName(), bg4.getDifficulty(), new Date(), true, PlayMode.PlayModeEnum.COOPERATIVE,
                        1);
                gameRecordDao.insert(gameRecord5);

                // record 6 - Dawn of Madness, solitaire, 1 player, win.
                GameRecord gameRecord6 = new GameRecord(group1.getId(), bg4.getBgName(), bg4.getDifficulty(), new Date(), true, PlayMode.PlayModeEnum.SOLITAIRE, 1);
                gameRecordDao.insert(gameRecord6);

                // record 7 - Dawn of Madness, solitaire, 1 player, lose
                GameRecord gameRecord7 = new GameRecord(group1.getId(), bg4.getBgName(), bg4.getDifficulty(), new Date(), true, PlayMode.PlayModeEnum.SOLITAIRE, 1);
                gameRecordDao.insert(gameRecord7);

                gameRecord1 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord1.getGroupId(), gameRecord1.getBoardGameName(), gameRecord1.getDate().getTime());
                gameRecord2 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord2.getGroupId(), gameRecord2.getBoardGameName(), gameRecord2.getDate().getTime());
                gameRecord3 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord3.getGroupId(), gameRecord3.getBoardGameName(), gameRecord3.getDate().getTime());
                gameRecord4 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord4.getGroupId(), gameRecord4.getBoardGameName(), gameRecord4.getDate().getTime());
                gameRecord5 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord5.getGroupId(), gameRecord5.getBoardGameName(), gameRecord5.getDate().getTime());
                gameRecord6 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord6.getGroupId(), gameRecord6.getBoardGameName(), gameRecord6.getDate().getTime());
                gameRecord7 = gameRecordDao.findNonLiveDataGameRecordByRecordId(gameRecord7.getGroupId(), gameRecord7.getBoardGameName(), gameRecord7.getDate().getTime());

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
            });
        }
    };
}
