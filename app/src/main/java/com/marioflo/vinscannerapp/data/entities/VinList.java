package com.marioflo.vinscannerapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Represents a single VIN list in the database.
 * <p>
 * Each list acts as a logical grouping for scanned VINs, such as
 * a vehicle batch, dealership section, or storage location.
 * </p>
 *
 * <p>Stored in the Room database table: <b>vin_lists</b>.</p>
 */
@Entity(tableName = "vin_lists")
public class VinList {

    /** Unique auto-generated identifier for each VIN list. */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /** Descriptive name for the list (e.g., "Lot A - Incoming Cars"). */
    @NonNull
    private String name;

    /** Number of VIN entries associated with this list. */
    private int vinCount;

    // ---------------------------------------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------------------------------------

    /**
     * Main constructor used by Room to create new VIN list entries.
     *
     * @param name the name of the VIN list.
     */
    public VinList(String name) {
        this.name = name;
        this.vinCount = 0;
    }

    /**
     * Convenience constructor (ignored by Room) for manual instantiation,
     * testing, or mock data creation.
     *
     * @param name     the list name.
     * @param vinCount the initial VIN count for this list.
     */
    @Ignore
    public VinList(String name, int vinCount) {
        this.name = name;
        this.vinCount = vinCount;
    }

    // ---------------------------------------------------------------------------------------------
    // Getters & Setters
    // ---------------------------------------------------------------------------------------------

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

    // ---------------------------------------------------------------------------------------------
    // Utility
    // ---------------------------------------------------------------------------------------------

    @NonNull
    @Override
    public String toString() {
        return "VinList{id=" + id + ", name='" + name + "', vinCount=" + vinCount + "}";
    }
}
