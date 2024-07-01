package com.example.vinscannerapp.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "vin_info",
        foreignKeys = @ForeignKey(entity = VinList.class,
                                    parentColumns = "id",
                                    childColumns = "listId",
                                    onDelete = ForeignKey.CASCADE))
public class VinInfo {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String vinNumber;
    private int listId;
    private String year;
    private String make;
    private String model;


    // Constructors, getters and setters
    public VinInfo(int id, String vinNumber, int listId,@Nullable String year,@Nullable String make,@Nullable String model) {
        this.id = id;
        this.vinNumber = vinNumber;
        this.listId = listId;
        this.year = year;
        this.make = make;
        this.model = model;
    }

    @Ignore
    public VinInfo(String vinNumber, int listId) {
        this.vinNumber = vinNumber;
        this.listId = listId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(@Nullable String year) {
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(@Nullable String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(@Nullable String model) {
        this.model = model;
    }
}
