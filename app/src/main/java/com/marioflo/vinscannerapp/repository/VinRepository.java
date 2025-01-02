package com.marioflo.vinscannerapp.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.marioflo.vinscannerapp.dao.VinInfoDao;
import com.marioflo.vinscannerapp.dao.VinListDao;
import com.marioflo.vinscannerapp.database.AppDatabase;
import com.marioflo.vinscannerapp.entities.VinInfo;
import com.marioflo.vinscannerapp.entities.VinList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VinRepository {

    private static final String TAG = "VinRepository";
    private VinListDao vinListDao;
    private VinInfoDao vinInfoDao;
    private static ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public VinRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        vinListDao = db.vinListDao();
        vinInfoDao = db.vinInfoDao();
    }

    // Wrapper methods for database operations
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


    public void insertVinInfo(VinInfo vinInfo) {
        databaseWriteExecutor.execute(() -> {
            try {
                vinInfoDao.insert(vinInfo);
                vinInfoDao.incrementVinCount(vinInfo.getListId());
            } catch (Exception e) {
                Log.e(TAG, "Error inserting VIN info", e);
            }        });
    }

    public void deleteVinInfo(VinInfo vinInfo) {
        databaseWriteExecutor.execute(() -> {
            try {
                vinInfoDao.delete(vinInfo);
                vinInfoDao.decrementVinCount(vinInfo.getListId());
            } catch (Exception e) {
                Log.e(TAG, "Error deleting VIN info", e);
            }        });
    }

    public void updateVinInfo(VinInfo vinInfo) {
        databaseWriteExecutor.execute(() -> {
            try {
                vinInfoDao.update(vinInfo);
            } catch (Exception e) {
                Log.e(TAG, "Error updating VIN info", e);
            }
        });
    }

    public LiveData<List<VinInfo>> getVinInfoForList(int listId) {
        return vinInfoDao.getVinInfoForList(listId);
    }


    // New method to get a single VinInfo by its ID
    public LiveData<VinInfo> getVinInfoById(int id) {
        return vinInfoDao.getVinInfoById(id);
    }
}
