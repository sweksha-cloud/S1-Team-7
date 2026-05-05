package store;

import db.DBConnection;
import model.PassengerRequest;
import model.User;
import model.Ride;
import model.UpcomingRide;
import model.Vehicle;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AppStore {

    private AppStore() {}

    private static final int BCRYPT_COST = 12;

    private static String hashPassword(String password) {
        if (password == null) throw new IllegalArgumentException("password is required");
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());
    }

    private static boolean verifyPassword(String password, String storedHashOrPlaintext) {
        if (password == null || storedHashOrPlaintext == null) return false;
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHashOrPlaintext);
        if (result.verified) return true;
        // Backward compatibility for accounts created before hashing existed.
        return storedHashOrPlaintext.equals(password);
    }

    private static void refreshRideStatuses(Connection c) throws SQLException {
        // Rides in the past should no longer accept bookings.
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE Rides SET Status = 'completed' WHERE Departure_Date <= NOW() AND Status <> 'completed'")) {
            ps.executeUpdate();
        }

        // Keep seat-based status aligned for upcoming rides.
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE Rides SET Status = 'full' WHERE Departure_Date > NOW() AND Seats_Left <= 0 AND Status <> 'completed'")) {
            ps.executeUpdate();
        }

        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE Rides SET Status = 'open' WHERE Departure_Date > NOW() AND Seats_Left > 0 AND Status = 'full'")) {
            ps.executeUpdate();
        }
    }

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

        try (Connection c = DBConnection.get()) {
            String passwordHash = hashPassword(password);

            int userId;
            try (PreparedStatement ps = c.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, sjsuId);
                ps.setString(2, firstName);
                ps.setString(3, lastName);
                ps.setString(4, email);
                ps.setString(5, gender);
                ps.setString(6, passwordHash);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No generated key returned");
                    userId = keys.getInt(1);
                }
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

            return new User(firstName, lastName, email, sjsuId, gender, passwordHash, roles);

        } catch (SQLException e) {
            throw new RuntimeException("createUser failed", e);
        }
    }

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
                if (!verifyPassword(password, storedHash)) return null;

                // If this account was stored as plaintext historically, upgrade it now.
                if (storedHash != null && storedHash.equals(password)) {
                    try (PreparedStatement upgrade = c.prepareStatement(
                            "UPDATE Users SET Password_Hash = ? WHERE Email = ? AND Account_Status = 'active'")) {
                        String upgradedHash = hashPassword(password);
                        upgrade.setString(1, upgradedHash);
                        upgrade.setString(2, email);
                        upgrade.executeUpdate();
                        storedHash = upgradedHash;
                    }
                }

                String firstName    = rs.getString("First_Name");
                String lastName     = rs.getString("Last_Name");
                String sjsuId       = rs.getString("SJSU_ID");
                String gender       = rs.getString("Gender");
                boolean isDriver    = rs.getInt("Is_Driver") == 1;
                boolean isPassenger = rs.getInt("Is_Passenger") == 1;

                Set<String> roles = new HashSet<>();
                if (isDriver)    roles.add("driver");
                if (isPassenger) roles.add("passenger");

                return new User(firstName, lastName, email, sjsuId, gender, storedHash, roles);
            }
        } catch (SQLException e) {
            throw new RuntimeException("authenticate failed", e);
        }
    }

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

    public static boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE Users SET Password_Hash = ? WHERE Email = ? AND Account_Status = 'active'";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, hashPassword(newPassword));
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("updatePassword failed", e);
        }
    }

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

    public static List<Vehicle> getVehiclesForOwner(String ownerEmail) {
        String sql =
            "SELECT v.Vehicle_ID, v.Make, v.Model, v.Color, v.License_Plate, v.Total_Seats, v.Insurance_Num " +
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
                        rs.getInt("Total_Seats"),
                        rs.getString("Insurance_Num")
                    ));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("getVehiclesForOwner failed", e);
        }
    }

    public static void addVehicle(String ownerEmail, String make, String model,
                                   String color, String plate, int totalSeats, String insuranceNum) {
        String insertVehicle =
            "INSERT INTO Vehicles (License_Plate, Make, Model, Color, Total_Seats, Insurance_Num) VALUES (?, ?, ?, ?, ?, ?)";
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
                pv.setString(6, insuranceNum);
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

    public static void updateVehicle(String ownerEmail, String vehicleId, String make, String model,
                                     String color, String plate, int totalSeats, String insuranceNum) {
        String sql =
            "UPDATE Vehicles v " +
            "SET v.Make = ?, v.Model = ?, v.Color = ?, v.License_Plate = ?, v.Total_Seats = ?, v.Insurance_Num = ? " +
            "WHERE v.Vehicle_ID = ? " +
            "AND EXISTS (SELECT 1 FROM Owns o JOIN Users u ON u.User_ID = o.User_ID " +
            "            WHERE o.Vehicle_ID = v.Vehicle_ID AND u.Email = ?)";

        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, make);
            ps.setString(2, model);
            ps.setString(3, color);
            ps.setString(4, plate);
            ps.setInt(5, totalSeats);
            ps.setString(6, insuranceNum);
            ps.setInt(7, Integer.parseInt(vehicleId));
            ps.setString(8, ownerEmail);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("updateVehicle failed", e);
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

    // --------------------------------------------------------------- rides

    public static void createBooking(String passengerEmail, String origin, String destination, String departureDate, int seatsLeft) {
        String nextRideIdSql = "SELECT COALESCE(MAX(Ride_ID), 0) + 1 AS Next_Ride_ID FROM Rides";
        String nextBookingIdSql = "SELECT COALESCE(MAX(Booking_ID), 0) + 1 AS Next_Booking_ID FROM Bookings";
        String insertRide = "INSERT INTO Rides (Ride_ID, Origin, Destination, Departure_Date, Seats_Left, Status) " +
                            "VALUES (?, ?, ?, ?, ?, 'open')";
        String insertBooking = "INSERT INTO Bookings (Booking_ID, Booking_Timestamp, Status) VALUES (?, CURRENT_TIMESTAMP, 'pending')";
        String linkBookingRide = "INSERT INTO Booking_Ride (Booking_ID, Ride_ID) VALUES (?, ?)";
        String linkPassengerBooking =
            "INSERT INTO Makes (User_ID, Booking_ID) " +
            "SELECT u.User_ID, ? FROM Users u " +
            "WHERE u.Email = ? AND u.Account_Status = 'active'";

        try (Connection c = DBConnection.get()) {
            c.setAutoCommit(false);

            int rideId;
            try (PreparedStatement rideIdStatement = c.prepareStatement(nextRideIdSql);
                 ResultSet rideRows = rideIdStatement.executeQuery()) {
                if (!rideRows.next()) {
                    throw new SQLException("Unable to allocate ride id");
                }
                rideId = rideRows.getInt("Next_Ride_ID");
            }

            int bookingId;
            try (PreparedStatement bookingIdStatement = c.prepareStatement(nextBookingIdSql);
                 ResultSet bookingRows = bookingIdStatement.executeQuery()) {
                if (!bookingRows.next()) {
                    throw new SQLException("Unable to allocate booking id");
                }
                bookingId = bookingRows.getInt("Next_Booking_ID");
            }

            try (PreparedStatement rideStatement = c.prepareStatement(insertRide);
                 PreparedStatement bookingStatement = c.prepareStatement(insertBooking);
                 PreparedStatement linkStatement = c.prepareStatement(linkBookingRide);
                 PreparedStatement makesStatement = c.prepareStatement(linkPassengerBooking)) {

                rideStatement.setInt(1, rideId);
                rideStatement.setString(2, origin);
                rideStatement.setString(3, destination);
                rideStatement.setString(4, departureDate);
                rideStatement.setInt(5, seatsLeft);
                rideStatement.executeUpdate();

                bookingStatement.setInt(1, bookingId);
                bookingStatement.executeUpdate();

                linkStatement.setInt(1, bookingId);
                linkStatement.setInt(2, rideId);
                linkStatement.executeUpdate();

                makesStatement.setInt(1, bookingId);
                makesStatement.setString(2, passengerEmail);
                if (makesStatement.executeUpdate() == 0) {
                    throw new SQLException("No passenger row found for booking");
                }

                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("createBooking failed", e);
        }
    }

    public static void createBookingForRide(String passengerEmail, int rideId) {
        requestSeatOnRide(passengerEmail, rideId);
    }

    public static void requestSeatOnRide(String passengerEmail, int rideId) {
        createBookingForRideInternal(passengerEmail, rideId, "pending");
    }

    public static void directBookSeatOnRide(String passengerEmail, int rideId) {
        createBookingForRideInternal(passengerEmail, rideId, "accepted");
    }

    private static void createBookingForRideInternal(String passengerEmail, int rideId, String bookingStatus) {
        String rideCheckSql =
            "SELECT Ride_ID, Seats_Left, Status, Departure_Date FROM Rides WHERE Ride_ID = ? FOR UPDATE";
        String nextBookingIdSql =
            "SELECT COALESCE(MAX(Booking_ID), 0) + 1 AS Next_Booking_ID FROM Bookings";
        String insertBooking =
            "INSERT INTO Bookings (Booking_ID, Booking_Timestamp, Status) VALUES (?, CURRENT_TIMESTAMP, ?)";
        String linkBookingRide =
            "INSERT INTO Booking_Ride (Booking_ID, Ride_ID) VALUES (?, ?)";
        String linkPassengerBooking =
            "INSERT INTO Makes (User_ID, Booking_ID) " +
            "SELECT u.User_ID, ? FROM Users u " +
            "WHERE u.Email = ? AND u.Account_Status = 'active'";
        String decreaseRideSeat =
            "UPDATE Rides SET Seats_Left = Seats_Left - 1 WHERE Ride_ID = ? AND Seats_Left > 0";
        String setFullIfNeeded =
            "UPDATE Rides SET Status = 'full' WHERE Ride_ID = ? AND Seats_Left <= 0 AND Departure_Date > NOW()";

        try (Connection c = DBConnection.get()) {
            c.setAutoCommit(false);

            refreshRideStatuses(c);

            int seatsLeft;
            String rideStatus;
            Timestamp departureDate;

            try (PreparedStatement rideCheckStatement = c.prepareStatement(rideCheckSql)) {
                rideCheckStatement.setInt(1, rideId);
                try (ResultSet rideRows = rideCheckStatement.executeQuery()) {
                    if (!rideRows.next()) {
                        throw new SQLException("Ride not found");
                    }
                    seatsLeft = rideRows.getInt("Seats_Left");
                    rideStatus = rideRows.getString("Status");
                    departureDate = rideRows.getTimestamp("Departure_Date");
                }
            }

            boolean hasDeparted = departureDate == null || !departureDate.after(new Timestamp(System.currentTimeMillis()));
            if (hasDeparted || !"open".equalsIgnoreCase(rideStatus) || seatsLeft <= 0) {
                throw new SQLException("Ride is not available for booking");
            }

            int bookingId;
            try (PreparedStatement bookingIdStatement = c.prepareStatement(nextBookingIdSql);
                 ResultSet bookingRows = bookingIdStatement.executeQuery()) {
                if (!bookingRows.next()) {
                    throw new SQLException("Unable to allocate booking id");
                }
                bookingId = bookingRows.getInt("Next_Booking_ID");
            }

              try (PreparedStatement bookingStatement = c.prepareStatement(insertBooking);
                 PreparedStatement linkStatement = c.prepareStatement(linkBookingRide);
                  PreparedStatement makesStatement = c.prepareStatement(linkPassengerBooking);
                  PreparedStatement decreaseSeatStatement = c.prepareStatement(decreaseRideSeat);
                  PreparedStatement setFullStatement = c.prepareStatement(setFullIfNeeded)) {

                bookingStatement.setInt(1, bookingId);
                 bookingStatement.setString(2, bookingStatus);
                bookingStatement.executeUpdate();

                linkStatement.setInt(1, bookingId);
                linkStatement.setInt(2, rideId);
                linkStatement.executeUpdate();

                makesStatement.setInt(1, bookingId);
                makesStatement.setString(2, passengerEmail);
                if (makesStatement.executeUpdate() == 0) {
                    throw new SQLException("No passenger row found for booking");
                }

                if ("accepted".equalsIgnoreCase(bookingStatus)) {
                    decreaseSeatStatement.setInt(1, rideId);
                    if (decreaseSeatStatement.executeUpdate() == 0) {
                        throw new SQLException("Ride is full");
                    }
                    setFullStatement.setInt(1, rideId);
                    setFullStatement.executeUpdate();
                }

                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("createBookingForRide failed", e);
        }
    }

    public static java.util.List<Ride> getAvailableRides() {
        String sql =
            "SELECT r.Ride_ID, r.Origin, r.Destination, r.Departure_Date, r.Seats_Left, r.Status, " +
            "       CONCAT(u.First_Name, ' ', LEFT(u.Last_Name, 1), '.') AS Driver_Name, " +
            "       (SELECT CONCAT(v.Color, ' ', v.Make, ' ', v.Model, ' (Plate ', v.License_Plate, ')') " +
            "        FROM Owns o2 " +
            "        JOIN Vehicles v ON v.Vehicle_ID = o2.Vehicle_ID " +
            "        WHERE o2.User_ID = u.User_ID " +
            "        ORDER BY v.Vehicle_ID ASC LIMIT 1) AS Vehicle_Info " +
            "FROM Rides r " +
            "LEFT JOIN Schedules s ON s.Ride_ID = r.Ride_ID " +
            "LEFT JOIN Users u ON u.User_ID = s.User_ID " +
            "WHERE r.Status = 'open' " +
            "  AND r.Seats_Left > 0 " +
            "  AND r.Departure_Date > NOW() " +
            "ORDER BY r.Departure_Date ASC";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            refreshRideStatuses(c);
            java.util.List<Ride> list = new java.util.ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Ride(
                        String.valueOf(rs.getInt("Ride_ID")),
                        rs.getString("Origin"),
                        rs.getString("Destination"),
                        rs.getString("Departure_Date"),
                        rs.getInt("Seats_Left"),
                        rs.getString("Status"),
                        rs.getString("Driver_Name"),
                        rs.getString("Vehicle_Info")
                    ));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("getAvailableRides failed", e);
        }
    }

    public static java.util.List<UpcomingRide> getUpcomingRidesForPassenger(String passengerEmail) {
        String sql =
            "SELECT b.Booking_ID, b.Status AS Booking_Status, " +
            "       r.Origin, r.Destination, r.Departure_Date, r.Seats_Left, " +
            "       CONCAT(du.First_Name, ' ', LEFT(du.Last_Name, 1), '.') AS Driver_Name, " +
            "       (SELECT CONCAT(v.Color, ' ', v.Make, ' ', v.Model, ' (Plate ', v.License_Plate, ')') " +
            "        FROM Owns o2 " +
            "        JOIN Vehicles v ON v.Vehicle_ID = o2.Vehicle_ID " +
            "        WHERE o2.User_ID = du.User_ID " +
            "        ORDER BY v.Vehicle_ID ASC LIMIT 1) AS Vehicle_Info " +
            "FROM Bookings b " +
            "JOIN Makes m ON m.Booking_ID = b.Booking_ID " +
            "JOIN Users pu ON pu.User_ID = m.User_ID " +
            "JOIN Booking_Ride br ON br.Booking_ID = b.Booking_ID " +
            "JOIN Rides r ON r.Ride_ID = br.Ride_ID " +
            "LEFT JOIN Schedules s ON s.Ride_ID = r.Ride_ID " +
            "LEFT JOIN Users du ON du.User_ID = s.User_ID " +
            "WHERE pu.Email = ? " +
            "  AND pu.Account_Status = 'active' " +
            "  AND b.Status IN ('pending', 'accepted') " +
            "  AND r.Departure_Date >= NOW() " +
            "ORDER BY r.Departure_Date ASC";

        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            refreshRideStatuses(c);
            ps.setString(1, passengerEmail);
            java.util.List<UpcomingRide> list = new java.util.ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new UpcomingRide(
                        String.valueOf(rs.getInt("Booking_ID")),
                        rs.getString("Booking_Status"),
                        rs.getString("Origin"),
                        rs.getString("Destination"),
                        rs.getString("Departure_Date"),
                        rs.getInt("Seats_Left"),
                        rs.getString("Driver_Name"),
                        rs.getString("Vehicle_Info")
                    ));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("getUpcomingRidesForPassenger failed", e);
        }
    }

    public static boolean cancelUpcomingRideForPassenger(String passengerEmail, String bookingId) {
        String selectSql =
            "SELECT b.Status AS Booking_Status, r.Ride_ID, r.Status AS Ride_Status, r.Departure_Date " +
            "FROM Bookings b " +
            "JOIN Makes m ON m.Booking_ID = b.Booking_ID " +
            "JOIN Users pu ON pu.User_ID = m.User_ID " +
            "JOIN Booking_Ride br ON br.Booking_ID = b.Booking_ID " +
            "JOIN Rides r ON r.Ride_ID = br.Ride_ID " +
            "WHERE b.Booking_ID = ? " +
            "  AND pu.Email = ? " +
            "  AND pu.Account_Status = 'active' " +
            "FOR UPDATE";
        String cancelSql = "UPDATE Bookings SET Status = 'cancelled' WHERE Booking_ID = ?";
        String increaseSeatSql = "UPDATE Rides SET Seats_Left = Seats_Left + 1 WHERE Ride_ID = ?";

        try (Connection c = DBConnection.get()) {
            c.setAutoCommit(false);
            refreshRideStatuses(c);

            String bookingStatus;
            int rideId;
            String rideStatus;
            Timestamp departureDate;

            try (PreparedStatement selectStatement = c.prepareStatement(selectSql)) {
                selectStatement.setInt(1, Integer.parseInt(bookingId));
                selectStatement.setString(2, passengerEmail);
                try (ResultSet rs = selectStatement.executeQuery()) {
                    if (!rs.next()) {
                        c.rollback();
                        return false;
                    }
                    bookingStatus = rs.getString("Booking_Status");
                    rideId = rs.getInt("Ride_ID");
                    rideStatus = rs.getString("Ride_Status");
                    departureDate = rs.getTimestamp("Departure_Date");
                }
            }

            boolean hasDeparted = departureDate == null || !departureDate.after(new Timestamp(System.currentTimeMillis()));
            if (hasDeparted || "completed".equalsIgnoreCase(rideStatus) ||
                (!"pending".equalsIgnoreCase(bookingStatus) && !"accepted".equalsIgnoreCase(bookingStatus))) {
                c.rollback();
                return false;
            }

            try (PreparedStatement cancelStatement = c.prepareStatement(cancelSql);
                 PreparedStatement increaseSeatStatement = c.prepareStatement(increaseSeatSql)) {

                if ("accepted".equalsIgnoreCase(bookingStatus)) {
                    increaseSeatStatement.setInt(1, rideId);
                    increaseSeatStatement.executeUpdate();
                }

                cancelStatement.setInt(1, Integer.parseInt(bookingId));
                boolean cancelled = cancelStatement.executeUpdate() > 0;

                refreshRideStatuses(c);
                c.commit();
                return cancelled;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("cancelUpcomingRideForPassenger failed", e);
        }
    }

    public static java.util.List<PassengerRequest> getPassengerRequestsForDriver(String driverEmail) {
        String sql =
            "SELECT b.Booking_ID, b.Status AS Booking_Status, b.Booking_Timestamp, " +
            "       CONCAT(pu.First_Name, ' ', LEFT(pu.Last_Name, 1), '.') AS Passenger_Name, " +
            "       r.Origin, r.Destination, r.Departure_Date, r.Seats_Left " +
            "FROM Bookings b " +
            "JOIN Booking_Ride br ON br.Booking_ID = b.Booking_ID " +
            "JOIN Rides r ON r.Ride_ID = br.Ride_ID " +
            "JOIN Schedules s ON s.Ride_ID = r.Ride_ID " +
            "JOIN Users du ON du.User_ID = s.User_ID " +
            "JOIN Makes m ON m.Booking_ID = b.Booking_ID " +
            "JOIN Users pu ON pu.User_ID = m.User_ID " +
            "WHERE du.Email = ? AND du.Account_Status = 'active' " +
            "ORDER BY b.Booking_Timestamp DESC";

        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            refreshRideStatuses(c);
            ps.setString(1, driverEmail);

            java.util.List<PassengerRequest> list = new java.util.ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PassengerRequest(
                        String.valueOf(rs.getInt("Booking_ID")),
                        rs.getString("Passenger_Name"),
                        rs.getString("Origin"),
                        rs.getString("Destination"),
                        rs.getString("Departure_Date"),
                        rs.getInt("Seats_Left"),
                        rs.getString("Booking_Status"),
                        rs.getString("Booking_Timestamp")
                    ));
                }
            }

            return list;
        } catch (SQLException e) {
            throw new RuntimeException("getPassengerRequestsForDriver failed", e);
        }
    }

    public static boolean updatePassengerRequestStatus(String driverEmail, String bookingId, String newStatus) {
        String selectSql =
            "SELECT b.Status AS Booking_Status, r.Ride_ID, r.Seats_Left, r.Status AS Ride_Status, r.Departure_Date " +
            "FROM Bookings b " +
            "JOIN Booking_Ride br ON br.Booking_ID = b.Booking_ID " +
            "JOIN Rides r ON r.Ride_ID = br.Ride_ID " +
            "JOIN Schedules s ON s.Ride_ID = r.Ride_ID " +
            "JOIN Users du ON du.User_ID = s.User_ID " +
            "WHERE b.Booking_ID = ? " +
            "  AND du.Email = ? " +
            "  AND du.Account_Status = 'active' " +
            "FOR UPDATE";
        String updateBookingStatusSql = "UPDATE Bookings SET Status = ? WHERE Booking_ID = ?";
        String decreaseSeatSql = "UPDATE Rides SET Seats_Left = Seats_Left - 1 WHERE Ride_ID = ? AND Seats_Left > 0";

        if (!"accepted".equalsIgnoreCase(newStatus) && !"declined".equalsIgnoreCase(newStatus)) {
            return false;
        }

        try (Connection c = DBConnection.get()) {
            c.setAutoCommit(false);
            refreshRideStatuses(c);

            int rideId;
            int seatsLeft;
            String rideStatus;
            String bookingStatus;
            Timestamp departureDate;

            try (PreparedStatement selectStatement = c.prepareStatement(selectSql)) {
                selectStatement.setInt(1, Integer.parseInt(bookingId));
                selectStatement.setString(2, driverEmail);

                try (ResultSet rs = selectStatement.executeQuery()) {
                    if (!rs.next()) {
                        c.rollback();
                        return false;
                    }
                    rideId = rs.getInt("Ride_ID");
                    seatsLeft = rs.getInt("Seats_Left");
                    rideStatus = rs.getString("Ride_Status");
                    bookingStatus = rs.getString("Booking_Status");
                    departureDate = rs.getTimestamp("Departure_Date");
                }
            }

            if (!"pending".equalsIgnoreCase(bookingStatus)) {
                c.rollback();
                return false;
            }

            boolean hasDeparted = departureDate == null || !departureDate.after(new Timestamp(System.currentTimeMillis()));
            if ("accepted".equalsIgnoreCase(newStatus) &&
                (hasDeparted || !"open".equalsIgnoreCase(rideStatus) || seatsLeft <= 0)) {
                c.rollback();
                return false;
            }

            try (PreparedStatement updateBookingStatusStatement = c.prepareStatement(updateBookingStatusSql);
                 PreparedStatement decreaseSeatStatement = c.prepareStatement(decreaseSeatSql)) {

                if ("accepted".equalsIgnoreCase(newStatus)) {
                    decreaseSeatStatement.setInt(1, rideId);
                    if (decreaseSeatStatement.executeUpdate() == 0) {
                        c.rollback();
                        return false;
                    }
                }

                updateBookingStatusStatement.setString(1, newStatus);
                updateBookingStatusStatement.setInt(2, Integer.parseInt(bookingId));
                boolean updated = updateBookingStatusStatement.executeUpdate() > 0;

                refreshRideStatuses(c);
                c.commit();
                return updated;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("updatePassengerRequestStatus failed", e);
        }
    }

    public static void createRide(String driverEmail, String origin, String destination, String departureDate, int seatsLeft) {
        String nextRideIdSql = "SELECT COALESCE(MAX(Ride_ID), 0) + 1 AS Next_Ride_ID FROM Rides";
        String insertRide = "INSERT INTO Rides (Ride_ID, Origin, Destination, Departure_Date, Seats_Left, Status) " +
                            "VALUES (?, ?, ?, ?, ?, 'open')";
        String assignDriver =
            "INSERT INTO Schedules (User_ID, Ride_ID) " +
            "SELECT u.User_ID, ? FROM Users u " +
            "JOIN Drivers d ON d.User_ID = u.User_ID " +
            "WHERE u.Email = ? AND u.Account_Status = 'active'";

        try (Connection c = DBConnection.get()) {
            c.setAutoCommit(false);

            int rideId;
            try (PreparedStatement nextRideIdStatement = c.prepareStatement(nextRideIdSql);
                 ResultSet nextRideIdRows = nextRideIdStatement.executeQuery()) {
                if (!nextRideIdRows.next()) {
                    throw new SQLException("Unable to allocate ride id");
                }
                rideId = nextRideIdRows.getInt("Next_Ride_ID");
            }

            try (PreparedStatement rideStatement = c.prepareStatement(insertRide, Statement.RETURN_GENERATED_KEYS)) {
                rideStatement.setInt(1, rideId);
                rideStatement.setString(2, origin);
                rideStatement.setString(3, destination);
                rideStatement.setString(4, departureDate);
                rideStatement.setInt(5, seatsLeft);
                rideStatement.executeUpdate();

                try (PreparedStatement scheduleStatement = c.prepareStatement(assignDriver)) {
                    scheduleStatement.setInt(1, rideId);
                    scheduleStatement.setString(2, driverEmail);
                    if (scheduleStatement.executeUpdate() == 0) {
                        throw new SQLException("No driver row found for ride assignment");
                    }
                }

                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("createRide failed", e);
        }
    }

    /** Returns all rides created by this driver, most recent first. */
    public static java.util.List<Ride> getRidesForDriver(String driverEmail) {
        String sql =
            "SELECT r.Ride_ID, r.Origin, r.Destination, r.Departure_Date, r.Seats_Left, r.Status " +
            "FROM Rides r " +
            "JOIN Schedules s ON s.Ride_ID = r.Ride_ID " +
            "JOIN Users u ON u.User_ID = s.User_ID " +
            "WHERE u.Email = ? AND u.Account_Status = 'active' " +
            "ORDER BY r.Departure_Date DESC";

        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, driverEmail);
            java.util.List<Ride> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Ride(
                        String.valueOf(rs.getInt("Ride_ID")),
                        rs.getString("Origin"),
                        rs.getString("Destination"),
                        rs.getString("Departure_Date"),
                        rs.getInt("Seats_Left"),
                        rs.getString("Status"),
                        null, null
                    ));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("getRidesForDriver failed", e);
        }
    }

    /**
     * Cancels a ride and notifies all affected passengers via Notifications + Personalizes.
     * Returns true on success, false if ride can't be cancelled.
     */
    public static boolean cancelRide(String driverEmail, String rideId) {
        String verifySQL =
            "SELECT r.Status, r.Origin, r.Destination " +
            "FROM Rides r " +
            "JOIN Schedules s ON s.Ride_ID = r.Ride_ID " +
            "JOIN Users u ON u.User_ID = s.User_ID " +
            "WHERE r.Ride_ID = ? AND u.Email = ? AND u.Account_Status = 'active' FOR UPDATE";

        String getPassengersSQL =
            "SELECT u.User_ID " +
            "FROM Users u " +
            "JOIN Makes m ON m.User_ID = u.User_ID " +
            "JOIN Bookings b ON b.Booking_ID = m.Booking_ID " +
            "JOIN Booking_Ride br ON br.Booking_ID = b.Booking_ID " +
            "WHERE br.Ride_ID = ? AND b.Status IN ('pending', 'accepted')";

        String cancelBookingsSQL =
            "UPDATE Bookings b " +
            "JOIN Booking_Ride br ON br.Booking_ID = b.Booking_ID " +
            "SET b.Status = 'cancelled' " +
            "WHERE br.Ride_ID = ? AND b.Status IN ('pending', 'accepted')";

        String cancelRideSQL =
            "UPDATE Rides SET Status = 'cancelled' WHERE Ride_ID = ?";

        String insertNotifSQL =
            "INSERT INTO Notifications (Content, Timestamp, Read_Status) VALUES (?, NOW(), 'unread')";

        String insertPersonalizesSQL =
            "INSERT INTO Personalizes (User_ID, Notification_ID) VALUES (?, ?)";

        try (Connection c = DBConnection.get()) {
            c.setAutoCommit(false);

            // Verify ride belongs to driver and is cancellable.
            String rideStatus, origin, destination;
            try (PreparedStatement ps = c.prepareStatement(verifySQL)) {
                ps.setInt(1, Integer.parseInt(rideId));
                ps.setString(2, driverEmail);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) { c.rollback(); return false; }
                    rideStatus  = rs.getString("Status");
                    origin      = rs.getString("Origin");
                    destination = rs.getString("Destination");
                }
            }

            if ("cancelled".equalsIgnoreCase(rideStatus) || "completed".equalsIgnoreCase(rideStatus)) {
                c.rollback();
                return false;
            }

            // Collect affected passenger IDs before cancelling.
            java.util.List<Integer> passengerIds = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(getPassengersSQL)) {
                ps.setInt(1, Integer.parseInt(rideId));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) passengerIds.add(rs.getInt("User_ID"));
                }
            }

            // Cancel all active bookings.
            try (PreparedStatement ps = c.prepareStatement(cancelBookingsSQL)) {
                ps.setInt(1, Integer.parseInt(rideId));
                ps.executeUpdate();
            }

            // Cancel the ride.
            try (PreparedStatement ps = c.prepareStatement(cancelRideSQL)) {
                ps.setInt(1, Integer.parseInt(rideId));
                ps.executeUpdate();
            }

            // Notify each affected passenger.
            if (!passengerIds.isEmpty()) {
                String message = "Your ride from " + origin + " to " + destination + " has been cancelled by the driver.";
                try (PreparedStatement psNotif = c.prepareStatement(insertNotifSQL, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement psPersonalizes = c.prepareStatement(insertPersonalizesSQL)) {
                    for (int userId : passengerIds) {
                        psNotif.setString(1, message);
                        psNotif.executeUpdate();
                        int notifId;
                        try (ResultSet keys = psNotif.getGeneratedKeys()) {
                            if (!keys.next()) continue;
                            notifId = keys.getInt(1);
                        }
                        psPersonalizes.setInt(1, userId);
                        psPersonalizes.setInt(2, notifId);
                        psPersonalizes.executeUpdate();
                    }
                }
            }

            c.commit();
            return true;

        } catch (SQLException e) {
            try { throw new RuntimeException("cancelRide failed", e); }
            finally { }
        }
    }
}