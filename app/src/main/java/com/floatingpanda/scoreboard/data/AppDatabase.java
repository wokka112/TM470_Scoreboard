package com.floatingpanda.scoreboard.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {AssignedCategories.class, BgCategory.class, BoardGame.class, Group.class, Member.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MemberDao memberDao();
    public abstract BgCategoryDao bgCategoryDao();
    public abstract GroupDao groupDao();
    public abstract BoardGameDao boardGameDao();
    public abstract AssignedCategoriesDao assignedCategoriesDao();

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

                Member member = new Member("Bill", "Bill", "Bill");
                memberDao.insert(member);
                member = new Member("Frank", "Frank", "Frank");
                memberDao.insert(member);
                member = new Member("Bailey", "Bailey", "Bailey");
                memberDao.insert(member);
                member = new Member("Bailey", "Bailey", "Bailey");
                memberDao.insert(member);
                member = new Member("Bailey", "Bailey", "Bailey");
                memberDao.insert(member);
                member = new Member("Bailey", "Bailey", "Bailey");
                memberDao.insert(member);
                member = new Member("Bailey", "Bailey", "Bailey");
                memberDao.insert(member);
                member = new Member("Bailey", "Bailey", "Bailey");
                memberDao.insert(member);
                member = new Member("Bailey", "Bailey", "Bailey");
                memberDao.insert(member);


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
                AssignedCategoriesDao acDao = INSTANCE.assignedCategoriesDao();
                BgCategoryDao boardGameCategoryDao = INSTANCE.bgCategoryDao();

                BgCategory strategy = new BgCategory("Strategy");
                boardGameCategoryDao.insert(strategy);
                BgCategory skill = new BgCategory("Skill");
                boardGameCategoryDao.insert(skill);
                BgCategory luck = new BgCategory("Luck");
                boardGameCategoryDao.insert(luck);
                BgCategory gambling = new BgCategory("Gambling");
                boardGameCategoryDao.insert(gambling);

                BoardGame bg = new BoardGame("Medieval", 3, 1, 8, "N/A",
                        "N/A", "N/A", "N/A", "N/A");
                BoardGame bg1 = new BoardGame("Monopoly", 3, 1, 8, "N/A",
                        "N/A", "N/A", "N/A", "N/A");
                BoardGame bg2 = new BoardGame("Go", 3, 1, 8, "N/A",
                        "N/A", "N/A", "N/A", "N/A");
                BoardGame bg3 = new BoardGame("Game of Life", 3, 1, 8, "N/A",
                        "N/A", "N/A", "N/A", "N/A");
                BoardGame bg4 = new BoardGame("Dawn of Madness", 3, 1, 8, "N/A",
                        "N/A", "N/A", "N/A", "N/A");

                List<AssignedCategories> acs = new ArrayList<>();

                bgDao.insert(bg);
                acs.add(new AssignedCategories(bg.getBgName(), strategy.getCategoryName()));
                acs.add(new AssignedCategories(bg.getBgName(), luck.getCategoryName()));
                acDao.insertAll(acs.toArray(new AssignedCategories[acs.size()]));

                Log.w("Database.java", "Inserted bg");

                bgDao.insert(bg1);
                acs.add(new AssignedCategories(bg1.getBgName(), gambling.getCategoryName()));
                acDao.insert(acs.get(0));
                Log.w("Database.java", "Inserted bg1");

                acs.add(new AssignedCategories(bg2.getBgName(), gambling.getCategoryName()));
                acs.add(new AssignedCategories(bg3.getBgName(), strategy.getCategoryName()));
                acs.add(new AssignedCategories(bg4.getBgName(), luck.getCategoryName()));

                bgDao.insert(bg2);
                Log.w("Database.java", "Inserted bg2");
                bgDao.insert(bg3);
                Log.w("Database.java", "Inserted bg3");
                bgDao.insert(bg4);
                Log.w("Database.java", "Inserted bg4");
                acDao.insertAll(acs.toArray(new AssignedCategories[acs.size()]));
            });
        }
    };
}
