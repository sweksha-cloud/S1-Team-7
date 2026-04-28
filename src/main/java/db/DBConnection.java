package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Opens a JDBC connection to the team7 MySQL database.
 *
 * Set these three environment variables before starting Tomcat:
 *   DB_URL      e.g.  jdbc:mysql://localhost:3306/team7
 *   DB_USER     e.g.  root
 *   DB_PASSWORD e.g.  yourpassword
 *
 */
public final class DBConnection {

    private static final String URL =
        System.getenv("DB_URL") != null
            ? System.getenv("DB_URL")
            : "jdbc:mysql://localhost:3306/team7";

    private static final String USER =
        System.getenv("DB_USER") != null
            ? System.getenv("DB_USER")
            : "root";

    private static final String PASSWORD =
        System.getenv("DB_PASSWORD") != null
            ? System.getenv("DB_PASSWORD")
            : "yourpassword";

    private DBConnection() {}

    public static Connection get() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
