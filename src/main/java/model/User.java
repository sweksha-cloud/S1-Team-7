package model;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String sjsuId;
    private final String gender;
    private final String password;
    private final Set<String> roles;
    private final Instant createdAt;

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

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
