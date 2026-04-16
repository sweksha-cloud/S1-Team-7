# UniRide JSP Application

This repository has been migrated from React/Vite to a JSP + Servlet web application.

## Stack

- Java 17
- Jakarta Servlet API 6
- JSP views
- Maven WAR packaging
- In-memory application store for demo users and vehicles

## Project Structure

- `src/main/java/com/uniride/model`: Domain models (`User`, `Vehicle`)
- `src/main/java/com/uniride/store`: In-memory store
- `src/main/java/com/uniride/servlet`: Route and action servlets
- `src/main/webapp/WEB-INF/views`: JSP pages
- `src/main/webapp/assets/css`: CSS files

## Routes

- `/home`: Landing page
- `/signup`: Sign-up form
- `/login`: Login form
- `/dashboard/passenger`: Passenger dashboard
- `/dashboard/driver`: Driver dashboard
- `/logout`: Session logout (POST)
- `/delete-account`: Account deletion (POST)

## Build

```bash
mvn clean package
```

WAR output:

- `target/s1-team-7-1.0.0.war`

## Run (Tomcat)

1. Build WAR with Maven.
2. Deploy `target/s1-team-7-1.0.0.war` to Tomcat.
3. Open the app at `http://localhost:8080/s1-team-7/home`.

## Notes

- This version removes React runtime/state and uses server-side rendering with JSP.
- Data persistence is currently in-memory for demo behavior parity. Restarting the app clears stored users/vehicles.
- To connect to your MySQL schema, replace `AppStore` calls with DAO/JDBC operations.
