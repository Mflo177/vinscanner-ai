package com.marioflo.vinscannerapp.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.marioflo.vinscannerapp.data.dao.VinInfoDao;
import com.marioflo.vinscannerapp.data.dao.VinListDao;
import com.marioflo.vinscannerapp.database.AppDatabase;
import com.marioflo.vinscannerapp.data.entities.VinInfo;
import com.marioflo.vinscannerapp.data.entities.VinList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository layer for managing VIN-related data operations.
 * <p>
 * The repository abstracts access to multiple data sources (currently Room)
 * and provides a clean API for the ViewModel and UI layers.
 * It handles threading, error logging, and relationship updates between
 * {@link VinList} and {@link VinInfo}.
 * </p>
 *
 * <p>Implements the recommended Android MVVM architecture pattern.</p>
 */
public class VinRepository {

    private static final String TAG = "VinRepository";

    // DAOs
    private VinListDao vinListDao;
    private VinInfoDao vinInfoDao;

    // Thread pool for background operations
    private static ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    /**
     * Initializes the repository and retrieves DAO instances.
     *
     * @param application Application context used for database access.
     */
    public VinRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        vinListDao = db.vinListDao();
        vinInfoDao = db.vinInfoDao();
    }

    // ---------------------------------------------------------------------------------------------
    // VIN LIST OPERATIONS
    // ---------------------------------------------------------------------------------------------

    /** Inserts a new VIN list asynchronously. */
    public void insertVinList(VinList vinList) {
        databaseWriteExecutor.execute(() -> {
            try {
                vinListDao.insert(vinList);
            } catch (Exception e) {
                Log.e(TAG, "Error inserting VIN list", e);
            }
        });
    }

    public void deleteVinList(VinList vinList) {
        databaseWriteExecutor.execute(() -> {
            try {
                vinListDao.delete(vinList);
            } catch (Exception e) {
                Log.e(TAG, "Error deleting VIN list", e);
            }        });
    }

    public void updateVinList(VinList vinList) {
        databaseWriteExecutor.execute(() -> {
            try {
                vinListDao.update(vinList);
            } catch (Exception e) {
                Log.e(TAG, "Error updating VIN list", e);
            }        });
    }

    public LiveData<List<VinList>> getAllVinLists() {
        return vinListDao.getAllVinLists();
    }

    public LiveData<VinList> getVinList(int id) {
        return vinListDao.getVinList(id);
    }


    // ---------------------------------------------------------------------------------------------
    // VIN INFO OPERATIONS
    // ---------------------------------------------------------------------------------------------

    /** Inserts a new VIN info entry and increments its parent list's VIN count. */
    public void insertVinInfo(@NonNull VinInfo vinInfo) {
        executeSafely(() -> {
            vinInfoDao.insert(vinInfo);
            vinInfoDao.incrementVinCount(vinInfo.getListId());
        }, "insertVinInfo");
    }

    /** Deletes a VIN info entry and decrements its parent list's VIN count. */
    public void deleteVinInfo(@NonNull VinInfo vinInfo) {
        executeSafely(() -> {
            vinInfoDao.delete(vinInfo);
            vinInfoDao.decrementVinCount(vinInfo.getListId());
        }, "deleteVinInfo");
    }

    /** Updates an existing VIN info entry. */
    public void updateVinInfo(@NonNull VinInfo vinInfo) {
        executeSafely(() -> vinInfoDao.update(vinInfo), "updateVinInfo");
    }

    /** Retrieves all VIN info entries for a specific list. */
    public LiveData<List<VinInfo>> getVinInfoForList(int listId) {
        return vinInfoDao.getVinInfoForList(listId);
    }

    /** Retrieves a single VIN info entry by its ID. */
    public LiveData<VinInfo> getVinInfoById(int id) {
        return vinInfoDao.getVinInfoById(id);
    }


    // ---------------------------------------------------------------------------------------------
    // UTILITY
    // ---------------------------------------------------------------------------------------------

    /**
     * Safely executes a database operation in a background thread.
     * Logs any thrown exceptions for debugging.
     *
     * @param action      Runnable database task.
     * @param operation   Descriptive operation name for log clarity.
     */
    private void executeSafely(@NonNull Runnable action, @NonNull String operation) {
        databaseWriteExecutor.execute(() -> {
            try {
                action.run();
            } catch (Exception e) {
                Log.e(TAG, "Database error during " + operation, e);
            }
        });
    }
}
