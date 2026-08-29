# Quản Lý Nhà Hàng

Đây là project quản lý nhà hàng bằng Java Spring Boot theo kiến trúc Microservices.
Project tập trung vào quản lý món ăn, bàn, khu vực, khách hàng, đặt bàn, hóa đơn và thanh toán.

## Công nghệ sử dụng

* Java 17
* Spring Boot 4
* Spring Data JPA
* Spring Security + JWT
* SQL Server
* Spring Cloud Eureka
* Spring Cloud Gateway
* Redis
* Apache Kafka
* Resilience4j
* OpenAPI / Swagger UI
* Spring Boot Actuator
* Maven
* Docker

## Kiến trúc

Project gồm 6 service:

* discovery-service: quản lý Service Discovery bằng Eureka
* api-gateway: Gateway xử lý và định tuyến request
* auth-service: đăng nhập, đăng ký, xác thực JWT
* restaurant-service: quản lý món ăn, bàn và khu vực
* reservation-service: quản lý khách hàng và đặt bàn
* payment-service: quản lý hóa đơn, chi tiết hóa đơn và thanh toán

Các service đăng ký và tìm kiếm nhau thông qua Eureka Service Discovery.

## Backend Engineering Practices

`restaurant-service` hiện được nâng cấp theo baseline production-ready để làm mẫu cho các service còn lại:

* Global Exception Handling với `@RestControllerAdvice`
* Bean Validation với `@Valid`, `@NotBlank`, `@NotNull`, `@Positive`
* Pagination + sorting + dynamic filtering bằng `Pageable` và `Specification`
* DTO tách khỏi Entity, MapStruct mapping
* RBAC bằng `@PreAuthorize` + `@EnableMethodSecurity`
* Structured request logging + `X-Request-Id`
* API response format thống nhất qua `ApiResponse<T>`
* Transaction boundary rõ ràng với `@Transactional`
* JPA auditing: `createdAt`, `updatedAt`
* OpenAPI / Swagger UI + JWT Bearer scheme
* Actuator: health, info, metrics, prometheus
* Redis cache với TTL và cache eviction
* Resilience4j: retry, timeout, circuit breaker cho Kafka publisher
* Kafka event-driven: publish `restaurant.menu.events` khi món ăn được tạo, cập nhật hoặc xóa

## API documentation

Khi chạy `restaurant-service`, Swagger UI có tại:

`http://localhost:8082/swagger-ui.html`

OpenAPI JSON:

`http://localhost:8082/v3/api-docs`

Actuator health:

`http://localhost:8082/actuator/health`

## Một số API

### Auth

POST /auth/login
POST /auth/register
POST /auth/refresh
POST /auth/logout
PUT  /auth/change-password

### Restaurant

GET    /monan
GET    /monan/{id}
POST   /monan
PUT    /monan/{id}
DELETE /monan/{id}
GET    /ban
GET    /khuvuc

### Reservation

POST /datban
GET  /datban
GET  /datban/{id}
PUT  /datban/{id}/cancel

### Payment

GET    /hoadon
GET    /hoadon/{maHD}
POST   /hoadon
PUT    /hoadon/{maHD}/thanhtoan
POST   /chitiethoadon/{maHD}/them-mon
PUT    /chitiethoadon/{maHD}/{maMon}
DELETE /chitiethoadon/{maHD}/{maMon}

## Infrastructure

`docker-compose.yml` hiện có thêm:

* Redis 7 với persistent volume
* Apache Kafka 4 chạy KRaft mode
* restaurant-service kết nối Redis và Kafka qua Docker network

## Database

Project sử dụng SQL Server và mỗi microservice có database riêng, không dùng chung database giữa các service:

* restaurant_auth
* restaurant_db
* restaurant_reservation
* restaurant_payment

Cách tách database giúp mỗi service quản lý dữ liệu độc lập và phù hợp với kiến trúc Microservices.
