package model;

/**
 * Immutable passenger booking view model for upcoming rides.
 */
public class UpcomingRide {
    private final String bookingId;
    private final String bookingStatus;
    private final String origin;
    private final String destination;
    private final String departureDate;
    private final int seats;
    private final String driverName;
    private final String vehicleInfo;

    public UpcomingRide(
        String bookingId,
        String bookingStatus,
        String origin,
        String destination,
        String departureDate,
        int seats,
        String driverName,
        String vehicleInfo
    ) {
        this.bookingId = bookingId;
        this.bookingStatus = bookingStatus;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.seats = seats;
        this.driverName = driverName;
        this.vehicleInfo = vehicleInfo;
    }

    public String getBookingId() { return bookingId; }
    public String getBookingStatus() { return bookingStatus; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public String getDepartureDate() { return departureDate; }
    public int getSeats() { return seats; }
    public String getDriverName() { return driverName; }
    public String getVehicleInfo() { return vehicleInfo; }
}