package model;

import java.util.UUID;

/**
 * Immutable vehicle record associated with a driver account.
 */
public class Vehicle {
    private final String id;
    private final String ownerEmail;
    private final String make;
    private final String color;
    private final String plate;

    public Vehicle(String ownerEmail, String make, String color, String plate) {
        // UUID avoids coordination and works well for in-memory demo data.
        this.id = UUID.randomUUID().toString();
        this.ownerEmail = ownerEmail;
        this.make = make;
        this.color = color;
        this.plate = plate;
    }

    public String getId() {
        return id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getMake() {
        return make;
    }

    public String getColor() {
        return color;
    }

    public String getPlate() {
        return plate;
    }
}
