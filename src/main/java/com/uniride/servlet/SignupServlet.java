package com.uniride.servlet;

import com.uniride.model.User;
import com.uniride.store.AppStore;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern SJSU_ID_PATTERN = Pattern.compile("^\\d{9}$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = safe(req.getParameter("firstName"));
        String lastName = safe(req.getParameter("lastName"));
        String email = safe(req.getParameter("email")).toLowerCase();
        String sjsuId = safe(req.getParameter("sjsuId"));
        String gender = safe(req.getParameter("gender"));
        String password = safe(req.getParameter("password"));
        String[] rolesArray = req.getParameterValues("roles");

        req.setAttribute("firstName", firstName);
        req.setAttribute("lastName", lastName);
        req.setAttribute("email", email);
        req.setAttribute("sjsuId", sjsuId);
        req.setAttribute("gender", gender);

        if (firstName.isBlank()) {
            req.setAttribute("errorFirstName", "First name is required.");
        }
        if (lastName.isBlank()) {
            req.setAttribute("errorLastName", "Last name is required.");
        }
        if (email.isBlank()) {
            req.setAttribute("errorEmail", "Email is required.");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            req.setAttribute("errorEmail", "Enter a valid email address.");
        } else if (AppStore.hasUser(email)) {
            req.setAttribute("errorEmail", "This email is already registered.");
        }
        if (!SJSU_ID_PATTERN.matcher(sjsuId).matches()) {
            req.setAttribute("errorSjsuId", "SJSU ID must be 9 digits.");
        }
        if (gender.isBlank()) {
            req.setAttribute("errorGender", "Please select a gender.");
        }
        if (password.isBlank()) {
            req.setAttribute("errorPassword", "Password is required.");
        } else if (password.length() < 8) {
            req.setAttribute("errorPassword", "Password must be at least 8 characters.");
        }

        Set<String> roles = new HashSet<>();
        if (rolesArray != null) {
            for (String role : rolesArray) {
                if ("driver".equals(role) || "passenger".equals(role)) {
                    roles.add(role);
                }
            }
        }
        if (roles.isEmpty()) {
            req.setAttribute("errorRoles", "Please choose at least one registration type.");
        }

        if (hasErrors(req)) {
            req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
            return;
        }

        User user = AppStore.createUser(firstName, lastName, email, sjsuId, gender, password, roles);
        if (user == null) {
            req.setAttribute("errorEmail", "This email is already registered.");
            req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("successMessage", "Registration complete. Your account has been created.");
        req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
    }

    private boolean hasErrors(HttpServletRequest req) {
        return req.getAttribute("errorFirstName") != null
            || req.getAttribute("errorLastName") != null
            || req.getAttribute("errorEmail") != null
            || req.getAttribute("errorSjsuId") != null
            || req.getAttribute("errorGender") != null
            || req.getAttribute("errorPassword") != null
            || req.getAttribute("errorRoles") != null;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
