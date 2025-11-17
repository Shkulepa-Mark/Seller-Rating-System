# Seller Rating System

This project is a Java-based implementation of the "Seller Rating System", built using **Spring 6**.

The architecture follows a classic **Layered Monolith** pattern (Controller, Service, Repository) to ensure separation of concerns (SOLID).

## Key Technologies

* **Core:** Spring 6 (JavaConfig)
* **Web:** Spring MVC (REST API)
* **Security:** Spring Security 6 (JWT Authentication)
* **Database:** Spring Data JPA (Hibernate)
* **DB Migration:** Liquibase
* **DB Connection:** PostgreSQL (with HikariCP)
* **Cache:** Spring Data Redis (for confirmation tokens)
* **Build:** Maven (packaged as a WAR)

---

## How to Run

This is **not** a Spring Boot application. It does not run from an embedded server.

1.  **Build the Project:**
    ```bash
    mvn clean package
    ```
2.  **Deploy the WAR:**
    * Take the generated `seller-rating-system.war` file from the `/target/` directory.
    * Deploy this file to an external Servlet Container (e.g., **Apache Tomcat 10**).
3.  **Database & Cache:**
    * Ensure you have a local **PostgreSQL** instance running with a database named `seller_rating_db`.
    * Ensure you have a local **Redis** instance running on `localhost:6379`.
4.  **Access:**
    * The API will be available at `http://localhost:8080/seller-rating-system/` (or your Tomcat's port).