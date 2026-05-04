<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.User, java.time.LocalDateTime, java.time.format.DateTimeFormatter" %>
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
            <a class="login-submit action-find" href="<%= cp %>/dashboard/passenger?action=showCreateBookingForm" style="text-decoration: none; display: inline-flex; align-items: center; justify-content: center;">Find a Ride</a>
            <button class="login-submit action-bookings" type="button">My Bookings</button>
          </div>
        </section>

        <section class="dashboard-section dashboard-passenger-section">
          <div class="dashboard-section-heading">
            <h3>Available Rides</h3>
            <p>Search results will appear here once rides are available for your route.</p>
          </div>

          <div class="dashboard-search-filters" style="display:flex; gap: 0.75rem; flex-wrap: wrap;">
            <input id="rides-filter-text" type="text" placeholder="Filter by origin, destination, driver, or vehicle" class="dashboard-search" style="flex: 1 1 280px;" />
            <input id="rides-filter-date" type="date" class="dashboard-search" style="flex: 0 1 220px;" />
          </div>

          <div id="rides-list" class="ride-list">
            <%
              java.util.List<model.Ride> availableRides = (java.util.List<model.Ride>) request.getAttribute("availableRides");
              if (availableRides == null) {
                availableRides = java.util.Collections.emptyList();
              }
            %>

            <% if (availableRides.isEmpty()) { %>
              <div id="no-rides" class="dashboard-empty">
                <p>No active rides found for your search.</p>
              </div>
            <% } else { %>
              <div id="no-rides" class="dashboard-empty" style="display:none;">
                <p>No active rides found for your search.</p>
              </div>
              <% for (model.Ride ride : availableRides) { %>
                <div class="ride-item" data-origin="<%= ride.getOrigin().toLowerCase() %>" data-destination="<%= ride.getDestination().toLowerCase() %>" data-driver="<%= (ride.getDriverName() == null ? "" : ride.getDriverName()).toLowerCase() %>" data-vehicle="<%= (ride.getVehicleInfo() == null ? "" : ride.getVehicleInfo()).toLowerCase() %>" data-departure="<%= ride.getDepartureDate().toLowerCase() %>">
                  <div class="ride-row">
                    <div class="ride-info">
                      <strong><%= ride.getOrigin() %> → <%= ride.getDestination() %></strong><br />
                      <%
                        String rawDeparture = ride.getDepartureDate();
                        String formattedDeparture = rawDeparture;
                        String formattedTime = "";
                        try {
                          DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                          DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                          DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
                          LocalDateTime dt = LocalDateTime.parse(rawDeparture, parser);
                          formattedDeparture = dt.format(dateFormatter);
                          formattedTime = dt.format(timeFormatter);
                        } catch (Exception ignored) {
                        }
                      %>
                      <small class="ride-meta">Driver: <%= ride.getDriverName() != null ? ride.getDriverName() : "Not assigned" %></small>
                      <small class="ride-meta">Vehicle: <%= ride.getVehicleInfo() != null ? ride.getVehicleInfo() : "Not assigned" %></small>
                      <small class="ride-meta">Departure: <%= formattedDeparture %></small>
                      <% if (!formattedTime.isBlank()) { %>
                        <small class="ride-meta">Time: <%= formattedTime %></small>
                      <% } %>
                      <small class="ride-meta">Seats left: <%= ride.getSeatsLeft() %></small>
                    </div>
                    <div class="ride-actions">
                      <form method="post" action="<%= cp %>/dashboard/passenger" class="dashboard-inline-form">
                        <input type="hidden" name="action" value="processRideRequest" />
                        <input type="hidden" name="rideId" value="<%= ride.getId() %>" />
                        <button type="submit" class="login-submit">Request</button>
                      </form>
                    </div>
                  </div>
                </div>
              <% } %>
            <% } %>
          </div>

          <script>
            (function() {
              const textInput = document.getElementById('rides-filter-text');
              const dateInput = document.getElementById('rides-filter-date');
              const list = document.getElementById('rides-list');
              const noRides = document.getElementById('no-rides');

              function filter() {
                const textQuery = textInput.value.trim().toLowerCase();
                const dateQuery = dateInput.value.trim();
                const items = list.querySelectorAll('.ride-item');
                let visible = 0;
                items.forEach(item => {
                  const origin = item.dataset.origin || '';
                  const destination = item.dataset.destination || '';
                  const driver = item.dataset.driver || '';
                  const vehicle = item.dataset.vehicle || '';
                  const departure = item.dataset.departure || '';
                  const departureDate = departure.split(' ')[0] || '';
                  const textMatches = !textQuery || origin.includes(textQuery) || destination.includes(textQuery) || driver.includes(textQuery) || vehicle.includes(textQuery);
                  const dateMatches = !dateQuery || departureDate === dateQuery;
                  if (textMatches && dateMatches) {
                    item.style.display = '';
                    visible++;
                  } else {
                    item.style.display = 'none';
                  }
                });
                noRides.style.display = visible === 0 ? '' : 'none';
              }

              textInput.addEventListener('input', filter);
              dateInput.addEventListener('input', filter);
            })();
          </script>

        </section>
      </div>
    </div>
  </div>
</body>
</html>
