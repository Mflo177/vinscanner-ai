package com.example.vinscannerapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.vinscannerapp.entities.VinInfo;

import java.util.List;

@Dao
public interface VinInfoDao {

    @Insert
    void insert(VinInfo vinInfo);

    @Delete
    void delete(VinInfo vinInfo);

    @Update
    void update(VinInfo vinInfo);

    @Query("SELECT * FROM vin_info WHERE listId = :listId")
    LiveData<List<VinInfo>> getVinInfoForList(int listId);

    @Query("UPDATE vin_lists SET vinCount = vinCount + 1 WHERE id = :listId")
    void incrementVinCount(int listId);

    @Query("UPDATE vin_lists SET vinCount = vinCount - 1 WHERE id = :listId")
    void decrementVinCount(int listId);

}
