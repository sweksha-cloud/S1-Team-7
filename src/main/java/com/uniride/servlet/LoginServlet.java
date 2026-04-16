package com.uniride.servlet;

import com.uniride.model.User;
import com.uniride.store.AppStore;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = safe(req.getParameter("email")).toLowerCase();
        String password = safe(req.getParameter("password"));

        req.setAttribute("email", email);

        if (email.isBlank()) {
            req.setAttribute("errorEmail", "Email is required.");
        }

        if (password.isBlank()) {
            req.setAttribute("errorPassword", "Password is required.");
        }

        if (req.getAttribute("errorEmail") != null || req.getAttribute("errorPassword") != null) {
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        User user = AppStore.authenticate(email, password);
        if (user == null) {
            req.setAttribute("errorPassword", "Incorrect email or password.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        req.getSession(true).setAttribute("currentUser", user);
        if (user.hasRole("driver")) {
            resp.sendRedirect(req.getContextPath() + "/dashboard/driver");
        } else {
            resp.sendRedirect(req.getContextPath() + "/dashboard/passenger");
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
