package com.uniride.store;

import com.uniride.model.User;
import com.uniride.model.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class AppStore {
    private static final Map<String, User> USERS_BY_EMAIL = new ConcurrentHashMap<>();
    private static final Map<String, List<Vehicle>> VEHICLES_BY_OWNER = new ConcurrentHashMap<>();

    private AppStore() {
    }

    public static boolean hasUser(String email) {
        return USERS_BY_EMAIL.containsKey(email);
    }

    public static User createUser(
        String firstName,
        String lastName,
        String email,
        String sjsuId,
        String gender,
        String password,
        Set<String> roles
    ) {
        User user = new User(firstName, lastName, email, sjsuId, gender, password, roles);
        User existing = USERS_BY_EMAIL.putIfAbsent(email, user);
        return existing == null ? user : null;
    }

    public static User authenticate(String email, String password) {
        User user = USERS_BY_EMAIL.get(email);
        if (user == null) {
            return null;
        }
        return user.getPassword().equals(password) ? user : null;
    }

    public static void deleteUser(String email) {
        USERS_BY_EMAIL.remove(email);
        VEHICLES_BY_OWNER.remove(email);
    }

    public static List<Vehicle> getVehiclesForOwner(String ownerEmail) {
        List<Vehicle> vehicles = VEHICLES_BY_OWNER.get(ownerEmail);
        if (vehicles == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(vehicles);
    }

    public static void addVehicle(String ownerEmail, String make, String color, String plate) {
        VEHICLES_BY_OWNER.compute(ownerEmail, (key, current) -> {
            List<Vehicle> next = current == null ? new ArrayList<>() : new ArrayList<>(current);
            next.add(new Vehicle(ownerEmail, make, color, plate));
            return next;
        });
    }

    public static void deleteVehicle(String ownerEmail, String vehicleId) {
        VEHICLES_BY_OWNER.computeIfPresent(ownerEmail, (key, current) -> {
            List<Vehicle> next = new ArrayList<>();
            for (Vehicle vehicle : current) {
                if (!vehicle.getId().equals(vehicleId)) {
                    next.add(vehicle);
                }
            }
            return next;
        });
    }
}
