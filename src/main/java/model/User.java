package model;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable account model shared across the authentication, dashboard, and
 * settings flows.
 *
 * The object captures the account state that downstream views need without
 * exposing mutators, which keeps the session copy predictable after login.
 */
public class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String sjsuId;
    private final String gender;
    private final String password;
    private final Set<String> roles;
    private final Instant createdAt;

    /**
     * Builds an immutable user snapshot for the signed-in account.
     *
     * @param firstName account first name used in greetings and profile display
     * @param lastName account last name used in profile display
     * @param email unique login identifier
     * @param sjsuId university identifier stored for registration checks
     * @param gender registration field preserved for the profile record
     * @param password stored credential value used by the app's authentication flow
     * @param roles registration roles granted to the account
     */
    public User(
        String firstName,
        String lastName,
        String email,
        String sjsuId,
        String gender,
        String password,
        Set<String> roles
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.sjsuId = sjsuId;
        this.gender = gender;
        this.password = password;
        this.roles = Collections.unmodifiableSet(new HashSet<>(roles));
        this.createdAt = Instant.now();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getSjsuId() {
        return sjsuId;
    }

    public String getGender() {
        return gender;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Checks whether the account was created with a given registration role.
     *
     * @param role role name to check, such as driver or passenger
     * @return true when the role is present in the immutable role set
     */
    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
