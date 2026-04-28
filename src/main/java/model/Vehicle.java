package model;

import java.util.UUID;

/**
 * Vehicle record associated with a driver account.
 *
 * Two constructors:
 *  - Vehicle(ownerEmail, make, color, plate)  — used when adding a new vehicle
 *    (id is generated as a UUID placeholder until the DB row is persisted)
 *  - Vehicle(id, ownerEmail, make, color, plate) — used when loading from DB
 *    (id is the string form of the MySQL Vehicle_ID int)
 */
public class Vehicle {
    private final String id;
    private final String ownerEmail;
    private final String make;
    private final String color;
    private final String plate;

    /** Original constructor — kept so existing call sites still compile. */
    public Vehicle(String ownerEmail, String make, String color, String plate) {
        this.id = UUID.randomUUID().toString();
        this.ownerEmail = ownerEmail;
        this.make = make;
        this.color = color;
        this.plate = plate;
    }

    /** New constructor used by AppStore when loading rows from MySQL. */
    public Vehicle(String id, String ownerEmail, String make, String color, String plate) {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.make = make;
        this.color = color;
        this.plate = plate;
    }

    public String getId()         { return id; }
    public String getOwnerEmail() { return ownerEmail; }
    public String getMake()       { return make; }
    public String getColor()      { return color; }
    public String getPlate()      { return plate; }
}
