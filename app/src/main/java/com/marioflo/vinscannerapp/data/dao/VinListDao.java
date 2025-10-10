package com.marioflo.vinscannerapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.marioflo.vinscannerapp.data.entities.VinList;

import java.util.List;


/**
 * Data Access Object (DAO) for performing CRUD operations on the VIN List table.
 * This interface defines all database interactions related to VIN list management.
 *
 * Each method is designed to be used asynchronously via the repository layer
 * to prevent blocking the main thread.
 */
@Dao
public interface VinListDao {

        // --------------------------------------------------
        // Basic CRUD Operations
        // --------------------------------------------------

        /**
         * Inserts a new VIN list into the database.
         *
         * @param vinList The VIN list entity to insert.
         */
        @Insert
        void insert(VinList vinList);

        /**
         * Deletes a specific VIN list from the database.
         *
         * @param vinList The VIN list entity to delete.
         */
        @Delete
        void delete(VinList vinList);


        /**
         * Updates an existing VIN list in the database.
         *
         * @param vinList The VIN list entity to update.
         */
        @Update
        void update(VinList vinList);

        // --------------------------------------------------
        // Query Methods
        // --------------------------------------------------

        /**
         * Retrieves all VIN lists from the database, ordered alphabetically by name.
         *
         * @return A LiveData object containing a list of all VIN lists.
         */
        @Query("SELECT * FROM vin_lists")
        LiveData<List<VinList>> getAllVinLists();


        /**
         * Retrieves a specific VIN list by its unique ID.
         *
         * @param id The ID of the VIN list.
         * @return A LiveData object containing the VIN list entity.
         */
        @Query("SELECT * FROM vin_lists WHERE id = :id LIMIT 1")
        LiveData<VinList> getVinList(int id);
}
