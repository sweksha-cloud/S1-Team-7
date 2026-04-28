package servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Ends the current session and returns the user to the public home page.
 *
 * The logout action is a POST so sign-out is driven by an explicit user
 * action rather than a link click.
 */
@WebServlet("/logout")
public class Logout extends HttpServlet {
    /**
     * Invalidates the active session and redirects to the landing page.
     *
     * @param req current request carrying the session to clear
     * @param resp response used to send the redirect
     * @throws IOException if the redirect cannot be written
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Invalidate to clear all session-scoped state in one operation.
        req.getSession(true).invalidate();
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}
