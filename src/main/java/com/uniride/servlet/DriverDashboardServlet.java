package com.uniride.servlet;

import com.uniride.model.User;
import com.uniride.store.AppStore;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/dashboard/driver")
public class DriverDashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
