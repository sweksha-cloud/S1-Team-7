package servlet;

import model.User;
import store.AppStore;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/delete-account")
public class DeleteAccountServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession(true).getAttribute("currentUser");
        if (user != null) {
            AppStore.deleteUser(user.getEmail());
        }
        req.getSession(true).invalidate();
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}
