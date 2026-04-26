# 🚗 Carpooling System Database

## Relational Schema & ERD Implementation

---

## 📌 Project Overview

The system supports:

* User management (Passengers & Drivers)
* Ride scheduling and booking
* Vehicle management
* Reviews and notifications
* Logging and saved routes

---

## 🧱 Database Design Approach

This project follows a **strict ERD → Relational Schema mapping**, meaning:

* All **entities** are converted into tables
* All **relationships** (including 1:N and M:N) are also represented as tables
* No optimization (like merging relationships into foreign keys) was performed to stay consistent with academic requirements

---

## 🗂️ Tables Overview

### 🔹 Entity Tables (10)

1. Users
2. Passengers
3. Drivers
4. Vehicles
5. Rides
6. Bookings
7. Reviews
8. Notifications
9. Saved_Routes
10. Logs

---

### 🔹 Relationship Tables (9)

1. Makes (User ↔ Booking)
2. For (Booking ↔ Ride)
3. Has (Booking ↔ Review)
4. Owns (Driver ↔ Vehicle)
5. Schedules (Driver ↔ Ride)
6. Assigned (Vehicle ↔ Ride)
7. Personalizes (User ↔ Notification)
8. Records (User ↔ Logs)
9. Saves (User ↔ Saved_Routes)

---

## 🔗 Key Design Notes

* **ISA Relationship**:

  * `Passengers` and `Drivers` are subclasses of `Users`
  * They share the same primary key (`User_ID`)

* **Composite Keys**:

  * Relationship tables use **composite primary keys**
  * Example:

    ```
    Owns(User_ID, License_Plate)
    ```

* **Data Integrity**:

  * Foreign keys enforce relationships between tables
  * Each relationship table connects two entities

---

## 🛠️ Technologies Used

* MySQL Workbench
* SQL (DDL & DML)
* ER Modeling

---

## ▶️ How to Run

1. Open **MySQL Workbench**
2. Create or select your schema (e.g., `team7`)
3. Run the provided SQL script:

   * Creates all tables (19 total)
   * Inserts sample data (10 rows per table)
4. Refresh schema if tables do not appear

---

## 🌐 Run Web App on External Tomcat

This repository includes a Maven profile that builds and deploys the app to your local Tomcat automatically.

### Prerequisites

1. Java 21
2. Maven 3.8+
3. Apache Tomcat 9.x

### One-Time Setup

Set your Tomcat home directory in your shell:

```bash
export CATALINA_HOME=/path/to/apache-tomcat-9.0.xx
```

You can add this to your shell profile (for example, `~/.zshrc`) so it is always available.

### Build + Deploy

From the project root, run:

```bash
mvn -Pdeploy-local-tomcat package
```

This command will:

1. Build `target/uniride.war`
2. Copy it to `$CATALINA_HOME/webapps/ROOT.war`

### Start Tomcat

```bash
$CATALINA_HOME/bin/startup.sh
```

Then open:

* http://localhost:8080/

### After Code Changes

Run the same deploy command again to publish updates:

```bash
mvn -Pdeploy-local-tomcat package
```

### Stop Tomcat

```bash
$CATALINA_HOME/bin/shutdown.sh
```

---

## 📊 Sample Data

Each table contains **10 sample records** to:

* Demonstrate relationships
* Enable testing of queries
* Simulate realistic system usage

---

## 🎯 Features Supported

* Create and manage users
* Assign drivers to rides
* Book rides as passengers
* Store and retrieve reviews
* Track notifications and logs
* Save frequently used routes

---

## ⚠️ Notes

* This design prioritizes **clarity and ERD accuracy** over optimization
* Some relationships could be simplified using foreign keys, but were kept as tables per assignment requirements

---

## 👥 Team

* Team 7

---

## 📌 Conclusion

This project demonstrates how to systematically convert an ERD into a relational database schema while preserving all entities and relationships. It serves as a strong foundation for building a full-stack carpooling application.

---