package com.example.vinscannerapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.vinscannerapp.entities.VinInfo;
import com.example.vinscannerapp.entities.VinList;
import com.example.vinscannerapp.repository.VinRepository;

import java.util.List;

public class VinViewModel extends AndroidViewModel {
    private static final String TAG = "VinViewModel";
    private VinRepository repository;
    private LiveData<List<VinList>> allVinLists;
    public VinViewModel(@NonNull Application application) {
        super(application);
        repository = new VinRepository(application);
        allVinLists = repository.getAllVinLists();
    }

    // Methods for the UI to interact with
    public void insertVinList(VinList vinList) {
        try {
            repository.insertVinList(vinList);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting VIN list", e);
        }
    }

    public void deleteVinList(VinList vinList) {
        try {
            repository.deleteVinList(vinList);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting VIN list", e);
        }    }

    public void updateVinList(VinList vinList) {
        try {
            repository.updateVinList(vinList);
        } catch (Exception e) {
            Log.e(TAG, "Error updating VIN list", e);
        }    }

    public LiveData<List<VinList>> getAllVinLists() {
        return allVinLists;
    }

    public void insertVinInfo(VinInfo vinInfo) {
        try {
            repository.insertVinInfo(vinInfo);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting VIN info", e);
        }    }

    public void deleteVinInfo(VinInfo vinInfo) {
        try {
            repository.deleteVinInfo(vinInfo);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting VIN info", e);
        }    }

    public void updateVinInfo(VinInfo vinInfo) {
        try {
            repository.updateVinInfo(vinInfo);
        } catch (Exception e) {
            Log.e(TAG, "Error updating VIN info", e);
        }    }

    public LiveData<List<VinInfo>> getVinInfoForList(int listId) {
        return repository.getVinInfoForList(listId);
    }

}
