package com.example.vinscannerapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.vinscannerapp.dao.VinInfoDao;
import com.example.vinscannerapp.dao.VinListDao;
import com.example.vinscannerapp.entities.VinInfo;
import com.example.vinscannerapp.entities.VinList;

@Database(entities = {VinList.class, VinInfo.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract VinListDao vinListDao();
    public abstract VinInfoDao vinInfoDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "vin_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
