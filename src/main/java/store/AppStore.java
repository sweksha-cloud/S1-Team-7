package store;

import db.DBConnection;
import model.User;
import model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AppStore {

    private AppStore() {}

    /**
     * Checks whether an active account already exists for the supplied email.
     *
     * @param email email to look up
     * @return true when an active user row exists for that email
     */
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
     * Creates the base user row and any requested role rows in the database.
     *
     * @param firstName first name stored in the Users table
     * @param lastName last name stored in the Users table
     * @param email unique email used for login
     * @param sjsuId university id captured during signup
     * @param gender gender selection from the registration form
     * @param password credential value stored in Password_Hash
     * @param roles selected registration roles
     * @param licenseNumber optional driver license number
     * @return the created User snapshot, or null when the email is already active
     */
    public static User createUser(
            String firstName,
            String lastName,
            String email,
            String sjsuId,
            String gender,
            String password,
            Set<String> roles,
            String licenseNumber) {

        if (hasUser(email)) return null;

        String insertUser =
            "INSERT INTO Users (SJSU_ID, First_Name, Last_Name, Email, Gender, " +
            "                   Password_Hash, Account_Status) " +
            "VALUES (?, ?, ?, ?, ?, ?, 'active')";

        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, sjsuId);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, email);
            ps.setString(5, gender);
            ps.setString(6, password);
            ps.executeUpdate();

            int userId;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("No generated key returned");
                userId = keys.getInt(1);
            }

            if (roles.contains("driver")) {
                try (PreparedStatement pd = c.prepareStatement(
                        "INSERT INTO Drivers (User_ID, License_Number, Verification_Status, Driver_Rating) VALUES (?, ?, 'pending', 0.0)")) {
                    pd.setInt(1, userId);
                    pd.setString(2, licenseNumber);
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
     * Authenticates an active account by email and password.
     *
     * The method rebuilds the role set from the related tables so the returned
     * session object reflects the account's current permissions.
     *
     * @param email email used to locate the account
     * @param password password value supplied by the caller
     * @return a populated User when the credentials match, otherwise null
     */
    public static User authenticate(String email, String password) {
        String sql =
            "SELECT u.First_Name, u.Last_Name, u.SJSU_ID, u.Gender, u.Password_Hash, " +
            "       CASE WHEN d.User_ID IS NOT NULL THEN 1 ELSE 0 END AS Is_Driver, " +
            "       CASE WHEN p.User_ID IS NOT NULL THEN 1 ELSE 0 END AS Is_Passenger " +
            "FROM Users u " +
            "LEFT JOIN Drivers d ON d.User_ID = u.User_ID " +
            "LEFT JOIN Passengers p ON p.User_ID = u.User_ID " +
            "WHERE u.Email = ? AND u.Account_Status = 'active' LIMIT 1";

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
                boolean isDriver = rs.getInt("Is_Driver") == 1;
                boolean isPassenger = rs.getInt("Is_Passenger") == 1;

                Set<String> roles = new HashSet<>();
                if (isDriver) roles.add("driver");
                if (isPassenger) roles.add("passenger");

                return new User(firstName, lastName, email, sjsuId, gender, storedHash, roles);
            }
        } catch (SQLException e) {
            throw new RuntimeException("authenticate failed", e);
        }
    }

    /**
     * Marks an account as deleted without physically removing the row.
     *
     * Soft deletion keeps the record available for referential integrity and
     * historical reporting while preventing future logins.
     *
     * @param email active account email to deactivate
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

    /**
     * Updates the stored password for an active account.
     *
     * @param email account email to update
     * @param newPassword replacement credential value
     * @return true when exactly one active row was updated
     */
    public static boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE Users SET Password_Hash = ? WHERE Email = ? AND Account_Status = 'active'";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("updatePassword failed", e);
        }
    }

    /**
     * Retrieves the driver's verification state for the active account.
     *
     * @param email driver email to query
     * @return pending, verified, or null when the account is not a driver
     */
    public static String getDriverVerificationStatus(String email) {
        String sql =
            "SELECT d.Verification_Status FROM Drivers d " +
            "JOIN Users u ON u.User_ID = d.User_ID " +
            "WHERE u.Email = ? AND u.Account_Status = 'active' LIMIT 1";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("Verification_Status") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getDriverVerificationStatus failed", e);
        }
    }

    // --------------------------------------------------------------- vehicles

    /**
     * Loads all vehicles owned by an active account.
     *
     * @param ownerEmail account email used to filter the ownership join
     * @return vehicles visible to the driver dashboard
     */
    public static List<Vehicle> getVehiclesForOwner(String ownerEmail) {
        String sql =
            "SELECT v.Vehicle_ID, v.Make, v.Model, v.Color, v.License_Plate, v.Total_Seats " +
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
                        rs.getString("Model"),
                        rs.getString("Color"),
                        rs.getString("License_Plate"),
                        rs.getInt("Total_Seats")
                    ));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("getVehiclesForOwner failed", e);
        }
    }

    /**
     * Adds a vehicle row and associates it with the active owner.
     *
     * @param ownerEmail driver email that should own the new vehicle
     * @param make vehicle make entered on the form
     * @param model vehicle model entered on the form
     * @param color vehicle color entered on the form
     * @param plate license plate entered on the form
     * @param totalSeats total seats entered on the form
     */
    public static void addVehicle(String ownerEmail, String make, String model, String color, String plate, int totalSeats) {
        String insertVehicle =
            "INSERT INTO Vehicles (License_Plate, Make, Model, Color, Total_Seats) VALUES (?, ?, ?, ?, ?)";
        String insertOwns =
            "INSERT INTO Owns (User_ID, Vehicle_ID) " +
            "SELECT User_ID, ? FROM Users WHERE Email = ? AND Account_Status = 'active'";

        try (Connection c = DBConnection.get()) {
            int vehicleId;
            try (PreparedStatement pv = c.prepareStatement(
                    insertVehicle, Statement.RETURN_GENERATED_KEYS)) {
                pv.setString(1, plate);
                pv.setString(2, make);
                pv.setString(3, model);
                pv.setString(4, color);
                pv.setInt(5, totalSeats);
                pv.executeUpdate();
                try (ResultSet keys = pv.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No generated key for vehicle");
                    vehicleId = keys.getInt(1);
                }
            }

            try (PreparedStatement po = c.prepareStatement(insertOwns)) {
                    /**
                     * Deletes a vehicle only when it is owned by the active account.
                     *
                     * @param ownerEmail driver email used to authorize the delete
                     * @param vehicleId vehicle identifier submitted by the dashboard form
                     */
                po.setInt(1, vehicleId);
                po.setString(2, ownerEmail);
                po.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("addVehicle failed", e);
        }
    }

    public static void deleteVehicle(String ownerEmail, String vehicleId) {
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
