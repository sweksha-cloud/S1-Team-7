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
 * Passenger dashboard page for authenticated non-driver users.
 *
 * The page is still guarded by the session because passenger-specific actions
 * are only available to signed-in users.
 */
@WebServlet("/dashboard/passenger")
public class PassengerDashboard extends HttpServlet {
    /**
     * Forwards signed-in passengers to their dashboard view.
     *
     * @param req current request used to validate the session
     * @param resp response used to redirect anonymous users
     * @throws ServletException if the JSP dispatch fails
     * @throws IOException if the redirect or forward cannot be written
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Require an active session before exposing dashboard content.
        User user = (User) req.getSession(true).getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = safe(req.getParameter("action"));

        if ("showCreateBookingForm".equals(action)) {
            req.getRequestDispatcher("/WEB-INF/views/create-booking.jsp").forward(req, resp);
            return;
        }

        // Load available rides for display
        req.setAttribute("availableRides", store.AppStore.getAvailableRides());

        req.getRequestDispatcher("/WEB-INF/views/passenger-dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession(true).getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = safe(req.getParameter("action"));

        if ("processCreateBooking".equals(action)) {
            String origin = safe(req.getParameter("origin"));
            String destination = safe(req.getParameter("destination"));
            String departureDate = safe(req.getParameter("departureDate"));
            String seatsLeft = safe(req.getParameter("seatsLeft"));

            if (!origin.isBlank() && !destination.isBlank() && !departureDate.isBlank()) {
                try {
                    int seats = Integer.parseInt(seatsLeft);
                    String sqlTimestamp = departureDate.replace("T", " ") + ":00";
                    AppStore.createBooking(user.getEmail(), origin, destination, sqlTimestamp, seats);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if ("processRideRequest".equals(action)) {
            String rideId = safe(req.getParameter("rideId"));
            if (!rideId.isBlank()) {
                try {
                    AppStore.createBookingForRide(user.getEmail(), Integer.parseInt(rideId));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        resp.sendRedirect(req.getContextPath() + "/dashboard/passenger");
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
