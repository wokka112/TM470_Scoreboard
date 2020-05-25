package com.floatingpanda.scoreboard.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.floatingpanda.scoreboard.typeconverters.PlayModeTypeConverter;
import com.floatingpanda.scoreboard.typeconverters.TeamOptionTypeConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {AssignedCategory.class, BgCategory.class, BoardGame.class, Group.class, Member.class, PlayMode.class}, version = 18, exportSchema = false)
@TypeConverters({PlayModeTypeConverter.class, TeamOptionTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract MemberDao memberDao();
    public abstract BgCategoryDao bgCategoryDao();
    public abstract GroupDao groupDao();
    public abstract BoardGameDao boardGameDao();
    public abstract AssignedCategoryDao assignedCategoryDao();
    public abstract PlayModeDao playModeDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //TODO implement proper migrating method, not fallbacktodestructivemigration()
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "scoreboard_database")
                            .fallbackToDestructiveMigration()
                            //.allowMainThreadQueries() // Use for testing purposes
                            .addCallback(sRoomDatabaseCallback)
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
                MemberDao memberDao = INSTANCE.memberDao();
                memberDao.deleteAll();

                Member member = new Member("Bill", "Bill");
                memberDao.insert(member);
                member = new Member("Frank",  "Frank");
                memberDao.insert(member);
                member = new Member("Bailey","Bailey");
                memberDao.insert(member);
                member = new Member("Bailey",  "Bailey");
                memberDao.insert(member);
                member = new Member("Bailey", "Bailey");
                memberDao.insert(member);
                member = new Member("Bailey",  "Bailey");

                /*
                BgCategoryDao bgCategoryDao = INSTANCE.bgCategoryDao();
                bgCategoryDao.deleteAll();

                BgCategory category = new BgCategory("Strategy");
                bgCategoryDao.insert(category);
                category = new BgCategory("Gambling");
                bgCategoryDao.insert(category);
                category = new BgCategory("Bluffing");
                bgCategoryDao.insert(category);
                category = new BgCategory("Eurotrash");
                bgCategoryDao.insert(category);
                category = new BgCategory("Ameritrash");
                bgCategoryDao.insert(category);
                */

                GroupDao groupDao = INSTANCE.groupDao();
                groupDao.deleteAll();

                Group group = new Group("Ragnarok", "", "");
                groupDao.insert(group);
                group = new Group("The Monday Knights", "", "");
                groupDao.insert(group);
                group = new Group("The Hospitallers", "", "");
                groupDao.insert(group);
                group = new Group("The Templars", "", "");
                groupDao.insert(group);
                group = new Group("The Crusaders", "", "");
                groupDao.insert(group);
                group = new Group("The Saracens", "", "");
                groupDao.insert(group);
                group = new Group("The Turks", "", "");
                groupDao.insert(group);

                BoardGameDao bgDao = INSTANCE.boardGameDao();
                AssignedCategoryDao acDao = INSTANCE.assignedCategoryDao();
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

                List<PlayMode.PlayModeEnum> playModes = new ArrayList<>();
                playModes.add(PlayMode.PlayModeEnum.COMPETITIVE);

                BoardGame.TeamOption teamOption = BoardGame.TeamOption.NO_TEAMS;
                Log.w("AppDatabase.java", "Playmodes: " + playModes.toString());
                Log.w("AppDatabase.java", "Team options: " + teamOption.toString());
                BoardGame bg = new BoardGame("Medieval", 3, 1, 8, teamOption,
                        "N/A", "N/A", "N/A", "N/A", playModes);
                Log.w("AppDatabase.java", "bg Playmode: " + bg.getPlayModesString() + ", Team Options: " + bg.getTeamOptionsString());

                playModes.add(PlayMode.PlayModeEnum.COOPERATIVE);

                teamOption = BoardGame.TeamOption.TEAMS_ONLY;
                Log.w("AppDatabase.java", "Playmode: " + playModes.toString());
                Log.w("AppDatabase.java", "Team options: " + teamOption.toString());
                BoardGame bg1 = new BoardGame("Monopoly", 3, 1, 8, teamOption,
                        "N/A", "N/A", "N/A", "N/A", playModes);
                Log.w("AppDatabase.java", "bg1 Playmode: " + bg1.getPlayModesString() + ", Team Options: " + bg1.getTeamOptionsString());

                teamOption = BoardGame.TeamOption.TEAMS_OR_SOLOS;
                Log.w("AppDatabase.java", "Playmode: " + playModes.toString());
                Log.w("AppDatabase.java", "Team options: " + teamOption.toString());
                BoardGame bg2 = new BoardGame("Go", 3, 1, 8, teamOption,
                        "N/A", "N/A", "N/A", "N/A", playModes);
                Log.w("AppDatabase.java", "bg2 Playmode: " + bg2.getPlayModesString() + ", Team Options: " + bg.getTeamOptionsString());

                playModes.add(PlayMode.PlayModeEnum.SOLITAIRE);

                teamOption = BoardGame.TeamOption.NO_TEAMS;
                Log.w("AppDatabase.java", "Playmode: " + playModes.toString());
                Log.w("AppDatabase.java", "Team options: " + teamOption.toString());
                BoardGame bg3 = new BoardGame("Game of Life", 3, 1, 8, teamOption,
                        "N/A", "N/A", "N/A", "N/A", playModes);
                Log.w("AppDatabase.java", "bg3 Playmode: " + bg3.getPlayModesString() + ", Team Options: " + bg.getTeamOptionsString());

                Log.w("AppDatabase.java", "Playmode: " + playModes.toString());
                Log.w("AppDatabase.java", "Team options: " + teamOption.toString());
                BoardGame bg4 = new BoardGame("Dawn of Madness", 3, 1, 8, teamOption,
                        "N/A", "N/A", "N/A", "N/A", playModes);
                Log.w("AppDatabase.java", "bg4 Playmode: " + bg4.getPlayModesString() + ", Team Options: " + bg.getTeamOptionsString());

                Log.w("AppDatabase.java", "Playmode: " + playModes.toString());
                Log.w("AppDatabase.java", "Team options: " + teamOption.toString());
                BoardGame bg5 = new BoardGame("No Category Bg", 3, 1, 8, teamOption,
                         "N/A", "N/A", "N/A", "N/A", playModes);
                Log.w("AppDatabase.java", "bg5 Playmode: " + bg5.getPlayModesString() + ", Team Options: " + bg.getTeamOptionsString());

                List<AssignedCategory> acs = new ArrayList<>();

                PlayModeDao playModeDao = INSTANCE.playModeDao();

                bgDao.insert(bg);
                PlayMode playMode = new PlayMode(bg.getId(), bg.getPlayModes().get(0));
                playModeDao.insert(playMode);

                bg = bgDao.findNonLiveDataByName(bg.getBgName());
                acs.add(new AssignedCategory(bg.getId(), strategy.getId()));
                acs.add(new AssignedCategory(bg.getId(), luck.getId()));
                acDao.insertAll(acs.toArray(new AssignedCategory[acs.size()]));

                Log.w("Database.java", "Inserted bg");

                bgDao.insert(bg1);
                playMode = new PlayMode(bg1.getId(), bg1.getPlayModes().get(0));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg1.getId(), bg1.getPlayModes().get(1));
                playModeDao.insert(playMode);

                bg1 = bgDao.findNonLiveDataByName(bg1.getBgName());
                acs.add(new AssignedCategory(bg1.getId(), gambling.getId()));
                acDao.insert(acs.get(0));
                Log.w("Database.java", "Inserted bg1");

                bgDao.insert(bg2);
                playMode = new PlayMode(bg2.getId(), bg2.getPlayModes().get(0));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg2.getId(), bg2.getPlayModes().get(1));
                playModeDao.insert(playMode);

                bg2 = bgDao.findNonLiveDataByName(bg2.getBgName());
                acs.add(new AssignedCategory(bg2.getId(), gambling.getId()));
                Log.w("Database.java", "Inserted bg2");

                bgDao.insert(bg3);
                playMode = new PlayMode(bg3.getId(), bg3.getPlayModes().get(0));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg3.getId(), bg3.getPlayModes().get(1));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg3.getId(), bg3.getPlayModes().get(2));
                playModeDao.insert(playMode);

                bg3 = bgDao.findNonLiveDataByName(bg3.getBgName());
                acs.add(new AssignedCategory(bg3.getId(), strategy.getId()));
                Log.w("Database.java", "Inserted bg3");
                bgDao.insert(bg4);
                playMode = new PlayMode(bg4.getId(), bg4.getPlayModes().get(0));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg4.getId(), bg4.getPlayModes().get(1));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg4.getId(), bg4.getPlayModes().get(2));
                playModeDao.insert(playMode);

                bg4 = bgDao.findNonLiveDataByName(bg4.getBgName());
                acs.add(new AssignedCategory(bg4.getId(), luck.getId()));
                Log.w("Database.java", "Inserted bg4");
                acDao.insertAll(acs.toArray(new AssignedCategory[acs.size()]));

                bgDao.insert(bg5);
                playMode = new PlayMode(bg5.getId(), bg5.getPlayModes().get(0));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg5.getId(), bg5.getPlayModes().get(1));
                playModeDao.insert(playMode);
                playMode = new PlayMode(bg5.getId(), bg5.getPlayModes().get(2));
                playModeDao.insert(playMode);
            });
        }
    };
}
