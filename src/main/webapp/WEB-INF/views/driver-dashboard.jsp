<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="model.PassengerRequest" %>
<%@ page import="model.User" %>
<%
String cp = request.getContextPath();
User currentUser = (User) session.getAttribute("currentUser");
boolean pending = request.getAttribute("pendingVerification") != null;
boolean canSwapDashboard = currentUser != null && currentUser.hasRole("driver") && currentUser.hasRole("passenger");
List<PassengerRequest> passengerRequests = (List<PassengerRequest>) request.getAttribute("passengerRequests");
if (passengerRequests == null) {
  passengerRequests = java.util.Collections.emptyList();
}
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Driver Dashboard | UniRide</title>
  <link rel="stylesheet" href="<%= cp %>/assets/css/common.css?v=20260427" />
  <link rel="stylesheet" href="<%= cp %>/assets/css/home.css?v=20260427" />
  <link rel="stylesheet" href="<%= cp %>/assets/css/login.css?v=20260427" />
  <link rel="stylesheet" href="<%= cp %>/assets/css/dashboard.css?v=20260427" />
  <style>
  </style>
</head>
<body>
  <div class="login-page dashboard-page">
    <nav class="navbar">
      <h1 class="logo"><a href="<%= cp %>/home">Uni<span class="highlight">Ride</span></a></h1>
      <div class="nav-links dashboard-nav-links">
        <span class="dashboard-welcome">Welcome <%= currentUser != null ? currentUser.getFirstName() : "Driver" %></span>
        <a href="<%= cp %>/settings" class="nav-btn-primary dashboard-settings">Settings</a>
        <% if (canSwapDashboard) { %>
          <a href="<%= cp %>/dashboard/passenger" class="nav-btn-secondary">Switch to Passenger</a>
        <% } %>
        <form method="post" action="<%= cp %>/logout" class="dashboard-inline-form">
          <button type="submit" class="nav-btn-secondary dashboard-signout">Sign Out</button>
        </form>
      </div>
    </nav>

    <div class="dashboard-main">
      <div class="dashboard-main-inner">

        <% if (pending) { %>
          <%-- Pending verification screen --%>
          <div class="login-shell dashboard-card" style="text-align:center; padding: 2rem;">
            <h3>Account Pending Verification</h3>
            <p style="margin-top: 1rem;">
              Your driver account is currently under review. Once verified by an admin,
              you will have full access to the driver dashboard.
            </p>
            <p style="margin-top: 0.5rem; color: #888;">
              In the meantime, you can use UniRide as a passenger.
            </p>
            <div style="margin-top: 1.5rem;">
              <a href="<%= cp %>/dashboard/passenger" class="login-submit" style="text-decoration:none; padding: 0.6rem 1.2rem;">
                Go to Passenger Dashboard
              </a>
            </div>
          </div>

        <% } else { %>
          <%-- Full driver dashboard --%>
          <section class="dashboard-hero dashboard-section">
            <div class="dashboard-hero-copy">
              <span class="campus-tag">Driver Hub</span>
              <h2>Manage your rides and vehicles from one place.</h2>
              <p>
                Keep your car details updated, review driver tools, and stay ready for the next ride request.
              </p>
            </div>
            <div class="dashboard-actions">
              <a href="<%= cp %>/dashboard/driver?action=showCreateRideForm" class="login-submit action-create" style="text-decoration: none; display: inline-flex; align-items: center; justify-content: center;">
                Create a Ride
              </a>
              <a href="<%= cp %>/dashboard/driver?action=showVehicles" class="login-submit action-requests" style="text-decoration: none; display: inline-flex; align-items: center; justify-content: center;">
                My Vehicles
              </a>
              <button class="login-submit action-earnings" type="button">My Earnings</button>
            </div>
          </section>

          <section class="dashboard-section dashboard-vehicle-card">
            <div class="dashboard-section-heading">
              <h3>Passenger Requests</h3>
              <p>Incoming requests appear here so you can review them directly from your dashboard.</p>
            </div>

            <% if (passengerRequests.isEmpty()) { %>
              <div class="dashboard-empty">
                <p>No passenger requests yet.</p>
              </div>
            <% } else { %>
              <div class="ride-list">
                <% for (PassengerRequest requestRow : passengerRequests) {
                     String rawDeparture = requestRow.getDepartureDate();
                     String formattedDeparture = rawDeparture;
                     String formattedDepartureTime = "";
                     try {
                       DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                       LocalDateTime departureDt = LocalDateTime.parse(rawDeparture, parser);
                       formattedDeparture = departureDt.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
                       formattedDepartureTime = departureDt.format(DateTimeFormatter.ofPattern("h:mm a"));
                     } catch (Exception ignored) {
                     }

                     String rawRequestedAt = requestRow.getBookingTimestamp();
                     String formattedRequestedAt = rawRequestedAt;
                     try {
                       DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                       LocalDateTime requestedDt = LocalDateTime.parse(rawRequestedAt, parser);
                       formattedRequestedAt = requestedDt.format(DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a"));
                     } catch (Exception ignored) {
                     }
                %>
                  <div class="ride-item">
                    <div class="ride-row">
                      <div class="ride-info">
                        <strong><%= requestRow.getOrigin() %> -> <%= requestRow.getDestination() %></strong>
                        <small class="ride-meta">Passenger: <%= requestRow.getPassengerName() %></small>
                        <small class="ride-meta">Request ID: #<%= requestRow.getBookingId() %></small>
                        <small class="ride-meta">Requested seats: <%= requestRow.getRequestedSeats() %></small>
                        <small class="ride-meta">Departure: <%= formattedDeparture %></small>
                        <% if (!formattedDepartureTime.isBlank()) { %>
                          <small class="ride-meta">Departure time: <%= formattedDepartureTime %></small>
                        <% } %>
                        <small class="ride-meta">Requested at: <%= formattedRequestedAt %></small>
                        <small class="ride-meta">Status: <%= requestRow.getBookingStatus() %></small>
                      </div>
                      <% if ("pending".equalsIgnoreCase(requestRow.getBookingStatus())) { %>
                        <div class="ride-actions request-actions">
                          <form method="post" action="<%= cp %>/dashboard/driver" class="dashboard-inline-form">
                            <input type="hidden" name="action" value="processPassengerRequest" />
                            <input type="hidden" name="bookingId" value="<%= requestRow.getBookingId() %>" />
                            <input type="hidden" name="decision" value="accept" />
                            <button type="submit" class="request-approve">Accept Request</button>
                          </form>
                          <form method="post" action="<%= cp %>/dashboard/driver" class="dashboard-inline-form">
                            <input type="hidden" name="action" value="processPassengerRequest" />
                            <input type="hidden" name="bookingId" value="<%= requestRow.getBookingId() %>" />
                            <input type="hidden" name="decision" value="decline" />
                            <button type="submit" class="request-decline">Decline</button>
                          </form>
                        </div>
                      <% } %>
                    </div>
                  </div>
                <% } %>
              </div>
            <% } %>

          </section>
        <% } %>

      </div>
    </div>
  </div>

</body>
</html>