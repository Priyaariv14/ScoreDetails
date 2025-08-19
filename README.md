# 🏏 ScoreDetails - Spring Boot Microservice

## 📖 Overview

**ScoreDetails** is a Spring Boot–based microservice designed to handle **user authentication** and **cricket score management**. It demonstrates enterprise-grade backend patterns, modern Spring Boot features, secure authentication, and production-ready design practices.

The project is structured following **Domain-Driven Design (DDD)** and **Layered Architecture** principles, making it easy to extend, test, and maintain.

---

## 🚀 Features

* **User Authentication** with JWT (login, register, token validation)
* **Secure REST APIs** protected with Spring Security
* **Score Management** (create, update, fetch scores)
* **Centralized Exception Handling** with custom error responses
* **Request Logging & Sensitive Data Masking**
* **Outbox Event Publishing** pattern for reliable async processing
* **Kafka Integration** for event-driven communication
* **External Service Calls** using RestTemplate
* **Resilience4j Integration**:
  - Retry mechanism for transient failures  
  - Circuit breaker to avoid cascading failures  
  - Rate limiting for API protection
* **API Documentation** using Swagger/OpenAPI
* **Unit & Integration Tests** with JUnit + Spring Boot Te
---

## 🏗️ Architecture & Design Patterns

### 1. **Layered Architecture**

* **Controller Layer** → Handles HTTP requests/responses (e.g., `AuthController`, `ScoreController`).
* **Service Layer** → Business logic encapsulated in services (`AuthService`, `ScoreService`).
* **Repository Layer** → Persistence with Spring Data JPA (`UserRepository`, `ScoreRepository`).

### 2. **DTO Pattern**

* Request/response separation using `ScoreRequest`, `ScoreResponse`, `AuthRequest`, `AuthResponse`.
* Prevents leaking of domain entities to the API.

### 3. **Mapper Pattern**

* `ScoreMapper` used to transform between entities and DTOs.
* Promotes clean separation of concerns.

### 4. **Security & Authentication**

* **JWT Authentication** implemented via `JwtUtil` and `JwtAuthFilter`.
* `SecurityConfig` ensures endpoints are protected.

### 5. **Filter Pattern**

* `LoggingFilter` for request logging.
* `JwtAuthFilter` for authentication validation.

### 6. **Interceptor Pattern**

* `SensitiveDataMaskingInterceptor` ensures logs do not expose sensitive data (e.g., passwords).

### 7. **Publisher/Outbox + Kafka Pattern**
* `OutboxPublisher` and `OutboxEvent` ensure reliable event-driven communication.
* Outbox events are **published to Kafka topics** for downstream processing.
* `OutboxRepository` manages transactional consistency

### 8. **Resilience Patterns (Resilience4j)**
* **Retry Pattern** → Automatically retries failed external calls via RestTemplate.  
* **Circuit Breaker Pattern** → Prevents repeated calls to failing services.  
* **Rate Limiting** → Protects APIs from overuse and throttles requests gracefully.  
* All applied seamlessly through `RestTemplateConfig` and service-level annotations.

### 9. **Exception Handling Strategy**

* `GlobalExceptionHandler` + custom exceptions (`UserNotFoundException`, `TimeoutException`, etc.).
* Consistent error responses with `ErrorResponse` model.

## 🛠️ Tech Stack

* **Language**: Java 17+
* **Framework**: Spring Boot 3.x
* **Security**: Spring Security + JWT
* **Persistence**: Spring Data JPA (Hibernate)
* **Database**: H2 / PostgreSQL (configurable)
* **Build Tool**: Gradle
* **API Docs**: Swagger / OpenAPI
* **Testing**: JUnit 5, Spring Boot Test, Mockito

---

## ⚙️ Setup & Run

### 1. Clone the Repository

```bash
git clone https://github.com/username/ScoreDetails.git
cd ScoreDetails
```

### 2. Build the Project

```bash
./gradlew clean build
```

### 3. Run the Application

```bash
./gradlew bootRun
```

App will be available at: `http://localhost:8080`

### 4. API Documentation

Visit Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🔑 Example API Endpoints

### Authentication

* `POST /auth/register` → Register new user
* `POST /auth/login` → Authenticate & get JWT

### Scores

* `GET /scores` → Fetch all scores
* `POST /scores` → Add a new score

---

## ✅ Testing

Run unit and integration tests:

```bash
./gradlew test
```

Test coverage includes:

* Controller tests (`AuthControllerTest`, `ScoreControllerTest`)
* Service tests (`AuthServiceImplTest`, `ScoreServiceImplTest`)
* Integration tests (`AuthServiceIntegrationTest`)

---

## 🔒 Security Best Practices Implemented

* JWT token-based authentication
* Password encryption with Spring Security
* Sensitive data masking in logs
* Role-based access control

---

## 📐 Design Principles Followed

* **SOLID Principles**
* **Separation of Concerns**
* **Convention over Configuration** (Spring Boot)
* **Fail-fast Exception Handling**

---

## 📊 Future Enhancements

* Docker & Kubernetes deployment
* Redis caching for performance
* CI/CD integration (GitHub Actions/Jenkins)
* Observability with Prometheus & Grafana

---

## 👩‍💻 Author

Developed by **Priyaariv14** as part of a full-stack enterprise-grade solution.

