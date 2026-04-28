package store;

import db.DBConnection;
import model.User;
import model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Drop-in replacement for the old in-memory AppStore.
 * Same public method signatures — servlets do not need to change.
 * All data is now persisted in the team7 MySQL database.
 *
 * Users table columns used:
 *   User_ID, SJSU_ID, First_Name, Last_Name, Email,
 *   Gender, Password_Hash, Role, Account_Status
 *
 * Vehicles are linked to users via the Owns join table:
 *   Owns(User_ID, Vehicle_ID)  ->  Vehicles(Vehicle_ID, License_Plate, Make, Color, ...)
 */
public final class AppStore {

    private AppStore() {}

    // ------------------------------------------------------------------ users

    /** Returns true if an active account with this email already exists. */
    public static boolean hasUser(String email) {
        String sql = "SELECT 1 FROM Users WHERE Email = ? AND Account_Status = 'active' LIMIT 1";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("hasUser failed", e);
        }
    }

    /**
     * Inserts a new user row plus a row in Drivers and/or Passengers.
     * Returns the created User, or null if the email is already taken.
     *
     * The DB schema stores Role as a single varchar. When the user picks
     * both roles we store "driver" (driver takes precedence) so the login
     * redirect logic continues to work unchanged.
     */
    public static User createUser(
            String firstName,
            String lastName,
            String email,
            String sjsuId,
            String gender,
            String password,
            Set<String> roles) {

        if (hasUser(email)) {
            return null;
        }

        // Decide the stored role value (driver wins if both are selected).
        String roleValue = roles.contains("driver") ? "driver" : "passenger";

        String insertUser =
            "INSERT INTO Users (SJSU_ID, First_Name, Last_Name, Email, Gender, " +
            "                   Password_Hash, Role, Account_Status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, 'active')";

        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, sjsuId);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, email);
            ps.setString(5, gender);
            ps.setString(6, password);
            ps.setString(7, roleValue);
            ps.executeUpdate();

            // Get the auto-generated User_ID.
            int userId;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("No generated key returned");
                userId = keys.getInt(1);
            }

            // Insert into Drivers / Passengers subtype tables as needed.
            if (roles.contains("driver")) {
                try (PreparedStatement pd = c.prepareStatement(
                        "INSERT INTO Drivers (User_ID, Verification_Status, Driver_Rating) VALUES (?, 'pending', 0.0)")) {
                    pd.setInt(1, userId);
                    pd.executeUpdate();
                }
            }
            if (roles.contains("passenger")) {
                try (PreparedStatement pp = c.prepareStatement(
                        "INSERT INTO Passengers (User_ID, Total_Rides_Taken) VALUES (?, 0)")) {
                    pp.setInt(1, userId);
                    pp.executeUpdate();
                }
            }

            return new User(firstName, lastName, email, sjsuId, gender, password, roles);

        } catch (SQLException e) {
            throw new RuntimeException("createUser failed", e);
        }
    }

    /**
     * Looks up the user by email and checks the password.
     * Returns the User object on success, null on failure.
     */
    public static User authenticate(String email, String password) {
        String sql =
            "SELECT First_Name, Last_Name, SJSU_ID, Gender, Password_Hash, Role " +
            "FROM Users WHERE Email = ? AND Account_Status = 'active' LIMIT 1";

        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String storedHash = rs.getString("Password_Hash");
                if (!storedHash.equals(password)) return null;

                String firstName = rs.getString("First_Name");
                String lastName  = rs.getString("Last_Name");
                String sjsuId    = rs.getString("SJSU_ID");
                String gender    = rs.getString("Gender");
                String role      = rs.getString("Role");

                // Rebuild the roles Set the rest of the app expects.
                Set<String> roles = new HashSet<>();
                if ("driver".equals(role))    roles.add("driver");
                if ("passenger".equals(role)) roles.add("passenger");
                if ("both".equals(role))      { roles.add("driver"); roles.add("passenger"); }

                return new User(firstName, lastName, email, sjsuId, gender, storedHash, roles);
            }
        } catch (SQLException e) {
            throw new RuntimeException("authenticate failed", e);
        }
    }

    /**
     * Soft-deletes the user by setting Account_Status = 'deleted'.
     * Hard-deleting would require cascading through many FK tables.
     */
    public static void deleteUser(String email) {
        String sql = "UPDATE Users SET Account_Status = 'deleted' WHERE Email = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("deleteUser failed", e);
        }
    }

    // --------------------------------------------------------------- vehicles

    /** Returns all vehicles owned by the user with this email. */
    public static List<Vehicle> getVehiclesForOwner(String ownerEmail) {
        String sql =
            "SELECT v.Vehicle_ID, v.Make, v.Color, v.License_Plate " +
            "FROM Vehicles v " +
            "JOIN Owns o ON o.Vehicle_ID = v.Vehicle_ID " +
            "JOIN Users u ON u.User_ID = o.User_ID " +
            "WHERE u.Email = ? AND u.Account_Status = 'active'";

        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, ownerEmail);
            List<Vehicle> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Vehicle(
                        String.valueOf(rs.getInt("Vehicle_ID")),
                        ownerEmail,
                        rs.getString("Make"),
                        rs.getString("Color"),
                        rs.getString("License_Plate")
                    ));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("getVehiclesForOwner failed", e);
        }
    }

    /** Inserts a new Vehicle row and links it to the owner via Owns. */
    public static void addVehicle(String ownerEmail, String make, String color, String plate) {
        String insertVehicle =
            "INSERT INTO Vehicles (License_Plate, Make, Color) VALUES (?, ?, ?)";
        String insertOwns =
            "INSERT INTO Owns (User_ID, Vehicle_ID) " +
            "SELECT User_ID, ? FROM Users WHERE Email = ? AND Account_Status = 'active'";

        try (Connection c = DBConnection.get()) {
            int vehicleId;
            try (PreparedStatement pv = c.prepareStatement(
                    insertVehicle, Statement.RETURN_GENERATED_KEYS)) {
                pv.setString(1, plate);
                pv.setString(2, make);
                pv.setString(3, color);
                pv.executeUpdate();
                try (ResultSet keys = pv.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No generated key for vehicle");
                    vehicleId = keys.getInt(1);
                }
            }

            try (PreparedStatement po = c.prepareStatement(insertOwns)) {
                po.setInt(1, vehicleId);
                po.setString(2, ownerEmail);
                po.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("addVehicle failed", e);
        }
    }

    /**
     * Deletes the vehicle row (and cascade-removes the Owns row) for the
     * given owner. vehicleId here is the string representation of Vehicle_ID.
     */
    public static void deleteVehicle(String ownerEmail, String vehicleId) {
        // Only delete if the vehicle actually belongs to this user (security check).
        String sql =
            "DELETE v FROM Vehicles v " +
            "JOIN Owns o ON o.Vehicle_ID = v.Vehicle_ID " +
            "JOIN Users u ON u.User_ID = o.User_ID " +
            "WHERE u.Email = ? AND v.Vehicle_ID = ?";

        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ownerEmail);
            ps.setInt(2, Integer.parseInt(vehicleId));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("deleteVehicle failed", e);
        }
    }
}
