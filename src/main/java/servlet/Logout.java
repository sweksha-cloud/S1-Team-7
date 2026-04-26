package servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Ends the current session and returns the user to the public home page.
 */
@WebServlet("/logout")
public class Logout extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Invalidate to clear all session-scoped state in one operation.
        req.getSession(true).invalidate();
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}
