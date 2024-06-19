package com.example.vinscannerapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.vinscannerapp.dao.VinInfoDao;
import com.example.vinscannerapp.dao.VinListDao;
import com.example.vinscannerapp.database.AppDatabase;
import com.example.vinscannerapp.entities.VinInfo;
import com.example.vinscannerapp.entities.VinList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VinRepository {

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
            vinListDao.insert(vinList);
        });
    }

    public void deleteVinList(VinList vinList) {
        databaseWriteExecutor.execute(() -> {
            vinListDao.delete(vinList);
        });
    }

    public void updateVinList(VinList vinList) {
        databaseWriteExecutor.execute(() -> {
            vinListDao.update(vinList);
        });
    }

    public LiveData<List<VinList>> getAllVinLists() {
        return vinListDao.getAllVinLists();
    }

    public void insertVinInfo(VinInfo vinInfo) {
        databaseWriteExecutor.execute(() -> {
            vinInfoDao.insert(vinInfo);
        });
    }

    public void deleteVinInfo(VinInfo vinInfo) {
        databaseWriteExecutor.execute(() -> {
            vinInfoDao.delete(vinInfo);
        });
    }

    public void updateVinInfo(VinInfo vinInfo) {
        databaseWriteExecutor.execute(() -> {
            vinInfoDao.update(vinInfo);
        });
    }

    public LiveData<List<VinInfo>> getVinInfoForList(int listId) {
        return vinInfoDao.getVinInfoForList(listId);
    }
}
