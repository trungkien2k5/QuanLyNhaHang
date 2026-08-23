# Quản Lý Nhà Hàng

Đây là project quản lý nhà hàng mình làm bằng Java Spring Boot, theo kiến trúc Microservices.
Project tập trung vào các chức năng chính như quản lý món ăn, bàn, khu vực, khách hàng, đặt bàn, hóa đơn và thanh toán.

## Công nghệ sử dụng

* Java
* Spring Boot
* Spring Data JPA
* Spring Security
* JWT
* SQL Server
* Spring Cloud Eureka
* Spring Cloud Gateway
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

Sơ đồ hoạt động:

                         API Gateway
                            :8080
                              |
          +-------------------+-------------------+
          |                   |                   |
          v                   v                   v
    auth-service       restaurant-service    reservation-service
       :8081                  :8082                 :8083
                                                     |
                                                     v
                                              restaurant-service

                         payment-service
                              :8084
                                |
                                v
                         restaurant-service


                    discovery-service
                         :8761

Các service đăng ký và tìm kiếm nhau thông qua Eureka Service Discovery.

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

## Database

Project sử dụng SQL Server và mỗi microservice có database riêng, không dùng chung database giữa các service:

restaurant_auth
restaurant_db
restaurant_reservation
restaurant_payment


Cách tách database này giúp mỗi service quản lý dữ liệu độc lập và phù hợp với kiến trúc Microservices.
