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
 */
@WebServlet("/dashboard/passenger")
public class PassengerDashboard extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Require an active session before showing dashboard content.
        User user = (User) req.getSession(true).getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/passenger-dashboard.jsp").forward(req, resp);
    }
}
