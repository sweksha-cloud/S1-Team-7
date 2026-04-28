<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.User" %>
<%@ page import="model.Vehicle" %>
<%
String cp = request.getContextPath();
User currentUser = (User) session.getAttribute("currentUser");
List<Vehicle> vehicles = (List<Vehicle>) request.getAttribute("vehicles");
boolean pending = request.getAttribute("pendingVerification") != null;
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
  <link rel="stylesheet" href="<%= cp %>/assets/css/common.css" />
  <link rel="stylesheet" href="<%= cp %>/assets/css/home.css" />
  <link rel="stylesheet" href="<%= cp %>/assets/css/login.css" />
  <link rel="stylesheet" href="<%= cp %>/assets/css/dashboard.css" />
</head>
<body>
  <div class="login-page dashboard-page">
    <nav class="navbar">
      <h1 class="logo"><a href="<%= cp %>/home">UniRide</a></h1>
      <div class="nav-links dashboard-nav-links">
        <span class="dashboard-welcome">Welcome <%= currentUser != null ? currentUser.getFirstName() : "Driver" %></span>
        <form method="post" action="<%= cp %>/logout" class="dashboard-inline-form">
          <button type="submit" class="login-submit dashboard-signout">Sign Out</button>
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
            <div class="dashboard-danger-wrap" style="margin-top: 2rem;">
              <form method="post" action="<%= cp %>/delete-account"
                    onsubmit="return confirm('Are you sure you want to delete your account?');">
                <button type="submit" class="dashboard-danger-btn">Delete Account</button>
              </form>
            </div>
          </div>

        <% } else { %>
          <%-- Full driver dashboard --%>
          <div class="dashboard-actions">
            <button class="login-submit action-create"   type="button">Create a Ride</button>
            <button class="login-submit action-requests" type="button">Passenger Requests</button>
            <button class="login-submit action-earnings" type="button">My Earnings</button>
            <button class="login-submit action-settings" type="button">Settings</button>
          </div>

          <div class="login-shell dashboard-card">
            <h3>My Registered Vehicles</h3>

            <% if (vehicles.isEmpty()) { %>
              <p class="dashboard-empty">No vehicles added yet.</p>
            <% } else { %>
              <% for (Vehicle vehicle : vehicles) { %>
                <div class="vehicle-row">
                  <div>
                    <strong><%= vehicle.getColor() %> <%= vehicle.getMake() %></strong><br />
                    <small class="vehicle-plate">Plate: <%= vehicle.getPlate() %></small>
                  </div>
                  <form method="post" action="<%= cp %>/dashboard/driver" class="dashboard-inline-form">
                    <input type="hidden" name="action"    value="deleteVehicle" />
                    <input type="hidden" name="vehicleId" value="<%= vehicle.getId() %>" />
                    <button type="submit" class="vehicle-delete">Delete</button>
                  </form>
                </div>
              <% } %>
            <% } %>

            <form method="post" action="<%= cp %>/dashboard/driver" class="dashboard-form-grid">
              <input type="hidden" name="action" value="addVehicle" />
              <input type="text" name="make"  placeholder="Vehicle make (e.g. Toyota)" required />
              <input type="text" name="color" placeholder="Vehicle color"              required />
              <input type="text" name="plate" placeholder="License plate"              required />
              <button class="login-submit dashboard-add-vehicle" type="submit">Add New Vehicle</button>
            </form>

            <div class="dashboard-danger-wrap">
              <form method="post" action="<%= cp %>/delete-account"
                    onsubmit="return confirm('Are you sure you want to delete your account?');">
                <button type="submit" class="dashboard-danger-btn">Delete Account</button>
              </form>
            </div>
          </div>
        <% } %>

      </div>
    </div>
  </div>
</body>
</html>
