<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.User" %>
<%
String cp = request.getContextPath();
User currentUser = (User) session.getAttribute("currentUser");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Passenger Dashboard | UniRide</title>
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
        <span class="dashboard-welcome">Welcome <%= currentUser != null ? currentUser.getFirstName() : "Passenger" %></span>
        <form method="post" action="<%= cp %>/logout" class="dashboard-inline-form">
          <button type="submit" class="login-submit dashboard-signout">Sign Out</button>
        </form>
      </div>
    </nav>

    <div class="dashboard-main">
      <div class="dashboard-main-inner">

        <div class="dashboard-actions">
          <button class="login-submit action-find" type="button">Find a Ride</button>
          <button class="login-submit action-bookings" type="button">My Bookings</button>
        </div>

        <div class="login-shell dashboard-card">
          <h3>Available Rides</h3>

          <input type="text" placeholder="Search by destination (e.g. SJSU North Garage)" class="dashboard-search" />

          <div class="dashboard-empty">
            <p>No active rides found for your search.</p>
          </div>

          <div class="dashboard-danger-wrap">
            <form method="post" action="<%= cp %>/delete-account" onsubmit="return confirm('Are you sure you want to delete your account?');">
              <button type="submit" class="dashboard-danger-btn">
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
