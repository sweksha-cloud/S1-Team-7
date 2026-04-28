package servlet;

import model.User;
import store.AppStore;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Settings page for authenticated users to change their password.
 * Accessible to both drivers and passengers.
 */
@WebServlet("/settings")
public class Settings extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Require an active session before showing settings.
        User user = (User) req.getSession(true).getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/settings.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession(true).getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String currentPassword = safe(req.getParameter("currentPassword"));
        String newPassword     = safe(req.getParameter("newPassword"));
        String confirmPassword = safe(req.getParameter("confirmPassword"));

        // Validate current password
        if (!currentPassword.equals(user.getPassword())) {
            req.setAttribute("errorCurrentPassword", "Current password is incorrect.");
        }

        // Validate new password
        if (newPassword.isBlank()) {
            req.setAttribute("errorNewPassword", "New password is required.");
        } else if (newPassword.length() < 8) {
            req.setAttribute("errorNewPassword", "Password must be at least 8 characters.");
        }

        // Validate confirmation
        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("errorConfirmPassword", "Passwords do not match.");
        }

        // Check for errors
        if (hasErrors(req)) {
            req.getRequestDispatcher("/WEB-INF/views/settings.jsp").forward(req, resp);
            return;
        }

        // Update password in database
        if (AppStore.updatePassword(user.getEmail(), newPassword)) {
            req.setAttribute("successMessage", "Password changed successfully.");
            req.getRequestDispatcher("/WEB-INF/views/settings.jsp").forward(req, resp);
        } else {
            req.setAttribute("errorNewPassword", "Failed to update password.");
            req.getRequestDispatcher("/WEB-INF/views/settings.jsp").forward(req, resp);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean hasErrors(HttpServletRequest req) {
        return req.getAttribute("errorCurrentPassword") != null ||
               req.getAttribute("errorNewPassword") != null ||
               req.getAttribute("errorConfirmPassword") != null;
    }
}
