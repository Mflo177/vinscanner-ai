package com.example.vinscannerapp.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "vin_info",
        foreignKeys = @ForeignKey(entity = VinList.class,
                                    parentColumns = "id",
                                    childColumns = "listId",
                                    onDelete = ForeignKey.CASCADE),
        indices = {@Index("listId")})
public class VinInfo {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String vinNumber;
    private int listId;
    private String rowLetter;
    private int spaceNumber;
    private String extraNotes;


    public int getSpaceNumber() {
        return spaceNumber;
    }

    public void setSpaceNumber(int spaceNumber) {
        this.spaceNumber = spaceNumber;
    }

    public String getRowLetter() {
        return rowLetter;
    }

    public void setRowLetter(String rowLetter) {
        this.rowLetter = rowLetter;
    }

    public String getExtraNotes() {
        return extraNotes;
    }

    public void setExtraNotes(String extraNotes) {
        this.extraNotes = extraNotes;
    }

    // Constructors, getters and setters
    public VinInfo(int id, String vinNumber, int listId) {
        this.id = id;
        this.vinNumber = vinNumber;
        this.listId = listId;
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


}
