package com.example.vinscannerapp.dao;

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
    List<VinInfo> getVinInfoForList(int listId);

}
