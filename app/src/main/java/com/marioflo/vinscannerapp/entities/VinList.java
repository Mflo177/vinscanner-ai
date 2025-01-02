package com.marioflo.vinscannerapp.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "vin_lists")
public class VinList {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int vinCount;


    // Constructors, getters and setters
    public VinList(String name) {
        this.name = name;
        this.vinCount = 0;
    }

    @Ignore
    public VinList(String name, int vinCount) {
        this.name = name;
        this.vinCount = vinCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVinCount() {
        return vinCount;
    }

    public void setVinCount(int vinCount) {
        this.vinCount = vinCount;
    }
}
