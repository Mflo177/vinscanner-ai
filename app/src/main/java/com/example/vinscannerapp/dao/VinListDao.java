package com.example.vinscannerapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.vinscannerapp.entities.VinList;

import java.util.List;

@Dao
public interface VinListDao {

        @Insert
        void insert(VinList vinList);

        @Delete
        void delete(VinList vinList);

        @Update
        void update(VinList vinList);

        @Query("SELECT * FROM vin_lists")
        List<VinList> getAllVinLists();
}
