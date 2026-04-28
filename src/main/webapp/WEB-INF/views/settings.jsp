<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.User" %>
<%
// Pull the context path once so every server-generated URL stays portable across deployments.
String cp = request.getContextPath();
// The authenticated user is stored in the session by the login flow and reused here for display only.
User currentUser = (User) session.getAttribute("currentUser");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Settings | UniRide</title>
  <link rel="stylesheet" href="<%= cp %>/assets/css/common.css?v=20260427" />
  <link rel="stylesheet" href="<%= cp %>/assets/css/home.css?v=20260427" />
  <link rel="stylesheet" href="<%= cp %>/assets/css/login.css?v=20260427" />
  <link rel="stylesheet" href="<%= cp %>/assets/css/dashboard.css?v=20260427" />
</head>
<body>
  <div class="login-page dashboard-page">
    <nav class="navbar">
      <!-- The header reuses dashboard navigation styles so settings feels part of the same logged-in shell. -->
      <h1 class="logo"><a href="<%= cp %>/home">Uni<span class="highlight">Ride</span></a></h1>
      <div class="nav-links dashboard-nav-links">
        <span class="dashboard-welcome">Welcome <%= currentUser != null ? currentUser.getFirstName() : "User" %></span>
        <a href="<%= cp %>/home" class="nav-btn-secondary">Back to Home</a>
        <form method="post" action="<%= cp %>/logout" class="dashboard-inline-form">
          <button type="submit" class="nav-btn-secondary dashboard-signout">Sign Out</button>
        </form>
      </div>
    </nav>

    <div class="dashboard-main">
      <div class="dashboard-main-inner">
          <div class="settings-full">
            <!-- The account panel uses the dashboard wrapper but stays full-width to avoid a cramped settings form. -->
            <h2 style="margin-top: 0; margin-bottom: 1.5rem;">Account Settings</h2>

          <!-- Request-scoped messages are rendered here so the POST handler can redirect users back with feedback. -->
          <% if (request.getAttribute("successMessage") != null) { %>
            <div class="form-success" style="margin-bottom: 1rem; padding: 0.75rem; background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; color: #155724;">
              <%= request.getAttribute("successMessage") %>
            </div>
          <% } %>

          <!-- The profile fields are intentionally disabled because this page only supports password changes. -->
          <form id="passwordForm" method="post" action="<%= cp %>/settings" class="settings-form">
            <div class="form-group">
              <label for="email">Email Address</label>
              <input type="email" id="email" name="email" value="<%= currentUser != null ? currentUser.getEmail() : "" %>" disabled class="login-input" />
              <small style="color: #666; margin-top: 0.25rem; display: block;">Your email address cannot be changed.</small>
            </div>

            <div class="form-group">
              <label for="firstName">First Name</label>
              <input type="text" id="firstName" name="firstName" value="<%= currentUser != null ? currentUser.getFirstName() : "" %>" disabled class="login-input" />
            </div>

            <div class="form-group">
              <label for="lastName">Last Name</label>
              <input type="text" id="lastName" name="lastName" value="<%= currentUser != null ? currentUser.getLastName() : "" %>" disabled class="login-input" />
            </div>

            <hr style="margin: 2rem 0; border: none; border-top: 1px solid #ddd;" />

            <h3 style="margin-top: 0; margin-bottom: 1rem;">Change Password</h3>

            <div class="form-group">
              <label for="currentPassword">Current Password</label>
              <input type="password" id="currentPassword" name="currentPassword" class="login-input" required />
              <!-- Server-side validation errors are surfaced inline so the user can correct the exact field. -->
              <% if (request.getAttribute("errorCurrentPassword") != null) { %>
                <span class="form-error"><%= request.getAttribute("errorCurrentPassword") %></span>
              <% } %>
            </div>

            <div class="form-group">
              <label for="newPassword">New Password</label>
              <input type="password" id="newPassword" name="newPassword" class="login-input" required />
              <small style="color: #666; margin-top: 0.25rem; display: block;">Must be at least 8 characters long.</small>
              <% if (request.getAttribute("errorNewPassword") != null) { %>
                <span class="form-error"><%= request.getAttribute("errorNewPassword") %></span>
              <% } %>
            </div>

            <div class="form-group">
              <label for="confirmPassword">Confirm New Password</label>
              <input type="password" id="confirmPassword" name="confirmPassword" class="login-input" required />
              <% if (request.getAttribute("errorConfirmPassword") != null) { %>
                <span class="form-error"><%= request.getAttribute("errorConfirmPassword") %></span>
              <% } %>
            </div>

            <button type="submit" class="login-submit" style="margin-top: 1rem;">Update Password</button>
          </form>

          <% // When the form returns with feedback, bring the form back into view so the message is visible immediately. %>
          <% boolean hasMsgs = request.getAttribute("successMessage") != null ||
                            request.getAttribute("errorCurrentPassword") != null ||
                            request.getAttribute("errorNewPassword") != null ||
                            request.getAttribute("errorConfirmPassword") != null; %>

          <% if (hasMsgs) { %>
            <script>
              // The scroll is purely presentational; it helps users see validation feedback after a POST/forward.
              document.addEventListener('DOMContentLoaded', function() {
                var el = document.getElementById('passwordForm');
                if (el) {
                  el.scrollIntoView({ behavior: 'auto', block: 'center' });
                }
              });
            </script>
          <% } %>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
