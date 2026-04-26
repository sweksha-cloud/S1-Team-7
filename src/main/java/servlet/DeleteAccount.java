package servlet;

import model.User;
import store.AppStore;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Deletes the logged-in account and clears the active session.
 */
@WebServlet("/delete-account")
public class DeleteAccount extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession(true).getAttribute("currentUser");
        if (user != null) {
            AppStore.deleteUser(user.getEmail());
        }
        // Always invalidate, even if no user was found, to enforce sign-out.
        req.getSession(true).invalidate();
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}
