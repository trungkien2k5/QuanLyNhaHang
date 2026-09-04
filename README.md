# Quản Lý Nhà Hàng

Backend quản lý nhà hàng xây dựng bằng **Java Spring Boot** theo kiến trúc **Microservices**, tập trung vào quản lý món ăn, bàn, khách hàng, đặt bàn, hóa đơn và thanh toán.

## Tech Stack

* **Java 17 · Spring Boot 4 · Maven**
* **Spring Data JPA · SQL Server**
* **Spring Security · JWT · RBAC**
* **Spring Cloud Gateway · Eureka**
* **Redis · Apache Kafka · Resilience4j**
* **Docker · OpenAPI / Swagger · Actuator**

## Architecture

Hệ thống gồm 6 service:

* **Discovery Service** — Service Discovery với Eureka
* **API Gateway** — Routing và bảo vệ API
* **Auth Service** — Đăng ký, đăng nhập, JWT, Refresh Token, OTP
* **Restaurant Service** — Món ăn, loại món, bàn, khu vực
* **Reservation Service** — Khách hàng, đặt bàn
* **Payment Service** — Hóa đơn, chi tiết hóa đơn, thanh toán

Mỗi domain có **database riêng**, giao tiếp giữa các service thông qua Service Discovery và REST API.

## Key Features

* JWT Authentication + Refresh Token Rotation
* Phân quyền **RBAC** với `@PreAuthorize`
* Validation và Global Exception Handling
* DTO + MapStruct, JPA Auditing
* Pagination, Sorting và Dynamic Filtering
* Redis Cache với TTL và Cache Eviction
* Kafka Event-Driven Architecture
* Resilience4j: Retry, Timeout, Circuit Breaker
* Structured Logging + `X-Request-Id`
* Swagger / OpenAPI
* Actuator: Health, Metrics, Prometheus
* Docker Compose cho infrastructure

## Main APIs

### Auth

`POST /auth/login` · `POST /auth/register` · `POST /auth/refresh` · `POST /auth/logout` · `PUT /auth/change-password`

### Restaurant

`GET/POST/PUT/DELETE /monan` · `GET /ban` · `GET /khuvuc`

### Reservation

`POST /datban` · `GET /datban` · `GET /datban/{id}` · `PUT /datban/{id}/cancel`

### Payment

`GET /hoadon` · `POST /hoadon` · `PUT /hoadon/{id}/thanhtoan` · CRUD `/chitiethoadon`

## Infrastructure

Docker Compose cung cấp:

* SQL Server
* Redis 7
* Apache Kafka 4 (KRaft)
* Các Spring Boot services

## API Documentation

Swagger UI: `http://localhost:8082/swagger-ui.html`

OpenAPI: `http://localhost:8082/v3/api-docs`

Actuator: `http://localhost:8082/actuator/health`
