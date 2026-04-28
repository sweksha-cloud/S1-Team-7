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
 *
 * The servlet keeps the vehicle list server-rendered so ownership checks and
 * edits are enforced on the backend before the JSP is shown.
 */
@WebServlet("/dashboard/driver")
public class DriverDashboard extends HttpServlet {
    /**
     * Loads the driver's vehicles only after confirming the session is active.
     *
     * @param req current request used to read the session and expose vehicle data
     * @param resp response used to redirect anonymous users
     * @throws ServletException if the JSP dispatch fails
     * @throws IOException if the redirect or forward cannot be written
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Guard the route before any vehicle query runs.
        User user = (User) req.getSession(true).getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setAttribute("vehicles", AppStore.getVehiclesForOwner(user.getEmail()));
        req.getRequestDispatcher("/WEB-INF/views/driver-dashboard.jsp").forward(req, resp);
    }

    /**
     * Handles the dashboard form actions that mutate vehicle data.
     *
     * The page uses one endpoint for multiple buttons, so the action parameter
     * decides which database operation to run.
     *
     * @param req current form submission containing the action and fields
     * @param resp response used to redirect anonymous users and refresh the view
     * @throws IOException if the redirect cannot be written
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession(true).getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // One endpoint keeps the form simple while still supporting multiple actions.
        String action = req.getParameter("action");
        if ("addVehicle".equals(action)) {
            String make = safe(req.getParameter("make"));
            String model = safe(req.getParameter("model"));
            String color = safe(req.getParameter("color"));
            String plate = safe(req.getParameter("plate"));
            String totalSeats = safe(req.getParameter("totalSeats"));
            if (!make.isBlank() && !model.isBlank() && !color.isBlank() && !plate.isBlank() && !totalSeats.isBlank()) {
                try {
                    int seats = Integer.parseInt(totalSeats);
                    if (seats > 0) {
                        AppStore.addVehicle(user.getEmail(), make, model, color, plate, seats);
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore invalid seat counts and fall through to the redirect.
                }
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
     * Normalizes form values before validation or persistence.
     *
     * @param value raw request parameter value
     * @return a trimmed, non-null string
     */
    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
