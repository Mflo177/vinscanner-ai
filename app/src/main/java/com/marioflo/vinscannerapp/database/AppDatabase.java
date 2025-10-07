package com.marioflo.vinscannerapp.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.marioflo.vinscannerapp.data.dao.VinInfoDao;
import com.marioflo.vinscannerapp.data.dao.VinListDao;
import com.marioflo.vinscannerapp.entities.VinInfo;
import com.marioflo.vinscannerapp.entities.VinList;

/**
 * Central Room database for the VIN Scanner application.
 * <p>
 * This class provides the main access point to persisted data and handles database
 * creation, versioning, and singleton initialization.
 * </p>
 *
 * <p>
 * The database stores:
 * <ul>
 *   <li>{@link VinList} - Represents collections of scanned VINs.</li>
 *   <li>{@link VinInfo} - Represents individual VIN entries linked to a list.</li>
 * </ul>
 * </p>
 */
@Database(entities = {VinList.class, VinInfo.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {

    public abstract VinListDao vinListDao();
    public abstract VinInfoDao vinInfoDao();
    private static volatile AppDatabase INSTANCE;

    /**
     * Returns a singleton instance of the Room database.
     * <p>
     * Uses double-checked locking to ensure thread safety while avoiding redundant synchronization.
     * </p>
     *
     * @param context Application context used to initialize the database.
     * @return Singleton instance of {@link AppDatabase}.
     */
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

    // ---------------------------------------------------------------------------------------------
    // OPTIONAL: DATABASE CALLBACK (e.g., for initial seeding or logging)
    // ---------------------------------------------------------------------------------------------

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Optional: Prepopulate data or log creation event.
            // Example: Log.d("AppDatabase", "Database created successfully.");
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // Optional: Perform actions each time the database is opened.
        }
    };
}
