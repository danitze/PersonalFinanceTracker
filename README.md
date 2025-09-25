# Personal Finance Tracker

A **Spring Boot**–based web application for managing personal finances.  
It allows users to track expenses, set budget limits, and receive real-time notifications when thresholds are reached.  
The system is designed as **microservices** with Kafka-based messaging for scalability and resilience.

---

## ✨ Features

- **Accounts & Transactions**:  
  - Create multiple accounts with balance tracking.
  - Add, update, and view transactions (income/expense).
  - View summaries by category or date range.

- **Budget Limits**:
  - Set spending limits per category.
  - Automatic notifications when 80% and 100% thresholds are reached.

- **Notifications Microservice**:
  - Dedicated **Notification Service** running as a separate Spring Boot app.
  - Asynchronous communication using **Apache Kafka**.
  - Email and push notifications.
  - Retry and dead-letter queue (DLQ) support.

- **Observability**:
  - **Spring Boot Actuator** with health/metrics endpoints.
  - Prometheus & Grafana integration for monitoring and dashboards.

---

## 🏗️ Architecture

The application is split into **microservices**:

- **Main Service**:  
  Handles Accounts, Transactions, and Budget Limits.

- **Notification Service**:  
  Dedicated to sending notifications.  
  Communicates with the main service via **Kafka** topics.

### Key Technologies
- **Backend**: Spring Boot 3, Spring Data JPA, Spring Security
- **Messaging**: Apache Kafka (with retries & DLQ)
- **Database**: PostgreSQL (main) & H2 (tests)
- **Monitoring**: Spring Boot Actuator, Prometheus, Grafana
- **Build Tool**: Gradle (Kotlin DSL)

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Gradle

### 1️⃣ Run Infrastructure

```bash
docker-compose up -d
```

This starts:
- PostgreSQL
- Kafka broker

### 2️⃣ Run Microservices

- **Main Service**:  
  ```bash
  ./gradlew bootRun
  ```
- **Notification Service**:  
  Navigate to the `notification-service` folder:
  ```bash
  ./gradlew bootRun
  ```

---

## ⚙️ Configuration

Set environment variables or edit `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finance
spring.kafka.bootstrap-servers=localhost:9092
MAILTRAP_TOKEN=your-mailtrap-token
```

---

## 📈 Monitoring

- Access Prometheus at: `http://localhost:9090`
- Grafana dashboards at: `http://localhost:3000`
- Actuator endpoints (main service):  
  - Health: `http://localhost:8080/actuator/health`
  - Metrics: `http://localhost:8080/actuator/metrics`

---

## 🧩 Future Improvements

- Add front-end UI (React or Angular).
- Expand notification channels (SMS, mobile push).
- Deploy microservices with Kubernetes.

---

## 📜 License

This project is licensed under the MIT License.

---

### Author
**Danylo Andrieiev**  
Middle Android / Software Engineer
