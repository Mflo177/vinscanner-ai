package com.marioflo.vinscannerapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marioflo.vinscannerapp.entities.VinInfo;
import com.marioflo.vinscannerapp.entities.VinList;
import com.marioflo.vinscannerapp.repository.VinRepository;

import java.util.List;


/**
 * {@link AndroidViewModel} serving as a bridge between the UI and the {@link VinRepository}.
 * <p>
 * Exposes methods for inserting, updating, deleting, and retrieving {@link VinList} and
 * {@link VinInfo} entities. All database interactions are delegated to the repository layer,
 * following the MVVM architecture pattern.
 * </p>
 */
public class VinViewModel extends AndroidViewModel {

    private static final String TAG = "VinViewModel";

    private VinRepository repository;
    private LiveData<List<VinList>> allVinLists;

    /**
     * Constructor that initializes the repository and LiveData sources.
     *
     * @param application The application context, required for {@link AndroidViewModel}.
     */
    public VinViewModel(@NonNull Application application) {
        super(application);
        repository = new VinRepository(application);
        allVinLists = repository.getAllVinLists();
    }

    // ---------------------------------------------------------------------------------------------
    // VIN LIST METHODS
    // ---------------------------------------------------------------------------------------------

    /**
     * Inserts a new {@link VinList} into the database.
     *
     * @param vinList The VIN list entity to insert.
     */
    public void insertVinList(VinList vinList) {
        try {
            repository.insertVinList(vinList);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting VIN list", e);
        }
    }

    /**
     * Deletes an existing {@link VinList} from the database.
     *
     * @param vinList The VIN list entity to delete.
     */
    public void deleteVinList(VinList vinList) {
        try {
            repository.deleteVinList(vinList);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting VIN list", e);
        }
    }

    /**
     * Updates an existing {@link VinList}.
     *
     * @param vinList The VIN list entity to update.
     */
    public void updateVinList(VinList vinList) {
        try {
            repository.updateVinList(vinList);
        } catch (Exception e) {
            Log.e(TAG, "Error updating VIN list", e);
        }    }

    /**
     * @return LiveData list of all VIN lists in the database.
     */
    public LiveData<List<VinList>> getAllVinLists() {
        return allVinLists;
    }


    /**
     * Retrieves a single {@link VinList} by its ID.
     *
     * @param id The VIN list ID.
     * @return LiveData of the VIN list.
     */
    public  LiveData<VinList> getVinList(int id) {
        return repository.getVinList(id);
    }

    // ---------------------------------------------------------------------------------------------
    // VIN INFO METHODS
    // ---------------------------------------------------------------------------------------------

    /**
     * Inserts a new {@link VinInfo} into the database.
     *
     * @param vinInfo The VIN info entity to insert.
     */
    public void insertVinInfo(VinInfo vinInfo) {
        try {
            repository.insertVinInfo(vinInfo);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting VIN info", e);
        }    }

    /**
     * Deletes an existing {@link VinInfo} from the database.
     *
     * @param vinInfo The VIN info entity to delete.
     */
    public void deleteVinInfo(VinInfo vinInfo) {
        try {
            repository.deleteVinInfo(vinInfo);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting VIN info", e);
        }    }

    /**
     * Updates an existing {@link VinInfo}.
     *
     * @param vinInfo The VIN info entity to update.
     */
    public void updateVinInfo(VinInfo vinInfo) {
        try {
            repository.updateVinInfo(vinInfo);
        } catch (Exception e) {
            Log.e(TAG, "Error updating VIN info", e);
        }    }

    /**
     * Retrieves all {@link VinInfo} associated with a specific VIN list.
     *
     * @param listId The ID of the VIN list.
     * @return LiveData list of VIN info objects.
     */
    public LiveData<List<VinInfo>> getVinInfoForList(int listId) {
        return repository.getVinInfoForList(listId);
    }


    /**
     * Retrieves a single {@link VinInfo} by its ID.
     *
     * @param vinInfoId The VIN info ID.
     * @return LiveData of the VIN info.
     */
    public LiveData<VinInfo> getVinInfoById(int vinInfoId) {
        return repository.getVinInfoById(vinInfoId);
    }
}
