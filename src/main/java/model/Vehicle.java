package model;

import java.util.UUID;

public class Vehicle {
    private final String id;
    private final String ownerEmail;
    private final String make;
    private final String color;
    private final String plate;

    public Vehicle(String ownerEmail, String make, String color, String plate) {
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
