<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.uniride.model.User" %>
<%@ page import="com.uniride.model.Vehicle" %>
<%
String cp = request.getContextPath();
User currentUser = (User) session.getAttribute("currentUser");
List<Vehicle> vehicles = (List<Vehicle>) request.getAttribute("vehicles");
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
</head>
<body>
  <div class="login-page" style="min-height: 100vh; display: flex; flex-direction: column;">
    <nav class="navbar">
      <h1 class="logo"><a href="<%= cp %>/home" style="color: inherit; text-decoration: none;">UniRide</a></h1>
      <div class="nav-links" style="gap: 12px;">
        <span style="color: #9aa4b2; font-size: 0.9rem;">Welcome <%= currentUser != null ? currentUser.getFirstName() : "Driver" %></span>
        <form method="post" action="<%= cp %>/logout" style="margin: 0;">
          <button type="submit" class="login-submit" style="margin: 0; padding: 10px 14px;">Sign Out</button>
        </form>
      </div>
    </nav>

    <div style="flex: 1; display: flex; justify-content: center; align-items: flex-start; padding-top: 50px;">
      <div style="display: flex; flex-direction: column; align-items: center; width: 100%; padding: 20px; box-sizing: border-box;">

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px; width: 100%; max-width: 500px; margin-bottom: 30px;">
          <button class="login-submit" style="background-color: #2ecc71; margin: 0;" type="button">Create a Ride</button>
          <button class="login-submit" style="background-color: #3498db; margin: 0;" type="button">Passenger Requests</button>
          <button class="login-submit" style="background-color: #9b59b6; margin: 0;" type="button">My Earnings</button>
          <button class="login-submit" style="background-color: #95a5a6; margin: 0;" type="button">Settings</button>
        </div>

        <div class="login-shell" style="width: 100%; max-width: 500px; text-align: left; margin: 0 auto;">
          <h3 style="text-align: center; margin-bottom: 20px;">My Registered Vehicles</h3>

          <% if (vehicles.isEmpty()) { %>
            <p style="text-align: center; color: #666;">No vehicles added yet.</p>
          <% } else { %>
            <% for (Vehicle vehicle : vehicles) { %>
              <div style="display: flex; justify-content: space-between; padding: 15px 0; border-bottom: 1px solid #eee; align-items: center;">
                <div>
                  <strong><%= vehicle.getColor() %> <%= vehicle.getMake() %></strong><br />
                  <small style="color: #888;">Plate: <%= vehicle.getPlate() %></small>
                </div>
                <form method="post" action="<%= cp %>/dashboard/driver" style="margin: 0;">
                  <input type="hidden" name="action" value="deleteVehicle" />
                  <input type="hidden" name="vehicleId" value="<%= vehicle.getId() %>" />
                  <button type="submit" style="background: none; border: none; color: #e74c3c; cursor: pointer;">Delete</button>
                </form>
              </div>
            <% } %>
          <% } %>

          <form method="post" action="<%= cp %>/dashboard/driver" style="margin-top: 20px; display: grid; gap: 10px;">
            <input type="hidden" name="action" value="addVehicle" />
            <input type="text" name="make" placeholder="Vehicle make (e.g. Toyota)" required />
            <input type="text" name="color" placeholder="Vehicle color" required />
            <input type="text" name="plate" placeholder="License plate" required />
            <button class="login-submit" style="margin: 0; background: #34495e; width: 100%;" type="submit">Add New Vehicle</button>
          </form>

          <div style="margin-top: 40px; border-top: 1px solid #eee; padding-top: 20px; text-align: center;">
            <form method="post" action="<%= cp %>/delete-account" onsubmit="return confirm('Are you sure you want to delete your account?');">
              <button
                type="submit"
                style="background-color: transparent; color: #e74c3c; border: 1px solid #e74c3c; padding: 8px 15px; border-radius: 5px; cursor: pointer; font-size: 0.9rem; font-weight: 600;"
              >
                Delete Account
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
