package servlet;

import model.User;
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
        req.getRequestDispatcher("/WEB-INF/views/passenger-dashboard.jsp").forward(req, resp);
    }
}
