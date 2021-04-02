package com.soumikshah.investmenttracker.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.soumikshah.investmenttracker.database.dao.InvestmentDao;
import com.soumikshah.investmenttracker.database.model.Investment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Investment.class}, version = 1, exportSchema = false)
public abstract class InvestmentRoomDatabase extends RoomDatabase {

    public abstract InvestmentDao investmentDao();
    private static volatile InvestmentRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static InvestmentRoomDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (InvestmentRoomDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            InvestmentRoomDatabase.class,"investments_database").addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                InvestmentDao dao = INSTANCE.investmentDao();


                Investment investment = new Investment(1,"Demo",25000,1,"Demo","Demo", (long) 250060,1);
                dao.insertInvestment(investment);
            });
        }
    };


}
