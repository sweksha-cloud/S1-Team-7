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
 * Driver dashboard for viewing and managing the current user's vehicles.
 */
@WebServlet("/dashboard/driver")
public class DriverDashboard extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Guard driver routes behind an authenticated session.
        User user = (User) req.getSession(true).getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setAttribute("vehicles", AppStore.getVehiclesForOwner(user.getEmail()));
        req.getRequestDispatcher("/WEB-INF/views/driver-dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession(true).getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Single endpoint handles multiple button actions from the dashboard form.
        String action = req.getParameter("action");
        if ("addVehicle".equals(action)) {
            String make = safe(req.getParameter("make"));
            String color = safe(req.getParameter("color"));
            String plate = safe(req.getParameter("plate"));
            if (!make.isBlank() && !color.isBlank() && !plate.isBlank()) {
                AppStore.addVehicle(user.getEmail(), make, color, plate);
            }
        }

        if ("deleteVehicle".equals(action)) {
            String vehicleId = safe(req.getParameter("vehicleId"));
            if (!vehicleId.isBlank()) {
                AppStore.deleteVehicle(user.getEmail(), vehicleId);
            }
        }

        resp.sendRedirect(req.getContextPath() + "/dashboard/driver");
    }

    /**
     * Null-safe trim helper for request parameters.
     */
    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
