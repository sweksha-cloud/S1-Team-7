<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.User" %>
<%@ page import="model.Vehicle" %>
<%
String cp = request.getContextPath();
User currentUser = (User) session.getAttribute("currentUser");
List<Vehicle> vehicles = (List<Vehicle>) request.getAttribute("vehicles");
boolean pending = request.getAttribute("pendingVerification") != null;
boolean canSwapDashboard = currentUser != null && currentUser.hasRole("driver") && currentUser.hasRole("passenger");
if (vehicles == null) {
  vehicles = java.util.Collections.emptyList();
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
              <button class="login-submit action-requests" type="button">Passenger Requests</button>
              <button class="login-submit action-earnings" type="button">My Earnings</button>
            </div>
          </section>

          <section class="dashboard-section dashboard-vehicle-card">
            <div class="dashboard-section-heading">
              <h3>My Registered Vehicles</h3>
              <p>Add each vehicle once and keep its details current for matching and pickup.</p>
            </div>

            <div class="vehicle-list-container">
                <% if (vehicles.isEmpty()) { %>
                  <p class="dashboard-empty">No vehicles added yet.</p>
                <% } else { %>
                  <% for (Vehicle vehicle : vehicles) { %>
                    <div class="vehicle-row">
                      <div class="vehicle-info">
                        <strong><%= vehicle.getColor() %> <%= vehicle.getMake() %> <%= vehicle.getModel() %></strong><br />
                        <small class="vehicle-meta">Plate: <%= vehicle.getPlate() %></small>
                        <small class="vehicle-meta">Total seats: <%= vehicle.getTotalSeats() %></small>
                      </div>
                      <form method="post" action="<%= cp %>/dashboard/driver" class="dashboard-inline-form">
                        <input type="hidden" name="action"    value="deleteVehicle" />
                        <input type="hidden" name="vehicleId" value="<%= vehicle.getId() %>" />
                        <button type="submit" class="vehicle-delete">Delete</button>
                      </form>
                    </div>
                  <% } %>
                <% } %>
            </div>

            <%-- Centered Add Vehicle Form --%>
            <div class="vehicle-form-container">
                <h4 style="margin-bottom: 1rem; color: var(--sjsu-gold);">Add a New Vehicle</h4>
                <form method="post" action="<%= cp %>/dashboard/driver" class="dashboard-form-grid">
                  <input type="hidden" name="action" value="addVehicle" />
                  <input type="text" name="make"  class="login-input" placeholder="Vehicle make (e.g. Toyota)" required />
                  <input type="text" name="model" class="login-input" placeholder="Vehicle model (e.g. Camry)" required />
                  <input type="text" name="color" class="login-input" placeholder="Vehicle color"              required />
                  <input type="text" name="plate" class="login-input" placeholder="License plate"              required />
                  <input type="number" name="totalSeats" class="login-input" placeholder="Total seats" min="1" required />
                  <button class="login-submit dashboard-add-vehicle" type="submit" style="width: 100%; margin-top: 1rem;">Add Vehicle</button>
                </form>
            </div>

          </section>
        <% } %>

      </div>
    </div>
  </div>
</body>
</html>