package com.marioflo.vinscannerapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.marioflo.vinscannerapp.data.entities.VinInfo;

import java.util.List;


/**
 * Data Access Object (DAO) for performing CRUD operations on the VIN Info table.
 * This interface defines all database interactions related to VIN entries.
 *
 * Each method runs asynchronously when called through a repository
 * with an ExecutorService or coroutine to prevent blocking the main thread.
 */
@Dao
public interface VinInfoDao {

    // --------------------------------------------------
    // Basic CRUD Operations
    // --------------------------------------------------

    /**
     * Inserts a new VIN information record into the database.
     *
     * @param vinInfo The VIN info entity to insert.
     */
    @Insert
    void insert(VinInfo vinInfo);

    /**
     * Deletes a specific VIN information record from the database.
     *
     * @param vinInfo The VIN info entity to delete.
     */
    @Delete
    void delete(VinInfo vinInfo);

    /**
     * Updates an existing VIN information record in the database.
     *
     * @param vinInfo The VIN info entity to update.
     */
    @Update
    void update(VinInfo vinInfo);


    // --------------------------------------------------
    // Query Methods
    // --------------------------------------------------

    /**
     * Retrieves all VIN info entries associated with a specific list.
     *
     * @param listId The ID of the VIN list.
     * @return A LiveData object containing a list of VIN info entities.
     */
    @Query("SELECT * FROM vin_info WHERE listId = :listId")
    LiveData<List<VinInfo>> getVinInfoForList(int listId);

    /**
     * Retrieves a specific VIN info record by its ID.
     *
     * @param id The VIN info ID.
     * @return A LiveData object containing the VIN info entity.
     */
    @Query("SELECT * FROM vin_info WHERE id = :id LIMIT 1")
    LiveData<VinInfo> getVinInfoById(int id);

    // --------------------------------------------------
    // VIN Count Management (for parent list)
    // --------------------------------------------------

    /**
     * Increments the VIN count for a given VIN list.
     *
     * @param listId The ID of the VIN list to update.
     */
    @Query("UPDATE vin_lists SET vinCount = vinCount + 1 WHERE id = :listId")
    void incrementVinCount(int listId);

    /**
     * Decrements the VIN count for a given VIN list.
     *
     * @param listId The ID of the VIN list to update.
     */
    @Query("UPDATE vin_lists SET vinCount = vinCount - 1 WHERE id = :listId")
    void decrementVinCount(int listId);



}
