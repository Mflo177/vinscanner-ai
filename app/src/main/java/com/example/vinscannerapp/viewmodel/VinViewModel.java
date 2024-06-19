package com.example.vinscannerapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.vinscannerapp.entities.VinInfo;
import com.example.vinscannerapp.entities.VinList;
import com.example.vinscannerapp.repository.VinRepository;

import java.util.List;

public class VinViewModel extends AndroidViewModel {

    private VinRepository repository;
    private LiveData<List<VinList>> allVinLists;
    public VinViewModel(@NonNull Application application) {
        super(application);
        repository = new VinRepository(application);
        allVinLists = repository.getAllVinLists();
    }

    // Methods for the UI to interact with
    public void insertVinList(VinList vinList) {
        repository.insertVinList(vinList);
    }

    public void deleteVinList(VinList vinList) {
        repository.deleteVinList(vinList);
    }

    public void updateVinList(VinList vinList) {
        repository.updateVinList(vinList);
    }

    public LiveData<List<VinList>> getAllVinLists() {
        return allVinLists;
    }

    public void insertVinInfo(VinInfo vinInfo) {
        repository.insertVinInfo(vinInfo);
    }

    public void deleteVinInfo(VinInfo vinInfo) {
        repository.deleteVinInfo(vinInfo);
    }

    public void updateVinInfo(VinInfo vinInfo) {
        repository.updateVinInfo(vinInfo);
    }

    public LiveData<List<VinInfo>> getVinInfoForList(int listId) {
        return repository.getVinInfoForList(listId);
    }

}
