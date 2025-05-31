# PayPal integration with Spring Boot

**Description:**
A Spring Boot application that demonstrates integration with PayPal’s REST API for payment processing. The project uses Java 21, Spring Boot 3.4.5, and the official PayPal Java SDK. It also leverages PostgreSQL for data persistence and includes JPA for ORM and validation support.

**Key Features:**
- RESTful API endpoints built with Spring Boot
- PayPal payment integration using the official SDK
- PostgreSQL database support (runtime)
- Data validation and JPA-based persistence
- Lombok for boilerplate code reduction
- Ready for testing with Spring Boot’s test starter

**Tech Stack:**
- Java 21
- Spring Boot 3.4.5
- PayPal REST API SDK (v1.14.0)
- PostgreSQL
- Lombok
- Maven

**Getting Started:**
- Clone the repository
- Configure your PayPal credentials and database connection in `application.properties`
- Build and run the application using Maven

**Build & Run:**
```bash
mvn clean install
mvn spring-boot:run
```

**Testing:**
```bash
mvn test
```
