package com.uniride.servlet;

import com.uniride.model.User;
import com.uniride.store.AppStore;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
