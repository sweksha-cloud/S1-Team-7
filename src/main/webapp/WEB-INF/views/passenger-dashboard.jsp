<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.User" %>
<%
String cp = request.getContextPath();
User currentUser = (User) session.getAttribute("currentUser");
boolean canSwapDashboard = currentUser != null && currentUser.hasRole("driver") && currentUser.hasRole("passenger");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Passenger Dashboard | UniRide</title>
  <link rel="stylesheet" href="<%= cp %>/assets/css/common.css?v=20260427" />
  <link rel="stylesheet" href="<%= cp %>/assets/css/home.css?v=20260427" />
  <link rel="stylesheet" href="<%= cp %>/assets/css/login.css?v=20260427" />
  <link rel="stylesheet" href="<%= cp %>/assets/css/dashboard.css?v=20260427" />
</head>
<body>
  <div class="login-page dashboard-page">
    <nav class="navbar">
      <h1 class="logo"><a href="<%= cp %>/home">Uni<span class="highlight">Ride</span></a></h1>
      <div class="nav-links dashboard-nav-links">
        <span class="dashboard-welcome">Welcome <%= currentUser != null ? currentUser.getFirstName() : "Passenger" %></span>
        <a href="<%= cp %>/settings" class="nav-btn-primary dashboard-settings">Settings</a>
        <% if (canSwapDashboard) { %>
          <a href="<%= cp %>/dashboard/driver" class="nav-btn-secondary">Switch to Driver</a>
        <% } %>
        <form method="post" action="<%= cp %>/logout" class="dashboard-inline-form">
          <button type="submit" class="nav-btn-secondary dashboard-signout">Sign Out</button>
        </form>
      </div>
    </nav>

    <div class="dashboard-main">
      <div class="dashboard-main-inner">

        <section class="dashboard-hero dashboard-section">
          <div class="dashboard-hero-copy">
            <span class="campus-tag">Passenger Hub</span>
            <h2>Find the right ride and keep your trips organized.</h2>
            <p>
              Search for rides near campus, review your bookings, and manage your passenger account from one place.
            </p>
          </div>

          <div class="dashboard-actions">
            <button class="login-submit action-find" type="button">Find a Ride</button>
            <button class="login-submit action-bookings" type="button">My Bookings</button>
          </div>
        </section>

        <section class="dashboard-section dashboard-passenger-section">
          <div class="dashboard-section-heading">
            <h3>Available Rides</h3>
            <p>Search results will appear here once rides are available for your route.</p>
          </div>

          <input type="text" placeholder="Search by destination (e.g. SJSU North Garage)" class="dashboard-search" />

          <div class="dashboard-empty">
            <p>No active rides found for your search.</p>
          </div>

        </section>
      </div>
    </div>
  </div>
</body>
</html>
