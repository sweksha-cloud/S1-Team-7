package servlet;

import model.User;
import store.AppStore;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@WebServlet("/signup")
public class Signup extends HttpServlet {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern SJSU_ID_PATTERN = Pattern.compile("^\\d{9}$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName     = safe(req.getParameter("firstName"));
        String lastName      = safe(req.getParameter("lastName"));
        String email         = safe(req.getParameter("email")).toLowerCase();
        String sjsuId        = safe(req.getParameter("sjsuId"));
        String gender        = safe(req.getParameter("gender"));
        String password      = safe(req.getParameter("password"));
        String licenseNumber = safe(req.getParameter("licenseNumber"));
        String[] rolesArray  = req.getParameterValues("roles");

        req.setAttribute("firstName",     firstName);
        req.setAttribute("lastName",      lastName);
        req.setAttribute("email",         email);
        req.setAttribute("sjsuId",        sjsuId);
        req.setAttribute("gender",        gender);
        req.setAttribute("licenseNumber", licenseNumber);

        if (firstName.isBlank())  req.setAttribute("errorFirstName", "First name is required.");
        if (lastName.isBlank())   req.setAttribute("errorLastName",  "Last name is required.");

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
        if (gender.isBlank()) req.setAttribute("errorGender", "Please select a gender.");

        if (password.isBlank()) {
            req.setAttribute("errorPassword", "Password is required.");
        } else if (password.length() < 8) {
            req.setAttribute("errorPassword", "Password must be at least 8 characters.");
        }

        Set<String> roles = new HashSet<>();
        if (rolesArray != null) {
            for (String role : rolesArray) {
                if ("driver".equals(role) || "passenger".equals(role)) roles.add(role);
            }
        }
        if (roles.isEmpty()) {
            req.setAttribute("errorRoles", "Please choose at least one registration type.");
        }

        // Require license number if registering as a driver.
        if (roles.contains("driver") && licenseNumber.isBlank()) {
            req.setAttribute("errorLicense", "License number is required for drivers.");
        }

        if (hasErrors(req)) {
            req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
            return;
        }

        User user = AppStore.createUser(firstName, lastName, email, sjsuId, gender, password, roles, licenseNumber);
        if (user == null) {
            req.setAttribute("errorEmail", "This email is already registered.");
            req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
            return;
        }

        // Tell the user their account is pending if they signed up as a driver.
        if (roles.contains("driver")) {
            req.setAttribute("successMessage",
                "Registration complete! Your driver account is pending verification. " +
                "You can log in as a passenger in the meantime.");
        } else {
            req.setAttribute("successMessage", "Registration complete. Your account has been created.");
        }
        req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
    }

    private boolean hasErrors(HttpServletRequest req) {
        return req.getAttribute("errorFirstName") != null
            || req.getAttribute("errorLastName")  != null
            || req.getAttribute("errorEmail")     != null
            || req.getAttribute("errorSjsuId")    != null
            || req.getAttribute("errorGender")    != null
            || req.getAttribute("errorPassword")  != null
            || req.getAttribute("errorRoles")     != null
            || req.getAttribute("errorLicense")   != null;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
