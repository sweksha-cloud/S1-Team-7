<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.User" %>
<%
String cp = request.getContextPath();
User currentUser = (User) session.getAttribute("currentUser");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book a Ride | UniRide</title>
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
                <a href="<%= cp %>/dashboard/passenger" class="nav-btn-secondary">Back to Dashboard</a>
            </div>
        </nav>

        <div class="dashboard-main">
            <div class="dashboard-main-inner">
                <section class="dashboard-section">
                    <div class="settings-quick-card" style="max-width: 600px; margin: 0 auto;">
                        <div class="dashboard-section-heading">
                            <h3>Find and Book a Ride</h3>
                            <p>Enter your trip details and we will save the booking request with the matching ride information.</p>
                        </div>

                        <form method="post" action="<%= cp %>/dashboard/passenger" class="settings-form">
                            <input type="hidden" name="action" value="processCreateBooking">

                            <div class="form-group">
                                <label><strong>Pickup Location (Origin)</strong></label>
                                <input type="text" name="origin" class="login-input" placeholder="e.g. King Library" required>
                            </div>

                            <div class="form-group">
                                <label><strong>Drop-off Location (Destination)</strong></label>
                                <input type="text" name="destination" class="login-input" placeholder="e.g. San Jose Diridon" required>
                            </div>

                            <div class="form-group">
                                <label><strong>Departure Date & Time</strong></label>
                                <input type="datetime-local" name="departureDate" class="login-input" required>
                            </div>

                            <div class="form-group">
                                <label><strong>Seats Needed</strong></label>
                                <input type="number" name="seatsLeft" class="login-input" min="1" max="10" placeholder="Number of seats" required>
                            </div>

                            <hr style="border-top: 1px solid var(--border-color); margin: 20px 0;">

                            <button type="submit" class="action-create" style="width: 100%; text-align: center; display: block;">
                                Book Ride
                            </button>
                        </form>
                    </div>
                </section>
            </div>
        </div>
    </div>
</body>
</html>