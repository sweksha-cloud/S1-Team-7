<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.uniride.model.User" %>
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
</head>
<body>
  <div class="login-page" style="min-height: 100vh; display: flex; flex-direction: column;">
    <nav class="navbar">
      <h1 class="logo"><a href="<%= cp %>/home" style="color: inherit; text-decoration: none;">UniRide</a></h1>
      <div class="nav-links" style="gap: 12px;">
        <span style="color: #9aa4b2; font-size: 0.9rem;">Welcome <%= currentUser != null ? currentUser.getFirstName() : "Passenger" %></span>
        <form method="post" action="<%= cp %>/logout" style="margin: 0;">
          <button type="submit" class="login-submit" style="margin: 0; padding: 10px 14px;">Sign Out</button>
        </form>
      </div>
    </nav>

    <div style="flex: 1; display: flex; justify-content: center; align-items: flex-start; padding-top: 50px;">
      <div style="display: flex; flex-direction: column; align-items: center; width: 100%; padding: 20px; box-sizing: border-box;">

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px; width: 100%; max-width: 500px; margin-bottom: 30px;">
          <button class="login-submit" style="background-color: #3498db; margin: 0;" type="button">Find a Ride</button>
          <button class="login-submit" style="background-color: #9b59b6; margin: 0;" type="button">My Bookings</button>
        </div>

        <div class="login-shell" style="width: 100%; max-width: 500px; text-align: left;">
          <h3 style="text-align: center; margin-bottom: 20px;">Available Rides</h3>

          <input
            type="text"
            placeholder="Search by destination (e.g. SJSU North Garage)"
            style="width: 100%; padding: 10px; margin-bottom: 20px; border-radius: 4px; border: 1px solid #ccc;"
          />

          <div style="text-align: center; color: #666; padding: 20px;">
            <p>No active rides found for your search.</p>
          </div>

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
