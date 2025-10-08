package com.marioflo.vinscannerapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Represents detailed information about a single VIN entry
 * within a specific VIN list in the local Room database.
 *
 * Each VinInfo record is associated with one VinList via a foreign key.
 */
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
    private String spaceNumber;
    private String extraNotes;

    // ----------------------------
    // Constructors
    // ----------------------------

    /** Full constructor used by Room. */
    public VinInfo(int id, @NonNull String vinNumber, int listId) {
        this.id = id;
        this.vinNumber = vinNumber;
        this.listId = listId;
    }

    /** Convenience constructor for creating new VIN entries before saving to database. */
    @Ignore
    public VinInfo(@NonNull String vinNumber, int listId) {
        this.vinNumber = vinNumber;
        this.listId = listId;
    }

    // ----------------------------
    // Getters and Setters
    // ----------------------------

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

    public String getRowLetter() {
        return rowLetter;
    }

    public void setRowLetter(String rowLetter) {
        this.rowLetter = rowLetter;
    }
    public String getSpaceNumber() {
        return spaceNumber;
    }

    public void setSpaceNumber(String spaceNumber) {
        this.spaceNumber = spaceNumber;
    }

    public String getExtraNotes() {
        return extraNotes;
    }

    public void setExtraNotes(String extraNotes) {
        this.extraNotes = extraNotes;
    }

    // ----------------------------
    // Utility Methods
    // ----------------------------

    @NonNull
    @Override
    public String toString() {
        return "VIN: " + vinNumber +
                (rowLetter != null ? ", Row: " + rowLetter : "") +
                (spaceNumber != null ? ", Space: " + spaceNumber : "") +
                (extraNotes != null ? ", Notes: " + extraNotes : "");
    }
}
